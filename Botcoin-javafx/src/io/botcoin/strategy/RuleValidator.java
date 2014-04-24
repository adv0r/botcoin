
package io.botcoin.strategy;

import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.models.Market;
import io.botcoin.utils.Response;
import io.botcoin.utils.Utils;
import static io.botcoin.utils.Utils.LOG_ERR;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class RuleValidator {

//Class Variables




//Constructor



//Methods
    
    //This method contains the logic if a rule can be or can't be
    public static Response validate(Rule r)
    {
        boolean valid = true;
        String message = "";
        
        //First let's make sure that these values are what they are supposed to be -----------------------------------------------------
        if(!r.getDirection().equals(Rule.ABOVE) && !r.getDirection().equals(Rule.BELOW))
        {
            valid = false;
            message += "Wrong direction ("+r.getDirection()+") The direction can be either '"+Rule.BELOW+"' or '"+ Rule.ABOVE+"'\n";
        }
        

        if(!r.getOperation().equals(Rule.BUY) && !r.getOperation().equals(Rule.SELL))
        {
            valid = false;            
            message += "Wrong operation ("+r.getOperation()+") The operation can be either '"+Rule.BUY+"' or '"+ Rule.SELL+"'\n";
        }
        
        if(!r.getCoin().equals(Constant.BTC) && !r.getCoin().equals(Constant.LTC))
        {
            valid = false;            
            message += "Wrong coin ("+r.getCoin()+") The coin can be either '"+Constant.BTC+"' or '"+ Constant.LTC+"'\n";
        }   
        
        if(!r.getCurrency().equals(Constant.USD) && !r.getCurrency().equals(Constant.EUR))
        {
            valid = false;            
            message += "Wrong currency ("+r.getCurrency()+") The currency can be either '"+Constant.EUR+"' or '"+ Constant.USD+"'\n";
        }                 

        if(!r.getMarket().equals(Constant.MTGOX) && !r.getMarket().equals(Constant.BTCE) && !r.getMarket().equals(Constant.BITSTAMP))
        {
            valid = false;            
            message += "Wrong market ("+r.getMarket()+") The market can be either '"+Constant.MTGOX+"' , '"+ Constant.BTCE+"' or '"+Constant.BITSTAMP+"\n";
        }                 

        if(r.getAmount()<=0)
        {
            valid = false;            
            message += "Wrong amount ("+r.getAmount()+") The amount must be >= 0\n";
        }

       if(r.getTarget()<=0)
        {
            valid = false;            
            message += "Wrong target ("+r.getTarget()+") The target must be >= 0\n";
        }
                
                
       
       
       //And now verify that the rule is compliant with the market (coins and currency) --------------------------------------------------

       Market marketObject = null;
       switch(r.getMarket()){
                case Constant.MTGOX : {  marketObject = Global.MtgoxMarket; break; }
                case Constant.BTCE : {  marketObject = Global.BtceMarket;break; }
                case Constant.BITSTAMP : {   marketObject = Global.BitstampMarket;break; }
                default: { Utils.log("market "+ r.getMarket()+" is not supported", LOG_ERR); return new Response("market "+ r.getMarket()+" is not supported",false); }
            }      
       
       
        if(r.getCoin().equals(Constant.BTC) && !marketObject.isSupportBTC())
           {
                valid = false;            
                message += "On "+r.getMarket()+" you can't trade Bitcoins\n";               
           }
        
       if(r.getCoin().equals(Constant.LTC) && !marketObject.isSupportLTC())
           {
                valid = false;            
                message += "On "+r.getMarket()+" you can't trade Litecoins\n";               
           }
       
       if (r.getCurrency().equals(Constant.USD) && !marketObject.isSupportUSD())
          {
                valid = false;            
                message += "On "+r.getMarket()+" you can't set thresholds in USD\n";               
           }
       
       if (r.getCurrency().equals(Constant.EUR) && !marketObject.isSupportEUR())
          {
                valid = false;            
                message += "On "+r.getMarket()+" you can't set thresholds in EUR\n";               
          }
           
       
       return new Response(message,valid); 
    }
}
