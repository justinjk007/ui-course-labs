// initializes the map to a fixed location
var map;
function initMap() {
    // var uoit = {lat: 43.945931, lng: -78.896336};
    map = new google.maps.Map(document.getElementById('eventLocation'), {zoom: 8});
    var geocoder = new google.maps.Geocoder;
    geocoder.geocode({'address': 'Toronto'}, function(results, status) {
        if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
        }
    });
    // Asks the user permission to access their location
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(locationSuccess, locationError);
    } else {
        locationError('geolocation not supported');;
    }
}
// Centers the map to user's current location
function locationSuccess(position){
    var pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
    };
    map.setCenter(pos);
    var marker = new google.maps.Marker({
        position: pos,
        map: map
    });
}
function locationError(msg){
    console.log(msg);
}
// add code to add a location search box with autocomplete suggestions here
// tutorial on how to place a search box on your map: https://developers-dot-devsite-v2-prod.appspot.com/maps/documentation/javascript/examples/places-searchbox
// the simple place search box only supports lookup by place names and address
// to add supoort for search by text, see the tutorial here: https://developers-dot-devsite-v2-prod.appspot.com/maps/documentation/javascript/examples/places-autocomplete-hotelsearch
// and here for more details on nearby search requests: https://developers.google.com/places/web-service/search#PlaceSearchRequests
