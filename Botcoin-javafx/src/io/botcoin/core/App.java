/*
 * Copyright Nicolò Paternoster 2013-2015
 * http://botcoin.io
 * mailto: leg@lize.it
 */
package io.botcoin.core;

import io.botcoin.UI.DashboardController;
import io.botcoin.task.ConnectionThread;
import static javafx.application.Application.launch;

import io.botcoin.task.ScheduledOperations;
import io.botcoin.UI.dialogs.PassphraseDialog;
import io.botcoin.UI.dialogs.PassphrasePrompt;
import io.botcoin.api.ApiKeys;
import io.botcoin.global.Constant;
import io.botcoin.strategy.Strategy;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.models.Market;
import io.botcoin.utils.MyLogger;
import io.botcoin.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * 
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class App extends Application {

    //Variables
    private ScheduledOperations priceCheker;
    public Stage primaryStage;
    
    protected boolean firstTime;
    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private static final int SPLASH_WIDTH = 640;
    private static final int SPLASH_HEIGHT = 277;

    protected StartupTask startupTask;
    
    public void initSplash()
    {
        ImageView splash = new ImageView(new Image(Settings.PATH_TO_SPLASH));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
        progressText = new Label("Loading Botcoin. . .");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-border-width:5; -fx-border-color: linear-gradient(to bottom, DarkBlue, derive(DarkBlue, 50%));");
        splashLayout.setEffect(new DropShadow());
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Global.app = new App();

        //Check if the first time the user open the app
            if(isTheFirstTime())
            {
                 firstTime = true;
                 //TODO change this part and ask to se the password after booting up the app for the first time
                 System.out.println("Checking existence of password : not found");
                 PassphraseDialog passDialog = new PassphraseDialog(this.primaryStage,Global.app,false,"");
                 passDialog.show();
            }
            else
            {
                firstTime = false;
                System.out.println("Please enter passphrase");
                askForPassword(this.primaryStage);
   
                Global.hostServices = getHostServices();
                Global.app.startupSplash(primaryStage);              
            }        

    }  
     
    public void startupSplash(Stage primaryStage)
    {
        initSplash();
        startupTask = new StartupTask(primaryStage);
        showSplash(primaryStage, startupTask);
        new Thread(startupTask).start();

    }
    
          
    private void initPriceChecker()
    {
        Global.priceCheckerOn = true;
        Global.app.getPriceCheker().toggle();

        Utils.log("Bot is checking prices",Utils.LOG_LOW);
        DashboardController dbc = (DashboardController) Global.getController(Constant.MAIN_DASHBOARD);
        dbc.setStatusString("Monitoring market");
    }
    
    public void createDashboard()
    {

        URL location = DashboardController.class.getResource(DashboardController.DASHBOARD);

        Global.dashBoardController = new FXMLLoader();
        Global.dashBoardController.setLocation(location);
        Global.dashBoardController.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = null;
        try {
             root = (Parent) Global.dashBoardController.load(location.openStream());
        } 
        catch (IOException ex) {
             Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex); 
         }           


        Global.dashboardStage = new Stage();
        Global.dashboardStage.setTitle(Settings.APP_TITLE + " | Dashboard");

        Global.dashboardStage.setScene(new Scene(root, Settings.WIDTH, Settings.HEIGHT));

        Global.dashboardStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        Global.dashboardStage.setResizable(false);
        Global.dashboardStage.show();

    }
      

   private void showSplash(final Stage initStage, Task task) {
    progressText.textProperty().bind(task.messageProperty());
    loadProgress.progressProperty().bind(task.progressProperty());
    task.stateProperty().addListener(new ChangeListener<Worker.State>() {
      @Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
        if (newState == Worker.State.SUCCEEDED) {
          System.out.println("Boot complete");
          loadProgress.progressProperty().unbind();
          loadProgress.setProgress(1);
          
          createDashboard();
          initPriceChecker();
          initStage.toFront();
          FadeTransition fadeSplash = new FadeTransition(Duration.seconds(0.5), splashLayout);
          fadeSplash.setFromValue(1.0);
          fadeSplash.setToValue(0.0);
          fadeSplash.setOnFinished(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
              initStage.hide();
            }
          });
          fadeSplash.play();
        } // todo add code to gracefully handle other task states.
      }
    });  
    
    Scene splashScene = new Scene(splashLayout);
    initStage.initStyle(StageStyle.UNDECORATED);
    final Rectangle2D bounds = Screen.getPrimary().getBounds();
    initStage.setScene(splashScene);
    //Center
    initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
    initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
    initStage.show();
    
    Global.startupComplete = true; 

   }
      
   public static void main(String[] args) { launch(args); }
 
   
   //Startup task 
   protected class StartupTask extends Task
     {
        private Stage primaryStage;
        public StartupTask(Stage primaryStage)
        {
            this.primaryStage = primaryStage;
        }

        @Override
        protected Object call() throws Exception {
            updateProgress(1, 10);

            Thread.sleep(700); //To give the time to the JVM to wake up
            updateMsg("Booting the app ...");
            updateProgress(2, 10);

            //init logger
            updateMsg("Init logging system");

            try {
                  MyLogger.setup();
            } 
            catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
             }
            Utils.LOGGER.setLevel(Level.INFO);
            updateProgress(3, 10);

            updateMsg("Setting up SSL certificates");
            System.setProperty("javax.net.ssl.trustStore",Settings.KEYSTORE_PATH);
            System.setProperty("javax.net.ssl.trustStorePassword",Settings.KEYSTORE_PWD);
            //System.setProperty("javax.net.debug","ssl"); //Uncomment for debugging SSL    
            updateProgress(4, 10);

            updateMsg("Init markets : Mt.Gox");
            Global.MtgoxMarket = new Market(Constant.MTGOX, false, false, true, false, true, true);
            updateMsg("Init markets : Btc-e");
            Global.BtceMarket = new Market(Constant.BTCE, false, false, true, true, true, false);
            updateMsg("Init markets : Bitstamp");
            Global.BitstampMarket = new Market(Constant.BITSTAMP, false, false, true, false, true, false);
            
            updateProgress(5, 10);

            updateMsg("Creating connection-check thread");

            Global.connectionChecker = new ConnectionThread();
            Global.connectionThread = new Thread(Global.connectionChecker);
            updateProgress(6, 10);    

            updateMsg("Checking operating system");
            System.out.println("mac? "+ Utils.isMacPlatform()+", win? "+Utils.isWindowsPlatform()); //TODO improve
            updateProgress(7, 10);
            updateMsg("Setting up price checker thread");
            setPriceCheker(new ScheduledOperations(Settings.PRICE_CHECK_INTERVAL));
            updateProgress(8, 10);
            
            updateMsg("Reading strategy from local disk");
            Global.strategy = new Strategy(Settings.STRATEGY_PATH);
            updateProgress(9, 10);

            if(!firstTime)
            {
                updateMsg("Decrypting data: validate credentials");
                verifyKeys(this.primaryStage);
            } 

            updateProgress(10, 10);
            updateMsg("Ready to party hard"); 
            
             
            return  FXCollections.<String>observableArrayList(); //TODO remove
        }
        
        
         public void verifyKeys(Stage primaryStage)
        {
            updateMsg("Checking the existence of saved credentials");

            
            //Check Existence of saved api keys
            if(Utils.apiKeysSaved(Utils.getMarketFromName(Constant.MTGOX)))
            {
                updateMsg("Mt. Gox credentials found on disk. Now proceeding to validation");    
                validateKeys(Constant.MTGOX,primaryStage);              
            }
            else
            {  
                updateMsg("Mtgox credentials not found on disk");  
            }
            
            if(Utils.apiKeysSaved(Utils.getMarketFromName(Constant.BTCE)))
            {
                updateMsg("Btc-e credentials found on disk. Now proceeding to validation");  
                validateKeys(Constant.BTCE,primaryStage);
            }
            else
            {  
                updateMsg("Startup : Btc-e credentials not found on disk");  
            }
            if(Utils.apiKeysSaved(Utils.getMarketFromName(Constant.BITSTAMP)))
            {
                updateMsg("Bitstamp credentials found on disk. Now proceeding to validation");  
                validateKeys(Constant.BITSTAMP,primaryStage);
            }          
            else
            {  
                updateMsg("Bitstamp credentials not found on disk");  
            }
            
     }
         
   
    
        private void validateKeys(String market, Stage owner) {
              //Api files exist
              ApiKeys keys = ApiKeys.loadKeysFromFile(Global.passphrase, market); 
              if (keys!=null){
                  //VALIDATE KEYS!
                  String apiKey =  keys.getApiKey();
                  String secretKey = keys.getPrivateKey();
                  String clientID = keys.getClientID();
                  String apiValidMessage = ApiKeys.validate(secretKey, apiKey, clientID,market);
                  if(apiValidMessage.equals(ApiKeys.VALID_KEYS))
                  {
                    //Keys are valid, proceed normally
                    updateMsg(market+" credentials are valid."); 
                    Utils.initMarket(Utils.getMarketFromName(market), keys);
                  }   
                  else
                  {
                      //keys are invalid, ask to reset or skip
                      updateMsg(market+" credentials not valid.");
                     
                  }
               }
              else
                {
                   //Wrong passphrase
                   Dialogs.showErrorDialog(owner,
                           "I encoutered an error while decrypting your api keys. Contact geeks@botcoin.io",
                           "Error",
                           "Unable to load API");
                   Platform.exit();
                   System.exit(0);
                }
            }

   
    
    
    public void updateMsg(String message) {
    try {
        System.out.println("Startup : "+message);
        updateMessage(message);
        Thread.sleep(500-(int)(Math.random()*400));
    } 
    catch (InterruptedException ex) {
        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
   }
    
    

    /**
     * @return the priceCheker
     */
    public ScheduledOperations getPriceCheker() {
        return priceCheker;
    }

    /**
     * @param priceCheker the priceCheker to set
     */
    public void setPriceCheker(ScheduledOperations priceCheker) {
        this.priceCheker = priceCheker;
    }

    private boolean isTheFirstTime() {
        boolean firstTime=true;
        if((new File(Settings.PATH_TO_SAVED_PW).exists()) )
        {
            firstTime = false;
            System.out.println("Enter credential please!");
        }
        Global.isFirstTime=firstTime;
        return firstTime;
    }

    

    private void askForPassword(Stage primaryStage) {
        System.out.println("Asking the user to prompt the password");
            
        String passphrase="";    
        boolean correct = false;
        do
        {
            passphrase = new PassphrasePrompt(true).show(primaryStage);
            String passDecrypt = Utils.decode(Settings.PATH_TO_SAVED_PW, passphrase);
            correct = !passDecrypt.equals("-1");
            if (!correct)
             {
                System.out.println("Startup : Password incorrect"); 
                Dialogs.showErrorDialog(primaryStage,
                        "The passphrase is incorrect",
                        "Error",
                        "Wrong Passphrase");
             }
        }
        while(!correct);

        //Password correct
        
  
        System.out.println("Startup : Password is correct!");
        Global.passphrase=passphrase;  


        }   
}
