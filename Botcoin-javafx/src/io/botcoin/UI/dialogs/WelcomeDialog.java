
package io.botcoin.UI.dialogs;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
import io.botcoin.global.Settings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class WelcomeDialog  {
    
private Stage parentStage;
private int w = 670;
private int h = 550;
    private WelcomeStage welcomeStage;

    public WelcomeDialog(Stage parentStage){
        this.parentStage=parentStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
      
        welcomeStage = new WelcomeStage(parentStage,w,h);
        welcomeStage.initModality(Modality.APPLICATION_MODAL);
        welcomeStage.setX((bounds.getWidth()/2)-w/2);
        welcomeStage.setY((bounds.getHeight()/2)-h/2);
        welcomeStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        welcomeStage.setOpacity(0.9);
        welcomeStage.setResizable(false);
    }
    
    public void show() { 

        welcomeStage.show();
    }
}
class WelcomeStage extends Stage {
  private Stage owner;
    private Button nextBtn;

    public WelcomeStage(Stage owner,int w,int h) {
        super();
        this.owner=owner;

        initOwner(owner);
        setTitle(Settings.APP_TITLE + " | Welcome (1/3) ");
        
        Group root = new Group();
        Scene scene = new Scene(root, w, h, Color.WHITE);
        scene.getStylesheets().add(Settings.CSS_BUTTON_FILE_PATH);
        setScene(scene);
        
        BorderPane border = new BorderPane();
        HBox hbox = addTop();
        border.setTop(hbox);
        
        //addStackPane(hbox);         // Add stack to HBox in top region

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
 
    private Browser addGridPane() {
      //see http://docs.oracle.com/javafx/2/ui_controls/editor.htm
       return new Browser();
     
    }

     
    private Node addBottom() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);


        Image nextImg = new Image(Settings.PATH_TO_NEXT);
        nextBtn = new Button("Next",new ImageView(nextImg));
        nextBtn.setPrefSize(100, 20);

        nextBtn.setDefaultButton(true);
        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //new PassphraseDialog(owner).show();
                close();
            }
        });
        
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(nextBtn);

        return hbox;    
    }
    
    class Browser extends Region {
 
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
     
    public Browser() {
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
        webEngine.load(Settings.WELCOME_MESSAGE_FREE_URL);
        //add the web view to the scene
        getChildren().add(browser);
 
    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 600;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 300;
    }
}
   
}
