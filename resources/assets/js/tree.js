/**
 * jstree 기반 확장 트리
 * @author JSCHOI@PMIS
 * @since  2021
 * @version 1.0
 * @see
 * Copyright (C)  All right reserved.
*/

/*  var treedata = [
       { "id" : "152983", "parent" : "#", "text" : "내문서" , "icon" : "fa fa-rim-tree fa-folder" },
       { "id" : "ajson2", "parent" : "#", "text" : "트리작업중" , "icon" : "fa fa-rim-tree  fa-folder" ,  "state" : { "opened" : true, "selected" : true } },
       { "id" : "ajson3", "parent" : "ajson2", "text" : "Child 1" , "icon" : "fa fa-rim-tree fa-folder"},
       { "id" : "ajson4", "parent" : "ajson2", "text" : "Child 2" , "icon" : "fa fa-rim-tree fa-folder"},
    ];
 */
    (function ($) {
	
        $.fn.coreTree = function (cbClick,cbDBClick,cbCheckBox,ctxMenu) {
    
            var coreTree = this;
    
            coreTree.clickId = null;
            coreTree.coreNodes = [];
            
            var defaults = {
                check_callback : true,
            multiple : false,
            };
            
            var settings = $.extend({}, defaults);
            
            var handler = [];
            
            var run = function(){
                methods.init();
                event.onChange().onSelectNode().onDblclick().onHoverNode().onDeSelectAllNode();
                return methods;
            };
    
            var methods = {
                init : function() {  
                    if(typeof ctxMenu === "function"){
                        coreTree.jstree({ 
                            'core' : settings , 
                            'plugins': ['contextmenu'],
                            'contextmenu' : {items: ctxMenu}
                        });
                        
                    }
                    else if(typeof cbCheckBox === "function"){
                            settings.multiple = true;
                    coreTree.jstree({ 
                        'core' : settings , 
                            'plugins': ['checkbox'],
                            'checkbox' : { 'keep_selected_style' : false , 'tie_selection' : false , 'three_state' : false }
                    });
                    }
                    else{
                        coreTree.jstree({ 'core' : settings});
                    }   			
                },
                
                //private
                getNodeData : function(path,parent,id,text,icon,opened,selected,disabled,core,hidden){
                    // 0값처리 ( isEmpty 에서 false 처리되는것 방지 )
                    parent = parent + '';
                    id = id + '';
                    
                    if(this.isEmpty(opened) ){
                        opened = false;
                    }
        
                    if(this.isEmpty(disabled) ){
                        disabled = false;
                    }
        
                    if(this.isEmpty(selected) ){
                        selected = false;
                    }
                    
                    if(this.isEmpty(icon) ){
                        icon = "fa fa-folder";
                    }
    
                    if(this.isEmpty(hidden) ){
                        hidden = false;
                    }
                    if(this.isEmpty(parent) ){
                        parent = "#";
                    }
                    else{
                        if(core){
                            var result = $.grep(coreTree.coreNodes, function(e){ return e.id == parent; });
                            if (result.length == 0) {
                              // not found
                                parent = "#";
                            } else if (result.length == 1) {
                              // access the foo property using result[0].foo
                            } else {
                              // multiple items found
                            }	
                        }
                    }
                    
                    var node =  { 
                            parent : parent, 
                            id : id, 
                            text : text,
                            icon : icon ,
                            state : { opened : opened, disabled : disabled , selected : selected , hidden : hidden}, 
                            data : { path : path , parent : parent , id : id , text : text , icon : icon },
                            a_attr : { title: text } 
                    };
                    return node;	
                },
                //public 다량 데이터 설정시 node 객체 추가
                addCoreNode : function(path,parent,id,text,icon,opened,selected,disabled,extendData,hidden){		
                    var node = this.getNodeData(path,parent,id,text,icon,opened,selected,disabled,true,hidden);
                    //node추가 데이터 연결
                    if( !this.isEmpty(extendData) ){
                        $.extend( node.data , extendData );
                    }		
                    coreTree.coreNodes.push(node);		
                },
                //public 대량의 데이터 한번에 설정시 addNode사용할경우 렌더링 속도이슈발생.대량데이터는 node만든후 한번에 설정
                renderCoreNode : function(cbRefresh){
                    
                    coreTree.jstree(true).settings.core.data = coreTree.coreNodes;
                    coreTree.jstree(true).refresh();
                    
                    //렌터링 완료후 호출될 함수연결		
                    coreTree.on('refresh.jstree', {self : coreTree} ,function (e, data) {			
                        if(typeof cbRefresh === "function" ){
                            cbRefresh();
                        }
                        if($.isFunction(handler.onLoadedHandler)) {
                            handler["onLoadedHandler"]();			
                         }
                        e.data.self.coreNodes.length = 0;			
                    });
                },
                //public 노드 추가 ( 1차 모든데이터 렌더링완료후 개별적으로 추가시사용할것 )
                addNode : function(path,parent,id,text,icon,opened,selected,disabled,extendData,hidden){
                    var self = coreTree;
                    var node = this.getNodeData(path,parent,id,text,icon,opened,selected,disabled,false,hidden);
                    
                    if( !this.isEmpty(extendData) ){
                        $.extend( node.data , extendData );
                    }
                    
                    //이미 추가된 노드면 수정
                    var oldNode = this.getNode(id);
                    if( !this.isEmpty(oldNode) ){
                        //console.debug(oldNode);
                        
                        if(oldNode.data.icon != node.data.icon){
                            oldNode.data.icon = node.data.icon;
                            self.jstree('set_icon',$("#" + (id+'')),node.data.icon);
                        }
                        
                        if(oldNode.data.text != node.data.text){
                            oldNode.data.text = node.data.text;
                            oldNode.a_attr.title = node.data.text;
                            self.jstree('set_text',$("#" + (id+'')),text);
                        }
                        
                        if( !this.isEmpty(extendData) ){
                            oldNode.data =  node.data;
                        }
                                    if(selected) {
                                        this.deSelectAll();
                                        this.selectNode(id);
                                    }
                        /*	icon: "fa fa-use-tree fa-sitemap"
                            id: "0000000124"
                            parent: "0000000002"
                            path: "0000000000;0000000001;0000000002;0000000124;"
                            text: "asdfas"
                        */			
                    }else {
                        if( this.isEmpty(this.getNode(parent)) ){
                            node.parent = null;				
                            //node추가 데이터 연결
                            //if( !this.isEmpty(data) ){
                            //	$.extend(treeData , data );
                            //}
                            /*부모없이 최상위 생성*/
                            self.jstree("create_node", null , node ,"last", function(){
                                if(opened){
                                    methods.openNode(parent);
                                }						
                            }, false);
                        }else{
                            //node추가 데이터 연결
                            /*if( !this.isEmpty(data) ){
                                $.extend(treeData , data );
                            }*/
                            
                            self.jstree("create_node", $("#"+ (parent+'') )  , node ,"last", function(){
                                if(opened){
                                    methods.openNode(parent);
                                }
                                if(selected){
                                    methods.deSelectAll();
                                    methods.selectNode(id);
                                }					
                            }, false);				
                        }		
                    }
                },
                //public 배열로 받은 노드 open ['0000000000','0000000001','0000000002','0000000113']
                openNodes : function( Ids ){
                    Ids = Ids.filter(Boolean);//빈값제거
                    coreTree.jstree("open_node", Ids );	
                },
                //public 펼치기
                openNode : function(id){		
                    coreTree.jstree("open_node", $("#" +  (id+'')  ) );
                },
                //public 노드 정보
                getNode : function(id){		
                    var node = coreTree.jstree("get_node", $("#" + (id+'')));
                    //console.debug(node);
                    return node;
                },
                //public 노드선택
                selectNode : function(id){
                    return coreTree.jstree("select_node", $("#" + (id+'')));
                },
                //첫번째 노트 선택
                selectFirstNode : function() {
                    coreTree.jstree('select_node', 'ul > li:first');	
                    coreTree.jstree('open_node', 'ul > li:first');
                },	
                //public 선택된 노드 데이터
                selectedNode : function(){
                        if(coreTree.jstree('get_selected').length == 0 )
                            return null;
                    return coreTree.jstree("get_node", $("#" + coreTree.jstree('get_selected') ));		
                },	
                //public 노드 삭제
                deleteNode : function(id){
                    var oldNode = this.getNode(id);
                    if( !this.isEmpty(oldNode) ){
                        coreTree.jstree("delete_node", $("#" + (id+'')));
                    }
                    //삭제후 노드가 없으므로 최상위 선택처리
                    //coreTree.jstree("select_node", "ul > li:first");
                },
                renameNode : function(node){
                    coreTree.jstree("rename_node", node , node.text);
                },
                //public checkbox checked 노드 데이터
                getCheckedNode : function(){
                    var checked_datas = [];
                    var checked = coreTree.jstree("get_checked",null,true);	    		
                    for(var i = 0 ; i < checked.length ; i++ ) {
                        var node = this.getNode(checked[i]);
                        checked_datas.push(node.data);
                    }
                    return checked_datas;
                },
                getCheckedNodeId : function(){
                    var checked_Ids = [];
                    var checked = coreTree.jstree("get_checked",null,true);	    		
                    for(var i = 0 ; i < checked.length ; i++ ) {	    		    
                        checked_Ids.push(checked[i]);
                    }
                    return checked_Ids;
                },
                //public checkbox checked 처리
                setCheckNode : function(id){
                    coreTree.jstree("check_node", $("#" + (id+'')));
                },
                setCheckAll : function(){
                    coreTree.jstree("check_all");
                },
                setUnCheckAll : function(){
                    coreTree.jstree("uncheck_all");
                },
                //public jstree object 제거
                destory : function(){
                    coreTree.jstree("destroy");
                },
                //public 노드 모두 미선택처리
                deSelectAll : function(){
                    coreTree.clickId = null;
                    coreTree.jstree('deselect_all');
                },
                //public
                openAll : function(){
                    coreTree.jstree("open_all");
                },
                //public
                closeAll : function(){
                    coreTree.jstree("close_all");
                },
                showNode : function(id){
                    coreTree.jstree("show_node", $("#" + (id+'')));
                },
                hideNode : function(id){
                    coreTree.jstree("hide_node", $("#" + (id+'')));
                },
                hideNodes : function(ids){
                    coreTree.jstree("hide_node", ids);
                },
                isHidden : function(id){
                    return coreTree.jstree("is_hidden",  $("#" + (id+'')));
                },
                //private 빈문자열 검사 true : 빈값
                isEmpty : function(text) {
                    if (text == ""
                        || text == null
                        || text == undefined
                        || (text != null && typeof text == "object" && !Object
                        .keys(text).length)) {
                        return true;
                    }
                    return false;
                },
                //트리 한개라도 추가여부
                isExists : function(){
                    if(coreTree.find("li").length > 0 ){
                        return true;
                    }		
                    return false;
                },
                
                onChangeHandler : function(func) {
                    handler["onChangeHandler"] = func;
                },
                
                onSelectNodeHandler : function(func) {
                    handler["onSelectNodeHandler"] = func;
                },
                
                onDblclickHandler : function(func) {
                    handler["onDblclickHandler"] = func;
                },
                
                onHoverNodeHandler : function(func) {
                    handler["onHoverNodeHandler"] = func;
                },
                onLoadedHandler : function(func) {
                    handler["onLoadedHandler"] = func;
                }
            };
              
            var event = {
                onChange : function() {
                    coreTree.on('changed.jstree',{ self : this },function (e, data) {				
                        for(var i = 0 ; i < data.selected.length ; i++ ){
                            var node = data.instance.get_node(data.selected[i]);
                            var changed = (coreTree.clickId == node.id)?false:true;
                            
                            if( changed ){
                                coreTree.clickId = node.id;
                                if(typeof cbClick === "function"){    					
                                cbClick(node);
                                    }
                                if(typeof cbCheckBox === "function"){    					
                                cbCheckBox(node);
                                }
                            }
                            
                            if($.isFunction(handler.onChangeHandler)) {
                            handler["onChangeHandler"]({
                                    node : node
                                   ,changed : changed			    				
                                });
                        }
                            
                         }
                    });
                    return this;
                },
                
                onSelectNode : function() {
                    coreTree.on('select_node.jstree', { self : this },function (e, data) {
                        //console.debug('select_node.jstree : ' + e.data.self.clickedNodeId);
                        //var id = data.instance.get_node(data.selected).id;        //id 가져오기
                        //var type = data.instance.get_node(data.selected).type;    //type 가져오기
                        //var path = data.instance.get_node(data.selected).path;    //paht 가져오기
                        //var a = data.instance.get_node(data.selected).data.a;    //data 에서 a 가져오기		
                        if($.isFunction(handler.onSelectNodeHandler)) {
                            handler["onSelectNodeHandler"]({
                                
                            });
                        }
                    });
                    return this;
                },
                
                onDblclick : function() {
                    coreTree.on("dblclick.jstree", function (e) {
                        var node =  coreTree.jstree("get_node", $("#" + coreTree.jstree('get_selected')));
                        if($.isFunction(handler.onDblclickHandler)) {
                            handler["onDblclickHandler"]({
                                node : node
                            });
                    }
                        if(typeof cbDBClick === "function"){    					
                            cbDBClick(node);
                        }
                    });	
                    return this;
                },
                
                onHoverNode : function() {
                    coreTree.on("hover_node.jstree", function (e, data) {
                        //console.debug(data);
                        //alert(data.rslt.obj.attr("id"));
                        if($.isFunction(handler.onHoverNodeHandler)) {
                            handler["onHoverNodeHandler"]({
                                
                            });
                        }
                    });
                    return this;
                },
                
                onDeSelectAllNode : function() {
                    coreTree.on("deselect_all.jstree",{ self : this }, function (e, data) {
                        //console.debug('deselect_all.jstree :' + e.data.self.clickedNodeId);
                        e.data.self.clickedNodeId = '';
                    });
                    return this;
                }
                
            };        
            return run();
        }
    })(jQuery);