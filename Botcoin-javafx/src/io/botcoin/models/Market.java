
package io.botcoin.models;

import io.botcoin.api.ApiKeys;
import io.botcoin.api.TradeInterface;
import io.botcoin.global.Constant;
import io.botcoin.global.Settings;
import io.botcoin.utils.Utils;
import static io.botcoin.utils.Utils.LOG_ERR;
import java.text.NumberFormat;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class Market {

NumberFormat formatter = NumberFormat.getInstance();
    
    
//Class Variables
private String name;


private boolean keysValid;

private ApiKeys keys;

private TradeInterface trade;

private boolean connected;

private Balance balance;
    
   
private boolean supportBTC;
private boolean supportLTC;
private boolean supportUSD;
private boolean supportEUR;

private double lastBTC_USD;
private double lastBTC_EUR;
private double lastLTC_USD;

private String PATH_TO_SECRET;
private String PATH_TO_API;
private String PATH_TO_CLIENTID;



//Constructor

    public Market(String name, boolean keysValid, boolean connected, boolean supportBTC, boolean supportLTC, boolean supportUSD, boolean supportEUR) {
        this.name = name;
        this.keysValid = keysValid;
        this.connected = connected;
        this.supportBTC = supportBTC;
        this.supportLTC = supportLTC;
        this.supportUSD = supportUSD;
        this.supportEUR = supportEUR;
        
        lastBTC_EUR = 0;
        lastBTC_USD = 0;
        lastLTC_USD = 0;
        

        
        switch(name){
            case Constant.MTGOX : { PATH_TO_SECRET = Settings.HIDDEN_FOLDER+".kf8fMLgF6"; PATH_TO_API= Settings.HIDDEN_FOLDER+".uzIDF99Lm6";  break; }
            case Constant.BTCE : { PATH_TO_SECRET = Settings.HIDDEN_FOLDER+".br7VcHLgP2"; PATH_TO_API= Settings.HIDDEN_FOLDER+".onH008b4XX";  break; }
            case Constant.BITSTAMP : { PATH_TO_SECRET = Settings.HIDDEN_FOLDER+".zU1zdyu215"; PATH_TO_API= Settings.HIDDEN_FOLDER+".OthcFZBj8A"; PATH_TO_CLIENTID = Settings.HIDDEN_FOLDER+".AS8dH0rwZ"; break; }
            default: { Utils.log("market "+ name+" is not supported", LOG_ERR); break ;  }
          }
        
    }


//Methods

    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the keysValid
     */
    public boolean isKeysValid() {
        return keysValid;
    }

    /**
     * @param keysValid the keysValid to set
     */
    public void setKeysValid(boolean keysValid) {
        this.keysValid = keysValid;
    }

    /**
     * @return the keys
     */
    public ApiKeys getKeys() {
        return keys;
    }

    /**
     * @param keys the keys to set
     */
    public void setKeys(ApiKeys keys) {
        this.keys = keys;
    }

    /**
     * @return the trade
     */
    public TradeInterface getTrade() {
        return trade;
    }

    /**
     * @param trade the trade to set
     */
    public void setTrade(TradeInterface trade) {
        this.trade = trade;
    }

    /**
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @param connected the connected to set
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * @return the supportBTC
     */
    public boolean isSupportBTC() {
        return supportBTC;
    }

    /**
     * @param supportBTC the supportBTC to set
     */
    public void setSupportBTC(boolean supportBTC) {
        this.supportBTC = supportBTC;
    }

    /**
     * @return the supportLTC
     */
    public boolean isSupportLTC() {
        return supportLTC;
    }

    /**
     * @param supportLTC the supportLTC to set
     */
    public void setSupportLTC(boolean supportLTC) {
        this.supportLTC = supportLTC;
    }

    /**
     * @return the supportUSD
     */
    public boolean isSupportUSD() {
        return supportUSD;
    }

    /**
     * @param supportUSD the supportUSD to set
     */
    public void setSupportUSD(boolean supportUSD) {
        this.supportUSD = supportUSD;
    }

    /**
     * @return the supportEUR
     */
    public boolean isSupportEUR() {
        return supportEUR;
    }

    /**
     * @param supportEUR the supportEUR to set
     */
    public void setSupportEUR(boolean supportEUR) {
        this.supportEUR = supportEUR;
    }

    /**
     * @return the lastBTC_USD
     */
    public double getLastBTC_USD() {
        return lastBTC_USD;
    }

    /**
     * @param lastBTC_USD the lastBTC_USD to set
     */
    public void setLastBTC_USD(double lastBTC_USD) {
        this.lastBTC_USD = Utils.round(lastBTC_USD,2);
    }

    /**
     * @return the lastBTC_EUR
     */
    public double getLastBTC_EUR() {
        return lastBTC_EUR;
    }

    /**
     * @param lastBTC_EUR the lastBTC_EUR to set
     */
    public void setLastBTC_EUR(double lastBTC_EUR) {
        this.lastBTC_EUR = Utils.round(lastBTC_EUR,2);
    }


    /**
     * @return the lastLTC_USD
     */
    public double getLastLTC_USD() {
        return lastLTC_USD;
    }

    /**
     * @param lastLTC_USD the lastLTC_USD to set
     */
    public void setLastLTC_USD(double lastLTC_USD) {
        this.lastLTC_USD = Utils.round(lastLTC_USD,3);
    }

    /**
     * @return the balance
     */
    public Balance getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    /**
     * @return the PATH_TO_SECRET
     */
    public String getPATH_TO_SECRET() {
        return PATH_TO_SECRET;
    }

    /**
     * @return the PATH_TO_CLIENTID
     */
    public String getPATH_TO_CLIENTID() {
        return PATH_TO_CLIENTID;
    }
    
    /**
     * @param PATH_TO_SECRET the PATH_TO_SECRET to set
     */
    public void setPATH_TO_SECRET(String PATH_TO_SECRET) {
        this.PATH_TO_SECRET = PATH_TO_SECRET;
    }

    /**
     * @return the PATH_TO_API
     */
    public String getPATH_TO_API() {
        return PATH_TO_API;
    }

    /**
     * @param PATH_TO_API the PATH_TO_API to set
     */
    public void setPATH_TO_API(String PATH_TO_API) {
        this.PATH_TO_API = PATH_TO_API;
    }
}

