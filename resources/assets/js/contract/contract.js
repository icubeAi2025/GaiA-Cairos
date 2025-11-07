// approval_common.js
var BASEPATH = '/api/projectcost/';

function numberFormat(e){
	const price = e.value;
	let result = 0;
	if(price !== null){
		result = price.toLocaleString();
	} 
	return result;	
}