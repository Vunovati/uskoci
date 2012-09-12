$(function () {
  "use strict";
                       
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
  
  
  if (fbjson.userID != null) {
     if (fbjson.msgType == "chat")
        setChatContent(fbjson.userID,fbjson.userName,fbjson.text);
     else
     if (fbjson.msgType == "joingame")  
        setelements(fbjson.userID,fbjson.userName);
  }   
   
  
  };
  
  var fbsubSocket = fbsocket.subscribe(fbrequest);
  
  
  $("#join-button").click(joingame);
  
  $("#chatButton").click(sendMessage);
  
  $("#chatInput").keypress(function (e) {
                               if (e.keyCode == 13) {
                               
                                  sendMessage();
                               
                               }  
                                
                              });
  
                        
  
  
  
  
  function joingame(){
  
    FB.api('/me', function(fbresponse) {
         fbsubSocket.push(jQuery.stringifyJSON({userID: fbresponse.id,
                                               userName: fbresponse.name,
                                               msgType: "joingame" }));
         });
  
  }
  
  function sendMessage(){
    var old = $("#chatTextarea").html();
    var chatmessage = $("#chatInput").val(); 
    $("#chatInput").val("");
  
    FB.api('/me', function(fbresponse) {
         fbsubSocket.push(jQuery.stringifyJSON({userID: fbresponse.id,
                                               userName: fbresponse.name,
                                               text: chatmessage,
                                               msgType: "chat"           
                                               }));
         }); 
  }
  
  
  function setelements(userID,userName){
  
  $("#slika").attr('src','https://graph.facebook.com/' + userID + '/picture');
  $('#status').append("<p>Na redu je igrač: " + userName + "</p>");
  }
  
  function setChatContent(userID,userName,text){
  
    var stringArray = userName.split(" ");

    var firstName = stringArray[0];
    
    $("#chatTextarea").append("<p>" + firstName +" wrote: " + text + "</p>");
  
    $("#chatTextarea").scrollTop($("#chatTextarea")[0].scrollHeight);
  
  }
  
  });

