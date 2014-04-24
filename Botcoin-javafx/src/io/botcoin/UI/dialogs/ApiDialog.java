package io.botcoin.UI.dialogs;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */

import io.botcoin.UI.DashboardController;
import io.botcoin.UI.DashboardOptionsController;
import io.botcoin.api.ApiKeys;
import io.botcoin.api.BitStampTrading;
import io.botcoin.api.BtceTrading;
import io.botcoin.api.MtGoxTrading;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.models.Market;
import io.botcoin.utils.Utils;
import static io.botcoin.utils.Utils.LOG_ERR;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ApiDialog  {
    
private Stage parentStage;
private int w = 850;
private int h = 450;
protected boolean wizard;
protected String market;

    public ApiDialog(Stage parentStage,boolean wizard,String market){
        this.parentStage=parentStage;
        this.wizard = wizard;
        this.market= market;
    }
    
    public void show() { 
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        if (market.equals(Constant.BITSTAMP))
        {
            h=h+60;
        }
        final Stage apiStage = new ApiStage(parentStage,w,h,wizard,market);
        apiStage.initModality(Modality.APPLICATION_MODAL);
        apiStage.setX((bounds.getWidth()/2)-w/2);
        apiStage.setY((bounds.getHeight()/2)-h/2);
        apiStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                DashboardController dbc = Global.dashBoardController.getController();
                DashboardOptionsController controller = (DashboardOptionsController) Global.getController(Constant.OPTIONS);
                controller.updateStatusMessages();            
            }
        });

        apiStage.setOpacity(0.9);
        apiStage.setResizable(false);
        apiStage.show();
    }
}




class ApiStage extends Stage {
    private Stage owner;
    private String market;
    private Label infoLabel;
    private Button nextBtn;
    private TextField apikeyField;
    private TextField secretkeyField;
    private TextField clientIDField;

    private ProgressIndicator progIndicator;
    private Button saveBtn;
    private Button validateBtn;
    private Label resultLabel;
    private Image okImg;
    private Image wrongImg;
    private ImageView imageView;
    private Label infoLabel2;
    private boolean areKeysValidated;
    private SkipSaveHandler skipSaveHandler;
    private Button resetBtn;

    public ApiStage(Stage owner,int w,int h,boolean wizard,String market ) {
        super();
        this.market = market;
        this.owner=owner;
        initOwner(owner);
        areKeysValidated = false;
        
        if(wizard)
            setTitle(Settings.APP_TITLE + " | Insert valid Api Keys (3/3) ");
        else
            setTitle(Settings.APP_TITLE + " | Credentials Setup for "+market);

        Group root = new Group();
        Scene scene = new Scene(root, w, h, Color.WHITE);
        scene.getStylesheets().add(Settings.CSS_BUTTON_FILE_PATH);
        setScene(scene);
        
        BorderPane border = new BorderPane();
        HBox hbox = addTop();
        border.setTop(hbox);
        
        border.setBottom(addBottom());
        border.setCenter(addGridPane());
        root.getChildren().add(border);
    }
    
    public HBox addTop() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        ImageView logo = new ImageView(new Image(Settings.PATH_TO_LOGO));
        switch(market){
                case Constant.MTGOX : { logo = new ImageView(new Image(Settings.PATH_TO_MTGOX_LOGO)); break;  }
                case Constant.BTCE : {logo = new ImageView(new Image(Settings.PATH_TO_BTCE_LOGO)); break; }
                case Constant.BITSTAMP :{ logo = new ImageView(new Image(Settings.PATH_TO_BITSTAMP_LOGO)); break; }
                default: { Utils.log("market "+ market+" is not supported", LOG_ERR); break ;  }
            }           
        hbox.getChildren().addAll(logo);
        return hbox;
}  
 
    private Node addGridPane() {
      //see http://docs.oracle.com/javafx/2/ui_controls/editor.htm
        
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);

       Hyperlink link;

       if (market.equals(Constant.BITSTAMP))
       {
            link = new Hyperlink("Short tutorial: how to give API access to your "+market+" account");
            clientIDField = new TextField(); clientIDField.setPromptText("Insert your "+market+" Customer ID");  
            apikeyField = new TextField(); apikeyField.setPromptText("Insert your "+market+" API key");
            secretkeyField = new PasswordField(); secretkeyField.setPromptText("Insert your "+market+" API secret");  

            clientIDField.setMinWidth(300);             
            apikeyField.setMinWidth(300);             
            secretkeyField.setMinWidth(300);  
            infoLabel = new Label(market+" requires API Keys and your CustomerID to be able to trade. \n"
               + "Make sure to activate the API access in your profile");
       
            infoLabel2 = new Label(" Your credential will be encrypted and used locally your machine. \n If you have any doubt contact us support@botcoin.io ");
        }
       else 
       {
            //Api keys
            link = new Hyperlink("Short tutorial: how to obtain a pair of API keys on "+market);

            apikeyField = new TextField(); apikeyField.setPromptText("Insert your "+market+" Api Key");
            secretkeyField = new PasswordField(); secretkeyField.setPromptText("Insert your "+market+" API Secret Key");  
            apikeyField.setMinWidth(600);            
            secretkeyField.setMinWidth(600);
            infoLabel = new Label("You can generate a new pair of keys in  "+market+" website or use your existing pair. \n"
               + "Make sure to give 'trade' and 'get_info' permissions to the api key pairs");
       
            infoLabel2 = new Label(" Your keys will be encrypted and used locally your machine. \n If you have any doubt contact us support@botcoin.io ");
       
       }
       
           
       link.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        Utils.launchBrowser(Settings.URL_TO_HOWTO_API);
                    } catch (Exception ex) {
                        Utils.log(ex, Utils.LOG_ERR);
                    }
                }
            });
       
       ValidationHandler typeHandler = new ValidationHandler(this);
       apikeyField.addEventHandler(KeyEvent.KEY_RELEASED, typeHandler);
       secretkeyField.addEventHandler(KeyEvent.KEY_RELEASED, typeHandler);

       grid.add(infoLabel, 0, 0);
       grid.add(link, 0, 1);
       grid.add(infoLabel2, 0, 2);

       if(market.equals(Constant.BITSTAMP))
       {
           grid.add(clientIDField, 0,3);
           grid.add(apikeyField, 0, 4);      
           grid.add(secretkeyField, 0, 5); 
       }
       else
       {
            grid.add(apikeyField, 0, 3);      
            grid.add(secretkeyField, 0, 4); 
       }
       //show previous keys
       ApiKeys old_keys;
       Market marketObject = Utils.getMarketFromName(market);
       if(Utils.apiKeysSaved(marketObject))
                {
                    old_keys = marketObject.getKeys();
                    if (old_keys!=null)
                    {
                       apikeyField.setText(old_keys.getApiKey());
                       secretkeyField.setText(old_keys.getPrivateKey());
                       if(market.equals(Constant.BITSTAMP))
                           clientIDField.setText(old_keys.getClientID());
                    }
                    //validate previous keys
                    validateKeys(true);
                }
       return grid;
    }

     
    private Node addBottom() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);


        progIndicator = new ProgressIndicator();
        progIndicator.setVisible(false);

        imageView = new ImageView();
        imageView.setVisible(false);

        resultLabel = new Label("Insert the values and press Validate");
        validateBtn = new Button("Validate keys");
        validateBtn.setDefaultButton(true);
        validateBtn.setPrefSize(160, 20);
        validateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                validateKeys(false);
            }

        });
        
        resetBtn = new Button("Erase");
        resetBtn.setPrefSize(160, 20);
        resetBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dialogs.DialogResponse response = Dialogs.showConfirmDialog(owner, 
                                               "Are you sure?",
                                               "Reset Credentials",
                                               "Confirmation",Dialogs.DialogOptions.YES_NO);

                //check confirm dialog
                if(response.equals(Dialogs.DialogResponse.YES) || response.equals(Dialogs.DialogResponse.OK) )
                     {                       
                        Utils.eraseKeys(Utils.getMarketFromName(market));
                        apikeyField.setText("");
                        secretkeyField.setText("");
                        resetBtn.setVisible(false);
                        setNotValidState("Insert values and press Validate");
                        DashboardController dbc = Global.dashBoardController.getController();
                        DashboardOptionsController controller = dbc.getOptionsController().getController();
                        controller.updateStatusMessages();                           
                     }    
            }
        });
        


        saveBtn = new Button(SkipSaveHandler.SKIP);
        saveBtn.setPrefSize(160, 20);
       
        skipSaveHandler = new SkipSaveHandler(this);
        skipSaveHandler.setType(SkipSaveHandler.SKIP);
        
        saveBtn.setOnAction(skipSaveHandler);
        grid.add(progIndicator,0,0);
        grid.add(imageView,1,0);
        grid.add(resultLabel,2,0);

        grid.add(validateBtn,1,1);
        grid.add(saveBtn,2,1); 
        
        if(Utils.apiKeysSaved(Utils.getMarketFromName(market)))
         {
            grid.add(resetBtn,3,1);   
         }

        return grid;    
    }

     private void validateKeys(boolean oldExists) {
        String secretCandidate = secretkeyField.getText();
        String apiCandidate = apikeyField.getText();
        if((apiCandidate.length()<10 || secretCandidate.length()<20))
        {
            imageView.setImage(new Image(Settings.PATH_TO_WRONG));
            imageView.setVisible(true);
            resultLabel.setText("The values inserted are too short.");
            saveBtn.setDefaultButton(false);
        }
        else
        {
            progIndicator.setVisible(true); 
            imageView.setVisible(false);

            String secret = secretkeyField.getText();
            String api =  apikeyField.getText();
            String clientID ="";
            if(market.equals(Constant.BITSTAMP))
                clientID = clientIDField.getText();
            String validMessage = ApiKeys.validate(secret, api,clientID, market);
            
            if (validMessage.equals(ApiKeys.VALID_KEYS))
            {
                setValidState(validMessage);
            }
            else
            {
                setNotValidState(validMessage);
            }
           
            progIndicator.setVisible(false);
        }
       }
     
     private void setNotValidState(String message)
     {
            imageView.setImage(new Image(Settings.PATH_TO_WRONG));
            imageView.setVisible(true);
            resultLabel.setText(message);
            saveBtn.setDefaultButton(false);
            validateBtn.setDefaultButton(true);
            saveBtn.setText(SkipSaveHandler.SKIP);
            areKeysValidated = false;
            validateBtn.setDisable(false);
            skipSaveHandler.setType(SkipSaveHandler.SKIP);
     }
     
     private void setValidState(String message)
{
           resultLabel.setText(message);
           imageView.setImage(new Image(Settings.PATH_TO_OK));
           imageView.setVisible(true);
           saveBtn.setDefaultButton(true);
           saveBtn.setText("Finish");
           validateBtn.setDefaultButton(false);
           areKeysValidated = true;
           validateBtn.setDisable(true);
           skipSaveHandler.setType(SkipSaveHandler.SAVE);
           resetBtn.setVisible(true);
          
     }
     
     public void saveAction(String privateKey, String apiKey, String clientID, Stage toClose) {
            switch(market){
                case Constant.MTGOX : {
                    Global.MtgoxMarket.setKeys(ApiKeys.createApiKeys(Global.passphrase ,apiKey,privateKey ,Constant.MTGOX));
                    Global.MtgoxMarket.setTrade(new MtGoxTrading(Global.MtgoxMarket.getKeys()));                
                    Global.MtgoxMarket.setKeysValid(true);
                    break; 
                    }
                case Constant.BTCE : {
                    
                    Global.BtceMarket.setKeys(ApiKeys.createApiKeys(Global.passphrase , apiKey, privateKey,Constant.BTCE));
                    Global.BtceMarket.setTrade(new BtceTrading(Global.BtceMarket.getKeys()));
                    Global.BtceMarket.setKeysValid(true);

                    break; 
                    }
                case Constant.BITSTAMP :{
                    Global.BitstampMarket.setKeys(ApiKeys.createApiKeys(Global.passphrase , apiKey, privateKey,clientID,Constant.BITSTAMP));
                    Global.BitstampMarket.setTrade(new BitStampTrading(Global.BitstampMarket.getKeys()));                    
                    Global.BitstampMarket.setKeysValid(true);

                    break; 
                    }
                default: { Utils.log("market "+ market+" is not supported", LOG_ERR); break ;  }
                 }

                //Global.dashboard.showDashboard(true);
                //should set the pricer, now is under app. Mumble
                //setPriceCheker(new ScheduledOperations(Settings.PRICE_CHECK_INTERVAL, Global.tradeGox)); //TODO
                //Global.strategy = new Strategy(Settings.STRATEGY_PATH);
                DashboardController dbc = Global.dashBoardController.getController();
                DashboardOptionsController controller = dbc.getOptionsController().getController();                controller.updateStatusMessages();  
                toClose.close();
        }
     
      class ValidationHandler implements EventHandler<KeyEvent>
    {
        private Stage toClose;
        public ValidationHandler(Stage toClose)
        {
            this.toClose= toClose;
        }
            
        @Override
        public void handle(KeyEvent t) {
            if(t.getCode()==KeyCode.ENTER) //when the user press enter
            {
                 if(areKeysValidated)
                 {
                   //save
                   System.out.println("here 1");
                   String apiKey=  apikeyField.getText();
                   String privateKey = secretkeyField.getText();
                   String clientID ="";
                   if(market.equals(Constant.BITSTAMP))
                   {
                       clientID = clientIDField.getText();
                   }
                   saveAction(privateKey,apiKey,clientID, toClose); 
                   
                 }
                 else
                 {
                     System.out.println("here 2");
                     validateKeys(false);
                 }
            }
            else{
                //button different from enter
                if(areKeysValidated && 
                            t.getCode()!= KeyCode.TAB &&
                            t.getCode()!= KeyCode.KP_DOWN &&
                            t.getCode()!= KeyCode.KP_LEFT &&
                            t.getCode()!= KeyCode.KP_RIGHT &&
                            t.getCode()!= KeyCode.KP_UP &&
                            t.getCode()!= KeyCode.LEFT &&
                            t.getCode()!= KeyCode.RIGHT &&
                            t.getCode()!= KeyCode.UP &&
                            t.getCode()!= KeyCode.DOWN
                        )
                {
                    setNotValidState("Please, insert valid keys");
                    areKeysValidated = false;
                }
            }
        }
    }
      
        class SkipSaveHandler implements EventHandler<ActionEvent>
        {
        private String type;
        private Stage toClose;
        public final static  String SAVE ="Save";
        public final static String SKIP ="Cancel";
        
            public SkipSaveHandler(Stage toClose)
            {
                this.toClose=toClose;
            }
            
            
        @Override
        public void handle(ActionEvent t) {
            String apiKey=  apikeyField.getText();
            String privateKey = secretkeyField.getText();
            String clientID = "";
            if(market.equals(Constant.BITSTAMP))
            {
                clientID = clientIDField.getText();
            }
            switch (this.getType()) {
                case SAVE:
                    saveAction(privateKey,apiKey,clientID, toClose);
                    break;
                case SKIP:
                    if(Utils.apiKeysSaved(Utils.getMarketFromName(market)))
                    {
                        Dialogs.DialogResponse response = Dialogs.showConfirmDialog(owner, 
                                                        "The bot will use the credential you set before. Are you sure?",
                                                        "You didn't changed credentials.",
                                                        "The bot is not affected.",Dialogs.DialogOptions.YES_NO);

                         //check confirm dialog
                         if(response.equals(Dialogs.DialogResponse.YES) || response.equals(Dialogs.DialogResponse.OK) )
                              { 
                                  //Global.dashboard.showDashboard(true);
                                  toClose.close(); 
                                  DashboardController dbc = Global.dashBoardController.getController();
                                  DashboardOptionsController controller = dbc.getOptionsController().getController();
                                  controller.updateStatusMessages();  
                              }                  
                    }
                    else
                    {
                        Dialogs.DialogResponse response = Dialogs.showConfirmDialog(owner, 
                                                        "The bot will not work on "+market+" if you skip this step. Are you sure?",
                                                        "You have to do it sooner or later.",
                                                        "The bot will not work.",Dialogs.DialogOptions.YES_NO);

                         //check confirm dialog
                         if(response.equals(Dialogs.DialogResponse.YES) || response.equals(Dialogs.DialogResponse.OK) )
                              { 
                                  //Global.dashboard.showDashboard(true);
                                  toClose.close();
                                  DashboardController dbc = Global.dashBoardController.getController();
                                  DashboardOptionsController controller = dbc.getOptionsController().getController();                              controller.updateStatusMessages();  
                              }
                    }
                    break;
            }
            
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }

        
            
        }
}

