/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.global;

/**
 *
 * https://github.com/adv0r/mtgox-apiv2-java
 * @author adv0r <leg@lize.it>
 * MIT License (see LICENSE.md)
 */

public class Settings {
    
    public static final boolean TEST = true; /* DANGER ZONE ! Setting this to false will trigger rules execution!*/
   
    //App default width and height
    public static final int WIDTH = 910    ;
    public static final int HEIGHT = 550; 
    
    //Paths
    public static final String RES_PATH = "resources/";  //Path to the folder cointaing resources
    public static final String DATA_PATH = RES_PATH+"data/";
    
    //Folder cointaining encrypted stuff (api keys and pwd)
    public static final String HIDDEN_FOLDER = DATA_PATH + ".keys/";
   
    public static final String PATH_TO_SAVED_PW = HIDDEN_FOLDER+".7ZOmGUwSWC";
    
    //Css folders -- Deprecated, replaced by fxml
    public static final String CSS_FOLDER_PATH = "file:"+RES_PATH+"css/"; //Folder cointaining CSS
    public static final String CSS_FILE_PATH = CSS_FOLDER_PATH+"test.css"; //File cointaining CSS

    public static final String CSS_BUTTON_FILE_PATH = CSS_FOLDER_PATH+"button.css"; //File cointaining CSS
    public static final String CSS_DASHBOARD_FILE_PATH = CSS_FOLDER_PATH+"dashboard.css"; //File cointaining CSS

    //Strategy
    public static final String STRATEGY_PATH = DATA_PATH+"strategy.json"; //File cointaining the Strategy
    
    //Logs
    public static final String LOG_PATH = "logs/"; //Folder cointaining the logs
    
    //SSL Keystore
    public static final String KEYSTORE_PATH = RES_PATH+"ssl/botcoin_keystore.jks";
    public static final String KEYSTORE_PWD = "h4rdc0r_";   //password used to encrypt the keystore
    
    public static final String PATH_TO_LOGO =  "file:"+RES_PATH+"img/logo.png";
    
    
    //ICONS
    public static final String PATH_TO_ICON = RES_PATH+"img/icon_transparent_64.png";
    public static final String PATH_TO_NEXT =  "file:"+RES_PATH+"img/right_16.png";
    public static final String PATH_TO_BACK =  "file:"+RES_PATH+"img/left_16.png";
    public static final String PATH_TO_SAVE =  "file:"+RES_PATH+"img/save_16.png";
    public static final String PATH_TO_OK =  "file:"+RES_PATH+"img/tick_16.png";
    public static final String PATH_TO_WRONG =  "file:"+RES_PATH+"img/warning_16.png";
    public static final String PATH_TO_DELETE =  "file:"+RES_PATH+"img/delete_icon.jpg";
    
    public static final String PATH_TO_SPLASH = "file:"+RES_PATH+"img/splash-screen.png";


    //Market logos
    public static final String PATH_TO_MTGOX_LOGO =  "file:"+RES_PATH+"img/mtgox_logo.jpeg";
    public static final String PATH_TO_BTCE_LOGO =  "file:"+RES_PATH+"img/btce_logo.jpeg";
    public static final String PATH_TO_BITSTAMP_LOGO =  "file:"+RES_PATH+"img/bitstamp_logo.png";

    
    //Mix
    public static final String APP_TITLE = "Botcoin.io";  //Name of the application
    public static final int PRICE_CHECK_INTERVAL = 15;  //Interval in seconds for checking bitcoin price
    public static final int PING_TIME = (PRICE_CHECK_INTERVAL)*(1000);
    
    public static final String WELCOME_MESSAGE_FREE_URL =  "http://botcoin.io/java/messages/welcome_free.html";

    public static final int MIN_PASS_LENGTH=6;
    
    public static final String URL_TO_HOWTO_API = "http://botcoin.io/faq/api-setup";
    public static final String URL_LOST_PWD = "http://botcoin.io/users/password-lost";
    public static final String URL_REGISTER = "http://botcoin.io/users/register";
}
