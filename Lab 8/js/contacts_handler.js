
// Client ID and API key from the Developer Console
var CLIENT_ID = '352555526111-5ib5822imdskvr549ajijb415qc2klmn.apps.googleusercontent.com';
var API_KEY = 'AIzaSyAlX5TGts-uNq1ifhwusoj2RQ4rYrZkm7A';

// Array of API discovery doc URLs for APIs used by the quickstart
var DISCOVERY_DOCS = ["https://www.googleapis.com/discovery/v1/apis/people/v1/rest"];

// Authorization scopes required by the API; multiple scopes can be
// included, separated by spaces.
var SCOPES = "https://www.googleapis.com/auth/contacts.readonly https://www.googleapis.com/auth/userinfo.email";

var authorizeButton = document.getElementById('authorizeButton');
// var signoutButton = document.getElementById('signoutButton');

/**
 *  On load, called to load the auth2 library and API client library.
 */
function handleClientLoad() {
  gapi.load('client:auth2', initClient);
}

/**
 *  Initializes the API client library and sets up sign-in state
 *  listeners.
 */
function initClient() {
  gapi.client.init({
    apiKey: API_KEY,
    clientId: CLIENT_ID,
    discoveryDocs: DISCOVERY_DOCS,
    scope: SCOPES
  }).then(function () {
    // Listen for sign-in state changes.
    gapi.auth2.getAuthInstance().isSignedIn.listen(updateSigninStatus);

    // Handle the initial sign-in state.
    updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
    authorizeButton.onclick = handleAuthClick;
    // signoutButton.onclick = handleSignoutClick;
  }, function(error) {
    console.log(JSON.stringify(error, null, 2));
  });
}

/**
 *  Called when the signed in status changes, to update the UI
 *  appropriately. After a sign-in, the API is called.
 */
function updateSigninStatus(isSignedIn) {
  if (isSignedIn) {
    authorizeButton.setAttribute("disabled", "");
    // signoutButton.style.display = 'block';
    listConnectionNames();
  } else {
    // authorizeButton.style.display = 'block';
    // signoutButton.style.display = 'none';
  }
}

/**
 *  Sign in the user upon button click.
 */
function handleAuthClick(event) {
    console.log("here");
  gapi.auth2.getAuthInstance().signIn();
}

/**
 *  Sign out the user upon button click.
 */
function handleSignoutClick(event) {
  gapi.auth2.getAuthInstance().signOut();
}


function addToList(contactInfo) {
  // code to add the fetched contact details to the dropdown list for individualInvites should go here
  console.log(contactInfo);
}

// create a function for the locInviteList dropdown here
// it should generate and assign a random no. between 1-10 for distance from user to each contact
// based on the selected value in 'miles of radius to search' field, the contact list should be filtered  

function listConnectionNames() {
  gapi.client.people.people.connections.list({
     'resourceName': 'people/me',
     'pageSize': 30,
     'personFields': 'names,emailAddresses',
   }).then(function(response) {
     var connections = response.result.connections;
     console.log(connections);

     if (connections.length > 0) {
       for (i = 0; i < connections.length; i++) {
         var person = connections[i];
         var contactDetail = "";
         if (person.names && person.names.length > 0) {
           contactDetail += person.names[0].displayName;
         } else {
           console.log("No display name found for connection.");
         }
         if (person.emailAddresses && person.emailAddresses.length > 0) {
             if(contactDetail !== ""){
                 contactDetail += " - ";
             }
           contactDetail += person.emailAddresses[0].value;
           if(contactDetail !== ""){
               addToList(contactDetail);
           }
         } else {
           console.log("No email found for connection.");
         }
       }
     } else {
       console.log('No connections found.');
     }
   });
}
