var game = {};
playerID = null;
game.myTurn = false;
game.currentAction = null;
var initialization = true;

$(function () {
    "use strict";

    var content = $('#console');
    var socket = $.atmosphere;
    var request = { url: document.location.toString() + 'rest' + '/gamecontrol', //ovaj url kasnije promijeniti u web.xml
    contentType : "application/json",
    logLevel : 'debug',
    transport : 'long-polling' ,
    fallbackTransport: 'long-polling'};


    request.onOpen = function(response) {
        content.html($('<p>', { text: 'Uskoci (gameControl) connected using ' + response.transport }));
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }

        game.playerCards = json.playersCards[playerID];
        game.numberOfPlayers = json.numberOfPlayersJoined;
        game.myTurn = json.currentPlayerId == playerID
        game.currentPlayerID = json.currentPlayerId
        game.resourcePiles = json.playersResources;

        if(!initialization)
            redrawTable();

        modifyGameStatus(playerID + " / " + json.playersCards[playerID]);        
    };


    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    
    function flipCard() {
        var msg = $(this).attr("data-pattern");
        flipCard(msg);
    }

    function modifyGameStatus(gameStatusResponse) {
      content.append("<p>PlayerID / Hand: " + gameStatusResponse + "</p>");
  }

  var subSocket = socket.subscribe(request);

  function startGame()
  {
    if(game.playerCards == null)
        return;

    renderTable();
    $('#startGame').remove();
    $('#playerSelect').remove();
}

function nextTurn()
{
    subSocket.push(jQuery.stringifyJSON({userId: playerID, action: "setNextTurn", cardId: "", gameId: "0"}));
    game.currentAction = "nextTurn";        
}

function drawCard()
{
    subSocket.push(jQuery.stringifyJSON({userId: playerID, action: "drawCard", cardId: "", gameId: "0"}));
    game.currentAction = "drawCard";        
}

function repaintDeck(cardID)
{
    $("#deck").find(".back").addClass(cardID);
    $("#deck").find(".card").attr("data-pattern",cardID);
    $('.' + cardID).css("background-position", getCardPosition(cardID));
    $("#deck").find(".card").addClass("card-flipped")
    $('#deck').unbind('click', drawCard);
    $("#deck").click(putInHand);
}

function putInHand()
{
    $("#deck").find(".card").detach().prependTo($('#playerCards'));

    var cardAdded = $("#playerCards .card:first-child");
    var cardIndex = game.playerCards.length-1;

    cardAdded.css({"left" : (cardAdded.width() + 20) * (cardIndex % 4), "top" : (cardAdded.height() + 20) * Math.floor(cardIndex / 4)}).delay(100);

    $("#deck").append('<div class="card"><div class="face front"></div><div class="face back"></div></div>');
    $("#deck").unbind('click', putInHand);
    $("#deck").click(drawCard);
    cardAdded.click(playCard);
}

function repaintResourcePiles()
{
    $("#resourcePiles").empty();

    for(var i=1;i<=game.numberOfPlayers;i++)
    {
        var resourcePile = game.resourcePiles[i.toString()];
        var resourcePileID = 'player' + i.toString() + 'Resources';
        $("#resourcePiles").append('<div id=' + resourcePileID + ' class="resourcePile"><p class="resourcePileText">Player '
            + i.toString() +' resource pile</p><ul id="list' + resourcePileID + '"></ul></div>');

        for(var j=0;j<resourcePile.length;j++)
        {
            var cardID = resourcePile[j.toString()];
            $('#list'+resourcePileID).append(createCard(cardID));
            $('.' + cardID).css("background-position", getCardPosition(cardID));

        }

        $('#list'+resourcePileID).children().each(function(index) {

            $(this).css({"left" : ($(this).width()*0.3 + 5) * (index % 5), "top" : ($(this).height()*0.3 + 5) * Math.floor(index / 5)});

        });
    }
}


function createCard(cardID)
{
    var card = '<li class="card card-flipped" data-pattern="' + cardID +'"><div class="face front"></div><div class="face back ' + cardID +'"></div></li>';
    return card;
}

function playCard()
{
    if(!game.myTurn)
        return;

    var cardID = $(this).attr("data-pattern");
    subSocket.push(jQuery.stringifyJSON({userId: playerID, action: "playCard", cardId: cardID, gameId: "0"}));
    $(this).remove();
    modifyGameStatus("Player " + playerID + " puts card " + getCardSummary(cardID) + " on his resource pile.");
    game.currentAction = "playCard";
}

function setPlayer() {
    playerID = $("select option:selected").attr("value");
    subSocket.push(jQuery.stringifyJSON({userId: playerID, action: "playersCards", cardId: "", gameId: "0"}));
}

    //Navigation
    $('#startGame').click(startGame);
    $('#playerSelect').change(setPlayer);

    function renderTable() {

        $("#game").height(800);

        if(game.myTurn)
            $("#turnInfo").html("Your turn!");
        else
            $("#turnInfo").html("Wait... Player " + game.currentPlayerID + " is on turn.");

        $("#playerCards").append('<div class="card"><div class="face front"></div><div class="face back"></div></div>'); <!-- .card -->

        for(var i=0;i<game.playerCards.length-1;i++){
            $(".card:first-child").clone().appendTo("#playerCards");
        }

        $("#game").append('<div id="deck"><div class="card"><div class="face front"></div><div class="face back"></div></div></div>');
        $("#deck").click(drawCard);
        
        for(var i=1;i<=game.numberOfPlayers;i++)
        {
            $("#resourcePiles").append('<div id="player' + i.toString() + 'Resources" class="resourcePile"><p class="resourcePileText">Player '
                + i.toString() +' resource pile</p></div>')
        }

        $("#playerCards").children().each(function(index) {

            $(this).css({"left" : ($(this).width() + 20) * (index % 4), "top" : ($(this).height() + 20) * Math.floor(index / 4)
        });

            var pattern = game.playerCards.pop();
            $(this).find(".back").addClass(pattern);
            $(this).attr("data-pattern",pattern);
            $('.' + pattern).css("background-position", getCardPosition(pattern)); //dodjeljujemo CSS svakoj karti prema MongoDB
            $(this).click(playCard);
            flipCard(pattern);
        });

        $("#game").append('<button id="nextTurn" class="uskociButton">Next player!</button>');
        $("#nextTurn").click(nextTurn);
        initialization = false;
    }

    function redrawTable()
    {
        if(initialization)
            return;

        if(game.myTurn)
            $("#turnInfo").html("Your turn!")
        else
            $("#turnInfo").html("Wait... Player " + game.currentPlayerID + " is on turn.");

        if(game.currentAction == "drawCard" && game.myTurn)
            repaintDeck(LastOf(game.playerCards));
        else
            repaintResourcePiles();
    }

    function flipCard(cardID) {
      $("#playerCards").find('*[data-pattern="' + cardID + '"]').toggleClass("card-flipped")
      $('#console').append("<p>CardID / Summary: " + cardID + " / " + getCardSummary(cardID) + "</p>");
  }

  
  function getCardSummary(cardID) {
    var cardSummary = null;

    $.ajax({
        type: 'GET',   
        url: document.location.toString() + 'rest/card/' + cardID,
        async: false,
        dataType: 'json',
        success: function(card) {
            cardSummary = card.summary;
        }
    });

    return cardSummary;
}

function getCardPosition(cardID) {
    var cardPosition = null;

    $.ajax({
        type: 'GET',   
        url: document.location.toString() + 'rest/card/' + cardID,
        async: false,
        dataType: 'json',
        success: function(card) {
            cardPosition = card.position;
        }
    });

    return cardPosition;
}

function LastOf(array)
{
    return array[array.length-1]
}
});
