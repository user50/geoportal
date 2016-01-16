L.Polyline.Measure = L.Draw.Polyline.extend({
    addHooks: function() {
        L.Draw.Polyline.prototype.addHooks.call(this);
        if (this._map) {
            this._markerGroup = new L.LayerGroup();
            this._map.addLayer(this._markerGroup);

            this._markers = [];
            this._map.on('click', this._onClick, this);
            this._startShape();
        }
    },

    removeHooks: function () {
        L.Draw.Polyline.prototype.removeHooks.call(this);

        this._clearHideErrorTimeout();

        //!\ Still useful when control is disabled before any drawing (refactor needed?)
        this._map.off('mousemove', this._onMouseMove);
        this._clearGuides();
        this._container.style.cursor = '';

        this._removeShape();

        this._map.off('click', this._onClick, this);
    },

    _startShape: function() {
        this._drawing = true;
        this._poly = new L.Polyline([], this.options.shapeOptions);

        this._container.style.cursor = 'crosshair';

        this._updateTooltip();
        this._map.on('mousemove', this._onMouseMove, this);
    },

    _finishShape: function () {
        this._drawing = false;

        this._cleanUpShape();
        this._clearGuides();

        this._updateTooltip();

        this._map.off('mousemove', this._onMouseMove, this);
        this._container.style.cursor = '';
    },

    _removeShape: function() {
        if (!this._poly)
            return;
        this._map.removeLayer(this._poly);
        delete this._poly;
        this._markers.splice(0);
        this._markerGroup.clearLayers();
    },

    _onClick: function(e) {
        if (!this._drawing) {
            this._removeShape();
            this._startShape();
            return;
        }
    },

    _getTooltipText: function() {
        var labelText = L.Draw.Polyline.prototype._getTooltipText.call(this);
        if (!this._drawing) {
            labelText.text = '';
        }
        return labelText;
    }
});


L.Circle.Measure  = L.Draw.Circle.extend({
	_drawShape: function (latlng) {
		if (!this._shape) {
			this._shape = new L.Circle(this._startLatLng, this._startLatLng.distanceTo(latlng), this.options.shapeOptions);
			this._map.addLayer(this._shape);
		} else {
			this._shape.setRadius(this._startLatLng.distanceTo(latlng));
		}
	},
	disable: function () {
		L.Draw.Feature.prototype.disable.call(this);
		if(this._shapetooltip){
		this._shapetooltip.dispose();
		this._shapetooltip = null;
		}
	},
	addHooks: function () {
		L.Draw.Feature.prototype.addHooks.call(this);
		if (this._map) {
			this._map.dragging.disable();
			//TODO refactor: move cursor to styles
			this._container.style.cursor = 'crosshair';

			this._tooltip.updateContent({ text: this._initialLabelText });

			this._map
				.on('mousedown', this._onMouseDown, this)
				.on('mousemove', this._onMouseMove, this);
		}
	},

	removeHooks: function () {
		L.Draw.Feature.prototype.removeHooks.call(this);
		if (this._map) {
			this._map.dragging.enable();
			//TODO refactor: move cursor to styles
			this._container.style.cursor = '';

			this._map
				.off('mousedown', this._onMouseDown, this)
				.off('mousemove', this._onMouseMove, this);

			L.DomEvent.off(document, 'mouseup', this._onMouseUp);
		}
		this._isDrawing = false;
	},
	
	_onMouseUp: function () {
		var t = this._tooltip;
		this._tooltip = new L.Tooltip(this._map);
		var id = this._isDrawing;
		this.disable();
		//if (this.options.repeatMode) {
			this.enable();
		//}
		if (id) {
			this._shapetooltip = t;
		}
	},
	
	_onMouseDown: function (e) {
		this._isDrawing = true;
		if (this._shape) {
			this._map.removeLayer(this._shape);
			delete this._shape;			
		}
		if(this._shapetooltip){
			this._shapetooltip.dispose();
			this._shapetooltip = null;
		}
		this._startLatLng = e.latlng;

		L.DomEvent
			.on(document, 'mouseup', this._onMouseUp, this)
			.preventDefault(e.originalEvent);
	},

	_onMouseMove: function (e) {
		var latlng = e.latlng,
			radius;

		this._tooltip.updatePosition(latlng);
		if (this._isDrawing) {
			this._drawShape(latlng);

			// Get the new radius (rouded to 1 dp)
			radius = this._shape.getRadius().toFixed(1);

			this._tooltip.updateContent({
				text: 'Радиус: ' + radius + ' м'
			});
		}
	}
});



L.Control.MeasureControl = L.Control.extend({

    statics: {
        TITLE: 'Измерение расстояний',
        TITLE1: 'Измерение радиусов'
    },
    options: {
        position: 'topleft',
        handler: {},
    	handler1: {}
    },

    toggle: function() {
        if (this.handler.enabled()) {
            this.handler.disable.call(this.handler);
        } else {
        	if (this.handler1.enabled()) {
              this.handler1.disable.call(this.handler1);
            }
            this.handler.enable.call(this.handler);
        }
    },
    
    toggle1: function() {
        if (this.handler1.enabled()) {
            this.handler1.disable.call(this.handler1);
        } else {
        	if (this.handler.enabled()) {
                this.handler.disable.call(this.handler);
            } 
            this.handler1.enable.call(this.handler1);
        }
    },

    onAdd: function(map) {
        var className = 'leaflet-control-draw';

        this._container = L.DomUtil.create('div', 'leaflet-bar');

        this.handler = new L.Polyline.Measure(map, this.options.handler);
        this.handler1 = new L.Circle.Measure(map, this.options.handler);

        

        
        var link = L.DomUtil.create('a', className+'-measure', this._container);
        link.href = '#';
        link.title = L.Control.MeasureControl.TITLE;

        var link1 = L.DomUtil.create('a', className+'-radiusmeasure', this._container);
        link1.href = '#';
        link1.title = L.Control.MeasureControl.TITLE1;
        
        this.handler.on('enabled', function () {
            L.DomUtil.addClass(link, 'enabled');
        }, this);

        this.handler.on('disabled', function () {
            L.DomUtil.removeClass(link, 'enabled');
        }, this);

        this.handler1.on('enabled', function () {
            L.DomUtil.addClass(link1, 'enabled');
        }, this);

        this.handler1.on('disabled', function () {
            L.DomUtil.removeClass(link1, 'enabled');
        }, this);
        
        L.DomEvent
            .addListener(link, 'click', L.DomEvent.stopPropagation)
            .addListener(link, 'click', L.DomEvent.preventDefault)
            .addListener(link, 'click', this.toggle, this);
        
        L.DomEvent
        .addListener(link1, 'click', L.DomEvent.stopPropagation)
        .addListener(link1, 'click', L.DomEvent.preventDefault)
        .addListener(link1, 'click', this.toggle1, this);

        return this._container;
    }
});


L.Map.mergeOptions({
    measureControl: false
});


L.Map.addInitHook(function () {
    if (this.options.measureControl) {
        this.measureControl = L.Control.measureControl().addTo(this);
    }
});


L.Control.measureControl = function (options) {
    return new L.Control.MeasureControl(options);
};

