
package io.botcoin.api;

import io.botcoin.global.Constant;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class Tickers {


//Methods
public static double getLastPrice(String market, String coin, String currency)
{
    double toReturn=-1;
    
    TradeInterface trade = null;
    switch(market)
    {
        case Constant.MTGOX: trade = new MtGoxTrading() ; break;
        case Constant.BTCE: trade = new BtceTrading() ;break;
        case Constant.BITSTAMP: trade =  new BitStampTrading() ;break;
        default : trade = new MtGoxTrading(); break;
    }
    
    switch(currency)
    {
        case Constant.USD: toReturn = trade.getLastPriceUSD(coin) ; break;
        case Constant.EUR: toReturn = trade.getLastPriceEUR(coin) ; break;
    }
    return toReturn;
    
}

}
