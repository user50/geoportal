(function(d,i,g){var k=0;
var l="";
var b="group-";
var h="multiselectChange";
var f="multiselectSearch";
d.widget("uix.multiselect",{options:{availableListPosition:"right",collapsableGroups:true,created:null,defaultGroupName:"",filterSelected:false,locale:"auto",moveEffect:null,moveEffectOptions:{},moveEffectSpeed:null,optionRenderer:false,optionGroupRenderer:false,searchField:"toggle",searchFilter:null,searchHeader:"available",selectionMode:"click,d&d",showDefaultGroupHeader:false,showEmptyGroups:false,splitRatio:0.55,sortable:false,sortMethod:null},_create:function(){var x=this;
var u,s,y,w;
var t,v;
this.scope="multiselect"+(k++);
this.optionGroupIndex=1;
this._setLocale(this.options.locale);
this.element.addClass("uix-multiselect-original");
this._elementWrapper=d("<div></div>").addClass("uix-multiselect ui-widget").css({width:this.element.outerWidth(),height:this.element.outerHeight()}).append(d("<div></div>").addClass("multiselect-selected-list").append(d("<div></div>").addClass("ui-widget-header").append(v=d("<button></button>",{type:"button"}).addClass("uix-control-right").attr("data-localekey","deselectAll").attr("title",this._t("deselectAll")).button({icons:{primary:"ui-icon-arrowthickstop-1-e"},text:false}).click(function(z){z.preventDefault();
z.stopPropagation();
x.optionCache.setSelectedAll(false);
return false
})).append(u=d("<div></div>").addClass("header-text"))).append(s=d("<div></div>").addClass("uix-list-container ui-widget-content")))["right,top".indexOf(this.options.availableListPosition)>=0?"prepend":"append"](d("<div></div>").addClass("multiselect-available-list").append(d("<div></div>").addClass("ui-widget-header").append(t=d("<button></button>",{type:"button"}).addClass("uix-control-right").attr("data-localekey","selectAll").attr("title",this._t("selectAll")).button({icons:{primary:"ui-icon-arrowthickstop-1-w"},text:false}).click(function(z){z.preventDefault();
z.stopPropagation();
x.optionCache.setSelectedAll(true);
return false
})).append(y=d("<div></div>").addClass("header-text"))).append(w=d("<div></div>").addClass("uix-list-container ui-widget-content"))).insertAfter(this.element);
this._buttons={selectAll:t,deselectAll:v};
this._headers={selected:u,available:y};
this._lists={selected:s.attr("id",this.scope+"_selListContent"),available:w.attr("id",this.scope+"_avListContent")};
this.optionCache=new a(this);
this._searchDelayed=new o(this,{delay:500});
this._initSearchable();
this._applyListDroppable();
this.refresh(this.options.created)
},refresh:function(s){this._resize();
n(function(){this.optionCache.cleanup();
var y,w=this.element[0].childNodes;
for(var x=0,u=w.length;
x<u;
x++){y=w[x];
if(y.nodeType===1){if(y.tagName.toUpperCase()==="OPTGROUP"){var A=d(y).data("option-group")||(b+(this.optionGroupIndex++));
var z=y.childNodes;
this.optionCache.prepareGroup(d(y),A);
for(var v=0,t=z.length;
v<t;
v++){y=z[v];
if(y.nodeType===1){this.optionCache.prepareOption(d(y),A)
}}}else{this.optionCache.prepareOption(d(y))
}}}this.optionCache.reIndex();
if(this._searchField&&this._searchField.is(":visible")){this._search(null,true)
}if(s){s()
}},10,this)
},search:function(s){if(typeof s!="object"){s={showInput:true,text:s}
}if((s.toggleInput!=false)&&!this._searchField.is(":visible")){this._buttons.search.trigger("click")
}this._search(s.text,!!s.silent)
},locale:function(s){if(s===g){return this.options.locale
}else{this._setLocale(s);
this._updateControls();
this._updateHeaders()
}},_destroy:function(){this.optionCache.reset(true);
this._lists.selected.empty().remove();
this._lists.available.empty().remove();
this._elementWrapper.empty().remove();
delete this.optionCache;
delete this._searchDelayed;
delete this._lists;
delete this._elementWrapper;
this.element.removeClass("uix-multiselect-original")
},_initSearchable:function(){var u=("toggle"===this.options.searchField);
var t=this.options.searchHeader;
if(u){var s=this;
this._buttons.search=d("<button></button",{type:"button"}).addClass("uix-control-right").attr("data-localekey","search").attr("title",this._t("search")).button({icons:{primary:"ui-icon-search"},text:false}).click(function(w){w.preventDefault();
w.stopPropagation();
if(s._searchField.is(":visible")){var v=d(this);
s._headers[t].css("visibility","visible").fadeTo("fast",1);
s._searchField.hide("slide",{direction:"right"},200,function(){v.removeClass("ui-corner-right ui-state-active").addClass("ui-corner-all")
});
s._searchDelayed.cancelLastRequest();
s.optionCache.filter("")
}else{s._headers[t].fadeTo("fast",0.1,function(){d(this).css("visibility","hidden")
});
d(this).removeClass("ui-corner-all").addClass("ui-corner-right ui-state-active");
s._searchField.show("slide",{direction:"right"},200,function(){d(this).focus()
});
s._search()
}return false
}).insertBefore(this._headers[t])
}if(this.options.searchField){if(!u){this._headers[t].hide()
}this._searchField=d('<input type="text" />').addClass("uix-search ui-widget-content ui-corner-"+(u?"left":"all"))[u?"hide":"show"]().insertBefore(this._headers[t]).focus(function(){d(this).select()
}).on("keydown keypress",function(v){if(v.keyCode==13){v.preventDefault();
v.stopPropagation();
return false
}}).keyup(d.proxy(this._searchDelayed.request,this._searchDelayed))
}},_applyListDroppable:function(){if(this.options.selectionMode.indexOf("d&d")==-1){return
}var s=this.optionCache;
var t=this.scope;
var w=function(x){return s._elements[x.data("element-index")]
};
var v=function(y,x){y.droppable({accept:function(z){var A=w(z);
return A&&(A.selected!=x)
},activeClass:"ui-state-highlight",scope:t,drop:function(z,A){A.draggable.removeClass("ui-state-disabled");
A.helper.remove();
s.setSelected(w(A.draggable),x)
}})
};
v(this._lists.selected,true);
v(this._lists.available,false);
if(this.options.sortable){var u=this;
this._lists.selected.sortable({appendTo:"parent",axis:"y",containment:d(".multiselect-selected-list",this._elementWrapper),items:".multiselect-element-wrapper",handle:".group-element",revert:true,stop:d.proxy(function(x,y){var z;
d(".multiselect-element-wrapper",u._lists.selected).each(function(){var A=u.optionCache._groups.get(d(this).data("option-group"));
if(!z){u.element.append(A.groupElement)
}else{A.groupElement.insertAfter(z.groupElement)
}z=A
})
},this)})
}},_search:function(t,s){if(this._searchField.is(":visible")){if(typeof t==="string"){this._searchField.val(t)
}else{t=this._searchField.val()
}}this.optionCache.filter(t,s)
},_setLocale:function(s){if(s=="auto"){s=navigator.userLanguage||navigator.language||navigator.browserLanguage||navigator.systemLanguage||""
}if(!d.uix.multiselect.i18n[s]){s=""
}this.options.locale=s
},_t:function(t,s,u){return r({locale:this.options.locale,key:t,plural:s,data:u})
},_updateControls:function(){var s=this;
d(".uix-control-left,.uix-control-right",this._elementWrapper).each(function(){d(this).attr("title",s._t(d(this).attr("data-localekey")))
})
},_updateHeaders:function(){var s,u=this.optionCache.getSelectionInfo();
this._headers.selected.text(s=this._t("itemsSelected",u.selected.total,{count:u.selected.total})).parent().attr("title",this.options.filterSelected?this._t("itemsSelected",u.selected.count,{count:u.selected.count})+", "+this._t("itemsFiltered",u.selected.filtered,{count:u.selected.filtered}):s);
this._headers.available.text(this._t("itemsAvailable",u.available.total,{count:u.available.total})).parent().attr("title",this._t("itemsAvailable",u.available.count,{count:u.available.count})+", "+this._t("itemsFiltered",u.available.filtered,{count:u.available.filtered}))
},_resize:function(){var B=this.options.availableListPosition.toLowerCase();
var w=("left,right".indexOf(B)>=0)?"Width":"Height";
var v=("left,right".indexOf(B)>=0)?"Height":"Width";
var F=this.element["outer"+w]()*this.options.splitRatio;
var C=this.element["outer"+w]()-F;
var s=(v==="Width")?F:this.element.outerHeight();
var D=(v==="Width")?C:this.element.outerHeight();
var E=("left,right".indexOf(B)>=0)?"left":"top";
var u=("left,top".indexOf(B)>=0);
var t=("toggle"===this.options.searchField);
var x="ui-corner-tl ui-corner-tr ui-corner-bl ui-corner-br ui-corner-top";
var A=(v==="Width")?(u?"":"ui-corner-top"):(u?"ui-corner-tr":"ui-corner-tl");
var z=(v==="Width")?(u?"ui-corner-top":""):(u?"ui-corner-tl":"ui-corner-tr");
this._elementWrapper.find(".multiselect-available-list")[w.toLowerCase()](C).css(E,u?0:F)[v.toLowerCase()](this.element["outer"+v]()+1);
this._elementWrapper.find(".multiselect-selected-list")[w.toLowerCase()](F).css(E,u?C:0)[v.toLowerCase()](this.element["outer"+v]()+1);
this._buttons.selectAll.button("option","icons",{primary:q(B,"ui-icon-arrowthickstop-1-",false)});
this._buttons.deselectAll.button("option","icons",{primary:q(B,"ui-icon-arrowthickstop-1-",true)});
this._headers.available.parent().removeClass(x).addClass(z);
this._headers.selected.parent().removeClass(x).addClass(A);
if(!t){var y=Math.max(this._headers.selected.parent().height(),this._headers.available.parent().height());
this._headers.available.parent().height(y);
this._headers.selected.parent().height(y)
}if(this._searchField){this._searchField.width((w==="Width"?C:this.element.width())-(t?52:26))
}this._lists.available.height(D-this._headers.available.parent().outerHeight()-2);
this._lists.selected.height(s-this._headers.selected.parent().outerHeight()-2)
},_triggerUIEvent:function(t,u){var s;
if(typeof t==="string"){s=t;
t=d.Event(t)
}else{s=t.type
}this.element.trigger(t,u);
return !t.isDefaultPrevented()
},_setOption:function(s,t){switch(s){}if(typeof(this._superApply)=="function"){this._superApply(arguments)
}else{d.Widget.prototype._setOption.apply(this,arguments)
}}});
var m={standard:function(t,s){if(t>s){return 1
}if(t<s){return -1
}return 0
},natural:function e(K,J){var F=/(^-?[0-9]+(\.?[0-9]*)[df]?e?[0-9]?$|^0x[0-9a-f]+$|[0-9]+)/gi,w=/(^[ ]*|[ ]*$)/g,L=/(^([\w ]+,?[\w ]+)?[\w ]+,?[\w ]+\d+:\d+(:\d+)?[\w ]?|^\d{1,4}[\/\-]\d{1,4}[\/\-]\d{1,4}|^\w+, \w+ \d+, \d{4})/,v=/^0x[0-9a-f]+$/i,t=/^0/,H=function(x){return e.insensitive&&(""+x).toLowerCase()||""+x
},C=H(K).replace(w,"")||"",A=H(J).replace(w,"")||"",u=C.replace(F,"\0$1\0").replace(/\0$/,"").replace(/^\0/,"").split("\0"),E=A.replace(F,"\0$1\0").replace(/\0$/,"").replace(/^\0/,"").split("\0"),z=parseInt(C.match(v))||(u.length!=1&&C.match(L)&&Date.parse(C)),G=parseInt(A.match(v))||z&&A.match(L)&&Date.parse(A)||null,I,s;
if(G){if(z<G){return -1
}else{if(z>G){return 1
}}}for(var D=0,B=Math.max(u.length,E.length);
D<B;
D++){I=!(u[D]||"").match(t)&&parseFloat(u[D])||u[D]||0;
s=!(E[D]||"").match(t)&&parseFloat(E[D])||E[D]||0;
if(isNaN(I)!==isNaN(s)){return(isNaN(I))?1:-1
}else{if(typeof I!==typeof s){I+="";
s+=""
}}if(I<s){return -1
}if(I>s){return 1
}}return 0
}};
var c=["n","e","s","w"];
var j=["bottom","left","top","right"];
var q=function(u,t,s){return t+c[(d.inArray(u.toLowerCase(),j)+(s?2:0))%4]
};
var n=function(v,u,s){var t=Array.prototype.slice.call(arguments,3);
return setTimeout(function(){v.apply(s||i,t)
},u)
};
var o=function(t,s){this._widget=t;
this._options=s;
this._lastSearchValue=null
};
o.prototype={request:function(){if(this._widget._searchField.val()==this._lastSearchValue){return
}this.cancelLastRequest();
this._timeout=n(function(){this._timeout=null;
this._lastSearchValue=this._widget._searchField.val();
this._widget._search()
},this._options.delay,this)
},cancelLastRequest:function(){if(this._timeout){clearTimeout(this._timeout)
}}};
var p=function(u){var v=[];
var t={};
var s=u;
this.setComparator=function(w){s=w;
return this
};
this.clear=function(){v=[];
t={};
return this
};
this.containsKey=function(w){return !!t[w]
};
this.get=function(w){return t[w]
};
this.put=function(w,x){if(!t[w]){if(s){v.splice((function(){var z=0,C=v.length;
var B=-1,D=0;
while(z<C){B=parseInt((z+C)/2);
var A=t[v[B]].groupElement;
var y=x.groupElement;
D=s(A?A.attr("label"):l,y?y.attr("label"):l);
if(D<0){z=B+1
}else{if(D>0){C=B
}else{return B
}}}return z
})(),0,w)
}else{v.push(w)
}}t[w]=x;
return this
};
this.remove=function(w){delete t[w];
return v.splice(v.indexOf(w),1)
};
this.each=function(z){var x=Array.prototype.slice.call(arguments,1);
x.splice(0,0,null,null);
for(var y=0,w=v.length;
y<w;
y++){x[0]=v[y];
x[1]=t[v[y]];
z.apply(x[1],x)
}return this
}
};
var a=function(s){this._widget=s;
this._listContainers={selected:d("<div></div>").appendTo(this._widget._lists.selected),available:d("<div></div>").appendTo(this._widget._lists.available)};
this._elements=[];
this._groups=new p();
this._moveEffect={fn:s.options.moveEffect,options:s.options.moveEffectOptions,speed:s.options.moveEffectSpeed};
this._selectionMode=this._widget.options.selectionMode.indexOf("dblclick")>-1?"dblclick":this._widget.options.selectionMode.indexOf("click")>-1?"click":false;
this.reset()
};
a.Options={batchCount:200,batchDelay:50};
a.prototype={_createGroupElement:function(y,G,v){var A=this;
var E;
var u=function(){if(!E){E=A._groups.get(G)
}return E
};
var z=function(){return y?y.attr("label"):A._widget.options.defaultGroupName
};
var t=d("<span></span>").addClass("label").text(z()+" (0)").attr("title",z()+" (0)");
var x=function(){var I=u()[v?"selected":"available"];
I.listElement[(!v&&(I.count||A._widget.options.showEmptyGroups))||(I.count&&((E.optionGroup!=l)||A._widget.options.showDefaultGroupHeader))?"show":"hide"]();
var H=z()+" ("+I.count+")";
t.text(H).attr("title",H)
};
var C=d("<div></div>").addClass("ui-widget-header ui-priority-secondary group-element").append(d("<button></button>",{type:"button"}).addClass("uix-control-right").attr("data-localekey",(v?"de":"")+"selectAllGroup").attr("title",this._widget._t((v?"de":"")+"selectAllGroup")).button({icons:{primary:q(this._widget.options.availableListPosition,"ui-icon-arrowstop-1-",v)},text:false}).click(function(M){M.preventDefault();
M.stopPropagation();
var L=u()[v?"selected":"available"];
if(E.count>0){var I=[];
A._bufferedMode(true);
for(var J=E.startIndex,H=E.startIndex+E.count,K;
J<H;
J++){K=A._elements[J];
if(!K.filtered&&!K.selected!=v){A.setSelected(K,!v,true);
I.push(K.optionElement[0])
}}A._updateGroupElements(E);
A._widget._updateHeaders();
A._bufferedMode(false);
A._widget._triggerUIEvent(h,{optionElements:I,selected:!v})
}return false
})).append(t);
var s,B=(y)?y.attr("data-group-icon"):null;
if(this._widget.options.collapsableGroups){var D=(y)?y.attr("data-collapse-icon"):null,F=(D)?"ui-icon "+D:"ui-icon ui-icon-triangle-1-s";
var w=d("<span></span>").addClass("ui-icon collapse-handle").attr("data-localekey","collapseGroup").attr("title",this._widget._t("collapseGroup")).addClass(F).mousedown(function(H){H.stopPropagation()
}).click(function(H){H.preventDefault();
H.stopPropagation();
s(y);
return false
}).prependTo(C.addClass("group-element-collapsable"));
s=function(M){var L=u()[v?"selected":"available"],J=(M)?M.attr("data-collapse-icon"):null,H=(M)?M.attr("data-expand-icon"):null,K=(J)?"ui-icon "+J:"ui-icon ui-icon-triangle-1-s",I=(H)?"ui-icon "+H:"ui-icon ui-icon-triangle-1-e";
L.collapsed=!L.collapsed;
L.listContainer.slideToggle();
w.removeClass(L.collapsed?K:I).addClass(L.collapsed?I:K)
}
}else{if(B){d("<span></span>").addClass("collapse-handle "+B).css("cursor","default").prependTo(C.addClass("group-element-collapsable"))
}}return d("<div></div>").data("fnUpdateCount",x).data("fnToggle",s||d.noop).append(C)
},_createGroupContainerElement:function(x,w,t){var u=this;
var v=d("<div></div>");
var s;
if(this._widget.options.sortable&&t){v.sortable({tolerance:"pointer",appendTo:this._widget._elementWrapper,connectWith:this._widget._lists.available.attr("id"),scope:this._widget.scope,helper:"clone",receive:function(y,z){var A=u._elements[s=z.item.data("element-index")];
A.selected=true;
A.optionElement.prop("selected",true);
A.listElement.removeClass("ui-state-active")
},stop:function(y,z){var A;
if(s){A=u._elements[s];
s=g;
z.item.replaceWith(A.listElement.addClass("ui-state-highlight option-selected"));
u._widget._updateHeaders();
u._widget._triggerUIEvent(h,{optionElements:[A.optionElement[0]],selected:true})
}else{A=u._elements[z.item.data("element-index")];
if(A&&!A.selected){u._bufferedMode(true);
u._appendToList(A);
u._bufferedMode(false)
}}if(A){u._reorderSelected(A.optionGroup)
}},revert:true})
}if(this._selectionMode){d(v).on(this._selectionMode,"div.option-element",function(){var y=u._elements[d(this).data("element-index")];
y.listElement.removeClass("ui-state-hover");
u.setSelected(y,!t)
})
}return v
},_createElement:function(t,x){var w=this._widget.options.optionRenderer?this._widget.options.optionRenderer(t,x):d("<div></div>").text(t.text());
var s=t.attr("data-option-icon");
var v=d("<div></div>").append(w).addClass("ui-state-default option-element").attr("unselectable","on").data("element-index",-1).hover(function(){if(t.prop("selected")){d(this).removeClass("ui-state-highlight")
}d(this).addClass("ui-state-hover")
},function(){d(this).removeClass("ui-state-hover");
if(t.prop("selected")){d(this).addClass("ui-state-highlight")
}});
if(this._widget.options.selectionMode.indexOf("d&d")>-1){var u=this;
v.draggable({addClasses:false,cancel:(this._widget.options.sortable?".option-selected, ":"")+".ui-state-disabled",appendTo:this._widget._elementWrapper,scope:this._widget.scope,start:function(y,z){d(this).addClass("ui-state-disabled ui-state-active");
z.helper.width(d(this).width()).height(d(this).height())
},stop:function(y,z){d(this).removeClass("ui-state-disabled ui-state-active")
},helper:"clone",revert:"invalid",zIndex:99999,disabled:t.prop("disabled")});
if(t.prop("disabled")){v.addClass("ui-state-disabled")
}if(this._widget.options.sortable){v.draggable("option","connectToSortable",this._groups.get(x)["selected"].listContainer)
}}else{if(t.prop("disabled")){v[(t.prop("disabled")?"add":"remove")+"Class"]("ui-state-disabled")
}}if(s){v.addClass("grouped-option").prepend(d("<span></span>").addClass("ui-icon "+s))
}return v
},_isOptionCollapsed:function(s){return this._groups.get(s.optionGroup)[s.selected?"selected":"available"].collapsed
},_updateGroupElements:function(u){if(u){u.selected.count=0;
u.available.count=0;
for(var t=u.startIndex,s=u.startIndex+u.count;
t<s;
t++){u[this._elements[t].selected?"selected":"available"].count++
}u.selected.listElement.data("fnUpdateCount")();
u.available.listElement.data("fnUpdateCount")()
}else{this._groups.each(function(v,x,w){w._updateGroupElements(x)
},this)
}},_appendToList:function(t){var w=this;
var v=this._groups.get(t.optionGroup);
var x=v[t.selected?"selected":"available"];
if((t.optionGroup!=this._widget.options.defaultGroupName)||this._widget.options.showDefaultGroupHeader){x.listElement.show()
}if(x.collapsed){x.listElement.data("fnToggle")()
}else{x.listContainer.show()
}var s=t.index-1;
while((s>=v.startIndex)&&(this._elements[s].selected!=t.selected)){s--
}if(s<v.startIndex){x.listContainer.prepend(t.listElement)
}else{var u=this._elements[s].listElement;
if(u.parent().hasClass("ui-effects-wrapper")){u=u.parent()
}t.listElement.insertAfter(u)
}t.listElement[(t.selected?"add":"remove")+"Class"]("ui-state-highlight option-selected");
if((t.selected||!t.filtered)&&!this._isOptionCollapsed(t)&&this._moveEffect&&this._moveEffect.fn){t.listElement.hide().show(this._moveEffect.fn,this._moveEffect.options,this._moveEffect.speed)
}else{if(t.filtered){t.listElement.hide()
}}},_reorderSelected:function(w){var v=this._elements;
var u=this._groups.get(w);
var t=u.groupElement?u.groupElement:this._widget.element;
var s;
d(".option-element",u.selected.listContainer).each(function(){var x=v[d(this).data("element-index")].optionElement;
if(!s){t.prepend(x)
}else{x.insertAfter(s)
}s=x
})
},_bufferedMode:function(s){if(s){this._oldMoveEffect=this._moveEffect;
this._moveEffect=null;
this._widget._lists.selected.data("scrollTop",this._widget._lists.selected.scrollTop());
this._widget._lists.available.data("scrollTop",this._widget._lists.available.scrollTop());
this._listContainers.selected.detach();
this._listContainers.available.detach()
}else{this._widget._lists.selected.append(this._listContainers.selected).scrollTop(this._widget._lists.selected.data("scrollTop")||0);
this._widget._lists.available.append(this._listContainers.available).scrollTop(this._widget._lists.available.data("scrollTop")||0);
this._moveEffect=this._oldMoveEffect;
delete this._oldMoveEffect
}},reset:function(u){this._groups.clear();
this._listContainers.selected.empty();
this._listContainers.available.empty();
if(u){for(var t=0,v=this._elements,s=v.length;
t<s;
t++){v[t].optionElement.removeData("element-index")
}delete this._elements;
delete this._groups;
delete this._listContainers
}else{this._elements=[];
this.prepareGroup();
this._groups.setComparator(this.getComparator())
}},cleanup:function(){var w=this._widget.element[0];
var v=[];
this._groups.each(function(y,x){if(x.groupElement&&!d.contains(w,x.groupElement[0])){v.push(y)
}});
for(var t=0,u;
t<this._elements.length;
t++){u=this._elements[t];
if(!d.contains(w,u.optionElement[0])||(d.inArray(u.optionGroup,v)>-1)){this._elements.splice(t--,1)[0].listElement.remove()
}}for(var t=0,s=v.length;
t<s;
t++){this._groups.remove(v[t])
}this.prepareGroup()
},getComparator:function(){return this._widget.options.sortMethod?typeof this._widget.options.sortMethod=="function"?this._widget.options.sortMethod:m[this._widget.options.sortMethod]:null
},prepareGroup:function(t,s){s=s||l;
if(!this._groups.containsKey(s)){this._groups.put(s,{startIndex:-1,count:0,selected:{collapsed:false,count:0,listElement:this._createGroupElement(t,s,true),listContainer:this._createGroupContainerElement(t,s,true)},available:{collapsed:false,count:0,listElement:this._createGroupElement(t,s,false),listContainer:this._createGroupContainerElement(t,s,false)},groupElement:t,optionGroup:s})
}},prepareOption:function(s,u){var t;
if(s.data("element-index")===g){u=u||l;
this._elements.push(t={index:-1,selected:false,filtered:false,listElement:this._createElement(s,u),optionElement:s,optionGroup:u})
}else{this._elements[s.data("element-index")].listElement[(s.prop("disabled")?"add":"remove")+"Class"]("ui-state-disabled")
}},reIndex:function(){var t=this.getComparator();
if(t){var v=this._groups;
this._elements.sort(function(z,y){var C=v.get(z.optionGroup).groupElement;
var B=v.get(y.optionGroup).groupElement;
var A=t(C?C.attr("label"):l,B?B.attr("label"):l);
if(A!=0){return A
}else{return t(z.optionElement.text(),y.optionElement.text())
}})
}this._bufferedMode(true);
this._groups.each(function(C,A,z,y){if(!A.available.listContainer.parents(".multiselect-element-wrapper").length){if(A.groupElement){A.groupElement.data("option-group",C)
}var B=d("<div></div>").addClass("multiselect-element-wrapper").data("option-group",C);
var D=d("<div></div>").addClass("multiselect-element-wrapper").data("option-group",C);
B.append(A.selected.listElement.hide());
if(C!=l||(C==l&&y)){D.append(A.available.listElement.show())
}B.append(A.selected.listContainer);
D.append(A.available.listContainer);
z.selected.append(B);
z.available.append(D)
}A.count=0
},this._listContainers,this._widget.options.showDefaultGroupHeader);
for(var u=0,w,x,s=this._elements.length;
u<s;
u++){w=this._elements[u];
x=this._groups.get(w.optionGroup);
if(x.startIndex==-1||x.startIndex>=u){x.startIndex=u;
x.count=1
}else{x.count++
}w.listElement.data("element-index",w.index=u);
if(w.optionElement.data("element-index")==g||w.selected!=w.optionElement.prop("selected")){w.selected=w.optionElement.prop("selected");
w.optionElement.data("element-index",u);
this._appendToList(w)
}}this._updateGroupElements();
this._widget._updateHeaders();
this._groups.each(function(A,y,z){z._reorderSelected(A)
},this);
this._bufferedMode(false)
},filter:function(t,x){if(t&&!x){var y={term:t};
if(this._widget._triggerUIEvent(f,y)){t=y.term
}else{return
}}this._bufferedMode(true);
var s=this._widget.options.filterSelected;
var z=this._widget.options.searchFilter||function(C,B){return B.innerHTML.toLowerCase().indexOf(C)>-1
};
t=(this._widget.options.searchPreFilter||function(B){return B?(B+"").toLowerCase():false
})(t);
for(var u=0,A,v=this._elements.length,w;
u<v;
u++){A=this._elements[u];
w=!(!t||z(t,A.optionElement[0]));
if((!A.selected||s)&&(A.filtered!=w)){A.listElement[w?"hide":"show"]();
A.filtered=w
}else{if(A.selected){A.filtered=false
}}}this._widget._updateHeaders();
this._bufferedMode(false)
},getSelectionInfo:function(){var v={selected:{total:0,count:0,filtered:0},available:{total:0,count:0,filtered:0}};
for(var t=0,s=this._elements.length;
t<s;
t++){var u=this._elements[t];
v[u.selected?"selected":"available"][u.filtered?"filtered":"count"]++;
v[u.selected?"selected":"available"].total++
}return v
},setSelected:function(u,t,s){if(u.optionElement.attr("disabled")&&t){return
}u.optionElement.prop("selected",u.selected=t);
this._appendToList(u);
if(!s){if(this._widget.options.sortable&&t){this._reorderSelected(u.optionGroup)
}this._updateGroupElements(this._groups.get(u.optionGroup));
this._widget._updateHeaders();
this._widget._triggerUIEvent(h,{optionElements:[u.optionElement[0]],selected:t})
}},setSelectedAll:function(x){var t=[];
var w={};
this._bufferedMode(true);
for(var u=0,v,s=this._elements.length;
u<s;
u++){v=this._elements[u];
if(!((v.selected==x)||(v.optionElement.attr("disabled")||(x&&(v.filtered||v.selected))))){this.setSelected(v,x,true);
t.push(v.optionElement[0]);
w[v.optionGroup]=true
}}if(this._widget.options.sortable&&x){var y=this;
d.each(w,function(z){y._reorderSelected(z)
})
}this._updateGroupElements();
this._widget._updateHeaders();
this._bufferedMode(false);
this._widget._triggerUIEvent(h,{optionElements:t,selected:x})
}};
function r(y){var s=d.uix.multiselect.i18n[y.locale]?y.locale:"";
var x=d.uix.multiselect.i18n[s];
var u=y.plural||0;
var w=y.data||{};
var v;
if(u===2&&x[y.key+"_plural_two"]){v=x[y.key+"_plural_two"]
}else{if((u===2||u===3)&&x[y.key+"_plural_few"]){v=x[y.key+"_plural_few"]
}else{if(u>1&&x[y.key+"_plural"]){v=x[y.key+"_plural"]
}else{if(u===0&&x[y.key+"_nil"]){v=x[y.key+"_nil"]
}else{v=x[y.key]||""
}}}}return v.replace(/\{([^\}]+)\}/g,function(t,z){return w[z]
})
}d.uix.multiselect.i18n={"":{itemsSelected_nil:"no selected option",itemsSelected:"{count} selected option",itemsSelected_plural:"{count} selected options",itemsAvailable_nil:"no item available",itemsAvailable:"{count} available option",itemsAvailable_plural:"{count} available options",itemsFiltered_nil:"no option filtered",itemsFiltered:"{count} option filtered",itemsFiltered_plural:"{count} options filtered",selectAll:"Select All",deselectAll:"Deselect All",search:"Search Options",collapseGroup:"Collapse Group",expandGroup:"Expand Group",selectAllGroup:"Select All Group",deselectAllGroup:"Deselect All Group"}}
})(jQuery,window);