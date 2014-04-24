/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.UI;

import io.botcoin.UI.dialogs.ApiDialog;
import io.botcoin.UI.dialogs.PassphraseDialog;
import io.botcoin.UI.dialogs.PassphrasePrompt;
import io.botcoin.api.ApiKeys;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.models.Market;
import io.botcoin.utils.Utils;
import static io.botcoin.utils.Utils.LOG_ERR;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class DashboardOptionsController implements Initializable {
    @FXML //  fx:id="bitstampStatusLabel"
    private Label bitstampStatusLabel; // Value injected by FXMLLoader

    @FXML //  fx:id="btceStatusLabel"
    private Label btceStatusLabel; // Value injected by FXMLLoader

    @FXML //  fx:id="mtgStatusLabel"
    private Label mtgStatusLabel; // Value injected by FXMLLoader
    
    @FXML //  fx:id="setupBitstampBtn"
    private Button setupBitstampBtn; // Value injected by FXMLLoader

    @FXML //  fx:id="setupBtceBtn"
    private Button setupBtceBtn; // Value injected by FXMLLoader

    @FXML //  fx:id="setupMtgButton"
    private Button setupMtgButton; // Value injected by FXMLLoader
    
    @FXML //  fx:id="requestHelpButton"
    private Button requestHelpButton; // Value injected by FXMLLoader

    @FXML //  fx:id="giveFeedbackButton"
    private Button giveFeedbackButton; // Value injected by FXMLLoader
    
    @FXML //  fx:id="checkUpdatesButton"
    private Button checkUpdatesButton; // Value injected by FXMLLoader
    
    @FXML //  fx:id="changePassphraseButton"
    private Button changePassphraseButton; // Value injected by FXMLLoader
    
    @FXML //  fx:id="mtgStatusIcon"
    private ImageView statusIconMtg;
    
    @FXML // 
    private ImageView statusIconBtce;
    
    @FXML //  
    private ImageView statusIconBitstamp;
    
    
    private final String VALID_KEYS = "Valid keys";
    private final String KEYS_NOT_SET_OR_INVALID = "Invalid keys";

    private final String ICON_VALID = Settings.PATH_TO_OK;
    private final String ICON_WARNING = Settings.PATH_TO_WRONG;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert bitstampStatusLabel != null : "fx:id=\"bitstampStatusLabel\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert btceStatusLabel != null : "fx:id=\"btceStatusLabel\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert changePassphraseButton != null : "fx:id=\"changePassphraseButton\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert mtgStatusLabel != null : "fx:id=\"mtgStatusLabel\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert setupBitstampBtn != null : "fx:id=\"setupBitstampBtn\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert setupBtceBtn != null : "fx:id=\"setupBtceBtn\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert setupMtgButton != null : "fx:id=\"setupMtgButton\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert statusIconBitstamp != null : "fx:id=\"statusIconBitstamp\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert statusIconBtce != null : "fx:id=\"statusIconBtce\" was not injected: check your FXML file 'DashboardOptions.fxml'.";
        assert statusIconMtg != null : "fx:id=\"statusIconMtg\" was not injected: check your FXML file 'DashboardOptions.fxml'.";

        // initialize your logic here: all @FXML variables will have been injected
        
        //update valid/not valid state message
        updateStatusMessages();  
    }
    
    public void changePassHandler(ActionEvent event)
    {
        boolean correct = false;
        String passphrase = new PassphrasePrompt(false).show(Global.dashboardStage);
        String passDecrypt = Utils.decode(Settings.PATH_TO_SAVED_PW, passphrase);
        correct = !passDecrypt.equals("-1");
        if (!correct)
         {
            Dialogs.showErrorDialog(Global.dashboardStage,
                    "The passphrase is incorrect",
                    "Error",
                    "Wrong Passphrase");
         }

        else
        {
            System.out.println("Ok, proceeding"); //TODO remove

            //Password correct
            PassphraseDialog passDialog = new PassphraseDialog(Global.dashboardStage,Global.app,true,Global.passphrase);
            passDialog.show(); 
            
        }      
    }
    
    
    
     // Handler for Button[id="setupBitstampBtn"] onAction
    // Handler for Button[id="setupBtceBtn"] onAction
    // Handler for Button[id="setupMtgButton"] onAction
    public void buttonPressed(ActionEvent event) {
        String buttonID = ((Button) event.getSource()).getId();

        switch(buttonID)
        {
            case "setupMtgButton" : new ApiDialog(Global.dashboardStage,false, Constant.MTGOX).show(); break;
            case "setupBtceBtn" : new ApiDialog(Global.dashboardStage,false, Constant.BTCE).show(); break;
            case "setupBitstampBtn" : new ApiDialog(Global.dashboardStage,false, Constant.BITSTAMP).show();  break;
            default : Utils.log("Calling buttonPressed with wrong id : "+buttonID, Utils.LOG_ERR); break;
        }
    }
    
    
    public void requestHelp()
    {
        Dialogs.showInformationDialog(Global.dashboardStage, 
                  "Mail support@botcoin.io including your unique purchease ID\n"
                + "we will asnwer asap!" );   
    }
    
    public void giveFeedback()
    {
        Dialogs.showInformationDialog(Global.dashboardStage, 
                                    "Send your suggestion to support@botcoin.io or http://botcoin.io/feedback " );   
    }
    
    public void checkUpdates()
    {
        Dialogs.showInformationDialog(Global.dashboardStage, 
                                    "Your version is TODO. Check latest version at http://botcoin.io/upgrade" );//TODO    
    }
    
    private String getValidStatus(String market) {
            String message="";
            DashboardMarketsController dmc = (DashboardMarketsController) Global.getController(Constant.MARKETS);
            ApiKeys keys;
            switch(market){
                  case Constant.MTGOX : {  
                      if(Global.MtgoxMarket.isKeysValid()){
                          message = VALID_KEYS;
                          statusIconMtg.setImage(new Image(ICON_VALID));
                          dmc.activateMarket(market, true);
                      }
                      else{
                          message = KEYS_NOT_SET_OR_INVALID;
                          statusIconMtg.setImage(new Image(ICON_WARNING));
                          dmc.activateMarket(market, false);
                      }
                      break; 
                  }
                  case Constant.BTCE : {  
                      if(Global.BtceMarket.isKeysValid()){
                          message = VALID_KEYS;
                          statusIconBtce.setImage(new Image(ICON_VALID));
                          dmc.activateMarket(market, true);
                      }
                      else{
                          message = KEYS_NOT_SET_OR_INVALID;
                          statusIconBtce.setImage(new Image(ICON_WARNING));
                          dmc.activateMarket(market, false);
                      }
                    break; 
                  }
                  case Constant.BITSTAMP : { 
                      if(Global.BitstampMarket.isKeysValid()){
                          message = VALID_KEYS;
                          statusIconBitstamp.setImage(new Image(ICON_VALID));
                          dmc.activateMarket(market, true);
                      }
                      else{
                          message = KEYS_NOT_SET_OR_INVALID;
                          statusIconBitstamp.setImage(new Image(ICON_WARNING));
                          dmc.activateMarket(market, false);
                      }
                    break; 
                  } 
                  default: { Utils.log("market "+ market+" is not supported", LOG_ERR); break ;  }
              } 

       
     return message;
    }
       
    public void updateStatusMessages() {
        mtgStatusLabel.setText(getValidStatus(Constant.MTGOX));
        btceStatusLabel.setText(getValidStatus(Constant.BTCE));        
        bitstampStatusLabel.setText(getValidStatus(Constant.BITSTAMP));        
    }
}

