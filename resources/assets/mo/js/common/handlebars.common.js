Handlebars.registerHelper('debug', function(optionalValue) {
    console.log('\nCurrent Context');
    console.log('====================');
    console.log(this);

    if (arguments.length > 1) {
        console.log('Value');
        console.log('====================');
        console.log(optionalValue);
    }
});


Handlebars.registerHelper('toString', function(object) {
    return JSON.stringify(object);
});

Handlebars.registerHelper('bank', function(value, block) {
    // console.log('CODES', CODES);
    // console.log(this);
    // console.log(block);
    var data = {
        account: value,
        banks: CODES['BANK_KIND'].values,
        readonly: block.hash.readonly ? JSON.parse(block.hash.readonly) : false
    }

    return new Handlebars.SafeString(handlebarCompile($('#bank-template').html(), data));
});

var getBankName = function(bankCode) {
    var bankName = 'empty';

    if (CODES['BANK_KIND'].values) {
        for (var i = 0, length = CODES['BANK_KIND'].values.length; i < length; i++) {
            if (CODES['BANK_KIND'].values[i].value === bankCode) {
                bankName = CODES['BANK_KIND'].values[i].name;
                break;
            }
        }
    }

    return bankName;
}

Handlebars.registerHelper('bankName', getBankName);

Handlebars.registerHelper('giftPick', function(value) {
    let text = '';
    // 답례품 상태. -1: 받지 않음, 0: 미선택, 1: 선택, 2: 지급

    if (value.giftStatus == -1) {
        text = '담례품 받지않기 선택';
    } else if (value.giftStatus == 1) {
        text = value.eventGift.gift.name;
    } else if (value.giftStatus == 2) {
        text = '지급완료';
    } else {
        text = '아직 선택안함';
    }

    return text;
});

Handlebars.registerHelper('eventGroupName', function(type, value) {
    let name = '';

    console.log('type', type, 'value', value)
    if (type == EVENT_HOST_GROUP) {
        name = value === GUEST_GROUP_GROOM ? GUEST_GROUP_GROOM_NAME : GUEST_GROUP_BRIDE_NAME;
    } else {

    }

    return name;
});

Handlebars.registerHelper('eventGroupRoleName', function(type, value, value2) {
    let name = '';

    if (type == EVENT_HOST_GROUP) {
        switch (value) {
            case EVENT_GROUP_ROLE_1:
                name = EVENT_GROUP_ROLE_1_NAME;
                break;
            case EVENT_GROUP_ROLE_2:
                name = EVENT_GROUP_ROLE_2_NAME;
                break;
            case EVENT_GROUP_ROLE_3:
                name = EVENT_GROUP_ROLE_3_NAME;
                break;
            case EVENT_GROUP_ROLE_4:
                name = EVENT_GROUP_ROLE_4_NAME;
                break;
            case EVENT_GROUP_ROLE_5:
                name = EVENT_GROUP_ROLE_5_NAME;
                break;
            case EVENT_GROUP_ROLE_6:
                name = EVENT_GROUP_ROLE_6_NAME;
                break;
            case EVENT_GROUP_ROLE_7:
                name = EVENT_GROUP_ROLE_7_NAME;
                break;
            case EVENT_GROUP_ROLE_8:
                name = EVENT_GROUP_ROLE_8_NAME;
                break;
            case EVENT_GROUP_ROLE_9:
                name = EVENT_GROUP_ROLE_9_NAME;
                break;
            default:
                if (!value2) {
                    value2 = '';
                }

                name = `${EVENT_GROUP_ROLE_99_NAME} - ${value2}`;
                break;
        }
    } else {

    }

    return name;
});

Handlebars.registerHelper('calc', function(v1, operator, v2, options) {
    // var emotion = Handlebars.escapeExpression(this.emotion),
    //     name = Handlebars.escapeExpression(this.name);
    // return new Handlebars.SafeString('');
    // return numeral(eval(v1 + operator + v2)).format('0,0');
    let func = new Function('a', 'b', `return a ${operator} b`);
    return numeral(func(v1, v2, operator)).format('0,0');
});

Handlebars.registerHelper('link', function(url, text, css) {
    // console.log('ori', url);
    var url = Handlebars.escapeExpression(url),
        text = Handlebars.escapeExpression(text)
    css = Handlebars.escapeExpression(css)
    // console.log('chg', url);

    if (url.indexOf('js:') > -1) {
        var tmp = url.split(':');
        return new Handlebars.SafeString(`<a href='javascript:${tmp[1]}' class='${css}'>${text}</a>`);
    }

    return new Handlebars.SafeString(`<a href='${url}' class='${css}'>${text}</a>`);
});

// Handlebars.registerHelper('x', function(expression, options) {
//     var result;
//     var context = this;
//
//     with(context) {
//         result = (function() {
//             try {
//                 const code = `console.log(${expression}); return ${expression};`;
//                 const func = new Function(code);
//                 return func();
//                 // return eval(expression);
//             } catch (e) {
//                 console.warn('•Expression: {{x \'' + expression + '\'}}\n•JS-Error: ', e, '\n•Context: ', context);
//             }
//         }).call(context); // to make eval's lexical this=context
//     }
//     return result;
// });
// Handlebars.registerHelper('xif', function (expression, options) {
//     return Handlebars.helpers['x'].apply(this, [expression, options]) ? options.fn(this) : options.inverse(this);
// });
Handlebars.registerHelper('ifC', function(v1, v2, options) {
    if(v1 === v2) {
        return options.fn(this);
    }
    return options.inverse(this);
});
Handlebars.registerHelper('ifeq', function(a, b, opts) {
    if(a == b) // Or === depending on your needs
        return opts.fn(this);
    else
        return opts.inverse(this);
});
Handlebars.registerHelper('if_eq', function(context, options) {
    if (context == options.hash.compare)
        return options.fn(this);
    return options.inverse(this);
});
Handlebars.registerHelper('ifCond', function(v1, operator, v2, options) {
    switch (operator) {
        case '==':
            return (v1 == v2) ? options.fn(this) : options.inverse(this);
        case '===':
            return (v1 === v2) ? options.fn(this) : options.inverse(this);
        case '!=':
            return (v1 != v2) ? options.fn(this) : options.inverse(this);
        case '!==':
            return (v1 !== v2) ? options.fn(this) : options.inverse(this);
        case '<':
            return (v1 < v2) ? options.fn(this) : options.inverse(this);
        case '<=':
            return (v1 <= v2) ? options.fn(this) : options.inverse(this);
        case '>':
            return (v1 > v2) ? options.fn(this) : options.inverse(this);
        case '>=':
            return (v1 >= v2) ? options.fn(this) : options.inverse(this);
        case '&&':
            return (v1 && v2) ? options.fn(this) : options.inverse(this);
        case '||':
            return (v1 || v2) ? options.fn(this) : options.inverse(this);
        default:
            return options.inverse(this);
    }
});

//switch s
Handlebars.registerHelper('switch', function(value, options) {
    this.switch_value = value;
    this.switch_break = false;
    return options.fn(this);
});
Handlebars.registerHelper('case', function(value, options) {
    if (value == this.switch_value) {
        this.switch_break = true;
        return options.fn(this);
    }
});
Handlebars.registerHelper('default', function(options) {
    if (this.switch_break == false) {
        return options.fn(this);
    }
});
//switch e

Handlebars.registerHelper('breaklines', function(v) {
    return new Handlebars.SafeString(v.replace(/(\r\n|\n|\r)/gm, '<br/>'));
});

var getPhoneFormat = function(phoneNo) {
    var len = phoneNo.replace(/\D/g,'').length;

    if(len == 10) {
        phoneNo = phoneNo.replace(/\D/g, '').replace(/^(\d{3})(\d{3})(\d{4})$/, '$1-$2-$3');
    } else if(len == 11) {
        phoneNo = phoneNo.replace(/\D/g, '').replace(/^(\d{3})(\d{4})(\d{4})$/, '$1-$2-$3');
    }

    return phoneNo;
}

Handlebars.registerHelper('phoneFormat', function(v) {
    return new Handlebars.SafeString(getPhoneFormat(v));
});

Handlebars.registerHelper('today', function(block) {
    var f = block.hash.format || 'YYYY-MM-DD HH:mm:ss';
    return moment().format(f);
});

Handlebars.registerHelper('dateFormat', function(context, block) {
    // moment("20210314").format('YYYY년 MMMM Do dddd HH시mm분ss초')
    if (window.moment && context && moment(context).isValid()) {
        var f = block.hash.format || 'YYYY-MM-DD HH:mm:ss';
        return block.hash.parse ? moment(context, block.hash.parse).format(f) : moment(context).format(f);
    }else{
        return context;   //  moment plugin is not available, context does not have a truthy value, or context is not a valid date
    }
});

Handlebars.registerHelper('numberFormat', function(v) {
    return numeral(v).format('0,0');
});

Handlebars.registerHelper('concat', function(v1, v2) {
    return v1 + '' + v2;
});

Handlebars.registerHelper('limit', function(context, block) {
    var ret = '',
        offset = parseInt(block.hash.offset) || 0,
        limit = parseInt(block.hash.limit) || 5,
        i = (offset < context.length) ? offset : 0,
        j = ((limit + offset) < context.length) ? (limit + offset) : context.length;

    for(i,j; i<j; i++) {
        ret += block.fn(context[i]);
    }

    return ret;
});