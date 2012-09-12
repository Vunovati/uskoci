var game = {};
var playerID = "X";

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
        if(json.currentPlayerId != null){
        game.playerCards = json.playersCards;
        modifyGameStatus(json.currentPlayerId + " / " + json.playersCards); 
        }
    };


    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    
    function selectCard() {
            var msg = $(this).attr("data-pattern");
            subSocket.push(jQuery.stringifyJSON({userId: playerID, action: "drawCard", cardId: "", gameId: "0"}));
            flipCard(msg);

    }

    function modifyGameStatus(gameStatusResponse) {
          content.append("<p>PlayerID / Hand: " + gameStatusResponse + "</p>");
    }

    var subSocket = socket.subscribe(request);

    subSocket.push(jQuery.stringifyJSON({userId: playerID, action: "playersCards", cardId: "", gameId: "0"}));

    function startGame()
    {
        renderTable();
        $('#startGame').remove();
    }

    $('#startGame').click(startGame);

    function renderTable() {

        $("#cards").append('<div class="card"><div class="face front"></div><div class="face back"></div></div>'); <!-- .card -->

        for(var i=0;i<game.playerCards.length-1;i++){
            $(".card:first-child").clone().appendTo("#cards");
        }

        $("#cards").children().each(function(index) {

            $(this).css({"left" : ($(this).width() + 20) * (index % 4), "top" : ($(this).height() + 20) * Math.floor(index / 4)
            });

            var pattern = game.playerCards.pop();
            $(this).find(".back").addClass(pattern);
            $(this).attr("data-pattern",pattern);
            $('.' + pattern).css("background-position", getCardPosition(pattern)); //dodjeljujemo CSS svakoj karti prema MongoDB
            $(this).click(selectCard);
        });
    }

    function flipCard(cardID) {
          $("#cards").find('*[data-pattern="' + cardID + '"]').toggleClass("card-flipped")
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

});
