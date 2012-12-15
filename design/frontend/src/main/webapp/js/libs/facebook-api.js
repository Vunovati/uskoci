

(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/hr_HR/all.js#xfbml=1&appId=349684885089166";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


window.fbAsyncInit = function() {
    FB.init({
            appId      : '349684885089166',
            status     : true, // check login status
            cookie     : true, // enable cookies to allow the server to access the session
            xfbml      : true  // parse XFBML
            });
    
    
    
    // Listen to the auth.login which will be called when the user logs in
    // using the Login button
    FB.Event.subscribe('auth.login', function(response) {
                       // We want to reload the page now so PHP can read the cookie that the
                       // Javascript SDK sat. 
                       window.location = window.reload();
                       
                       });
    
    
};


// Invite friends via application "request" dialog box.
function Invite(){
    FB.ui(
          {
          method  : 'apprequests',
          title: 'Uskoci!',
          message : 'Dodji se igrati z menom!',
          },
          function (response) {
          // If response is null the user canceled the dialog
          if (response != null) {
          logResponse(response);
          }
          }
          );
    
 }
// Post message to your wall via "feed" dialog box.
function PostToWall(){
    FB.ui(
          {
          method : 'feed',
          name : 'Uskoci...Fuck Yea!',
          link   : 'http://apps.facebook.com/uskoci-game/',
          picture : ''
          },
          function (response) {
          // If response is null the user canceled the dialog
          if (response != null) {
          logResponse(response);
          }
          }
          );
    
}
// Send message to friend/friends via "send" dialog box.
function SendToFriend(){
    FB.ui(
          {
          method : 'send',
          link   : 'https://www.google.com/'
          },
          function (response) {
          // If response is null the user canceled the dialog
          if (response != null) {
          logResponse(response);
          }
          }
          );
    
}  


/*window.top.location.href = https://www.facebook.com/dialog/oauth/?
 client_id=YOUR_APP_ID
 &redirect_uri=YOUR_REDIRECT_URL
 &state=YOUR_STATE_VALUE
 &scope=COMMA_SEPARATED_LIST_OF_PERMISSION_NAMES;*/








