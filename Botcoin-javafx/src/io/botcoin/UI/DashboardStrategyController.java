/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.UI;

import io.botcoin.UI.dialogs.NewRuleDialogController;
import io.botcoin.global.Constant;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.strategy.Rule;
import io.botcoin.utils.QuickDirtyTest;
import io.botcoin.utils.Utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;


/**
 * FXML Controller class
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */

        
public class DashboardStrategyController implements Initializable {
        
    public Stage newRuleDialogStage;
    private FXMLLoader newRuleDialogController;
    private final String ACTIVE = "Active";
    private final String DISABLED = "Disabled";
    private final String DISABLED_EXEC = "Disabled (executed)";
    private final String COLOR_DISABLED ="#888888";
    private final String COLOR_ENABLED ="black";
        @FXML //  fx:id="strategyTableView"
        private TableView<Rule> strategyTableView;        
        
        @FXML //  fx:id="deleteCol"
        private TableColumn deleteCol;
        
        @FXML //  fx:id="statusCol"
        private TableColumn statusCol; 
        
        @FXML //  fx:id="idCol"
        private TableColumn idCol;    
        
        @FXML //  fx:id="marketCol"
        private TableColumn marketCol;  
        
        @FXML //  fx:id="typeCol"
        private TableColumn typeCol;     
        
        @FXML //  fx:id="amountCol"
        private TableColumn amountCol;    
        
        @FXML //  fx:id="coinCol"
        private TableColumn coinCol;
         
        @FXML //  fx:id="directionCol"
        private TableColumn directionCol; 
        
        @FXML //  fx:id="thresholdCol"
        private TableColumn thresholdCol; 
        
        @FXML //  fx:id="currencyCol"
        private TableColumn currencyCol;   
        
        @FXML //  fx:id="newRuleBtn"
        private Button newRuleBtn;
        
        @FXML //  fx:id="clearRulesBtn"
        private Button clearRulesBtn;
        
        @FXML //  fx:id="exportBtn"
        private Button exportBtn;
        
        @FXML //  fx:id="scrollPane"
        private ScrollPane scrollPane;
         
    /**
     * Initializes the controller class.
     */
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert amountCol != null : "fx:id=\"amountCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert clearRulesBtn != null : "fx:id=\"clearRulesBtn\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert coinCol != null : "fx:id=\"coinCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert currencyCol != null : "fx:id=\"currencyCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert deleteCol != null : "fx:id=\"deleteCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert directionCol != null : "fx:id=\"directionCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert exportBtn != null : "fx:id=\"exportBtn\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        //assert idCol != null : "fx:id=\"idCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert marketCol != null : "fx:id=\"marketCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert newRuleBtn != null : "fx:id=\"newRuleBtn\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert statusCol != null : "fx:id=\"statusCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert strategyTableView != null : "fx:id=\"strategyTableView\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert thresholdCol != null : "fx:id=\"thresholdCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";
        assert typeCol != null : "fx:id=\"typeCol\" was not injected: check your FXML file 'DashboardStrategy.fxml'.";

        
        deleteCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("active"));
        statusCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("active"));
        marketCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("market"));
        typeCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("operation"));
        amountCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("amount"));
        coinCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("coin"));
        directionCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("direction"));
        thresholdCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("target"));
        currencyCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("currency"));
    
        strategyTableView.setItems(Global.strategy.getRulesList());        
        strategyTableView.setEditable(false);
        
               
        
        
        //Styling the delete column
        Callback<TableColumn, TableCell> delCellFactory =
        new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell<String, Boolean>() {
                  @Override
                    public void updateItem(Boolean item, boolean empty) {
                       super.updateItem(item, empty);
                        if(item!=null)
                        {
                           setGraphic(getDeleteIcon());
                        }         
                    }
                };

                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        TableCell cell = (TableCell) event.getSource();
                        TableRow row = cell.getTableRow();
                        Rule toDelete = (Rule)strategyTableView.getSelectionModel().getSelectedItem();
                        //TODO check he clicked a real object
                        if (toDelete!=null)
                        {
                            Dialogs.DialogResponse response = Dialogs.showConfirmDialog(Global.dashboardStage, 
                                                    toDelete.toString(),
                                                    "Are you sure you want to delete the following rule :",
                                                    "Confirm rule deletion",Dialogs.DialogOptions.YES_NO);

                            //check confirm dialog
                            if(response.equals(Dialogs.DialogResponse.YES) || response.equals(Dialogs.DialogResponse.OK) )
                             { 
                                Global.strategy.delRuleById(toDelete.getId());
                             } 
                             
                        }
                    }
                });
                return cell;
            }
        };
        
        
        
        //Styling the status column
        Callback<TableColumn, TableCell> statusCellFactory =
        new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell<String, Boolean>() {
                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                       super.updateItem(item, empty);
                        if(item!=null)
                        {
                            if(item)
                            {
                               setText(ACTIVE);
                               getTableRow().setOpacity(1);    
                               setTextFill(Color.web(COLOR_ENABLED));
                               setTooltip(getActivateTooltip(true));
                            }
                            else
                            {
                               setText(DISABLED); 
                               getTableRow().setOpacity(0.8);    
                               setTextFill(Color.web(COLOR_DISABLED));
                               setTooltip(getActivateTooltip(false));
                            }
                        }
                                   
                    }

             
                };

                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        TableCell cell = (TableCell) event.getSource();
                        TableRow row = cell.getTableRow();
                        Rule toUpdate = (Rule)strategyTableView.getSelectionModel().getSelectedItem();
                        //TODO check he clicked a real object
                        if (toUpdate!=null)
                        {
                            toUpdate.setActive(!toUpdate.isActive());
                            Global.strategy.writeStrategyToFile(Settings.STRATEGY_PATH);
                          
                        }
                    }
                });
                
                return cell;
            }
        };
        
        
        deleteCol.setCellFactory(delCellFactory);
        statusCol.setCellFactory(statusCellFactory);
                
    }    
    
    public void clearRules(){
        Dialogs.DialogResponse response = Dialogs.showConfirmDialog(Global.dashboardStage, 
                                                    "Are you sure you want to clear "+Global.strategy.getRulesList().size()+" rules?",
                                                    "Potential dangerous operation",
                                                    "Confirm operation",Dialogs.DialogOptions.YES_NO);

       //check confirm dialog
       if(response.equals(Dialogs.DialogResponse.YES) || response.equals(Dialogs.DialogResponse.OK) )
        { 
            Global.strategy.deleteAllRules();
            Global.strategy.writeStrategyToFile(Settings.STRATEGY_PATH);
        }
    }
    
    public void deleteRule()
    {
        
    }
    
    public void exportStrategy()
    {
        
         Dialogs.showInformationDialog(Global.dashboardStage, 
                                    "The strategy is saved on your local disk : "+ Settings.STRATEGY_PATH );
    
                                    
    }
    
    public void newRule()
    {
        if (Global.MtgoxMarket.isKeysValid() ||
                Global.BtceMarket.isKeysValid() ||
                Global.BitstampMarket.isKeysValid())
        {
            URL location = NewRuleDialogController.class.getResource("NewRuleDialog.fxml");

            newRuleDialogController = new FXMLLoader();
            newRuleDialogController.setLocation(location);
            newRuleDialogController.setBuilderFactory(new JavaFXBuilderFactory());

            Parent root = null;
            try {
                 root = (Parent) newRuleDialogController.load(location.openStream());
            } 
            catch (IOException ex) {
                 Logger.getLogger(NewRuleDialogController.class.getName()).log(Level.SEVERE, null, ex); 
             }           

            newRuleDialogStage = new Stage();
            newRuleDialogStage.setTitle("Strategy configuration | Add rule");

            newRuleDialogStage.setScene(new Scene(root, 745, 300));
            newRuleDialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                 Utils.log("New rule dialog closed",Utils.LOG_LOW);
                }
            });

            newRuleDialogStage.show();
        }
        else
        {
               Dialogs.showInformationDialog(Global.dashboardStage, 
                                    "Before adding a rule you should setup some api keys.",
                                    "The bot does not work without API access", 
                                    "Add API keys before");
        }
    }   
    
    public TableView getTableView()
    {
        return strategyTableView;
    }
    
    private Node getDeleteIcon()
    {
        HBox box= new HBox();
        box.setSpacing(10) ;

        ImageView imageview = new ImageView();
        imageview.setFitHeight(16);
        imageview.setFitWidth(16);
        imageview.setImage(new Image(Settings.PATH_TO_DELETE)); 

        box.getChildren().addAll(imageview); 
        return box;
    }
    
    private Tooltip getActivateTooltip(boolean active) {
        
       if(active)
       {
          return new Tooltip("Click to disable this rule");
       }
       else 
          return new Tooltip("Click to enable this rule");

     }
}


