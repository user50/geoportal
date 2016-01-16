var massedit = (function() {
	// private
	var _offset = 0;
	var _perpage = 20;
	var _colHeaders = [];
	var _colMapping = [];
	var _table = null;
	var _layerId = null;
	var _sort = 'id';
	
	var _loadData = function(start, count){
		showMask();
		$.ajax({
	    	url : '../../mass/load',
			type : 'POST',
			data : {start: start, count: count, layerId: _layerId, sort: _sort}
		}).done(function(res) {
			hideMask();
			_offset = start;
			_table.loadData(res);
		}).fail(function(jqXHR, textStatus) {
			hideMask();
			alert("Произошла ошибка во время загрузки" );
		});
	};
	
	var _historyLink = function(instance, td, row, col, prop, value, cellProperties) {
		var id = value;
		td.innerHTML = '<a target="_blank" href="../../mass/history/'+id+'">'+id+'</a>';
		var data = instance.getData();
		var isPermitted = data[row]['__edited__'];
		if(isPermitted !== undefined){
			td.style.backgroundColor = (isPermitted)?'rgba(0, 128, 0, 0.2)':'rgba(255, 0, 0, 0.2)';
		}
		return td;
	};
	
	var _setCheckedStatus = function(row){
		$.ajax({
	    	url : '../../mass/check',
			type : 'GET',
			data : {id:row.id, checked:row.checked, layerId: _layerId}
		}).done(function(res){
			$('#checkedCount')[0].innerHTML = res;
		});
	};
	
	var _setNewTagValue = function(id, key, value, oldValue){
		$.ajax({
	    	url : '../../mass/update',
			type : 'GET',
			data : {id:id, key:key, value:value}
		}).done(function(res){
			if(res == false){
				_table.setDataAtCell(oldValue.row, oldValue.col, oldValue.value, 'internalundo');
			}
		});
	};
	
	return {
		// public
		init : function(layerId) {
			_layerId = layerId;
			_colHeaders.push('');
			_colHeaders.push('Идентификатор');
			_colHeaders.push('Наименование');
			
			_colMapping.push({data:'checked', type: 'checkbox'});
			_colMapping.push({data:'id', readOnly: true, renderer: _historyLink});
			_colMapping.push({data:'name'});
			
			//buttons
			$('#check_all').on('click', function(){
				showMask();
				$.ajax({
			    	url : '../../mass/checkall',
					type : 'GET',
					data : {layerId: _layerId, check : true}
				}).done(function(res){
					hideMask();
					$('#checkedCount')[0].innerHTML = res;
					_loadData(_offset, _perpage);	
				});
				
			});
			
			$('#uncheck_all').on('click', function(){
				showMask();
				$.ajax({
			    	url : '../../mass/checkall',
					type : 'GET',
					data : {layerId: _layerId, check : false}
				}).done(function(res){
					hideMask();
					$('#checkedCount')[0].innerHTML = res;
					_loadData(_offset, _perpage);	
				});
			});
			
			$('#tie').on('click', function(){
				var target = $('#layerSelect').val();
				if(target == ''){
					alert("Необходимо выбрать слой для привязки!");
					return;
				}
				confirmMessage('Копирование объектов', 'Выбранные объекты будут привязаны к слою.', function(){
				showMask();
				$.ajax({
			    	url : '../../mass/tie',
					type : 'POST',
					data : {targetId: target, layerId: _layerId}
				}).done(function(res){
					hideMask();
					console.log('Relation done');	
				}).fail(function(jqXHR, textStatus) {
					hideMask();
					alert("Произошла ошибка во время привязки" );
				});
				});
			});
			$('#clone').on('click', function(){
				var target = $('#layerSelect').val();
				if(target == ''){
					alert("Необходимо выбрать слой для копирования!");
					return;
				}
				confirmMessage('Клонирование объектов', 'Выбранные объекты будут скопированы и привязаны к слою.', function(){
				showMask();
				$.ajax({
			    	url : '../../mass/clone',
					type : 'POST',
					data : {targetId: target, layerId: _layerId}
				}).done(function(res){
					hideMask();
					console.log('Clone done');
					$('#massEditForm').submit();
				}).fail(function(jqXHR, textStatus) {
					hideMask();
					alert("Произошла ошибка во время копирования" );
				});
				});
			});
			$('#move').on('click', function(){
				var target = $('#layerSelect').val();
				if(target == ''){
					alert("Необходимо выбрать слой для перемещения!");
					return;
				}
				confirmMessage('Перемещение объектов', 'Выбранные объекты будут перемещены на слой.', function(){
				showMask();
				$.ajax({
			    	url : '../../mass/move',
					type : 'POST',
					data : {targetId: target, layerId: _layerId}
				}).done(function(res){
					hideMask();
					console.log('Relation done');
					$('#massEditForm').submit();
					/*_offset = 0;
					_loadData(_offset, _perpage);*/	
				}).fail(function(jqXHR, textStatus) {
					hideMask();
					alert("Произошла ошибка во время перемещения" );
				});
				});
			});
			$('#delete').on('click', function(){
				confirmMessage('Удаление объектов', 'Выбранные объекты, принадлежащие только этому слою будут удалены. Остальные отвязаны от слоя.', function(){
					showMask();
					$.ajax({
				    	url : '../../mass/delete',
						type : 'GET',
						data : {layerId: _layerId}
					}).done(function(res){
						hideMask();
						console.log('Deleting done: ' + res);
						$('#massEditForm').submit();
						/*_offset = 0;
						var checked = parseInt($('#checkedCount')[0].innerHTML);
						$('#checkedCount')[0].innerHTML =  checked - res;
						_loadData(_offset, _perpage);	*/
					}).fail(function(jqXHR, textStatus) {
						hideMask();
						alert("Произошла ошибка во время удаления" );
					});
    			});
			});
		},
		initTable: function(){
			_table = new Handsontable(document.getElementById('object_table'), {
				  rowHeaders: function(inx){
					  return inx + _offset +1;
				  },
				  manualColumnResize: true,
				  colHeaders: _colHeaders,
				  columns: _colMapping
				});
			_table.addHook('afterChange', function(changes, source) {
				if(source == 'internalundo')return;
				for(var inx in changes){
					var change = changes[inx];
					var row = _table.getSourceDataAtRow (change[0]);
					console.log(row);
					if(change[1] == 'checked'){
						_setCheckedStatus(row);
					}else{
						var col = _table.propToCol(change[1]);
						var oldVal = {col:col, row:change[0], value:change[2]};
						//изменение тегов или названия
						_setNewTagValue(row.id, change[1], row[change[1]], oldVal);
					}
				}
				
			});					
			
		},
		getColMapping : function(){
			return _colMapping;
		},
		getColHeaders : function(){
			return _colHeaders;
		},
		setPerPage : function(p){
			_perpage = p;
		},
		setSort : function(s){
			_sort = s;
		},
		initPaging : function(count){
			$("#pagination").paging(count, { 
			    format: '[< ncnnn >]', // define how the navigation should look like and in which order onFormat() get's called
			    perpage: _perpage, // show elements per page
			    lapping: 0, // don't overlap pages for the moment
			    page: 1, // start at page, can also be "null" or negative
			    onSelect: function (page) {
			    	_loadData(this.slice[0],this.slice[1]-this.slice[0]);
			        console.log(this);
			    },
			    onFormat: function (type) {
			        switch (type) {
			        case 'block': // n and c
			            return (this.value != this.page)?('<a href="#">' + this.value + '</a>'):('<span>' + this.value + '</span>');
			        case 'next': // >
			            return '<a class="paging_next" href="#"></a>';
			        case 'prev': // <
			            return '<a class="paging_prev" href="#"></a>';
			        case 'first': // [
			            return '<a class="paging_first" href="#"></a>';
			        case 'last': // ]
			            return '<a class="paging_last" href="#"></a>';
			        }
			    }
			});
		},
		addTag : function(){
			var key = prompt('Введите название нового тега', '');
			if(key == null || key == '')return false;
			var container = $('#tags_container');
			var inp = $('<input value="'+key+'" checked="checked" class="tag_checkbox" type="checkbox" name="tag_key"/>');
			container.append(inp);
			$('#massEditForm').submit();
		},
		deleteTag : function(obj){
			var label = $(obj).parent();
			var inp = $('input', label);
			var tagName = inp.val();
			confirmMessage('Удаление тега', 'Тег ' + tagName + ' и его значения будут удалены у всех объектов слоя!', function(){
				showMask();
			
			
			$.ajax({
		    	url : '../../mass/delete_tag',
				type : 'GET',
				data : {key: tagName, layerId: _layerId}
			}).done(function(res){
				hideMask()
				console.log('Remove done');
				label.remove();
				$('#massEditForm').submit();
			}).fail(function(jqXHR, textStatus) {
				hideMask();
				alert("Произошла ошибка во время удаления тега" );
			});
			});
		}
	};
})();


function showMask() {
	var mask = document.getElementById('loadmask');
	var img = document.getElementById('loadimg');
	mask.style.display = 'block';
	wWidth = (('innerWidth' in window) ? window.innerWidth : document.documentElement.clientWidth);
	wHeight = (('innerHeight' in window) ? window.innerHeight
			: document.documentElement.clientHeight);
	mask.style.height = wHeight + 'px';
	mask.style.width = wWidth + 'px';
	img.style.left = wWidth / 2 + 'px';
	img.style.top = wHeight / 2 + 'px';
}

function hideMask() {
	var mask = document.getElementById('loadmask');
	mask.style.display = 'none';
}

function confirmMessage(title, message, callback) {
	var confirmDiv = $("<div title='"+title+"'>"+
			"<p><span class='ui-icon ui-icon-alert' style='float: left; margin: 0 7px 20px 0;'></span>"+message+"</p>"+
	"</div>").appendTo("body");
	confirmDiv.dialog({
		resizable : true,
		//height : 180,
		modal : true,
		dialogClass: "gis-confirm",
		buttons : {
			"Да" : function() {
				$(this).dialog("close");
				callback();
				confirmDiv.remove();
			},
			"Нет" : function() {
				$(this).dialog("close");
				confirmDiv.remove();
			}
		}
	});
	}
