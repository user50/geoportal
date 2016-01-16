L.Control.PermissionControl = L.Control.extend({

    statics: {
        TITLE: 'Просмотр доступной области'
    },
    
    options: {
        position: 'topleft'
    },

    enabled: false,
    
    toggle: function() {
        if (!this.enabled) {
            map.openPermissionArea();
        } else {
        	map.closePermissionArea();
        }
        this.enabled = !this.enabled;
    },
    
    onAdd: function(map) {
        var className = 'leaflet-control-draw';

        this._container = L.DomUtil.create('div', 'leaflet-bar');

        var link = L.DomUtil.create('a', className+'-permission', this._container);
        link.href = '#';
        link.title = L.Control.PermissionControl.TITLE;

        L.DomEvent
        .addListener(link, 'click', L.DomEvent.stopPropagation)
        .addListener(link, 'click', L.DomEvent.preventDefault)
        .addListener(link, 'click', function(){
        	this.toggle();
        	if (this.enabled) {
        		L.DomUtil.addClass(link, 'enabled');
            } else {
            	 L.DomUtil.removeClass(link, 'enabled');
            }
        }, this);
        
        
        return this._container;
    }
});

L.Map.mergeOptions({
	permissionControl: false
});

L.Map.addInitHook(function () {
	 if (this.options.permissionControl) {
        this.permissionControl = L.Control.permissionControl().addTo(this);
	 }
});

L.Control.permissionControl = function (options) {
    return new L.Control.PermissionControl(options);
};