/* Bot for trading coins  - mailto leg@lize.it*/

/*TODO----------------------------------------------------
 Send current balance by email
*/

// variables read from config.json
var NOTIFICATIONS , API_KEY, API_SECRET_KEY, STRATEGY_FILE,ASK_CONFIRMATION,SENDER,G_PASSWD;

//Libraries
var nodemailer = require("nodemailer"); //Required for sending notification email
var csv = require("fast-csv"); 
var fs = require('fs'); //Required for reading the csv from the local filesystem

var querystring = require('querystring'); //Required for mtgox api
var https = require('https'); //Required for mtgox api
var crypto = require('crypto'); //Required for mtgox api
var cronJob = require('cron').CronJob; //Required for scheduling 

//Global variables
var client,task,csvInput,strategy;

//static global  variables
var FACTOR_TO_INT = 100000000; //Trading API requires the INT value for the amount of bitcoins.
var CONFIRM_SENTENCE = "go ahead!"; //sentence asked to input to the user to confirm scheduling trade
var LOG_PATH = 'logs/'+new Date().getTime()+'_bot.log'

var API_HOST ="data.mtgox.com";
var API_TICKER ="1/BTCUSD/public/ticker";
var API_PRIVATE_INFO="1/generic/private/info";
var API_ORDER="1/BTCUSD/private/order/add";


init();
 
function init(){
	 
	fs.readFile('cfg/config.json', 'utf8', function (err, data) {
		if (err) {
			console.log('Error: ' + err);
			return;
		}
		data = JSON.parse(data);
		NOTIFICATIONS = data.email_notification;
		API_KEY = data. api_key;
		API_SECRET_KEY = data. sec_key;
		STRATEGY_FILE=data.strategy;
		ASK_CONFIRMATION=data.ask_confirmation;
		SENDER  = data.gmail_sender;
		G_PASSWD = data.gmail_password;
		start();
		});

 }

function start()
{	
 	client = new MtGoxClient(API_KEY, API_SECRET_KEY);

 	//Initiate log file
	fs.writeFile(LOG_PATH, 'Log started', function(err) {
		    if(err) {
		        console.log(err);
		    } else {
		        printAndLog('Log file started ('+LOG_PATH+')')
		    }
	}); 

 	//So it does not stop working if something goes wrong
 	process.on('uncaughtException', function(err) {
 		printAndLog(err.stack);
	});

 	//Validate strategy and schedule the task
	if(isInputValid(process.argv))
 			readStrategyFromCsv();	
	else 
		console.log("Input incorrect .\n"+ USAGE);	
}

// Function to validate input correctness
function isInputValid(args)
{
	if(!fs.existsSync(STRATEGY_FILE))
		{
			console.log("The strategy file does not exist ("+STRATEGY_FILE+")");
			return false;
		}
	return true;   
}

function readStrategyFromCsv()
{
	strategy=new Array();
 	var stream = fs.createReadStream(STRATEGY_FILE); 
 	var count=0;

 	csv(stream,{headers : true})
		 .on("data", function(data){ //iterate on strategy items
			 var temp=new Object();
			 temp.operation=data.operation;
			 temp.amount=parseFloat(data.amount);
			 temp.direction=data.direction;
			 temp.target= parseFloat(data.target);
			 temp.comment=data.comment;
			 temp.executed = false;   //is it executed or not?

			 //validation conditions
			 var exit_c1 =  temp.operation!="sell" && temp.operation!="buy";
			 var exit_c2 =  temp.direction!="below" &&  temp.direction!="above";
			 var exit_c3 = typeof(temp.amount)!="number";
			 var exit_c4 = typeof(temp.target)!="number";

			 if(exit_c1 ||  exit_c2  ||  exit_c3  ||  exit_c4 )
			 {
			 	printAndLog("Strategy not valid. Check your "+STRATEGY_FILE+" file");
			 	process.exit(1);
			 }
			 else //Data is valid
			 {	

				 strategy[count]=temp;
				 count++;
			 }
		
		 })
		 .on("end", function(){
		 	//Do something after parsing the csv
		   	askUserForConfirmation();
		 })
		 .parse();	
}

//Ask the user for confirmation before scheduling the operation
function askUserForConfirmation(args)
{
	printAndLog("This bot will attempt to perform the following operations every 30 seconds :");
	for (var i = strategy.length - 1; i >= 0; i--) {
		printAndLog((strategy.length-i)+" : "+strategy[i].operation+" "+strategy[i].amount+" BTC when the prices goes "
			+strategy[i].direction+" "+strategy[i].target+"$. ("+strategy[i].comment+")");
	};

	if(ASK_CONFIRMATION=="true")
	{
		var readline = require('readline');

		var rl = readline.createInterface({
		  input: process.stdin,
		  output: process.stdout,
		  terminal: false
		});

		console.log("\nTo confirm type '"+CONFIRM_SENTENCE+"' and hit enter "); 
		rl.on('line', function (cmd) {
	 		if(cmd==CONFIRM_SENTENCE)
	 		{
	 			scheduleTask();
	 			console.log("I'm on it. Double check that you are not messing around, for fuck sake!\n"+
	 				"Task will start in 30 seconds");
	 			rl.close();
	 		}
	 		else 
	 		{
	 			console.log("Hej då!");
	 			rl.close();
	 		}	
		});
	}
	else
	{
		console.log("I'm on it. Double check that you are not messing around, for fuck sake!\n"+
	 				"Task will start in 30 seconds");
		scheduleTask();
	}
}

function scheduleTask()
{	
	task = new cronJob('*/30 * * * * *', function(){  //Execute it every 30secs
	    performTask();
	}, null, true, null);
}

// Get the last ticker from mtgox and perform the task
function performTask()
{
	//Log status
	//Update the variable with current_balance
	/*var current_balance = new Object();
	var client = new MtGoxClient(API_KEY, API_SECRET_KEY);
	client.query(API_PRIVATE_INFO, {}, function(json) {
		 current_balance.BTC = json.return.Wallets.BTC.Balance.value;
		 current_balance.USD = json.return.Wallets.USD.Balance.value;
		 current_balance.EUR = json.return.Wallets.EUR.Balance.value;
	});*/

	var client = new MtGoxClient(API_KEY, API_SECRET_KEY);
	client.query(API_TICKER, {},null, function(json_ticker, useless) {
		ticker=new Object();
		if(json_ticker.return!="undefined") //TODO FIX BUG
			if(json_ticker.return.last!="undefined")
			{
				ticker.price = json_ticker.return.last.value;
				 //ticker.sell = json_ticker.return.sell.value;
				 //ticker.buy = json_ticker.return.buy.value;
		 		 checkOperationNecessity(ticker);
			}
			else
				printAndLog("Error: Cannot update the price from the ticker.");	

		else
			printAndLog("Error: Cannot update the price from the ticker.");	
	});

}

//Logic controls before buy and sell operations
function checkOperationNecessity(ticker)
{
	lastPrice = ticker.price;
	var datetime  =  getReadableDate();
	printAndLog("\n"+datetime+"  |  Price updated: "+lastPrice+"$\n");
	for (var i = strategy.length - 1; i >= 0; i--) {
		ts = strategy[i];

		var operation = ts.operation;
		var amount = ts.amount;
		var direction = ts.direction;
		var target = ts.target;
	
		
		if(!ts.executed)
		{
			var order_string = operation+" "+amount+" "+direction+" "+target+"$";
			printAndLog(order_string);
			if(operation=="sell")
			{
				
				if  ((lastPrice<=target && direction=="below") || (lastPrice>=target && direction=="above") )
				{
					placeOrder(order_string,strategy,"ask",amount,i);			
				}
			}
			else  //buy operation
			{
				if  ( (lastPrice<=target && direction=="below") ||  (lastPrice>=target && direction=="above") )
				{
					placeOrder(order_string,strategy,"bid",amount,i);			
				}
			}
		}
	};	
}


//Place an order to sell at market price
function placeOrder(order_string,strategy,type,amount,i)
{
	printAndLog("Scheduling order for execution: "+ order_string);
	strategy[i].executed = true;
	var ts = strategy[i];
	var amount_int  =  get_btc_int_val(amount);

	client.query(API_ORDER, {"type":type, "amount_int":amount_int},ts, function(response,trade) {
		printAndLog("✔ [Trade executed] "+trade.operation+" "+trade.amount+" "+trade.direction+" "+trade.target+"$\n"+
			"Order response:  "+response.result +" "+ response.return); 
		 //send email confirmation
 		sendMail(trade,response, getReadableDate()); 
	});
	
}


//Transform BTC amount to INT (required from APIs)
function get_btc_int_val(btc_val) 
{
	return btc_val*FACTOR_TO_INT;
}


//Add zeros if < 10 to correct date
function addZeros(val)
{
	if(val<10)
		return "0"+val;
	return val;
}

//Get a String with current timestampp in human readable format
function getReadableDate()
{
	var now = new Date();
	return  addZeros(now.getDate())+"-"+addZeros((now.getMonth())+1)+
		"@"+addZeros(now.getHours())+":"+addZeros(now.getMinutes())+":"+addZeros(now.getSeconds());

}
//MtGox client start ------------------------------------------------------------------------
function MtGoxClient(key, secret) {
        this.key = key;
        this.secret = secret;
}

MtGoxClient.prototype.query = function(path, args, trade,callback) {
        var client = this;
 
        // if no args or invalid args provided, just reset the arg object
        if (typeof args != "object") args = {};
 
        // generate a nonce
        args['nonce'] = (new Date()).getTime() * 1000;
        // compute the post data
        var post = querystring.stringify(args);
        // compute the sha512 signature of the post data
        var hmac = crypto.createHmac('sha512', new Buffer(client.secret, 'base64'));
        hmac.update(post);
 
        // this is our query
        var options = {
                host: API_HOST,
                port: 443,
                path: '/api/' + path,
                method: 'POST',
                agent: false,
                headers: {
                        'Rest-Key': client.key,
                        'Rest-Sign': hmac.digest('base64'),
                        'User-Agent': 'Mozilla/4.0 (compatible; MtGox node.js client)',
                        'Content-type': 'application/x-www-form-urlencoded',
                        'Content-Length': post.length
                }
        };
 
        // run the query, buffer the data and call the callback
        var req = https.request(options, function(res) {
                res.setEncoding('utf8');
                var buffer = '';
                res.on('data', function(data) { buffer += data; });
                res.on('end', function() { if (typeof callback == "function") { callback(JSON.parse(buffer),trade); } });
        });
 
        // basic error management
        req.on('error', function(e) {
                printAndLog('warning: problem with request: ' + e.message);
        });
 
        // post the data
        req.write(post);
        req.end();
};
//MtGox client end ------------------------------------------------------------------------

//Send email
function sendMail(trade,response,date)
{
	// create reusable transport method (opens pool of SMTP connections)
	var smtpTransport = nodemailer.createTransport("SMTP",{
	    service: "Gmail",
	    auth: {
	        user: SENDER,
	        pass: G_PASSWD
	    }
	});

	// setup e-mail data with unicode symbols
	var mailOptions = {
	    from: "Bitcoin Bot  ✔ <no-reply@botc.gov>", // sender address
	    to: NOTIFICATIONS, // list of receivers
	    subject: "✔ [Trade executed] "+trade.operation+" "+trade.amount+" "+trade.direction+" "+trade.target+"$", // Subject line
	    text: "The bitcoin bot executed the operation:\n"+
	    	trade.operation+" "+trade.amount+" "+trade.direction+" "+trade.target+"$\n"+
	    	"Transaction result :"+response.result +" id: "+ response.return +"\n"+
	    	"Comments : "+trade.comment+"\n"+
	    	"Timestamp: "+date+"\n",  
	    html: "" // html body
	}

	// send mail with defined transport object
	smtpTransport.sendMail(mailOptions, function(error, response){
	    if(error){
	        printAndLog(error);
	    }else{
	        printAndLog("Notification sent to : "+NOTIFICATIONS);
	    }

	    // if you don't want to use this transport object anymore, uncomment following line
	    smtpTransport.close(); // shut down the connection pool, no more messages
	});
}

function printAndLog(message)
{
	var fs = require('fs');
	console.log(message);
	fs.appendFile(LOG_PATH, message+'\n', function(err) {
		    if(err) {
		        console.log(err);
		    } else {
		        //
		    }
	}); 
}




