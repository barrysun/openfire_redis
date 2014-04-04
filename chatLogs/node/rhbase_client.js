
var hbase=require('hbase');
var client=hbase({
	host:'192.168.1.230',
	port:8888
});
//使用put方法，插入数据既可以是插入单列也可以同时插入多列。
var table=client.getTable('chatlogs');
table.create('cl',function(err,success){
  if(err){
    console.log(err);
  }
  this.getRow('row').put(['cl:a'],['a'],function(err,success){
    console.log('insert one column');
    console.log(success);
  });
});



/*var myTable=client.getTable('test');
myTable.create('cf',function(err,success){
   this.getRow('row2').put(['cf:a','cf:b','cf:c'],['a','b','c'],function(err,success){
      console.log('insert multi column');
      console.log(success);
   });
});

var cells=
[{column:'cf:a',timestamp:Date.now(),$:'a'},
 {column:'cf:b',timestamp:Date.now(),$:'b'},
 {column:'cf:c',timestamp:Date.now(),$:'c'}
];
myTable.create('cf',function(err,success){
  this.getRow('row1').put(cells,function(err,success){
     console.log('insert multi column user array');
     console.log(success);
  });
});

//获取数据使用get方法，同样的获取数据时可以一次只获取一行一列，或者一行多列。
var hbase=require('hbase');
var client=hbase({
	host:'192.168.1.230',
	port:8090
});

var myRow=client.getTable('test').getRow('row1');
myRow.exists('cf',function(err,exists){
  if(exists){
  	this.get('cf',function(err,values){
  		console.log('get column family');
  		console.log(values);
  	});
  }
});

var myRow=client.getTable('test').getRow('row1');
myRow.exists('cf',function(err,exists){
   if(exists){
   	this.get('cf',function(err,values){
       console.log('get column family');
       console.log(values);
   	});
   }
});

myRow.exists('cf:a',function(err,exists){
   if(exists){
   	this.get('cf:a',function(err,value){
      console.log('get column a');
      console.log(value);
   	});
   }
});

//使用scanner获取数据
var hbase=require('hbase');
var scanner =hbase({
	host:'192.168.1.230',
	port:8090
}).getScanner('test');

scanner.create({
	batch:3
},function(err,success){
	this.get(function(err,cells){
		if(err){
			console.log('err');
		}
		if(cells){
			console.log(cells);
		}else{
			this.delete();
		}
	});
});

//删除数据使用delete方法
var hbase=require('hbase');
var client=hbase({
	host:'192.168.1.230',
	port:8090
});

var table=client.getTable('test');
var row=table.getRow('row1');
row.delete(function(err,success){
   if(success){
   	console.log('delete a row');
   }
});
var row1=table.getRow('row3');
row.delete('cf:b',function(err,success){
   if(success){
   	console.log('delete a column');
   }
});
row.delete(['cf:a','cf:c'],function(err,success){
	if(success){
		console.log('delete multi column');
	}
});
*/
