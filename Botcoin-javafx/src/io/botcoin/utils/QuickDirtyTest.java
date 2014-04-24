
package io.botcoin.utils;

import io.botcoin.UI.DashboardController;
import io.botcoin.UI.dialogs.ApiDialog;
import io.botcoin.UI.dialogs.NewRuleDialogController;
import io.botcoin.api.ApiKeys;
import io.botcoin.api.BitStampTrading;
import io.botcoin.core.App;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.models.Balance;
import io.botcoin.models.Market;
import io.botcoin.strategy.Rule;
import io.botcoin.strategy.Strategy;
import io.botcoin.task.ConnectionThread;
import io.botcoin.task.ScheduledOperations;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class QuickDirtyTest {

//Methods
    public static void main(String[] args)
    {
        setup();
        run();
        System.exit(0);
    }
    
    private static void setup() {
            Global.priceCheckerOn = false; //the bot will be stopped by default

            try {
                  MyLogger.setup();
            } 
            catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
             }
            Utils.LOGGER.setLevel(Level.INFO);

            System.setProperty("javax.net.ssl.trustStore",Settings.KEYSTORE_PATH);
            System.setProperty("javax.net.ssl.trustStorePassword",Settings.KEYSTORE_PWD);
            
            Global.MtgoxMarket = new Market(Constant.MTGOX, false, false, true, false, true, true);
            Global.BtceMarket = new Market(Constant.BTCE, false, false, true, true, true, false);
            Global.BitstampMarket = new Market(Constant.BITSTAMP, false, false, true, false, true, false);
            Global.connectionChecker = new ConnectionThread();
            Global.connectionThread = new Thread(Global.connectionChecker);
            //setPriceCheker(new ScheduledOperations(Settings.PRICE_CHECK_INTERVAL));
            Global.strategy = new Strategy(Settings.STRATEGY_PATH);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(QuickDirtyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
          
    }
    
    public static void run()
    {
        //bitstampApiTest();
        
        modifyRuleTest();
        
        //New  Dashboard testing ---------------------------------------------------------------------------------------

    
       /* Utils.log("btcevalid? "+Global.btceKeyValid,Utils.LOG_LOW);
        Utils.log("bitstamp? "+Global.bitStampKeyValid,Utils.LOG_LOW);
        Utils.log("mtgx? 0"+Global.mtGoxKeyValid,Utils.LOG_LOW);
        
        Utils.log(Global.tradeBtce.getBalance().getBitcoins(),Utils.LOG_LOW);
        Utils.log(Global.keys_btce.getApiKey() +"  "+Global.keys_btce.getPrivateKey(),Utils.LOG_LOW);
        * */
        
        /*
        ApiKeys keys1 = ApiKeys.loadKeysFromFile(Global.passphrase, Constant.MTGOX); 
        ApiKeys keys2 = ApiKeys.loadKeysFromFile(Global.passphrase, Constant.BTCE); 
        //ApiKeys keys3 = ApiKeys.loadKeysFromFile(Global.passphrase, Constant.BITSTAMP); 

        Utils.log("mtg "+ keys1.getApiKey() +"  "+keys1.getPrivateKey(),Utils.LOG_LOW);
        Utils.log("btce "+ keys2.getApiKey() +"  "+keys2.getPrivateKey(),Utils.LOG_LOW);
        //Utils.log("bitstamp"+ keys3.getApiKey() +"  "+keys3.getPrivateKey(),Utils.LOG_LOW);
        
       
        Utils.log("fromfile mtg api = "+Utils.decode(Settings.API_KEY_PATH_MTGOX, Global.passphrase));
        Utils.log("fromfile mtg secret = "+ Utils.decode(Settings.SECRET_KEY_PATH_MTGOX, Global.passphrase) );
      
        
        Utils.log("fromfile btce api = "+Utils.decode(Settings.API_KEY_PATH_BTCE, Global.passphrase));
        Utils.log("fromfile btce secret = "+Utils.decode(Settings.SECRET_KEY_PATH_BTCE, Global.passphrase));
        */

        //----TEST invalid rules insertions 
        /*
        Global.strategy.addRule(Rule.SELL, 2, Constant.LTC, Rule.ABOVE, 140, Constant.USD, "Sell 2 above 140  Should be exec", "", true, false,Constant.MTGOX);
        Global.strategy.addRule(Rule.SELL, 2, Constant.LTC, Rule.ABOVE, 140, Constant.EUR, "Sell 2 above 140  Should be exec", "", true, false,Constant.BTCE);
        */

     // Test Strategy  -------
        //Global.strategy.addRule(Rule.SELL, 2, Constant.BTC, Rule.ABOVE, 140, Constant.USD, "Sell 2 above 140  Should be exec", "", true, false,Constant.MTGOX);
        //Global.strategy.addRule(Rule.BUY, 12,Constant.BTC, Rule.BELOW, 191, Constant.USD, "Buy 12 above 152$. Should be exec", "", true, false,Constant.BTCE);
    
       // Global.strategy.addRule(Rule.BUY, 12,Constant.LTC, Rule.BELOW, 200, Constant.EUR, "Buy 12 below 200$. Should NOT be exec", "", false, false,Constant.BITSTAMP);   
        //Global.strategy.delRuleById(6);
       
      //  Global.strategy.addRule(Rule.BUY, 1, Constant.LTC,Rule.BELOW, 200, Constant.USD, "newly added", "", true, false,Constant.MTGOX);   
        
        
      // Test Withdraw mtgox  -------
      //String mirzaMtgWallet = "155KZtTAr2sb527XpX79EHTWMzAmXLrJzt";
      //Global.tradeGox.withdrawBTC(0.001, mirzaMtgWallet);
      
        
      //Test load from file  
      //ApiKeys provaApi = ApiKeys.loadKeysFromFile("", "", "");
      

      //Example ----- BTCE PERMISSIONS
      /*
      ApiPermissions permissions = Global.tradeBtce.getPermissions();
      if (permissions.isValid_keys())
      {
           System.out.println(permissions.toString());
      }
      
      else 
          System.out.println("Keys are not valid!");
      */
      
      //Example ----- BTCE LAST price

      /* System.out.println("1 BTC = "+Global.tradeBtce.getLastPriceUSD("BTC")+" $ \n"
              + "1 BTC = "+Global.tradeBtce.getLastPriceEUR("BTC")+"€ \n"
              + "1 LTC = "+Global.tradeBtce.getLastPriceUSD("LTC")+" $");*/
      
      //Example ----- BTCE orders
      //Test buy btce
      //System.out.println(Global.tradeBtce.buyLTC(0.2)); 
      //System.out.println(Global.tradeBtce.buyBTC(0.01)); 
      
      //Test sell btce
      //System.out.println(Global.tradeBtce.sellBTC(0.018)); 
      //System.out.println(Global.tradeBtce.sellLTC(0.4));
      
      
      /*boolean[] r1 = ApiKeys.getMtGoxApiPermissions(Global.keys.getPrivateKey(), Global.keys.getApiKey());
      boolean[] r2 = ApiKeys.getMtGoxApiPermissions("asiudhsaudhaduhsdhadsasada", "asdkaodoskadaodpksapdkpsoakopskodpsskdapo");
      
      System.out.println("Should be all true \n"+ r1[0]+"\n"+
              r1[1] +"\n "+
              r1[2] +"\n "+
              r1[3] +"\n "+
              r1[4] +"\n "+
              r1[5] +"\n ");
      
      System.out.println("Should be all false \n"+ r2[0]+"\n"+
              r2[1] +"\n "+
              r2[2] +"\n "+
              r2[3] +"\n "+
              r2[4] +"\n "+
              r2[5] +"\n ");
     */


     
     //Run the connection check thread
     /*ConnectionThread t = new ConnectionThread();
     Thread d = new Thread(t);*/
      
           
     // new LoginDialog(primaryStage).show();
      
     //new NewDashboard(primaryStage, app); 
      
     //------------ -------- TEST BITSTAMP--------- -------- --------   
     //System.out.println("USD: " +Global.tradeBitstamp.getLastPriceUSD(""));
     //System.out.println("EUR:"+Global.tradeBitstamp.getLastPriceEUR(""));
     //Balance bitstampBalance = Global.tradeBitstamp.getBalance();
     //System.out.println("USD: "+bitstampBalance.getDollars()+"\nBTC: "+bitstampBalance.getBitcoins());
     //System.out.println("Valid credentials : "+Global.tradeBitstamp.getPermissions().isValid_keys());
     //System.out.println("Bitstamp Test sell - Order id : "+Global.tradeBitstamp.sellBTC(0.015));
    
     //System.out.println("Bitstamp Test buy - Order id : "+Global.tradeBitstamp.buyBTC(0.013));
        
        
     //------------ -------- TEST Api KEY setup (one by one)--------- -------- --------   
     /*  new ApiDialog(new Stage(),false, Constant.MTGOX).show();
       new ApiDialog(new Stage(),false, Constant.BTCE).show();
       new ApiDialog(new Stage(),false, Constant.BITSTAMP).show();
     */
        
     //------------ -------- TEST showing a fmlx view--------- -------- --------   
     /*
        Parent root = null;
        try {
          //  Parent page = FXMLLoader.<Parent>load(MainWindowController.class.getResource("main.fxml").toExternalForm());
            root = FXMLLoader.load(NewRuleDialogController.class.getResource("NewRuleDialog.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(QuickDirtyTest.class.getName()).log(Level.SEVERE, null, ex);
            }
         Stage stage = new Stage();
         stage.setTitle("Api keys setup");
         stage.setScene(new Scene(root, 500, 300));
         stage.show();
      */
        
        
        
        
    }

    public static void modifyRuleTest()
    {
        Rule r = Global.strategy.getRuleByID(1);
        System.out.println(r.toStringComplete());

        r.setExecutedTimestamp(Utils.getTimestamp());
        r.setAmount(1.337);
        
        System.out.println(r.toStringComplete());

        Global.strategy.writeStrategyToFile(Settings.STRATEGY_PATH);
        
        r = Global.strategy.getRuleByID(1);
        System.out.println(r.toStringComplete());

    }
    
    
    public static void bitstampApiTest()
    {
        //Example ----- BITSTAMP BALANCE  

        ApiKeys bitstampkeys = new ApiKeys("upGHYUK2vpF65jRto8GUVjWPN9jv23Xa",
              "rW8lkDDJrTfKufXS4B6DuV2BMliRDknG",
              "22712");
        
        validateKeys(Constant.BITSTAMP, bitstampkeys);

                 
        BitStampTrading trade = new BitStampTrading(bitstampkeys);
      
       /* Balance balance = trade.getBalance();
        double balanceBTC = balance.getBitcoins();
        double balanceUSD = balance.getDollars();

            System.out.println("Bistsamp balance :"+ balanceBTC+ " ฿\n"+
                balanceUSD+"$\n");  */
            
        //System.out.println("Ticker= "+trade.getLastPriceEUR("BTC"));
        
        
        System.out.println("Bitstamp Test sell - Order id : "+trade.sellBTC(0.015));
            
    }
    
    public static void validateKeys(String market, ApiKeys keys)
    {
         
        String apiKey =  keys.getApiKey();
        String secretKey = keys.getPrivateKey();
        String clientID = keys.getClientID();
        String apiValidMessage = ApiKeys.validate(secretKey, apiKey, clientID,market);
   
        if(apiValidMessage.equals(ApiKeys.VALID_KEYS))
                  {
                    //Keys are valid, proceed normally
                    System.out.println(market+" credentials are valid."); 
                    Utils.initMarket(Utils.getMarketFromName(market), keys);
                  }   
                  else
                  {
                      //keys are invalid, ask to reset or skip
                      System.out.println(market+" credentials not valid.");                     
                  }
    }
}
