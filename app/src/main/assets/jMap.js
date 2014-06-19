
var map;

function addMarker(x,y, id)
{
    var marker = new google.maps.Marker({
                               position: new google.maps.LatLng(x, y),
                               map: map
                           });
    
    google.maps.event.addListener(marker, 'click', function() {
        Android.ShowDescr(id);
  });
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