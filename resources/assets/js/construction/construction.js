var BASEPATH = '/api/construction/';
var loginType = document.body.className;
var pjtNo = param.pjtNo
var cNo = param.cntrctNo

var now_utc = Date.now();
var timeOff = new Date().getTimezoneOffset()*60000;
var today = new Date(now_utc-timeOff).toISOString().split("T")[0];

function numberFormat(e, inType){
	let price = ""
	if(inType == "g"){
		price = e;
	}else{
		price = e.value;
	}
	let result = 0;
	if(price !== null){
		result = price.toLocaleString();
	} 
	return result;	
}


function dateFormat(e, inType){
	let date = ""
	if(inType == "g"){
		date = e;
	}else{
		date = e.value;
	}
	let result = "";
	if(date == null || date == ""){
		result = "";
	}else if(date.length > 6){
		result += date.substr(0, 4) + "-" + date.substr(4, 2) + "-" + date.substr(6);
	}else{
		result = date.substr(0, 4) + "-" + date.substr(4);
	}
	return result;
}