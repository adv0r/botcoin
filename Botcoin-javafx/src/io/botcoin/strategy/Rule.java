
package io.botcoin.strategy;

import io.botcoin.api.TradeInterface;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.utils.Utils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */

public class Rule  {
    
    public static final String SELL = "sell";
    public static final String BUY = "buy";
    public static final String BELOW = "below";
    public static final String ABOVE = "above";
    

    //Class Variables
    private SimpleLongProperty id;
    private SimpleStringProperty operation; //bid o ask
    private SimpleDoubleProperty amount; //amount in BTC
    private SimpleStringProperty coin; //BTC or LTC now
    private SimpleStringProperty direction; //above or below
    private SimpleDoubleProperty target; //execute order when passed
    private SimpleStringProperty currency; //the currency in which the treshold is expressed
    private SimpleStringProperty market; //the market
    private SimpleStringProperty comment;
    private SimpleBooleanProperty executed;
    private SimpleBooleanProperty active;
    private SimpleStringProperty executedOrderResponse;
    private SimpleStringProperty executedTimestamp;
    
    private SimpleBooleanProperty visualize; //It indicates whereas visualize it or not!
    private SimpleBooleanProperty seen; //It indicates whereas the user clicked on it


    
    //Constructor
    public Rule(long id,String operation, double amount, String coin, String direction, double target, 
            String currency, String comment,String executedOrderResponse, boolean active, boolean executed, 
            String market, String executedTimestamp, boolean seen, boolean visualize) {
        this.id = new SimpleLongProperty(id);
        this.operation =  new SimpleStringProperty(operation);
        this.amount = new SimpleDoubleProperty(amount);
        this.coin =  new SimpleStringProperty(coin);
        this.direction =  new SimpleStringProperty(direction);
        this.target = new SimpleDoubleProperty(target);
        this.currency =  new SimpleStringProperty(currency);
        this.comment =  new SimpleStringProperty(comment);
        this.executedOrderResponse =  new SimpleStringProperty(executedOrderResponse);
        this.active = new SimpleBooleanProperty(active);
        this.executed = new SimpleBooleanProperty(executed);   
        this.market =  new SimpleStringProperty(market);
        this.executedTimestamp =  new SimpleStringProperty(executedTimestamp);
        this.seen= new SimpleBooleanProperty(seen);
        this.visualize = new SimpleBooleanProperty(visualize);
    }
    

    //Methods

    /**
     * @return the type
     */
    public String getOperation() {
        return operation.get();
    }

    /**
     * @param type the type to set
     */
    
    public void setOperation(String type) {
        this.operation.set(type);
    }

    /**
     * @return the amountBTC
     */
    public double getAmount() {
        return amount.get();
    }

    /**
     * @param amountBTC the amountBTC to set
     */
    public void setAmount(double amountD) {
        this.amount.set(amountD);
    }

    /**
     * @return the direction
     */
    public String getDirection() {
        return direction.get();
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(String directionString) {
        this.direction.set(directionString);
    }

    /**
     * @return the threshold
     */
    public double getTarget() {
        return target.get();
    }

    /**
     * @param threshold the threshold to set
     */
    public void setTarget(double targetD) {
        this.target.set(targetD);
    }

    /**
     * @return the currencyTreshold
     */
    public String getCurrency() {
        return currency.get();
    }

    /**
     * @param currencyTreshold the currencyTreshold to set
     */
    public void setCurrency(String currencyTresholdS) {
        this.currency.set(currencyTresholdS);
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment.get();
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String commentS) {
        this.comment.set(commentS);
    }

        /**
     * @return the comment
     */
    public String getExecutedTimestamp() {
        return executedTimestamp.get();
    }

    /**
     * @param comment the comment to set
     */
    public void setExecutedTimestamp(String executedtimestampS) {
        this.executedTimestamp.set(executedtimestampS);
    }
    
    
    /**
     * @return the executed
     */
    public boolean isExecuted() {
        return executed.get();
    }

    /**
     * @param executed the executed to set
     */
    public void setExecuted(boolean executedB) {
        this.executed.set(executedB);
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean activeB) {
        this.active.set(activeB);
    }

    
    /**
     * @return the visualize
     */
    public boolean isVisualize() {
        return visualize.get();
    }

    /**
     * @param visualize the visualize to set
     */
    public void setVisualize(boolean visualize) {
        this.visualize.set(visualize);
    }

    /**
     * @return the seen
     */
    public boolean isSeen() {
        return seen.get();
    }

    /**
     * @param seen the seen to set
     */
    public void setSeen(boolean seen) {
        this.seen.set(seen);
    }

    /**
     * @return the executedOrderResponse
     */
    public String getExecutedOrderResponse() {
        return executedOrderResponse.get();
    }

    /**
     * @param executedOrderResponse the executedOrderResponse to set
     */
    public void setExecutedOrderResponse(String executedOrderResponseS) {
        this.executedOrderResponse.set(executedOrderResponseS);
    }
    
        /**
     * @return the market
     */
    public String getMarket() {
        return market.get();
    }

    /**
     * @param market the market to set
     */
    public void setMarket(String marketS) {
        market.set(marketS);
    }

    
    
    /**
     * @return the coin
     */
    public String getCoin() {
        return coin.get();
    }

    /**
     * @param coin the coin to set
     */
    public void setCoin(String coinS) {
        coin.set(coinS);
    }
    /**
     * @return the id
     */
    public long getId() {
        return id.get();
    }
    
   
    public boolean needToBeExec(double lastPrice) {
       boolean execute = false;
    
       //Utils.msg(this.toString());
       if(this.isActive()) //If the rule is active
                {
                 if (lastPrice > this.getTarget() && this.getDirection().equals(Rule.ABOVE))                     
                            execute = true;
                 else if (lastPrice < this.getTarget() && this.getDirection().equals(Rule.BELOW))  
                            execute = true;
                 else
                        execute=false;
                }
       return execute;
    }
    
    
    
    public void executeNow(TradeInterface trade)
    {
        Utils.log("Executing rule : "+this.toString(), Utils.LOG_TRANSACTION);

        String response ="";
        /*Balance balanceBefore = Global.tradeGox.getBalance();
        
        double balanceBTC = balanceBefore.getBitcoins();
        double balanceUSD = balanceBefore.getDollars();
        double balanceEUR = balanceBefore.getEuros();
        
        Utils.log("Current balance : "+balanceBTC+"BTC ;  "+ balanceUSD+"$",Utils.LOG_MID);
        */

        if(!Settings.TEST)  /**DANGER ZONE - BELOW THE ORDER GETS ACTUALLY EXECUTED **/
        {
            if (this.getOperation().equals(Rule.BUY))
            {
               if(this.getCoin().equals(Constant.BTC))
                    response = trade.buyBTC(getAmount());
               else if(this.getCoin().equals(Constant.LTC))
                    response = trade.buyLTC(getAmount());

               Utils.log(response,Utils.LOG_TRANSACTION);
            }
            else if (this.getOperation().equals(Rule.SELL))
            {
                if(this.getCoin().equals(Constant.BTC))            
                    Utils.log(trade.sellBTC(getAmount()),Utils.LOG_TRANSACTION);
                else if(this.getCoin().equals(Constant.LTC))            
                    Utils.log(trade.sellLTC(getAmount()),Utils.LOG_TRANSACTION);
            }
            else
                Utils.log("This should not be printed err: -43243",Utils.LOG_ERR);
        }
        
        this.setExecuted(true);
        this.setActive(false);
        this.setExecutedOrderResponse(response);
        this.setExecutedTimestamp(Utils.getTimestamp());
 
        updateStrategy();
    }
    
    public String toStringComplete()
    {
           return " id : " + this.getId() +
                  " | op : " + this.getOperation() +                   
                  " | am : " + this.getAmount() +
                  " | coin : " + this.getCoin() +
                  " | dir : " + this.getDirection()+
                  " | tar : "+ this.getTarget()+
                  " | act :"+ this.isActive()+
                  " | exec : "+ this.isExecuted() +
                  " | com : " + this.getComment() +
                  " | market : " + this.getMarket() +
                  " | resp: " + this.getExecutedOrderResponse() +
                  " | seen: " + this.isSeen()+
                  " | visualize: " + this.isVisualize()+
                  " | timestamp: " + this.getExecutedTimestamp();                  
    }
    
    public String toString()
    {
        return this.getOperation()+" "+this.getAmount()+" "+this.getCoin()+
                " on "+this.getMarket()+" if price goes "+this.getDirection()+
                " "+this.getTarget()+" "+this.getCurrency();
    }
    
    private void updateStrategy()
    {
        Global.strategy.writeStrategyToFile(Settings.STRATEGY_PATH);
    }
   


}
