var game = {};
playerID = null;
game.myTurn = false;
game.started = false;
game.numOfRetries = 0;
game.initialized = false;

$(function () {
    "use strict";

    var content = $('#console');
    var socket = $.atmosphere;
    var request = { url:document.location.toString() + 'rest' + '/gamecontrol',
        contentType:"application/json",
        logLevel:'debug',
        transport:'long-polling',
        fallbackTransport:'long-polling'};


    request.onOpen = function (response) {
        content.html($('<p>', { text:'Uskoci (gameControl) connected using ' + response.transport }));
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
            if (playerID !== null && json.actionStatus === "OK")
                assignValues(json);
            checkLastMessageAndPerformAction(json);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }
    };

    function paintDeckAndRestartNextTurnButtons() {
        $("#deck").append('<div class="card"><div class="face front"></div><div class="face back"></div></div>');
        $("#deck").click(drawCard);
        $("#deck").toggleClass('hidden');
        $("#controlButtons").append('<button id="nextTurn" class="uskociButton btn btn-primary btn-large">Next player!</button>');
        $("#controlButtons").append('<button id="restartGame" class="uskociButton btn btn-primary btn-large">Restart game</button>');
        $("#nextTurn").click(nextTurn);
        $("#restartGame").click(restartGame);
    }

    function checkLastMessageAndPerformAction(response) {
        if (response.actionStatus != "OK") {
            $('#turnInfo').html(response.actionStatus).addClass('alert-error');
            return;
        }

        $('#turnInfo').html(response.actionStatus).removeClass('alert-error');

        //noinspection FallthroughInSwitchStatementJS
        switch (response.lastAction["action"]) {
            case "startGame":
                if (!game.started)
                    drawJoinPlayerControls(response);
                break;
            case "restartGame":
                game.started = false;
                subSocket.push(jQuery.stringifyJSON({userId:"", action:"startGame", cardId:"", gameId:"0"}));
                location.reload();
                break;
            case "drawCard":
                animateCardDrawal();
                break;
        }

        if (game.initialized)
            repaintTable(response);
    }

    function drawJoinPlayerControls(response) {
        if (game.started)
            return;

        $('#startGame').remove();
        $('#playerSelect').toggleClass('hidden');
        $('#gameNavigation').append('<button id="joinGame" class="uskociButton btn btn-primary btn-large">Join game</button>');
        $('#joinGame').click(joinGame_MouseClick);
        content.append("Game started! ");
        content.append("Player " + response.currentPlayerId + " is on the move.");
        game.started = true;

    }

    function assignValues(response) {
        game.playerCards = response.playersCards[playerID];
        game.playersPoints = response.playersPoints;
        game.numberOfPlayers = response.numberOfPlayersJoined;
        game.myTurn = response.currentPlayerId == playerID;
        game.currentPlayerID = response.currentPlayerId;
        game.resourcePiles = response.playersResources;
        game.playersResourcesByType = response.playersResourcesByType;
        game.actionStatus = response.actionStatus;
    }

    function repaintTable(response) {
        checkPlayerOnTheMove();
        repaintResourcePiles();
        repaintHand();

        if (response !== null)
            modifyGameStatus(response);
    }


    request.onError = function (response) {
        content.html($('<p>', { text:'Sorry, but there\'s some problem with your socket or the server is down' }));
    };

    function modifyGameStatus(gameStatusResponse) {
        content.append('<p>PlayerID / lastAction / cardID: ' + gameStatusResponse.lastAction["userId"] + ' / ' + gameStatusResponse.lastAction["action"] + ' / ' + gameStatusResponse.lastAction["cardId"] + '</p>');
    }

    var subSocket = socket.subscribe(request);
    checkIfGameStarted();

    function checkIfGameStarted() {
        subSocket.push(jQuery.stringifyJSON({userId:"", action:"isGameStarted", cardId:"", gameId:"0"}));
    }

    function joinGame_MouseClick() {
        if (game.playerCards === null)
            return;

        $("#game").height(800);

        repaintTable(null);
        paintDeckAndRestartNextTurnButtons();

        $('#welcomeScreen').remove();
        game.initialized = true;
    }

    function startGame_MouseClick() {
        subSocket.push(jQuery.stringifyJSON({userId:playerID, action:"startGame", cardId:"", gameId:"0"}));
    }

    function nextTurn() {
        subSocket.push(jQuery.stringifyJSON({userId:playerID, action:"setNextTurn", cardId:"", gameId:"0"}));
    }

    function drawCard() {
        subSocket.push(jQuery.stringifyJSON({userId:playerID, action:"drawCard", cardId:"", gameId:"0"}));
    }

    function animateCardDrawal() {
        //TODO: create some nice animation sequences
    }

    function repaintResourcePiles() {

        var myResourcesByType = game.playersResourcesByType[playerID];

        for(var key in myResourcesByType) {
            if(myResourcesByType.hasOwnProperty(key)) {

                var selector = '#resource1 .' + key;
                $(selector).empty();

                var myResourcesByType = game.playersResourcesByType[playerID];

                for(var i=0; i<myResourcesByType[key].length; i++)
                {
                    var cardID = myResourcesByType[key][i.toString()];
                    $(selector).append(smallCardTemplate({cardID: cardID}));
                    $(selector + ' .' + cardID).css("background-position", getSmallCardPosition(cardID));
                    $(selector).find('[data-pattern="' + cardID + '"]').css({"top":20*i});
                }

            }
        }
    }

    function getSmallCardPosition(cardID) {
        var regex = /(.+)px\s(.+)px/;
        var originalPosition = getCard(cardID).position;
        var regexMatch = originalPosition.match(regex);
        var positionX_scaled = (parseFloat(regexMatch[1])/2).toString();
        var positionY_scaled = (parseFloat(regexMatch[2])/2).toString();
        return positionX_scaled + "px " + positionY_scaled + "px";
    }

    function playCard() {
        var card = $.parseJSON($(this).attr("data-pattern"))
        var cardID = card.id;
        cardClicked(cardID);
        subSocket.push(jQuery.stringifyJSON({userId:playerID, action:"playCard", cardId:cardID, gameId:"0"}));
    }

    function setPlayer() {
        if (!game.started)
            return;
        playerID = $("select option:selected").attr("value");
        subSocket.push(jQuery.stringifyJSON({userId:playerID, action:"playersCards", cardId:"", gameId:"0"}));
    }

    //Navigation
    $('#startGame').click(startGame_MouseClick);
    $('#playerSelect').change(setPlayer);

    function repaintHand() {

        $("#playerCards").empty();

        if(game.playerCards.length === 0)
            return;

        $("#playerCards").append('<div class="card"><div class="face front"></div><div class="face back"></div></div>');

        for (var i = 0; i < game.playerCards.length - 1; i++) {
            $("#playerCards .card:first-child").clone().appendTo("#playerCards");
        }

        $("#playerCards").children().each(function (index) {

            $(this).css({"left":($(this).width() + 20) * (index % 4), "top":($(this).height() + 20) * Math.floor(index / 4)
            });

            var cardID = game.playerCards.sort().pop();
            var card = getCard(cardID);
            $(this).find(".back").addClass(cardID);
            $(this).attr("data-pattern", jsonToString(card));
            $('.' + cardID).css("background-position", card.position);
            $(this).toggleClass("card-flipped");
            $(this).click(playCard);
            $(this).hover(card_onHoverIn, card_onHoverOut);
        });

    }

    function checkPlayerOnTheMove() {
        if (game.myTurn)
            $("#turnInfo").html("Your turn!");
        else
            $("#turnInfo").html("Wait... Player " + game.currentPlayerID + " is on turn.");
    }

    function card_onHoverIn()
    {
        var card = $.parseJSON($(this).attr("data-pattern"));
        var descriptionDIV = '<div class="description">' + card.description +'</div>';
        $(this).append(descriptionDIV);
        $(this).toggleClass('zoomed');
    }

    function card_onHoverOut()
    {
        $('div').remove('.description');
        $(this).toggleClass('zoomed');
    }

    function restartGame() {
        $(function () {
            $("#dialog-confirm").dialog({
                resizable:false,
                height:140,
                modal:true,
                buttons:{
                    "Restart game":function () {
                        subSocket.push(jQuery.stringifyJSON({userId:"", action:"restartGame", cardId:"", gameId:"0"}));
                    },
                    Cancel:function () {
                        $(this).dialog("close");
                    }
                }
            });
        });
    }

    function cardClicked(cardID) {
        $('#console').append("<p>CardID / Summary: " + cardID + " / " + getCard(cardID).summary + "</p>");
    }


    function getCard(cardID) {

        var card = null;

        $.ajax({
            type:'GET',
            url:document.location.toString() + 'rest/card/' + cardID,
            async:false,
            dataType:'json',
            success:function (json) {
                card = json;
            }
        });

        return card;
    }

    function jsonToString(json)
    {
        return JSON.stringify(json);
    }

});
