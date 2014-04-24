/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.UI;


import com.fxexperience.javafx.animation.PulseTransition;

import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.models.Balance;
import io.botcoin.models.Market;
import io.botcoin.utils.Utils;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */


public class DashboardMarketsController implements Initializable {
    
   @FXML //  fx:id="mtgoxBtcImg"
   private ImageView mtgoxBtcImg;
   
   
   @FXML //  fx:id="mtgoxEurg"
   private ImageView mtgoxEurImg;
   
   @FXML //  fx:id="mtgoxUsdImg"
   private ImageView mtgoxUsdImg;
      
   @FXML //  fx:id="btceBtcImg"
   private ImageView btceBtcImg;
         
   @FXML //  fx:id="btceLtcImg"
   private ImageView btceLtcImg;
            
   @FXML //  fx:id="btceUsdImg"
   private ImageView btceUsdImg;
   
   @FXML //  fx:id="bitstampUsdImg"
   private ImageView bitstampUsdImg;
                  
   @FXML //  fx:id="bitstampBtcImg"
   private ImageView bitstampBtcImg;
                     
                     
   @FXML //  fx:id="mtgoxText"
   private Text mtgoxText;
   
   @FXML //  fx:id="btceText"   
   private Text btceText;
   
   @FXML //  fx:id="bitstampText"   
   private Text bitstampText;
    
   @FXML //  fx:id="goxBalanceText"
   private Text goxBalanceText;
   
   @FXML //  fx:id="btceBalanceText"   
   private Text btceBalanceText;
   
   @FXML //  fx:id="bitstampBalanceText"   
   private Text bitstampBalanceText;
   
    @FXML //  fx:id="balanceMtgoxBTC"
    private Text balanceMtgoxBTC; // Value injected by FXMLLoader
         
    @FXML //  fx:id="balanceMtgoxUSD"
    private Text balanceMtgoxUSD; // Value injected by FXMLLoader
         
    @FXML //  fx:id="balanceMtgoxEUR"
    private Text balanceMtgoxEUR; // Value injected by FXMLLoader
        
    @FXML //  fx:id="lagMtgox"
    private Text lagMtgox; // Value injected by FXMLLoader

   
    @FXML //  fx:id="balanceBtceBTC"
    private Text balanceBtceBTC; // Value injected by FXMLLoader
    
    @FXML //  fx:id="balanceBtceUSD"
    private Text balanceBtceUSD; // Value injected by FXMLLoader
    
    @FXML //  fx:id="balanceBtceLTC"
    private Text balanceBtceLTC; // Value injected by FXMLLoader
    
    
    @FXML //  fx:id="balanceBitstampBTC"
    private Text balanceBitstampBTC; // Value injected by FXMLLoader
    
    @FXML //  fx:id="balanceBitstampUSD"
    private Text balanceBitstampUSD; // Value injected by FXMLLoader
    
      //Browser variables
    private WebView browser;
    private WebEngine webEngine;
    private Stage webStage;
    private Scene webScene;
    int webHeight, webWidth , webOffsety;
    
    
    //Last prices---------------------
    //MTG
    //Last price in EUR
  
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert balanceBitstampBTC != null : "fx:id=\"balanceBitstampBTC\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert balanceBitstampUSD != null : "fx:id=\"balanceBitstampUSD\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert balanceBtceBTC != null : "fx:id=\"balanceBtceBTC\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert balanceBtceLTC != null : "fx:id=\"balanceBtceLTC\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert balanceBtceUSD != null : "fx:id=\"balanceBtceUSD\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert balanceMtgoxBTC != null : "fx:id=\"balanceMtgoxBTC\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert balanceMtgoxEUR != null : "fx:id=\"balanceMtgoxEUR\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert balanceMtgoxUSD != null : "fx:id=\"balanceMtgoxUSD\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
        assert lagMtgox != null : "fx:id=\"lagMtgox\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
            
        activateMarket(Constant.MTGOX, Global.MtgoxMarket.isKeysValid());
        activateMarket(Constant.BTCE, Global.BtceMarket.isKeysValid());
        activateMarket(Constant.BITSTAMP, Global.BitstampMarket.isKeysValid());
   
        preLoadBrowser();

    }   
    
    
    public void mouseEnteredChangableTextField(MouseEvent me)
    {

        ((Node) me.getSource()).setCursor(Cursor.OPEN_HAND);
    }
    
    public void mouseExitChangableTextField(MouseEvent me)
    {
        ((Node) me.getSource()).setCursor(Cursor.DEFAULT); 
    }
    
    public void refreshBalance(Market market)
    {
      Balance newBalance = market.getBalance();
      NumberFormat formatter = NumberFormat.getInstance();
      switch(market.getName())
        {
            case Constant.MTGOX : {
                refreshField(balanceMtgoxBTC , Utils.round(newBalance.getBitcoins(), 6)+"");
                if(market.getBalance().haveDollars())
                    refreshField(balanceMtgoxUSD , formatter.format(Utils.round(newBalance.getDollars(),2))+"");
                if(market.getBalance().haveEuros())
                    refreshField(balanceMtgoxEUR , formatter.format(Utils.round(newBalance.getEuros(),2))+"");
                break;
            }
            case Constant.BTCE : {  
                refreshField(balanceBtceBTC ,  Utils.round(newBalance.getBitcoins(),6)+"");
                if(market.getBalance().haveDollars())
                    refreshField(balanceBtceUSD , formatter.format(Utils.round(newBalance.getDollars(),2))+"");
                if(market.getBalance().haveLitecoins())
                    refreshField(balanceBtceLTC , Utils.round(newBalance.getLitecoins(),4)+"");
                break;
            }
            case Constant.BITSTAMP : {   
                refreshField(balanceBitstampBTC , Utils.round(newBalance.getBitcoins(),6)+"");
                if(market.getBalance().haveDollars())
                    refreshField(balanceBitstampUSD , formatter.format(Utils.round(newBalance.getDollars(),2))+"");
                break;
            }              
            default : break;               
        }
    }

    
      
    public void refreshField(Text textField,String value)
    {
        if(!textField.getText().contains(value))
            {
                textField.setText(value);
                new PulseTransition(textField).play();
            }
    }
    
    public void refreshLag(String lagString)
    {
        refreshField(lagMtgox,lagString);
    }
    
    
    public void activateMarket(String market,boolean active)
    {
     double opacity;
     if (active)
         opacity = 0.8;
     else
         opacity = 0.2;
     switch(market)
     {
         case Constant.MTGOX:
         {
             balanceMtgoxBTC.setOpacity(opacity);
             balanceMtgoxUSD.setOpacity(opacity);
             balanceMtgoxEUR.setOpacity(opacity);
             mtgoxBtcImg.setOpacity(opacity);
             mtgoxEurImg.setOpacity(opacity);
             mtgoxUsdImg.setOpacity(opacity);
             
             goxBalanceText.setOpacity(opacity);
             mtgoxText.setOpacity(opacity);
             break;
         }
         case Constant.BTCE: 
         {
             balanceBtceBTC.setOpacity(opacity);
             balanceBtceUSD.setOpacity(opacity);
             balanceBtceLTC.setOpacity(opacity);
             btceText.setOpacity(opacity);
             btceBalanceText.setOpacity(opacity);
             btceBtcImg.setOpacity(opacity);
             btceLtcImg.setOpacity(opacity);
             btceUsdImg.setOpacity(opacity);
             break;
         }
         case Constant.BITSTAMP: 
         {
             balanceBitstampBTC.setOpacity(opacity);
             balanceBitstampUSD.setOpacity(opacity);
             bitstampText.setOpacity(opacity);
             bitstampBalanceText.setOpacity(opacity);
             bitstampBtcImg.setOpacity(opacity);
             bitstampUsdImg.setOpacity(opacity);
             break;
         }
     }
    }
        
    private void preLoadBrowser() {
           webHeight=393;
           webWidth=900;
           webOffsety = 10;

           browser = new WebView();
           webEngine = browser.getEngine();
           webEngine.load("http://bitcoinity.org/markets");

           webStage = new Stage(StageStyle.UNDECORATED);
           webStage.setTitle(Settings.APP_TITLE + " - Bitcoin market overview");

           webScene = new Scene(new BitcoinityBrowser(),webWidth,webHeight, Color.web("#666970"));
           webStage.setScene(webScene);
           webScene.getStylesheets().add(Settings.CSS_FOLDER_PATH+"browser.css");       
    }  


        
    public void toggleBitcoinity()
    {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        webStage.setX(10);
        webStage.setY(10);
        //TODO do
        //webStage.setX(Global.app.primaryStage.getX()-((webWidth - Global.app.primaryStage.getWidth())/2));
        //webStage.setY(Global.app.primaryStage.getY()+Global.app.primaryStage.getHeight()+webOffsety);
        if(!webStage.isShowing())
             webStage.show();
        else 
           webStage.hide();
    }
        
}
