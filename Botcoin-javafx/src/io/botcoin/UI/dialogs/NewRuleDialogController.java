/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.UI.dialogs;

import io.botcoin.UI.DashboardController;
import io.botcoin.UI.DashboardOptionsController;
import io.botcoin.global.Global;
import io.botcoin.strategy.Rule;
import io.botcoin.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class NewRuleDialogController implements Initializable {
    private static final String NEW_NOTIFICATION_RULE_FXML = "NewNotificationrulePane.fxml";
    private static final String NEW_STOP_RULE_FXML = "NewStoprulePane.fxml";
 
    private FXMLLoader stopRuleController;
    private FXMLLoader notificationRuleController;
      
    private Parent notificationRuleDash;
    private Parent stopRuleDash;
    
    
    @FXML //  fx:id="ruleTypeChoice"
    private ChoiceBox ruleTypeChoice;

    @FXML //  fx:id="rulePane"
    private Pane rulePane;
    
    @FXML //  fx:id="addRuleBtn"
    private Button addRuleBtn;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert addRuleBtn != null : "fx:id=\"addRuleBtn\" was not injected: check your FXML file 'NewRuleDialog.fxml'.";
        assert rulePane != null : "fx:id=\"rulePane\" was not injected: check your FXML file 'NewRuleDialog.fxml'.";
        assert ruleTypeChoice != null : "fx:id=\"ruleTypeChoice\" was not injected: check your FXML file 'NewRuleDialog.fxml'.";

        ruleTypeChoice.setItems(FXCollections.observableArrayList(
            "Buy/Sell order", 
            new Separator(), 
            "Price alert",          
            "Price difference across markets",
            "EMA/SMA") 
            );
        
        ruleTypeChoice.setTooltip(new Tooltip("Select the type of rule"));
        ruleTypeChoice.getSelectionModel().selectFirst();
        ruleTypeChoice.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        if(t1==0)
                        {
                            setCurrentPane(stopRuleDash);  
                        }
                        else
                        {
                            //Not avialable yet
                            Dialogs.showInformationDialog(new Stage(), 
                                    "This kind of rule is not available yet. Stay tuned",
                                    "Information Dialog", 
                                    "Coming soon");
                            setCurrentPane(stopRuleDash);  
                        }
                    }
             }
        );
        createPane(NEW_STOP_RULE_FXML);
        createPane(NEW_NOTIFICATION_RULE_FXML);

        rulePane.getChildren().add(stopRuleDash);
    }
    
    
    
    
     public void createPane(String paneToCreate)
      {
        URL location = NewRuleDialogController.class.getResource(paneToCreate);
 
        switch(paneToCreate)
        {
            case NEW_STOP_RULE_FXML: {
                stopRuleController = new FXMLLoader();
                stopRuleController.setLocation(location);
                stopRuleController.setBuilderFactory(new JavaFXBuilderFactory());
                
                
                try {
                     stopRuleDash = (Parent) stopRuleController.load(location.openStream());
                } catch (IOException ex) {
                    Logger.getLogger(NewRuleDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case NEW_NOTIFICATION_RULE_FXML: {
                notificationRuleController = new FXMLLoader();
                notificationRuleController.setLocation(location);
                notificationRuleController.setBuilderFactory(new JavaFXBuilderFactory());
                
                try {
                     notificationRuleDash = (Parent) notificationRuleController.load(location.openStream());
                } catch (IOException ex) {
                    Logger.getLogger(NewRuleDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            default: {Utils.log("Trying to create the wrong pane "+paneToCreate);break;}
        }  
      }
     
            
      public void setCurrentPane(Parent newPane)
      {
        //TODO check if this is the newpane
        rulePane.getChildren().clear();
        rulePane.getChildren().add(newPane);
        //new BounceTransition(newPane).play();
      }
      
      public void addRulePressed()
      {
          NewStoprulePaneController currentRuleController = (NewStoprulePaneController) stopRuleController.getController();
          String ruleValidationMessage = currentRuleController.isRuleValid();
          if (ruleValidationMessage.equals(currentRuleController.RULE_OK))
            {
                //Rule is valid, add it
                currentRuleController.addRule();

            }
          else
            {
                //Rule is not valid, popup a message
                 Dialogs.showInformationDialog(new Stage(), 
                                    ruleValidationMessage,
                                    "Check the correctness of the values", 
                                    "Invalid rule");
            }
      }
      
}
