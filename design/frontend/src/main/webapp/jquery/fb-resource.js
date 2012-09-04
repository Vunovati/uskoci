$(function () {
  "use strict";
  
  $("#join-button").click(joingame);
                       
  var fbsocket = $.atmosphere;
  
  var fbrequest = { url: document.location.toString() + 'rest' + '/fbresources', //ovaj url kasnije promijeniti u web.xml
  contentType : "application/json",
  logLevel : 'debug',
  transport : 'long-polling' ,
  fallbackTransport: 'long-polling'};                 
  
  
  fbrequest.onMessage = function biba(fbresponse) {
  var msg = fbresponse.responseBody;
  try {
  var fbjson = jQuery.parseJSON(msg);
  } catch (e) {
  console.log('This doesn\'t look like a valid JSON: ', msg.data);
  return;
  }
  
  if (fbjson.userName != null)
  setelements(fbjson.userID,fbjson.userName);
   
  
  };
  
  var fbsubSocket = fbsocket.subscribe(fbrequest);
  
  
  function joingame(){
  
  FB.api('/me', function(fbresponse) {
         fbsubSocket.push(jQuery.stringifyJSON({userID: fbresponse.id,
                                               userName: fbresponse.name}));
         });
  
  }
  
  
  function setelements(userID,userName){
  
  $("#slika").attr('src','https://graph.facebook.com/' + userID + '/picture');
  $('#status').append("<p>Na redu je igrač: " + userName + "</p>");
  }
  
  });

