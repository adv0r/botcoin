/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.UI;

import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.strategy.Rule;
import java.awt.Font;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialogs;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */


        
      
        
public class DashboardStatusController implements Initializable {
    @FXML //  fx:id="listActive"
    private ListView listActive;
    
    @FXML //  fx:id="listExecuted"
    private ListView listExecuted;
    
    @FXML //  fx:id="titleActive"
    private Text titleActive;    
    
    @FXML //  fx:id="titleExecuted"
    private Text titleExecuted;
    
    @FXML //  fx:id="clearBtn"
    private Button clearBtn;
    
    public final String TITLE_ACTIVE = "Active rules (";
    public final String TITLE_EXECUTED = "Executed orders (";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert clearBtn != null : "fx:id=\"clearBtn\" was not injected: check your FXML file 'DashboardStatus.fxml'.";
        assert listActive != null : "fx:id=\"listActive\" was not injected: check your FXML file 'DashboardStatus.fxml'.";
        assert listExecuted != null : "fx:id=\"listExecuted\" was not injected: check your FXML file 'DashboardStatus.fxml'.";
        assert titleActive != null : "fx:id=\"titleActive\" was not injected: check your FXML file 'DashboardStatus.fxml'.";
        assert titleExecuted != null : "fx:id=\"titleExecuted\" was not injected: check your FXML file 'DashboardStatus.fxml'.";

        updateTitles();
        listActive.setItems(Global.strategy.getActiveRulesList());
        listExecuted.setItems(Global.strategy.getExecutedRulesList());
        
        listActive.setCellFactory(new Callback<ListView<Rule>, 
            ListCell<Rule>>() {
                @Override
                public ListCell<Rule> call(ListView<Rule> p) {
                    return new RuleActive();
                }
            }
        );
        
        listExecuted.setCellFactory(new Callback<ListView<Rule>, 
            ListCell<Rule>>() {
                @Override
                public ListCell<Rule> call(ListView<Rule> p) {
                    return new RuleExecuted();
                }
            }
        );
        
        listExecuted.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<Rule>() {
            @Override
            public void changed(ObservableValue<? extends Rule> ov, Rule t, Rule t1) { 
                if(t1!=null)
                {
                    if(!t1.isSeen())
                    {
                        t1.setSeen(true);
                        Global.strategy.writeStrategyToFile(Settings.STRATEGY_PATH);
                        updateTitles();
                        updateLists();
                    }
                }
            }
        });
       }    
    
    
    public void clearExecuted()
    {
          Global.strategy.clearExecuted();
          updateTitles();
          updateLists();
    }
    
    public void updateTitles()
    {
        titleActive.setText(TITLE_ACTIVE+Global.strategy.countActiveRules()+")"); 
        titleExecuted.setText(TITLE_EXECUTED+Global.strategy.countExecutedRules()+")"); 
    }
    
    public void updateLists()
    {
        //Based on a workaround tnx stackoverflow http://stackoverflow.com/a/12613774/748381
        listActive.setVisible(false);
        listActive.setVisible(true);
        listExecuted.setVisible(false);
        listExecuted.setVisible(true);
    }
    
     static class RuleActive extends ListCell<Rule> {
        @Override
        public void updateItem(Rule item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                    setText(item.toString());     
            }
        }
    }
     
    static class RuleExecuted extends ListCell<Rule> {
        @Override
        public void updateItem(Rule item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                    setText(item.toString()+" ("+item.getExecutedTimestamp()+" - "+item.getExecutedOrderResponse()+")");
                    if(!item.isSeen())
                    {
                        setTextFill(Color.web("#287392"));

                    }
                    else
                    {
                        setTextFill(Color.web("black"));
                    }
            }
        }

        private void setTextFill(int BOLD) {
            throw new UnsupportedOperationException("Not supported yet."); //TODO change body of generated methods, choose Tools | Templates.
        }
        
    }
    
}
