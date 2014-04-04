var nodemailer = require("nodemailer");
var redis = require("redis");
var redis_config=require('./redis_config.js').db_config;
var mailOptions={};

var smtpTransport = nodemailer.createTransport("SMTP",{
    service: "BHG",
    auth: {
        user: "sunwubin",
        pass: "123"
    }
});


var client = redis.createClient(redis_config.port,redis_config.host);
client.select(redis_config.db, function(error){
    if(error) {
        console.log(error);
    }
});


function init(){
    mailOptions.from="Fred Foo ✔<sunwubin@swb.ownku.com>";
    mailOptions.to="1096490965@qq.com";
    mailOptions.subject="Hello ✔";
    mailOptions.text="Hello world ✔";
    mailOptions.html="<b>Hello world ✔<img src='http://file.100hg.com/images/products/10292/detail.jpg'/></b>" ;
}
init();
function pop(){
   client.lpop('mail',function(error,res){
        if(error){
            console.log(error);
        }else{
           console.log(res);   
           send(res);   
        }
   }); 
}

function send(res){
    if(res==null){
        console.log("end");
        smtpTransport.close();
        process.exit();
    }

     //    mailOptions.to=res;
   mailOpt=JSON.stringify(res);
   smtpTransport.sendMail(mailOptions, function(error, response){
    if(error){
        console.log(error);
        smtpTransport.close();
    }else{
        console.log("Message sent: " + response.message);
        pop();
       //   setInterval(pop,10000,function(id){
       // });
    }
     
  }); 
}

function run(){     
    init();
    //console.log(JSON.stringify(mailOptions));
    pop();
}
run();
//var jsStr=JSON.stringify(mailOptions);
// client.lpush('maix',jsStr,function(error){
// });
// client.lpop('maix',function(error,res){
//         if(error){
//             console.log(error);
//         }else{
//             console.log(JSON.parse(res).from);   
//             // send();   
//         }
//    }); 