﻿var game = {};
playerID = null;
game.myTurn = false;
game.started = false;
game.numOfRetries = 0;
game.initialized = false;

 _.templateSettings = {
        evaluate : /\{\[([\s\S]+?)\]\}/g,
        interpolate : /\{\{([\s\S]+?)\}\}/g
    };

$(function () {

    App = {
        Models: {},
        Collections: {},
        Views: {}
    };

    template = function(id) {
        return _.template($('#' + id).html());
    };

    App.Models.UskociCard = Backbone.Model.extend({});

    App.Views.UskociCard = Backbone.View.extend({
        tagName: 'div',

        template: template('cardTemplate'),

        events : {
            'click': 'cardClicked',
            'mouseenter': 'hoverIn',
            'mouseleave': 'hoverOut'
        },

        hoverIn: function (e) {
            e.preventDefault();
            var descriptionDIV = '<div class="description">' + this.model.get('description') +'</div>';
            this.$el.children(":first").append(descriptionDIV);
            this.$el.children(":first").toggleClass('zoomed');
        },
        
        hoverOut: function (e) {
            e.preventDefault();
            this.$el.children(":first").toggleClass('zoomed');
            this.$el.find('.description').remove();
        },

        cardClicked: function() {
            subSocket.push(jQuery.stringifyJSON({userId:playerID, action:"playCard", cardId:this.model.get('id'), gameId:"0"}));
        },

        render: function(index) {
            var uskociCardElement = $(this.template(this.model.toJSON()));
            uskociCardElement.css({"left": 182.5 * (index % 4), "top": 249 * Math.floor(index / 4)});
            this.$el.html(uskociCardElement);
            return this;
        }
    });

    App.Collections.UskociCards = Backbone.Collection.extend({
        model: App.Models.UskociCard,
        url: document.location + 'rest/cards'
    });

    App.Views.UskociHand = Backbone.View.extend({
        el: $('#playerCards'),

        //initialize with uskociCardsCollection's copy filtered by cardsInHand
        initialize: function(uskociCardsCollection, cardsInHand) {
            this.collection = _(uskociCardsCollection.filter(function(uskociCard) { return _.include(cardsInHand, uskociCard.id); }));
            this.render();
        },

        render: function () {
            this.$el.empty();
            this.collection.each(this.addOne, this);
            return this;
        },

        addOne: function(uskociCard) {
            var uskociCardView = new App.Views.UskociCard({model: uskociCard});
            var index = this.collection.indexOf(uskociCard);
            this.$el.append(uskociCardView.render(index).el);
        }
    });

    uskociCardsCollection = new App.Collections.UskociCards();
    
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
                break;
        }

        if (game.initialized)
            repaintTable(response);
    }

    function drawJoinPlayerControls(response) {
        if (game.started)
            return;

        uskociCardsCollection.fetch();

        $('#startGame').remove();
        $('#playerSelect').toggleClass('hidden');
        $('#gameNavigation').append('<button id="joinGame" class="uskociButton btn btn-primary btn-large">Join game</button>');
        $('#joinGame').click(joinGame_MouseClick);
        $("#game").toggleClass("hidden");
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

    function repaintResourcePiles() {

        var playersResourcesByType = rotateArray(game.playersResourcesByType, parseInt(playerID,10)-1);
        var smallCardTemplate = template('smallCardTemplate');

        for (var i = 1; i <= game.numberOfPlayers; i++) {

            var playerResourcesByType = playersResourcesByType[i-1];

            for (var key in playerResourcesByType) {
                if (playerResourcesByType.hasOwnProperty(key)) {

                    var selector = '#resource' + i + ' .' + key;
                    $(selector).empty();

                    for (var j = 0; j < playerResourcesByType[key].length; j++) {
                        var cardID = playerResourcesByType[key][j.toString()];
                        $(selector).append(smallCardTemplate({cardID:cardID}));
                        $(selector + ' .' + cardID).css("background-position", getSmallCardPosition(cardID));
                        $(selector).find('[data-pattern="' + cardID + '"]').css({"top":20 * j + 20});
                    }

                }
            }

        }
    }

    function rotateArray(array, offset)
    {
        var rotatedArray = [];
        for (var i = 0; i < _.size(array); i++) {
            rotatedArray[i] = array[((i+offset)%_.size(array))+1];
        }

        return rotatedArray;
    }

    function getSmallCardPosition(cardID) {
        var regex = /(.+)px\s(.+)px/;
        var originalPosition = getCard(cardID).position;
        var regexMatch = originalPosition.match(regex);
        var positionX_scaled = (parseFloat(regexMatch[1])/2).toString();
        var positionY_scaled = (parseFloat(regexMatch[2])/2).toString();
        return positionX_scaled + "px " + positionY_scaled + "px";
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
        uskociHandView = new App.Views.UskociHand(uskociCardsCollection, game.playerCards);
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