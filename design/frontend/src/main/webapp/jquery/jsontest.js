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
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }

        if (playerID != null && json.actionStatus == "OK")
            assignValues(json);

        checkLastMessageAndPerformAction(json);

    };

    function paintDeckAndRestartNextTurnButtons() {
        $("#game").append('<div id="deck"><div class="card"><div class="face front"></div><div class="face back"></div></div></div>');
        $("#deck").click(drawCard);
        $("#game").append('<button id="nextTurn" class="uskociButton">Next player!</button>');
        $("#game").append('<button id="restartGame" class="uskociButton">Restart game</button>');
        $("#nextTurn").click(nextTurn);
        $("#restartGame").click(restartGame);
    }

    function checkLastMessageAndPerformAction(response) {
        if (response.actionStatus != "OK") {
            console.log(response.actionStatus);
            return;
        }

        //noinspection FallthroughInSwitchStatementJS
        switch (response.lastAction["action"]) {
            case "isGameStarted":
                if (!response.gameStarted && game.numOfRetries < 50)
                    setTimeout(function () {
                        checkIfGameStarted();
                        game.numOfRetries++;
                    }, 100);
                //TODO: obavijest o timeoutu
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
        $('#gameNavigation').append('<button id="joinGame" class="uskociButton">Join game</button>');
        $('#joinGame').click(joinGame_MouseClick);
        content.append("Game started! ");
        content.append("Player " + response.currentPlayerId + " starts first.");
        game.started = true;

    }

    function assignValues(response) {
        game.playerCards = response.playersCards[playerID];
        game.playersPoints = response.playersPoints;
        game.numberOfPlayers = response.numberOfPlayersJoined;
        game.myTurn = response.currentPlayerId == playerID;
        game.currentPlayerID = response.currentPlayerId;
        game.resourcePiles = response.playersResources;
    }

    function repaintTable(response) {
        checkPlayerOnTheMove();
        repaintResourcePiles();
        repaintHand();

        if (response != null)
            modifyGameStatus(response);
    }


    request.onError = function (response) {
        content.html($('<p>', { text:'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    function modifyGameStatus(gameStatusResponse) {
        content.append('<p>PlayerID / lastAction / cardID: '
            + gameStatusResponse.lastAction["userId"] + ' / '
            + gameStatusResponse.lastAction["action"] + ' / '
            + gameStatusResponse.lastAction["cardId"] + '</p>');
    }

    var subSocket = socket.subscribe(request);
    checkIfGameStarted();

    function checkIfGameStarted() {
        subSocket.push(jQuery.stringifyJSON({userId:"", action:"isGameStarted", cardId:"", gameId:"0"}));
    }

    function joinGame_MouseClick() {
        if (game.playerCards == null)
            return;

        $("#game").height(800);

        repaintTable(null);
        paintDeckAndRestartNextTurnButtons();

        $('#joinGame').remove();
        $('#playerSelect').remove();
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
        $("#resourcePiles").empty();

        for (var i = 1; i <= game.numberOfPlayers; i++) {
            var resourcePile = game.resourcePiles[i.toString()];
            var resourcePileID = 'player' + i.toString() + 'Resources';
            $("#resourcePiles").append('<div id=' + resourcePileID + ' class="resourcePile"><p class="resourcePileText">Player '
                + i.toString() + ' resource pile (' + game.playersPoints[(i-1).toString()] + ')</p><ul id="list' + resourcePileID + '"></ul></div>');

            for (var j = 0; j < resourcePile.length; j++) {
                var cardID = resourcePile[j.toString()];
                $('#list' + resourcePileID).append(createCard(cardID));
                $('.' + cardID).css("background-position", getCardPosition(cardID));

            }

            $('#list' + resourcePileID).children().each(function (index) {

                $(this).css({"left":($(this).width() * 0.3 + 5) * (index % 5), "top":($(this).height() * 0.3 + 5) * Math.floor(index / 5)});

            });
        }
    }

    function createCard(cardID) {
        return '<li class="card card-flipped" data-pattern="' + cardID + '"><div class="face front"></div><div class="face back ' + cardID + '"></div></li>';
    }

    function playCard() {
        var cardID = $(this).attr("data-pattern");
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

        if(game.playerCards.length == 0)
            return;

        $("#playerCards").append('<div class="card"><div class="face front"></div><div class="face back"></div></div>');
        <!-- .card -->

        for (var i = 0; i < game.playerCards.length - 1; i++) {
            $("#playerCards .card:first-child").clone().appendTo("#playerCards");
        }

        $("#playerCards").children().each(function (index) {

            $(this).css({"left":($(this).width() + 20) * (index % 4), "top":($(this).height() + 20) * Math.floor(index / 4)
            });

            var pattern = game.playerCards.sort().pop();
            $(this).find(".back").addClass(pattern);
            $(this).attr("data-pattern", pattern);
            $('.' + pattern).css("background-position", getCardPosition(pattern));
            $(this).toggleClass("card-flipped");
            $(this).click(playCard);
        });

    }

    function checkPlayerOnTheMove() {
        if (game.myTurn)
            $("#turnInfo").html("Your turn!");
        else
            $("#turnInfo").html("Wait... Player " + game.currentPlayerID + " is on turn.");
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
        $('#console').append("<p>CardID / Summary: " + cardID + " / " + getCardSummary(cardID) + "</p>");
    }


    function getCardSummary(cardID) {
        var cardSummary = null;

        $.ajax({
            type:'GET',
            url:document.location.toString() + 'rest/card/' + cardID,
            async:false,
            dataType:'json',
            success:function (card) {
                cardSummary = card.summary;
            }
        });

        return cardSummary;
    }

    function getCardPosition(cardID) {
        var cardPosition = null;

        $.ajax({
            type:'GET',
            url:document.location.toString() + 'rest/card/' + cardID,
            async:false,
            dataType:'json',
            success:function (card) {
                cardPosition = card.position;
            }
        });

        return cardPosition;
    }

});
