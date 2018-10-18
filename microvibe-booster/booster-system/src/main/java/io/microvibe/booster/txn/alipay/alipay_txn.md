# 支付宝相关接口

数据格式参见: <a href="../readme.md">接口说明</a>

除部分自定义交易代码外,其他交易代码均为支付宝官方交易代码加前缀`pay.`,如
```
alipay.open.auth.token.app --> pay.alipay.open.auth.token.app
```


公共请求头参数:
head:{
	alipayRoute: '支付路由配置标识符,不传按默认来',
	alipayAuthAppId: '第三方授权应用ID,不传按默认来',
}


## Alipay0000(pay.alipay.common)

通用的 Alipay 调用接口

* 直接调用 alipay 的任意接口, 未作接口请求校验与响应的解析
* 由调用方负责传入经过校验的参数, 并解析获得的响应结果

请求参数:
{
	head:{
		txnCode:'pay.alipay.common或Alipay0000'
		alipayApiType: 'api类型: page/sdk/default'
	},
	body:{
		method:'Alipay接口方法名,如alipay.trade.pay',
		notify_url:'',
		auth_token:'',
		app_auth_token:'',
		...
		biz_content:{}
	}
}
## Alipay0001 (pay.alipay.oauth2.callback)

用户信息授权接口回调

请求参数:
{
	head:{
		txnCode:'pay.alipay.common或Alipay0000'
		alipayApiType: 'api类型: page/sdk/default'
	},
	body:{
		app_id:'',
		auth_code:'',
		app_auth_code:'',
		scope:'',
		state:''
	}
}

## Alipay0002 (pay.alipay.oauth2.public)

用户信息授权接口

参见官方文档: [小程序](https://docs.alipay.com/mini/introduce/authcode)
[通用WEB](https://docs.open.alipay.com/289/105656)

## Alipay0003 (pay.alipay.oauth2.app)

第三方应用授权接口

参见: [官方文档](https://docs.open.alipay.com/20160728150111277227/intro/)

## Alipay0004 (pay.alipay.open.auth.token.app)

使用app_auth_code换取app_auth_token

参见: [官方文档](https://docs.open.alipay.com/api_9/alipay.open.auth.token.app)


## Alipay0005 (alipay.open.auth.token.app.query)

查询授权信息

参见: [官方文档](https://docs.open.alipay.com/api_9/alipay.open.auth.token.app.query)

## Alipay0100 (pay.alipay.trade.page.pay)

电脑网站支付宝即时到账交易接口

参见: [官方文档](https://docs.open.alipay.com/270/105899/)



## Alipay0101 (pay.alipay.trade.pay)

统一收单交易支付接口

参见: [官方文档](https://docs.open.alipay.com/api_1/alipay.trade.pay)


## Alipay0102 (pay.alipay.trade.refund)

退款接口

参见: [官方文档](https://docs.open.alipay.com/api_1/alipay.trade.refund)


## Alipay0103 (pay.alipay.trade.query)

统一收单线下交易查询

参见: [官方文档](https://docs.open.alipay.com/api_1/alipay.trade.query)


## Alipay0104 (pay.alipay.trade.wap.pay)

外部商户创建订单并支付,手机网站支付接口2.0

参见: [官方文档](https://docs.open.alipay.com/api_1/alipay.trade.wap.pay)

## Alipay0105 (pay.alipay.trade.app.pay)

外部商户APP唤起快捷SDK创建订单并支付,app支付接口2.0

参见: [官方文档](https://docs.open.alipay.com/api_1/alipay.trade.app.pay)

## Alipay0106 (pay.alipay.trade.create)

统一收单交易创建接口

参见: [官方文档](https://docs.open.alipay.com/api_1/alipay.trade.create)
