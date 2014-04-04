
var assert = require('assert');
var redis = require("redis");
var fs=require('fs');
var ndir = require('ndir');
var redis_config=require('./redis_config.js').db_config;
console.log(redis_config.host);

var client = redis.createClient(redis_config.port,redis_config.host);
client.select(redis_config.db, function(error){
    if(error) {
        console.log(error);
        client.end();
    }
});

var mailOptions={};
function init(){
    mailOptions.from="Fred Foo ✔<sunwubin@swb.ownku.com>";
    mailOptions.to="";
    mailOptions.subject="Hello ✔";
    mailOptions.text="Hello world ✔";
    mailOptions.html="<b>Hello world ✔</b>" ;
}
init();
var lineNumber=0;
ndir.createLineReader('part-m-00001.txt').on('line', function(line) {
    assert.ok(Buffer.isBuffer(line));
    ++lineNumber;
    mailOptions.to=line.toString();
    var jsStr=JSON.stringify(mailOptions);
    client.lpush("mail", jsStr, function (err, reply) {
    //console.log(reply.toString());
    });
 }).on('end', function() {
  client.end();
  console.log('read a file done.');
}).on('error', function(err) {
   console.log('error: ', err.message);
});
