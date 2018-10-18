# 接口说明 

## 数据格式

* 请求数据格式:
```json
{
  "head":{
    "txnCode":"交易代码"
  },
  "body":{ /*请求数据体*/
  
  }
}
```
* 响应数据格式:
```json
{
  "head":{
    "txnCode":"交易代码",
    "success":"成功标志,true或false",
    "code":"响应代码",
    "message":"响应消息"
  },
  "body":{
    /*
    请求数据体,如:
    "app_auth_token":"",
    "trade_no":"",
    "out_trade_no":""
    */
  }
}
```
