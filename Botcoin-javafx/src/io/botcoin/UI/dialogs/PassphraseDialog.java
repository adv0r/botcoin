
package io.botcoin.UI.dialogs;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */

import io.botcoin.api.ApiKeys;
import io.botcoin.core.App;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.models.Market;
import io.botcoin.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class PassphraseDialog  {
    
private Stage parentStage;
private int w = 600;
private int h = 450;
private boolean oldPwdExists;
private String oldPwd;
private App app;
    public PassphraseDialog(Stage parentStage, App app, boolean oldPwdExists,String oldPwd){
        this.parentStage=parentStage;
        this.app = app;
        this.oldPwdExists = oldPwdExists;
        this.oldPwd = oldPwd;
    }
    
    public void show() { 
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
       
        Stage passpStage = new PasspStage(parentStage,w,h,app,oldPwdExists,oldPwd);
        passpStage.initModality(Modality.APPLICATION_MODAL);
        passpStage.setX((bounds.getWidth()/2)-w/2);
        passpStage.setY((bounds.getHeight()/2)-h/2);
        passpStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        
        passpStage.setOpacity(0.9);
        passpStage.setResizable(false);
        passpStage.show();
    }
}
class PasspStage extends Stage {
    private Stage owner;
    private App app;
  //private WelcomeDialog back;
    private PasswordField passphraseField;
    private PasswordField passphrase2Field;
    private Label infoLabel;
    private Button nextBtn;
    private boolean oldPwdExists;
    public PasspStage(Stage owner,int w,int h,App app,boolean oldPwdExists,String oldPwd) {
        super();
        this.owner=owner;
        this.app=app;
        this.oldPwdExists = oldPwdExists;
        //back = new WelcomeDialog(owner);
        initOwner(owner);
        setTitle(Settings.APP_TITLE + " | Choose a passphrase ");
        
        Group root = new Group();
        Scene scene = new Scene(root, w, h, Color.WHITE);
        scene.getStylesheets().add(Settings.CSS_BUTTON_FILE_PATH);
        setScene(scene);
        
        BorderPane border = new BorderPane();
        HBox hbox = addTop();
        border.setTop(hbox);
        
        border.setCenter(addGridPane());
        
        border.setBottom(addBottom());

        root.getChildren().add(border);
    }
    
    public HBox addTop() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        ImageView logo = new ImageView(new Image(Settings.PATH_TO_LOGO));
        hbox.getChildren().addAll(logo);
        return hbox;
}  
 
    private Node addGridPane() {
      //see http://docs.oracle.com/javafx/2/ui_controls/editor.htm
        
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       
       infoLabel = new Label("Insert a passphrase to encrypt your data and protect the application. \n"
               + " Use your fantasy but don't forget it, \n"
               + " you will need it to open the application.\n "
               + " Passphrase must contain at least "+Settings.MIN_PASS_LENGTH+" characters."); 
       
       passphraseField = new PasswordField(); passphraseField.setPromptText("Insert your encription passphrase");
       passphrase2Field = new PasswordField(); passphrase2Field.setPromptText("Confirm your encription passphrase");
       
       passphraseField.setId("pwdField");
       passphrase2Field.setId("pwdField");

       passphraseField.setMinWidth(400);            
       passphrase2Field.setMinWidth(400);

       TypingHandler typeHandler = new TypingHandler();
       passphrase2Field.addEventHandler(KeyEvent.KEY_RELEASED, typeHandler);
       passphraseField.addEventHandler(KeyEvent.KEY_RELEASED, typeHandler);


       grid.add(infoLabel, 0, 0);
       grid.add(passphraseField, 0, 1);      
       grid.add(passphrase2Field, 0, 2);    
       
       return grid;
     
    }

     
    private Node addBottom() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        /*
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefSize(100, 20);
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });*/
        
        /*Image backImg = new Image(Settings.PATH_TO_BACK);
        Button backBtn = new Button("Back",new ImageView(backImg));
        backBtn.setPrefSize(100, 20);
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                back.show();
                close();
            }
        });
        */
        Image nextImg = new Image(Settings.PATH_TO_NEXT);
        nextBtn = new Button("Done",new ImageView(nextImg));
        
        nextBtn.setPrefSize(100, 20);
        nextBtn.setDisable(true);
        nextBtn.setDefaultButton(true);
        nextBtn.setOnAction(new DoneHandler(app));
        
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(nextBtn);

        return hbox;    
    }

    class DoneHandler implements EventHandler<ActionEvent>
    {
        private App app;
        public DoneHandler(App app){
            this.app = app;
        }
        @Override
        public void handle(ActionEvent t) {
                String pass=passphraseField.getText();
                Utils.encodeToFile(pass, pass, Settings.PATH_TO_SAVED_PW);
                Global.passphrase = pass;
                if (oldPwdExists)
                {
                    Global.passphrase = pass;
                    System.out.println("Checking if keys ar setup"); 
                    
                    //TODO change if file exist, not is is correct!!!
            
                    //Re-encrypt api keys with newly set password

                    if(Utils.apiKeysSaved(Utils.getMarketFromName(Constant.MTGOX)))
                    {   
                        Market m = Global.MtgoxMarket;
                        ApiKeys.createApiKeys(pass,m.getKeys().getApiKey(),m.getKeys().getPrivateKey(),m.getName());
                        Utils.log("Updating "+Constant.MTGOX+" encrypted files with new passphrase");
                    }

                    if(Utils.apiKeysSaved(Utils.getMarketFromName(Constant.BTCE)))
                    {
                        Market m = Global.BtceMarket;
                        ApiKeys.createApiKeys(pass,m.getKeys().getApiKey(),m.getKeys().getPrivateKey(),m.getName());
                        Utils.log("Updating "+Constant.BTCE+" encrypted files with new passphrase");       
                    }

                    if(Utils.apiKeysSaved(Utils.getMarketFromName(Constant.BITSTAMP)))
                    {
                      Market m = Global.BitstampMarket;
                      ApiKeys.createApiKeys(pass,m.getKeys().getApiKey(),m.getKeys().getPrivateKey(),m.getKeys().getClientID(),m.getName()); 
                      Utils.log("Updating "+Constant.BITSTAMP+" encrypted files with new passphrase");               
                    }             
                }
                close();
        }
    }
    
    
    class TypingHandler implements EventHandler<KeyEvent>
    {

        @Override
        public void handle(KeyEvent t) {
            
            String pass=passphraseField.getText();
            String pass2 =passphrase2Field.getText(); 

            if(pass.equals(pass2) && !pass.equals("") && pass.length()>=Settings.MIN_PASS_LENGTH){
                nextBtn.setDisable(false);
             }
            else 
            {
                nextBtn.setDisable(true);
            }
        }
    }
}
