
package io.botcoin.UI.dialogs;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
import io.botcoin.global.Settings;
import io.botcoin.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class LoginDialog  {
    
private Stage parentStage;
private int w = 550;
private int h = 350;

    public LoginDialog(Stage parentStage){
        this.parentStage=parentStage;
    }
    
    public void show() { 
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Stage loginStage = new LoginStage(parentStage,w,h);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setX((bounds.getWidth()/2)-w/2);
        loginStage.setY((bounds.getHeight()/2)-h/2);
        loginStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                Dialogs.showConfirmDialog(parentStage, "Are you sure you want to exit? You will loose the opportunity of making money!"
                        + " If you need help send a mail to support@boicoin.io");
                //TODO read the asnwer instead of just quitting
                System.exit(0);
            }
        });
        loginStage.setOpacity(0.9);
        loginStage.setResizable(false);
        loginStage.show();
    }
}
class LoginStage extends Stage {
private Stage owner;
private WelcomeDialog next;
    private Label registerLabel;
    public LoginStage(Stage owner,int w,int h) {
        super();
        this.owner=owner;
        next= new WelcomeDialog(owner);
        initOwner(owner);
        setTitle(Settings.APP_TITLE + " | Login ");
        Group root = new Group();
        Scene scene = new Scene(root, w, h, Color.WHITE);
        scene.getStylesheets().add(Settings.CSS_BUTTON_FILE_PATH);
        setScene(scene);
        
        
        BorderPane border = new BorderPane();
        HBox hbox = addTop();
        border.setTop(hbox);
        
        border.setCenter(addGridPane());

        
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
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(15));
        gridpane.setHgap(15);
        gridpane.setVgap(15);
        
        Label userNameLbl = new Label("Username: ");
                
        Label passwordLbl = new Label("Password: ");

        final TextField userNameFld = new TextField();
        userNameFld.setPromptText("<your@email.here>");

        final PasswordField passwordFld = new PasswordField();
        passwordFld.setPromptText("<your password>");

        CheckBox cb = new CheckBox("Don't ask me again");
        
        Button cancelBtn = new Button("Cancel");
        
        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        //TODO
                        Utils.launchBrowser(Settings.URL_LOST_PWD);
                    } catch (Exception ex) {
                        Utils.log(ex, Utils.LOG_ERR);
                    }
                }
            });
        
                
        Hyperlink registerLink = new Hyperlink("Don't have an account yet? Register");
        registerLink.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        //TODO
                        Utils.launchBrowser(Settings.URL_REGISTER);
                    } catch (Exception ex) {
                        Utils.log(ex, Utils.LOG_ERR);
                    }
                }
            });
        
        
        // or you can use  stiles from http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
        // ButtonBuilder.create().text("LoginDialog").id("ipad-grey").build();
        
        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);
         loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 close();
                 next.show();
            }
        });
        
           
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });        
        
        gridpane.add(userNameLbl, 0, 0);
        gridpane.add(userNameFld, 1, 0);
        
        gridpane.add(passwordLbl, 0, 1);
        gridpane.add(passwordFld, 1, 1); 
        
        gridpane.add(cb, 1, 2);    
        gridpane.add(loginBtn, 2, 2);
         
        gridpane.add(forgotLink, 0, 3);
        gridpane.add(registerLink, 1, 3);

   
        GridPane.setHalignment(loginBtn, HPos.CENTER);
        GridPane.setHalignment(forgotLink, HPos.CENTER);
        
        GridPane.setHalignment(passwordFld, HPos.CENTER);
        GridPane.setHalignment(userNameFld, HPos.CENTER);
        
        return gridpane;
    }
   
}
