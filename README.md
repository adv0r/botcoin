![alt tag](http://www.lize.it/up/prototype-preview/res/logo.png)
### Automatic bitcoin and altcoin trading bot 

## What is botcoin
Botcoin is a simple trading bot that can listen to the market price and execute orders (buy/sell) . Very useful for stop-loss orders, it works on MtGox (RIP), btc-e and bitstamp. It also supports litecoin. The repository contains a node.js prototype and a more complete GUI version written in JavaFX.

## Small history
After dragging this project around for little more than a year, today I open source it.  I started with a node.js prototype bot that executed simple orders like "sell 4 BTC if price drops below 5%". That prototype can be found in the [node-prototype/](https://github.com/adv0r/botcoin/tree/master/node-prototype) directory of this repo, with its own instructions. Here a link to its announcement on [bitcointalk](https://bitcointalk.org/index.php?topic=161687.msg1705468#msg1705468). It used to work on MtGox (RIP), btc-e and bitstamp: the three major exchanges back in the days. It was quite innovative since was using the v.2.0 of gox's API. 

It saved my life-savings and also made some profit. More people started asking me to release it and that's why I decided to build a proper product in which bitcoiners can put their own trust : I was gonna build the [trading bot for the trust-no one kind of guys](https://bitcointalk.org/index.php?topic=182946.msg1940056#msg1940056). The code at this repository is the result. Is not finished and its incomplete but it was a very interesting exercise to carry on.  Check it out.

## Features

Create your own trading strategy in plain english, run the app, and then you are free to relax, no more nightmares, no more losses.  Botcoin runs locally, meaning that your secret API keys are encrypted and stored on your machine.
- Cross platform applications written and tested in JavaFx
- Api access keys stored on your machine and NOT in the wild web like other bots.
- Support for LITECOINS trading on btc-e
- BTC-e MtGox and Bitstamp trading
- Customizable trading strategy (with unlimited rules)
- Mail alerts, in-app charts
- Nice logs
- export/import trading strategy (csv)


## How to use it

### Bot Dashboard - Active Orders

![alt tag](http://www.lize.it/up/prototype-preview/res/bot_status.png)

### Bot Dashboard - Wallet
![alt tag](http://www.lize.it/up/prototype-preview/res/bot_wallet.png)

### Bot Dashboard - Strategy
![alt tag](http://www.lize.it/up/prototype-preview/res/bot_strategy.png)

### Bot Dashboard - Bot Dashboard - Settings
![alt tag](http://www.lize.it/up/prototype-preview/res/insert_api.png)

### Bot Dashboard - Add new buy/sell rule
![alt tag](http://www.lize.it/up/prototype-preview/res/new_rule_buy.png)

### Bot Dashboard - Add new notify rule
![alt tag](http://www.lize.it/up/prototype-preview/res/new_rule_notify.png)

### Bot Settings - Add API keys
![alt tag](http://www.lize.it/up/prototype-preview/res/bot_settings.png)


### Sample Strategy.csv

```
operation, amount,coin,direction,target,market

buy,10.1,btc,above,115,mtgox

sell,0.023,btc,below,110,bitstamp

sms,,ltc,above,5,btce

buy,4,ltc,above,3,btce

sell,30,btc,below,98,mtgox

mail,,btc,below,100,bitstamp

```



## How to contribute

Just go ahead ;) 

## Donation

1337AdvApU14cW4M8fU5E1zAqDEe4NZGGN

