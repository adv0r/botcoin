/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.UI.dialogs;

import io.botcoin.UI.DashboardController;
import io.botcoin.UI.DashboardOptionsController;
import io.botcoin.UI.DashboardStrategyController;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.strategy.Rule;
import io.botcoin.strategy.Strategy;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class NewStoprulePaneController implements Initializable {

    
    @FXML //  fx:id="coinChoice"
    private ChoiceBox coinChoice;
    
    @FXML //  fx:id="marketChoice"
    private ChoiceBox marketChoice;
    
    @FXML //  fx:id="operationChoice"
    private ChoiceBox operationChoice;
    
    @FXML //  fx:id="directionChoice"
    private ChoiceBox directionChoice;
    
    @FXML //  fx:id="currencyChoice"
    private ChoiceBox currencyChoice;
    
    @FXML //  fx:id="btcText"
    private Label btcText;
    
    @FXML //  fx:id="amountField"
    private TextField amountField;
    
    @FXML //  fx:id="thresholdField"
    private TextField thresholdField;
        
    @FXML //  fx:id="usdText"
    private Label usdText;
    
    private static final String ERROR_STYLE=" -fx-background-color: #ffcc99;";
    private static final String NORMAL_STYLE=" -fx-background-color: #ffffff;";
    
    private ArrayList<String> coinList = new ArrayList<>();
    private ObservableList<String> coinObservableList = FXCollections.observableArrayList(coinList);
    
    private ArrayList<String> marketList = new ArrayList<>();
    private ObservableList<String> marketObservableList = FXCollections.observableArrayList(marketList);
    
    private ArrayList<String> currencyList = new ArrayList<>();
    private ObservableList<String> currencyObservableList = FXCollections.observableArrayList(currencyList);
    
    private ArrayList<String> operationList = new ArrayList<>();
    private ObservableList<String> operationObservableList = FXCollections.observableArrayList(operationList);


    public final String RULE_OK = "Rule is ok";
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert btcText != null : "fx:id=\"btcText\" was not injected: check your FXML file 'NewStoprulePane.fxml'.";
        assert coinChoice != null : "fx:id=\"coinChoice\" was not injected: check your FXML file 'NewStoprulePane.fxml'.";
        assert currencyChoice != null : "fx:id=\"currencyChoice\" was not injected: check your FXML file 'NewStoprulePane.fxml'.";
        assert operationChoice != null : "fx:id=\"operationChoice\" was not injected: check your FXML file 'NewStoprulePane.fxml'.";
        assert directionChoice != null : "fx:id=\"directionChoice\" was not injected: check your FXML file 'NewStoprulePane.fxml'.";
        assert marketChoice != null : "fx:id=\"marketChoice\" was not injected: check your FXML file 'NewStoprulePane.fxml'.";
        assert usdText != null : "fx:id=\"usdText\" was not injected: check your FXML file 'NewStoprulePane.fxml'.";

        //Load the operation type
        operationObservableList.add(Rule.BUY);
        operationObservableList.add(Rule.SELL);
        operationChoice.setItems(operationObservableList);
        operationChoice.setTooltip(new Tooltip("Select the order type"));
        operationChoice.getSelectionModel().selectFirst();
        
        
        //Load the markets
        
        if(Global.MtgoxMarket.isKeysValid())
            marketObservableList.add(Global.MtgoxMarket.getName());
        if(Global.BtceMarket.isKeysValid())
            marketObservableList.add(Global.BtceMarket.getName());
        if(Global.BitstampMarket.isKeysValid())
            marketObservableList.add(Global.BitstampMarket.getName());
        
        marketChoice.setItems(marketObservableList);
        marketChoice.setTooltip(new Tooltip("Select the market where you want to place the order"));
        marketChoice.getSelectionModel().selectFirst();
        
        updateChoices((String) marketChoice.getSelectionModel().getSelectedItem());
        
        marketChoice.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        String market= (String) marketObservableList.get(t1.intValue());
                        updateChoices(market);
                    }
             }
        );
        
        //Load above and belows
        directionChoice.setItems(FXCollections.observableArrayList("above","below"));
        directionChoice.setTooltip(new Tooltip("Execute the operation when the price is above the threshold or below it?"));
        directionChoice.getSelectionModel().selectFirst();
         
        //set tooltip
        coinChoice.setTooltip(new Tooltip("Which cryptocurrency you want to trade?"));
        currencyChoice.setTooltip(new Tooltip("The bot will monitor the price expressed in the selected currency"));
        

        //Load coins accordingly
        
        //select default market
        
        //select default coin 
        
        //select default currency
        
    }    

    private void updateChoices(String market) {
        coinObservableList.clear();
        currencyObservableList.clear();
        
        coinObservableList.add(Constant.BTC);
        currencyObservableList.add(Constant.USD);

        switch (market)
        {
            case Constant.MTGOX : {
                currencyObservableList.add(Constant.EUR);
                btcText.setVisible(true);
                usdText.setVisible(false);
                coinChoice.setVisible(false);
                currencyChoice.setVisible(true);
                break;
            }
            case Constant.BTCE : {
                btcText.setVisible(false);
                usdText.setVisible(true);
                coinObservableList.add(Constant.LTC);
                coinChoice.setVisible(true);
                currencyChoice.setVisible(false);
                break;
            }
            case Constant.BITSTAMP :{
                btcText.setVisible(true);
                usdText.setVisible(true);
                coinChoice.setVisible(false);
                currencyChoice.setVisible(false);
                break;
            }
            
            default: break;
        }
        
        coinChoice.setItems(coinObservableList);
        currencyChoice.setItems(currencyObservableList);
        coinChoice.getSelectionModel().selectFirst();
        currencyChoice.getSelectionModel().selectFirst();
        
    }
    
    public String isRuleValid()
    {
        String toReturn=RULE_OK;
        String amount= amountField.getText();
        String target= thresholdField.getText(); 
        if(amount.equals(""))
        {
            toReturn = "You left the 'amount' field empty \n";
            amountField.setStyle(ERROR_STYLE);;
        }
        if (target.equals(""))
        {
            toReturn = "You left the 'threshold' field empty \n";
            thresholdField.setStyle(ERROR_STYLE);;

        }
        
        if (!toReturn.equals(RULE_OK))
            return toReturn;
        
        
        try
        {
            Double.parseDouble(target);
        }
        catch(Exception e)
        {
            toReturn="The threshold value can only be a number (e.g. 240.34 )\n";
            thresholdField.setStyle(ERROR_STYLE);
        }
        
        try
        {
            Double.parseDouble(amount);
        }
        catch(Exception e)
        {
            toReturn+="The amount field can only be a number (e.g. 1.34 )";
            amountField.setStyle(ERROR_STYLE);
        }
        if (toReturn.equals(RULE_OK))
        {
              amountField.setStyle(NORMAL_STYLE);
              thresholdField.setStyle(NORMAL_STYLE);;
        }
        return toReturn;
        
    }
    
    public void addRule()
    {
        String operation= (String) operationChoice.getSelectionModel().getSelectedItem();
        double amount= Double.parseDouble(amountField.getText());
        String coin =  getSelectedCoin();
        String direction= (String) directionChoice.getSelectionModel().getSelectedItem(); 
        double target= Double.parseDouble(thresholdField.getText()); 
        String currency= getSelectedCurrency(); 
        String comment= "";
        String executedOrderResponse= "";
        boolean active= true ;
        boolean executed= false ;
        String market = (String) marketChoice.getSelectionModel().getSelectedItem();
        
        Dialogs.DialogResponse response = Dialogs.showConfirmDialog(Global.dashboardStage, 
                                                    "Are you sure you want to : \n"+
                                                    operation+" "+amount+" "+coin+" on "+market+" if price goes "+direction+" "+target+" "+currency,
                                                    "Please read it twice!",
                                                    "Confirm rule",Dialogs.DialogOptions.YES_NO);

       //check confirm dialog
       if(response.equals(Dialogs.DialogResponse.YES) || response.equals(Dialogs.DialogResponse.OK) )
        { 
            Global.strategy.addRule(operation, amount,coin, direction, target, currency, comment, executedOrderResponse, active, executed,market); 
            ((DashboardStrategyController) Global.getController(Constant.STRATEGY)).newRuleDialogStage.close();
        }
              
    }

    private String getSelectedCoin() {
        if(btcText.isVisible())
            return Constant.BTC;
        else 
            return (String) coinChoice.getSelectionModel().getSelectedItem();
        }


    private String getSelectedCurrency() {
        if(usdText.isVisible())
           return Constant.USD;
        else
            return (String) currencyChoice.getSelectionModel().getSelectedItem();
    }
    
    
}