﻿$_L(["$wt.widgets.Widget"],"$wt.widgets.Item",null,function(){
c$=$_C(function(){
this.text=null;
this.image=null;
$_Z(this,arguments);
},$wt.widgets,"Item",$wt.widgets.Widget);
$_K(c$,
function(parent,style){
$_R(this,$wt.widgets.Item,[parent,style]);
this.text="";
},"$wt.widgets.Widget,~N");
$_K(c$,
function(parent,style,index){
this.construct(parent,style);
},"$wt.widgets.Widget,~N,~N");
$_M(c$,"getImage",
function(){
return this.image;
});
$_V(c$,"getNameText",
function(){
return this.getText();
});
$_M(c$,"getText",
function(){
return this.text;
});
$_M(c$,"releaseWidget",
function(){
$_U(this,$wt.widgets.Item,"releaseWidget",[]);
this.text=null;
this.image=null;
});
$_M(c$,"setImage",
function(image){
this.image=image;
},"$wt.graphics.Image");
$_M(c$,"setText",
function(string){
this.text=string;
},"~S");
});