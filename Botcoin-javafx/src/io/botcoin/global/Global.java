
package io.botcoin.global;


import io.botcoin.UI.DashboardController;
import io.botcoin.task.ConnectionThread;
import io.botcoin.user.BotUser;
import io.botcoin.core.App;
import io.botcoin.models.Market;
import io.botcoin.strategy.Strategy;
import io.botcoin.utils.Utils;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class Global {

    //Markets
    public static Market MtgoxMarket;
    public static Market BtceMarket;
    public static Market BitstampMarket;
    
    public static App app;
    
    
    public static boolean startupComplete = false;
    //Stage controlling ApiSetup
    public static FXMLLoader dashBoardController;
       
    public static boolean connectionDetection = false;
    
    public static ConnectionThread connectionChecker;
    public static Thread connectionThread;

    //Variables used by the whole app
    public static BotUser user;
    
    public static Strategy strategy ;

    public static boolean isFirstTime;
        
    public static String passphrase;
    
    public static HostServices hostServices;
    
    public static boolean priceCheckerOn = false;
    public static boolean executeOrdersOn = false;

    
    public static Stage dashboardStage;
           
//Constructor

//Methods
    
    public static void toggleBot(boolean on)
    {
        if(on)
        {
            Global.executeOrdersOn = true;
            Utils.log("the bot is ON",Utils.LOG_LOW);
            DashboardController dbc = (DashboardController) Global.getController(Constant.MAIN_DASHBOARD);
            dbc.getOnOffBtn().setText("Turn OFF");
            dbc.getProgIndicator().setVisible(true);
            dbc.setStatusString("Botcoin is running");
        }
        
        else
        {
            Global.executeOrdersOn = false;
            Utils.log("the bot is OFF",Utils.LOG_LOW);
            DashboardController dbc = (DashboardController) Global.getController(Constant.MAIN_DASHBOARD);
            dbc.getOnOffBtn().setText("Turn ON");
            dbc.getProgIndicator().setVisible(false);
            dbc.setStatusString("Botcoin is idle");
        }
    }
    
    public static Object getController(String controllerName)
    {
    Object toReturn = null;
    DashboardController dbc = Global.dashBoardController.getController();           
     
    switch(controllerName)
        {
            case Constant.MAIN_DASHBOARD : { 
                toReturn = dbc;
                break;
            }
            case Constant.STATUS : { 
                toReturn = dbc.getStatusController().getController();
                break;
            }
            case Constant.MARKETS : { 
                toReturn = dbc.getMarketsController().getController();
                break;}
            case Constant.OPTIONS : { 
                toReturn = dbc.getOptionsController().getController();
                break;
            }
            case Constant.STRATEGY : { 
                toReturn = dbc.getStrategyController().getController();
                break;
            }
            default : { break;}
        }
     return toReturn;  
    }
    
}
