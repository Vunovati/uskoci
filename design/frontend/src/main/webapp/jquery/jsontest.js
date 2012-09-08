var game = {};
game.deck = ['id1', 'id2', 'id3'];

$(function () {
    "use strict";

    game.deck.sort();
    for(var i=0;i<2;i++){
        $(".card:first-child").clone().appendTo("#cards");
    }

    $("#cards").children().each(function(index) {

        $(this).css({"left" : ($(this).width() + 20) * (index % 4), "top" : ($(this).height() + 20) * Math.floor(index / 4)
        });

        var pattern = game.deck.pop();
        $(this).find(".back").addClass(pattern);
        $(this).attr("data-pattern",pattern);
        $(this).click(selectCard);
    });

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
        modifyGameStatus(json.text);        
    };


    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    var subSocket = socket.subscribe(request);
    var cardSummary = '';
	      	  
    function selectCard() {
            var msg = $(this).attr("data-pattern");
            subSocket.push(jQuery.stringifyJSON({userId: "2", action: "drawCard", cardId: "", gameId: "0"}));
    }

    function flipCardMessage(cardID) {
          $("#cards").find('*[data-pattern="' + message + '"]').toggleClass("card-flipped")
		  getCardSummary(message);
          $('#console').append("<p>Odabrano: " + cardSummary + "</p>");
    }

	function getCardSummary(cardID) {
		$.ajax({
            type: 'POST',   
			url: document.location.toString() + 'rest/card/' + cardID,
			async: false,
			dataType: 'json',
			success: function(card) {
				cardSummary = card.summary;
			}
		});
	}

    function modifyGameStatus(gameStatusResponse) {
          content.append("<p>Na redu je igrač broj: " + gameStatusResponse + "</p>");
    }

});
