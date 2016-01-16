var revisions = (function() {
	// private
	var embdObj = null;
	var _map = null;
	
	return {
		// public
		init : function(embdId) {
			embdObj = $('#' + embdId);
			embdObj.selectable({
				stop : function() {
					$(".ui-selected", this).each(function() {
						var selected = $(this);
						var id = selected.attr('id');
						$('#revId').val(id);
						$('#revisionForm').submit();
					});
				}
			});
			var h = $('#history_map').parent().height();
			$('#history_map').height(h);
			_map = L.map('history_map');
			L.tileLayer('http://maps.yarcloud.ru:8088/enk/{z}/{x}/{y}.png',{attribution : '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'}).addTo(_map);
			
		},
		draw : function(geojson){
			var p = L.geoJson(geojson, {}).addTo(_map);
			_map.fitBounds(p.getBounds());
		}
	};
})();