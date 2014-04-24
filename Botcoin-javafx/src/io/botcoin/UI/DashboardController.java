/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.UI;

import com.fxexperience.javafx.animation.PulseTransition;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.utils.Utils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Dialogs;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class DashboardController implements Initializable {

        public static final String DASHBOARD = "Dashboard.fxml";
        public static final String DASHBOARD_MARKETS = "DashboardMarkets.fxml";
        public static final String DASHBOARD_OPTIONS = "DashboardOptions.fxml";
        public static final String DASHBOARD_STATUS = "DashboardStatus.fxml";
        public static final String DASHBOARD_STRATEGY = "DashboardStrategy.fxml";
        
        private FXMLLoader optionsController ;
        private FXMLLoader statusController ;
        private FXMLLoader strategyController ;
        private FXMLLoader marketsController ;
        
        private Parent optionsDash;
        private Parent statusDash;
        private Parent marketsDash;
        private Parent strategyDash;
      
        private Parent currentPane;
        
        private Tooltip toolTip;

        private boolean isShowingLastMtgoxUSD;

        //** paste below code that comes from FX Builder -------------------------------------------------------

         
        @FXML //  fx:id="lastMtgox"
        private Text lastMtgox; // Value injected by FXMLLoader

        @FXML //  fx:id="lastBtceLTC"
        private Text lastBtceLTC; // Value injected by FXMLLoader

         @FXML //  fx:id="lastBtceBTC"
        private Text lastBtceBTC; // Value injected by FXMLLoader

        @FXML //  fx:id="lastBitstamp"
        private Text lastBitstamp; // Value injected by FXMLLoader

        
        @FXML //  fx:id="progIndicator"
        private ProgressIndicator progIndicator;
         
        @FXML //  fx:id="statusText"
        private Text statusText; // Value injected by FXMLLoader
         
        @FXML //  fx:id="onOffBtn"
        private ToggleButton onOffBtn; // Value injected by FXMLLoader
          
        @FXML //  fx:id="optionsBtn"
        private ToggleButton optionsBtn; // Value injected by FXMLLoader

        @FXML //  fx:id="marketsBtn"
        private ToggleButton marketsBtn; // Value injected by FXMLLoader

        @FXML //  fx:id="stackPane"
        private StackPane stackPane; // Value injected by FXMLLoader
        
        @FXML //  fx:id="content"
        private Pane content; // Value injected by FXMLLoader
    
        @FXML //  fx:id="statusBtn"
        private ToggleButton statusBtn; // Value injected by FXMLLoader

        @FXML //  fx:id="strategyBtn"
        private ToggleButton strategyBtn; // Value injected by FXMLLoader


        @Override // This method is called by the FXMLLoader when initialization is complete
            public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
            assert content != null : "fx:id=\"content\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert marketsBtn != null : "fx:id=\"marketsBtn\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert getOnOffBtn() != null : "fx:id=\"onOffBtn\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert optionsBtn != null : "fx:id=\"optionsBtn\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert progIndicator != null : "fx:id=\"progIndicator\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert stackPane != null : "fx:id=\"stackPane\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert statusBtn != null : "fx:id=\"statusBtn\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert strategyBtn != null : "fx:id=\"strategyBtn\" was not injected: check your FXML file 'Dashboard.fxml'.";
            assert lastBitstamp != null : "fx:id=\"lastBitstamp\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
            assert lastBtceBTC != null : "fx:id=\"lastBtceBTC\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
            assert lastBtceLTC != null : "fx:id=\"lastBtceLTC\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";
            assert lastMtgox != null : "fx:id=\"lastMtgox\" was not injected: check your FXML file 'DashboardMarkets.fxml'.";

            //** paste above code that comes from FX Builder -------------------------------------------------------
            
            createPane(DASHBOARD_MARKETS);
            createPane(DASHBOARD_OPTIONS);
            createPane(DASHBOARD_STRATEGY);
            createPane(DASHBOARD_STATUS);
            
            isShowingLastMtgoxUSD=true;

            content.getChildren().add(statusDash);

            statusBtn.setSelected(true);
            
            progIndicator.setVisible(false);
            
               
            toolTip = new Tooltip();
            toolTip.setText("Click to change currency");
            toolTip.getStyleClass().add("ttip");
     
            
            Tooltip.install(lastMtgox, toolTip);
            
            Global.toggleBot(false);

          }

    
    public ProgressIndicator getProgIndicator()
    {
        return progIndicator;
    }
    

      // Handler for ToggleButton[fx:id="statusBtn"] onAction
      public void showStatus(ActionEvent event) {
        //Disable other buttons
        marketsBtn.setSelected(false);
        optionsBtn.setSelected(false);
        strategyBtn.setSelected(false);
        statusBtn.setSelected(true);       
        setCurrentPane(statusDash);     
      }    
      
      
      // Handler for ToggleButton[fx:id="marketsBtn"] onAction
      public void showMarkets(ActionEvent event) {
        //Disable other buttons
        statusBtn.setSelected(false);
        optionsBtn.setSelected(false);
        strategyBtn.setSelected(false); 
        
        marketsBtn.setSelected(true); 
        setCurrentPane(marketsDash);
      }    
      
      // Handler for ToggleButton[fx:id="strategyBtn"] onAction   
      public void showStrategy(ActionEvent event) {
        //Disable other buttons
        marketsBtn.setSelected(false);
        optionsBtn.setSelected(false);
        statusBtn.setSelected(false);    
        
        strategyBtn.setSelected(true); 
        setCurrentPane(strategyDash);
      }  
      
        
      // Handler for ToggleButton[fx:id="optionsBtn"] onAction
      public void showOptions(ActionEvent event) {
        //Disable other buttons
        marketsBtn.setSelected(false);
        statusBtn.setSelected(false);
        strategyBtn.setSelected(false);  

        optionsBtn.setSelected(true); 

        setCurrentPane(optionsDash);
      }
    
      
      public void createPane(String paneToCreate)
      {
        URL location = DashboardController.class.getResource(paneToCreate);
 
   
        switch(paneToCreate)
        {
            case DASHBOARD_MARKETS: {
                marketsController = new FXMLLoader();
                marketsController.setLocation(location);
                marketsController.setBuilderFactory(new JavaFXBuilderFactory());
                
                try {
                     marketsDash = (Parent) marketsController.load(location.openStream());
                } catch (IOException ex) {
                    Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case DASHBOARD_OPTIONS: {
                optionsController = new FXMLLoader();
                optionsController.setLocation(location);
                optionsController.setBuilderFactory(new JavaFXBuilderFactory());
                
                try {
                     optionsDash = (Parent) optionsController.load(location.openStream());
                } catch (IOException ex) {
                    Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case DASHBOARD_STRATEGY: {
                strategyController = new FXMLLoader();
                strategyController.setLocation(location);
                strategyController.setBuilderFactory(new JavaFXBuilderFactory());
                
                try {
                     strategyDash = (Parent) strategyController.load(location.openStream());
                } catch (IOException ex) {
                    Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case DASHBOARD_STATUS: {

                statusController = new FXMLLoader();
                statusController.setLocation(location);
                statusController.setBuilderFactory(new JavaFXBuilderFactory());
                
                try {
                     statusDash = (Parent) statusController.load(location.openStream());
                } catch (IOException ex) {
                    Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            default: {Utils.log("Trying to create the wrong pane "+paneToCreate);break;}
        }  
      }
      
   
          
      public void setCurrentPane(Parent newPane)
      {
        //TODO check if this is the newpane
        content.getChildren().clear();
        content.getChildren().add(newPane);
        //new BounceTransition(newPane).play();
      }
      
      
         public void refreshPrice(String market, String coin, String value,String currency)
    {
        Text textField = null;
        boolean needToUpdateUI=true;
        switch(market)
        {
            case Constant.MTGOX : { 
                if((isShowingLastMtgoxUSD && currency.equals(Constant.EUR) ) || (!isShowingLastMtgoxUSD && currency.equals(Constant.USD)))
                    needToUpdateUI = false;
                textField = lastMtgox; break ; }
            case Constant.BTCE : { 
                switch (coin)
                {
                    case Constant.BTC : {textField = lastBtceBTC; break ;}  
                    case Constant.LTC : {textField = lastBtceLTC; break ;}  
                    default : {break ;}  
                }
                break ; 
            }
            case Constant.BITSTAMP : { textField = lastBitstamp; break ; }              
            default : break;               
        }
        if (needToUpdateUI)
        {
            
            if(!textField.getText().equals(value))
            {            
               textField.setText(value);
               new PulseTransition(textField).play();
            }
        }
    }
    
      
      public void clickedOnMtgoxPrice()
    {
        //If keys are valid etc
        //new FadeInLeftBigTransition(lastMtgox).play();
        
        if(isShowingLastMtgoxUSD)
        {
           lastMtgox.setText(Global.MtgoxMarket.getLastBTC_EUR()+ " EUR");
           isShowingLastMtgoxUSD = false;
        }
        else
        {
           lastMtgox.setText(Global.MtgoxMarket.getLastBTC_USD()+ " USD");
           isShowingLastMtgoxUSD = true;
        }     

    }
         
    public void onOffButtonPressed(ActionEvent event) {
        
        if (getOnOffBtn().isSelected())
        {
            getOnOffBtn().setSelected(false);
            Dialogs.DialogResponse response = Dialogs.showConfirmDialog(Global.dashboardStage, 
                                                    "Are you sure you want to activate the bot?",
                                                    "Botcoin will try to execute the Active rules.",
                                                    "This operation needs confirmation",Dialogs.DialogOptions.YES_NO);
            //check confirm dialog
            if(response.equals(Dialogs.DialogResponse.YES) || response.equals(Dialogs.DialogResponse.OK) )
             { 
                 Global.toggleBot(true);
                 getOnOffBtn().setSelected(true);

             }
            else
            {
                getOnOffBtn().setSelected(false);
            }
        }
        else
            Global.toggleBot(false);
    }
    
    public void onWindowClose()
    {
        Platform.exit();
        System.exit(0);
    }

    public void setStatusString(String what)
    {
        statusText.setText(what);
    }
    /**
     * @return the optionsController
     */
    public FXMLLoader getOptionsController() {
        return optionsController;
    }

    /**
     * @param optionsController the optionsController to set
     */
    public void setOptionsController(FXMLLoader optionsController) {
        this.optionsController = optionsController;
    }

    /**
     * @return the statusController
     */
    public FXMLLoader getStatusController() {
        return statusController;
    }

    /**
     * @param statusController the statusController to set
     */
    public void setStatusController(FXMLLoader statusController) {
        this.statusController = statusController;
    }

    /**
     * @return the strategyController
     */
    public FXMLLoader getStrategyController() {
        return strategyController;
    }

    /**
     * @param strategyController the strategyController to set
     */
    public void setStrategyController(FXMLLoader strategyController) {
        this.strategyController = strategyController;
    }

    /**
     * @return the marketsController
     */
    public FXMLLoader getMarketsController() {
        return marketsController;
    }

    /**
     * @param marketsController the marketsController to set
     */
    public void setMarketsController(FXMLLoader marketsController) {
        this.marketsController = marketsController;
    }

    /**
     * @return the optionsDash
     */
    public Parent getOptionsDash() {
        return optionsDash;
    }

    /**
     * @param optionsDash the optionsDash to set
     */
    public void setOptionsDash(Parent optionsDash) {
        this.optionsDash = optionsDash;
    }

    /**
     * @return the statusDash
     */
    public Parent getStatusDash() {
        return statusDash;
    }

    /**
     * @param statusDash the statusDash to set
     */
    public void setStatusDash(Parent statusDash) {
        this.statusDash = statusDash;
    }

    /**
     * @return the marketsDash
     */
    public Parent getMarketsDash() {
        return marketsDash;
    }

    /**
     * @param marketsDash the marketsDash to set
     */
    public void setMarketsDash(Parent marketsDash) {
        this.marketsDash = marketsDash;
    }

    /**
     * @return the strategyDash
     */
    public Parent getStrategyDash() {
        return strategyDash;
    }

    /**
     * @param strategyDash the strategyDash to set
     */
    public void setStrategyDash(Parent strategyDash) {
        this.strategyDash = strategyDash;
    }

    /**
     * @return the statusText
     */
    public Text getStatusText() {
        return statusText;
    }

    /**
     * @param statusText the statusText to set
     */
    public void setStatusText(Text statusText) {
        this.statusText = statusText;
    }

    /**
     * @return the onOffBtn
     */
    public ToggleButton getOnOffBtn() {
        return onOffBtn;
    }

    /**
     * @param onOffBtn the onOffBtn to set
     */
    public void setOnOffBtn(ToggleButton onOffBtn) {
        this.onOffBtn = onOffBtn;
    }

 
}
