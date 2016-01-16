var autocompleteLib = (function(){
    
	var _cachedFullAddressResults = [];

	
	var arrayUnique = function(array) {
		var a = array.concat();
		for(var i=0; i<a.length; ++i) {
			for(var j=i+1; j<a.length; ++j) {
				if(a[i].value == a[j].value)
					a.splice(j--, 1);
			}
		}
		return a;
	};


	var _cachedFullAddressCallback = function(term, callback){
    	var cachedRes = [];
    	for(var i in _cachedFullAddressResults){
    		if(_cachedFullAddressResults[i].label.indexOf(term) == 0)cachedRes.push(_cachedFullAddressResults[i]);
    	}
		if(cachedRes.length > 0){
			callback(cachedRes);
			return;
		}
		$.ajax({
	   		url :  "fias/searchFullAddress",
	   		data : "term=" + term,
	   		type : 'GET'
	   	}).done(function (_obj){
	   		console.log(_obj);
	   		var res = [];
	   		for(var j in _obj){
	   			var addressObj = _obj[j];
	   			var label = '';
	   			var fiasCode = null;
	   			for(var i = addressObj.length-1; i > -1 ; i--){
	   				if(fiasCode == null) fiasCode = addressObj[i].fiasCode;
	   				if(0 == i){
	   					label += addressObj[i].name + " " + addressObj[i].prefix + ".";
	   				}else{
	   					label += addressObj[i].name + " " + addressObj[i].prefix + "., ";
	   				}
	   			}
	   			res.push({label:label, value:fiasCode, address: addressObj});
	   		}
	   		_cachedFullAddressResults = arrayUnique(_cachedFullAddressResults.concat(res));
	   		callback(res);
	   	}).fail(function(jqXHR, textStatus) {
	   		_cachedFullAddressResults = [];
	   		callback([]);
	   	});
    };
    
    var renderAddress = function(address){
    	var label = '';
    	for(var i in address){
   			if("обл" == address[i].prefix || "р-н" ==  address[i].prefix){
   				label += address[i].name + ' ' + address[i].prefix + ".";
            }else{
            	label += address[i].prefix + ". " + address[i].name;
            }
   			if(i<address.length-1)label+=', ';
			}
    	return label;
    };
   


    return {
    
        setAutocompleteKey : function(field){
        	try{
        		$(field).autocomplete( "destroy" );
        	}catch(e){
        		console.log(e);
        	}
            
            $(field).autocomplete({
            	source:"tags/autocompleteKey", 
                delay:10,
                minChars:0,
                matchSubset:1,
                autoFill:true,
                matchContains:1,
                cacheLength:10,
                selectFirst:true,
                maxItemsToShow:100,
                onItemSelect: function(node){

//                    var s = $(field).attr('name').substring("properties[".length);           
//                    var indx = s.substring(0, s.length - 5);
                    
                    var inputNamePart = $(field).attr('name').substring(0, $(field).attr('name').length - 4);

                    if(autocompleteLib.isFiasField(node.innerHTML)){
                        autocompleteLib.setAutocompleteFias($('input[name="'+ inputNamePart +'.value"]'), field);
                        return;
                    }
                    
                    showMask();
                    $.ajax({
                        url : 'tags/getByKey',
                        data : "key=" + node.innerHTML,
                        type : 'GET',
                        async: true
                    }).done(function (_obj){

                        if(!!_obj && "number" == _obj.type){
                            $('input[name="'+ inputNamePart +'.value"]').numeric({decimal: "," }).val("0,0");
                        }else{
                            if($('input[name="'+ inputNamePart +'.value"]').removeNumeric());
                        }
                        
                        hideMask();
                        
                    }).fail(function(jqXHR, textStatus) {
                        hideMask();
                        alert( "Request failed: " + textStatus );
                    });
                }
              }).click(function(){$(this).trigger("keydown");});
        },
        
        setAutocompleteValue : function(field, keyField){
        	try{
        		$(field).autocomplete( "destroy" );
        	}catch(e){
        		console.log(e);
        	}
            
            
            $(field).autocomplete({
            	source:"tags/autocompleteValue", 
                delay:10,
                minChars:0,
                matchSubset:1,
                autoFill:true,
                matchContains:1,
                cacheLength:10,
                selectFirst:true,
                extraParams : {f: function(){
                    return keyField.value;
                }},
                maxItemsToShow:100
              }).click(function(){$(this).trigger("keydown");});
        },
        
        setAutocompleteFiasFull : function(searchField, field, descriptionField){
        	try{
        		$(searchField).autocomplete( "destroy" );
        	}catch(e){
        		console.log(e);
        	}
            
            
           searchField.autocomplete({
                source: function(request, response){
        	   		_cachedFullAddressCallback(request.term, response);
           		} ,
              	delay:10,
                minChars:3,
                matchSubset:1,
                autoFill:true,
                matchContains:1,
                cacheLength:10,
                selectFirst:true,
                maxItemsToShow:100,
                select: function( event, ui ) {
        	   		field.val(ui.item.value);
        	   		var label = renderAddress(ui.item.address);
        	   		searchField.val(label);
        	   		if(descriptionField != null)descriptionField.text(ui.item.value);
        	   		return false;
           		},
           		focus: function( event, ui ) {
           			field.val(ui.item.value);
        	   		var label = renderAddress(ui.item.address);
        	   		searchField.val(label);
        	   		if(descriptionField != null)descriptionField.text(ui.item.value);
        	   		return false;
           		}
              }).click(function(){$(this).trigger("keydown");});
        },

        setAutocompleteFias : function(field, keyField){
            if(!!$(field).removeAutocomplete) $(field).removeAutocomplete();
    
            $(field).autocomplete({
            	source:"fias/searchObject", 
                delay:10,
                minChars:3,
                matchSubset:1,
                autoFill:true,
                matchContains:1,
                cacheLength:10,
                selectFirst:true,
                extraParams : (function(){
                    switch(keyField.value){
                        case "addr:region": return {levelLow: 1, levelHigh: 1};
                        case "addr:district": return {levelLow: 3, levelHigh: 3};
                        case "addr:settlement": return {levelLow: 1, levelHigh: 6};
                        case "addr:town": return {levelLow: 4, levelHigh: 6};
                        case "addr:city": return {levelLow: 4, levelHigh: 6};
                        case "addr:street": return {levelLow: 7, levelHigh: 100};
                        case "addr:street2": return {levelLow: 7, levelHigh: 100};
                    }
                    return {levelLow: 1, levelHigh: 100};
                })(),
                maxItemsToShow:100,
                onItemSelect: _onSelectFias,
                parseData : function(obj){
                    return [obj.name + " " + obj.prefix + "."];
                }
            }).click(function(){$(this).trigger("keydown");});
        },
        isFiasField : function(key){
            return 0 == key.search("addr:");
        },
        removeAutocomplete : function(field){
        	try{
        		$(field).autocomplete( "destroy" );
        	}catch(e){
        		console.log(e);
        	}
            
        }
    };
})();
