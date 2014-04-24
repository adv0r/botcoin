
package io.botcoin.task;

import io.botcoin.UI.DashboardController;
import io.botcoin.UI.DashboardMarketsController;
import io.botcoin.UI.DashboardOptionsController;
import io.botcoin.models.Balance;
import io.botcoin.api.Tickers;
import io.botcoin.api.TradeInterface;
import io.botcoin.global.Constant;
import io.botcoin.strategy.Rule;
import io.botcoin.global.Global;
import io.botcoin.models.Market;
import io.botcoin.utils.Utils;
import static io.botcoin.utils.Utils.LOG_ERR;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class ScheduledOperations {

//Class Variables
protected int interval;

private Timer timer;
private PriceCheckerTask task ;

private boolean running;
//Constructor
public ScheduledOperations(int interval) 
{
    this.running = false;
    this.interval = interval;
}

    /**
     * @return the isRunning
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @param isRunning the isRunning to set
     */
    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }


    //Methods
    public void toggle(){
    String statusString="";
    if(!isRunning())
    {
        //start/resume
        statusString="Running!";
        timer = new Timer();
        task = new PriceCheckerTask();
        timer.schedule(task, 0, getInterval()*1000);   
        setRunning(true);
    }
    else
    {
        //pause
        timer.cancel();
        statusString="Stopped!";
        setRunning(false);
    }
        Utils.log(statusString,Utils.LOG_LOW); //TODO here!
}

    /**
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

 
    
    //Inner class for task
    protected class PriceCheckerTask extends TimerTask
    {

        @Override
        public void run() 
        {
            Utils.log("\n\n\n\n-----#####################################---------------- Executing Task ",Utils.LOG_LOW);
            updateLastPrices();
            if(Global.priceCheckerOn)
            {
                updateBalances();
                //updateLags();
                if(Global.executeOrdersOn)
                    tryToExecuteOrders();
            }
            
        }
 
       
        private void updatePrice(Market market)
        {
            DashboardController controller = (DashboardController) Global.getController(Constant.MAIN_DASHBOARD);
            if(market.isConnected())
            {
                if(market.isSupportUSD() && market.isSupportBTC())
                {
                    //market.setLastBTC_USD(market.getTrade().getLastPriceUSD(Constant.BTC)); //OLD TOREMOVE
                    market.setLastBTC_USD(Tickers.getLastPrice(market.getName(), Constant.BTC, Constant.USD));
                    Utils.log("["+market.getName()+"] Last price of 1 "+Constant.BTC+": "+market.getLastBTC_USD()+" "+Constant.USD);
                    controller.refreshPrice(market.getName(),Constant.BTC , market.getLastBTC_USD()+" USD",Constant.USD);
                }
                if(market.isSupportEUR() && market.isSupportBTC())
                {
                    //market.setLastBTC_EUR(market.getTrade().getLastPriceEUR(Constant.BTC));
                    market.setLastBTC_EUR(Tickers.getLastPrice(market.getName(), Constant.BTC, Constant.EUR));
                    Utils.log("["+market.getName()+"] Last price of 1 "+Constant.BTC+": "+market.getLastBTC_EUR()+" "+Constant.EUR);   
                    controller.refreshPrice(market.getName(),Constant.BTC , market.getLastBTC_EUR()+" EUR",Constant.EUR); //TODO
                }                
                if(market.isSupportUSD() && market.isSupportLTC())
                {
                    //market.setLastLTC_USD(market.getTrade().getLastPriceUSD(Constant.LTC));
                    market.setLastLTC_USD(Tickers.getLastPrice(market.getName(), Constant.LTC, Constant.USD));
                    Utils.log("["+market.getName()+"] Last price of 1 "+Constant.LTC+": "+market.getLastLTC_USD()+" "+Constant.USD);
                    controller.refreshPrice(market.getName(),Constant.LTC , market.getLastLTC_USD()+" USD",Constant.USD);                    
                }                
               
            }
              else
                Utils.log(market.getName()+" offline : Price checker cannot update values.", Utils.LOG_ERR);
          
        }
        private void updateLastPrices() {
            updatePrice(Global.MtgoxMarket);  
            updatePrice(Global.BtceMarket);
            updatePrice(Global.BitstampMarket);
         
        }
        
        
        
        private void updateBalances() {  
            if(Global.MtgoxMarket.isKeysValid())
                updateBalance(Global.MtgoxMarket);
            if(Global.BtceMarket.isKeysValid())
                updateBalance(Global.BtceMarket);
            if(Global.BitstampMarket.isKeysValid())
                updateBalance(Global.BitstampMarket);     
        }


        private void tryToExecuteOrders() {
            ArrayList<Rule> activeRules = Global.strategy.getListOfActiveRules();
            Utils.log("Active rules("+activeRules.size()+") : "+Global.strategy.getIdAndComment(activeRules));

            for(int j=0; j< Global.strategy.getRulesList().size(); j++)
            { 
               Rule tempRule = Global.strategy.getRulesList().get(j);
               
               if (tempRule.needToBeExec(Utils.getLastSavedPrice(tempRule.getCurrency(),tempRule.getCoin(), tempRule.getMarket())))
                   tempRule.executeNow(Utils.getMarketFromName(tempRule.getMarket()).getTrade());   
            }
        }

        private void updateBalance(Market market) {
            DashboardMarketsController controller = (DashboardMarketsController) Global.getController(Constant.MARKETS);
            if(market.isKeysValid())
            {              
                market.setBalance(market.getTrade().getBalance()); //
                Utils.log("["+market.getName()+"] Balance updated :"+market.getBalance().toString());  
                controller.refreshBalance(market);
            }
          
        }

        private void updateLags() {
            DashboardMarketsController controller = (DashboardMarketsController) Global.getController(Constant.MARKETS);
            controller.refreshLag(Global.MtgoxMarket.getTrade().getLag());                    
        }
         
    }
}
