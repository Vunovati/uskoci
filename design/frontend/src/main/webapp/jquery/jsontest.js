﻿var game = {};
game.deck = ['Kralj', 'Dama', 'Dečko'];

$(function () {
    "use strict";

    game.deck.sort();
    for(var i=0;i<2;i++){
        $(".card:first-child").clone().appendTo("#cards");
    }

    $("#cards").children().each(function(index) {

        $(this).css({"left" : ($(this).width() + 20) * (index % 4), "top" : ($(this).height() + 20) * Math.floor(index / 4)
    });

        //game.timer = setInterval(gameloop,5000);

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
        content.html($('<p>', { text: 'Uskoci connected using ' + response.transport }));
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }

        addMessage(json);

    };


    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    var subSocket = socket.subscribe(request);
    //var subSocket2 = socket.subscribe(controllerRequest);

    function selectCard() {
      var msg = $(this).attr("data-pattern");
      subSocket.push(jQuery.stringifyJSON({userId: "1", action: "drawCard", cardId: "", gameId: "0"}));
      //      subSocket2.push(jQuery.stringifyJSON({userId: "1", action: "selectCard"}));
  }

  function addMessage(message) {
      $("#cards").find('*[data-pattern="' + message + '"]').toggleClass("card-flipped")
      $('#console').append("<p>Current player: " + message.currentPlayerId + "<br />Current players cards: " + message.playersCards + "<br />Card drawn at beginning of turn: " + message.beginningCardDrawn + "<br />Game started: " + message.gameStarted + "<br />Current phase: " + message.currentPhase + "</p>");
  }




    var controllerRequest = { url: document.location.toString() + 'rest' + '/gamecontrol', //ovaj url kasnije promijeniti u web.xml
    contentType : "application/json",
    logLevel : 'debug',
    transport : 'long-polling' ,
    fallbackTransport: 'long-polling'};

    
    controllerRequest.onOpen = function(response) {
        content.html($('<p>', { text: 'controller connected using ' + response.transport }));
    };

    controllerRequest.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }

        modifyGameStatus(json.text);
    }


    function modifyGameStatus(gameStatusResponse) {
      $('#status').append("<p>Na redu je igrač broj: " + gameStatusResponse + "</p>");
  }
});