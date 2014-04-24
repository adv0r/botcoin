#What is Botcoins.js

It is a simple Node.js app that can automatically sell/buy BTC on MTgox when certain thresholds are reached. It checks the price every 30seconds and execute your scheduled orders if needed. Everything gets logged in the directory logs/ and you will receive an email on a specified address whenever a trade is executed (works with gmail for now).


#Download it

Clone it with git (`git clone https://github.com/adv0r/botcoin.git`) or [download the zipped archive](https://github.com/adv0r/botcoin/archive/master.zip)

#Configure it

You need to specify your trade rules in a simple csv file.  Each line represents a rule.  Below you can see a sample *strategy.csv*  file.

| operation        | amount           | direction  | target   | comment |
| ------- |:-----:| -----------| :----:|  ----------------------| 
| buy     | 15 | below|  81 |here is one comment|
| sell     | 1  | above| 90.3 | sell one BTC when the price overcomes 90.3$|
| sell  | 100 | below| 55 | sell 100 BTC if the price drops under 55$|

In the *cfg/* directory you can find a sample *.csv* file. The currency is USD.

Than you need to edit the config file located in *cfg/config.json* entering your MTgox api keys, path to strategy file, email credential and a boolean parameter for showing/hiding a confirmation message before running it.

```json
{
    "api_key": "your-api-key-mtgox",
    "sec_key": "your-secret-key-mtgox",
    "strategy": "path/to/strategy.csv",
    "email_notification": "receive@address.com",
    "gmail_sender": "you_send@gmail.com",
    "gmail_password": "Your?gmail_p4sswd",
    "ask_confirmation": "true"
}
```

#Run it

Assuming you have node .js installed on your machine ([if not read this](http://howtonode.org/how-to-install-nodejs))
,  to run the bot type 

``` node botcoin.js ```

If you want to run it in background (for example on a server running 24/7) you can use nohup

```nohup node botcoin.js > logs/background_process.log & ```

#Love it
Disclaimer:  **This bot is probably faulty. Hence, dont trust me with your own money.** Try it with small amounts firsts.

I needed it for my own purpose. Now I can sleep good dreams and siestas having the app running 24/7 on a webserver . It worked like a charm for one week straight without failures and it saved my investments from big drops.

Let me know if you like it and you find it useful. Fork it, clone it, talk to me 

If you want to support open source projects like this, support me donating to  **1N7XxSvek1xVnWEBFGa5sHn1NhtDdMhkA7.**