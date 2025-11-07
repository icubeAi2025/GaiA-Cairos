var BASEPATH = '/api/projectcost/';
var loginType = document.body.className;
var pjtNo = param.pjtNo
var cNo = param.cntrctNo

function numberFormat(e){
	const price = e.value;
	let result = 0;
	if(price !== null){
		result = price.toLocaleString();
	} 
	result = result == 0 ? '' : result;
	return result;	
}