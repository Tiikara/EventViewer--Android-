function SMarker(x, y, id)
{
    this.x = x;
    this.y = y;
    this.id = id;
    this.googleMarker = new google.maps.Marker({
        position: new google.maps.LatLng(x, y),
        title:"Hello World!"
    });
}

var markers = new Array(1000);
var markerCount = 0;

var map;

function addMarker(x,y, id)
{
    markers[markerCount] = new SMarker(x,y, id);
    markers[markerCount].googleMarker.setMap(map);
    
    google.maps.event.addListener(markers[markerCount].googleMarker, 'click', function() {
        Android.ShowDescr(id);
  });
    
    markerCount++;
}

  function initialize() {
    var mapOptions = {
      center: new google.maps.LatLng(48.4808300, 135.0927800),
      zoom: 14,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"),
        mapOptions);
        
    Android.onPageLoaded();
  }