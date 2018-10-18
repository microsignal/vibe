## 查询接口

数据格式:
```
{
  search:[
    {key:'id',op:'eq',val:'1'},
    {key:'name',op:'like',val:'xx'},
    {'num__lt': 100},
    {
      conj:'or',
      search:[
        {key:'state',op:'eq',val:'a'},
        {key:'state',op:'eq',val:'b'},
        {key:'state',op:'eq',val:'c'}
      ]
    }
  ]
  sort:[
    {key:'createDate',dir:'asc'},
    {key:'updateDate',dir:'desc'}
  ],
  limit:{
    ps:10,pn:1
  }
}
```
各字段含义如下:

* search 查询条件,可嵌套
  * key 字段名, 如'id','name'
  * op: 条件关系符号, 如'eq','like'
  * val: 条件值
  * conj: 子条件组的连接词
* sort 排序字段列表
  * key 字段名, 如'createDate'
  * dir 排序正序('asc')或逆序('desc')
* limit 分页条件
  * ps 每页大小
  * pn 页码值
