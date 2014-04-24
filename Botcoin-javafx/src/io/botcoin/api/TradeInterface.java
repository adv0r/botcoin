/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.api;

import io.botcoin.models.Balance;

/**
 *
 * @author advanced
 */
public interface TradeInterface{
    //Constructor
    
    //Methods
    
   
    /**
    * Returns an array with the user's balance in different currencies. 
    * 
    * The first element of the array is the amount in BTC,
    * the second element in USD and the third in EUR.
    * @return      a Balance object with the current balance
    */
    public Balance getBalance() ;
    
   
    /**
    * Returns an array with the last price in USD and EUR. 
    * @param  coin  the type of coin you want to know the price ("BTC" "LTC")
    * @return      a double value with the current price of 1 coin in USD
    */
    public double getLastPriceUSD(String coin);
    
    /**
    * Returns an array with the last price in USD and EUR. 
    * @param  coin  the type of coin you want to know the price ("BTC" "LTC")
    * @return      a double value with the current price of 1 coin in EUR
    */
    public double getLastPriceEUR(String coin);
    
   
    /**
    * Returns the lag of the trading engine. 
    * @return      a string with the lag
    */
    public String getLag();
    
    /**
    * Withdraws an amount of BTC from the user's mtg account to a BTC address.
    * @param  amount  the amount of BTC to withdraw (min 0.1)
    * @param  dest_address  the address of the BTC wallet
    * @return      the transaction id (if success), (???) if failed //TODO
    */
    public boolean withdrawBTC(double amount, String dest_address);
    
    /**
    * Sells BTC at market price
    * @param  amount  the amount of BTC to sell (min 0.1)
    * @return  the transaction id (if success), (???) if failed //TODO
    */
    public String sellBTC(double amount);

    /**
    * Sells BTC at market price
    * @param  amount  the amount of BTC to buy (min 0.1)
    * @return  the transaction id (if success), (???) if failed //TODO
    */
    public String buyBTC(double amount);
    
        /**
    * Sells BTC at market price
    * @param  amount  the amount of LTC to sell (min 0.1)
    * @return  the transaction id (if success), (???) if failed //TODO
    */
    public String sellLTC(double amount);

    /**
    * Sells BTC at market price
    * @param  amount  the amount of LTC to buy (min 0.1)
    * @return  the transaction id (if success), (???) if failed //TODO
    */
    public String buyLTC(double amount);
    
    /**
    * Returns the permissions for the given api keys
    * @return an ApiPermission object. See the implementation for details
    */
    public ApiPermissions getPermissions();
}
