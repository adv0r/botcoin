
package io.botcoin.utils;

import io.botcoin.UI.dialogs.PassphraseDialog;
import io.botcoin.UI.dialogs.PassphrasePrompt;
import io.botcoin.api.ApiKeys;
import io.botcoin.api.BitStampTrading;
import io.botcoin.api.BtceTrading;
import io.botcoin.api.MtGoxTrading;
import io.botcoin.api.TradeInterface;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.models.Market;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class Utils {
    public final static int LOG_TRANSACTION = 1035; //Use it to log Mtgox Orders and withdrawals
    public final static int LOG_ERR = 2000; //Use it to log errors
    public final static int LOG_LOW = 100; //Will not log on file, only print on screen
    public final static int LOG_MID = 1000; //Default, equivalent of calling Utils.log with no params

    public final static Logger LOGGER = Logger.getLogger(Utils.class.getName());
    
//Class Variables


//Methods
    //Use it to log messages of normal importance
    public static void log(Object what)
    {
        LOGGER.info(what+"");
    }
    
    public static void log(Object what,int level)
    {
        if(level==LOG_ERR)
        {
            LOGGER.severe(what+"");
        }
        else if(level==LOG_TRANSACTION) 
        {
            LOGGER.warning(what+"");
        }
        else if(level==LOG_LOW)
        {
            LOGGER.fine(what+""); //print it to screen but don't log it
            System.out.println(what);  
        }
        else
            log(what);//default
    }
    
    
    public static String encodeToFile(String originalString, String passphrase,String pathToOutput) 
    {
       String encodedString="";
       MessageDigest digest;
       try {
           
            //System.out.println("Writing " +originalString +" to "+ pathToOutput +" with \npassphrase = "+passphrase);

            //Encapsule the passphrase in a 16bit SecretKeySpec key
            digest = MessageDigest.getInstance("SHA");
            digest.update(passphrase.getBytes());
            SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

            //Cypher the message
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, key);
           
            byte[] ciphertext = aes.doFinal(originalString.getBytes());
            encodedString = new String(ciphertext);

            FileUtils.writeByteArrayToFile(new File(pathToOutput), ciphertext);
            
            if(Utils.isWindowsPlatform())
            {
                //TODO hide the file
            }
        } 
       catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException |InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Utils.log(ex,LOG_ERR);
        }
        
       return encodedString;
    }
    
       public static String decode(String pathToFile, String passphrase)
    {
       String clearString=null;
       MessageDigest digest;
        try {
            //Encapsule the passphrase in a 16bit SecretKeySpec key
            digest = MessageDigest.getInstance("SHA");
            digest.update(passphrase.getBytes());
            SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
            
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, key);
            
            byte[] ciphertextBytes = FileUtils.readFileToByteArray(new File(pathToFile));
            clearString = new String(aes.doFinal(ciphertextBytes));

        } 
        catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
            Utils.log(ex,LOG_ERR);
            return "-1";
        }
       return clearString;
    }

    
    public static double round(double value,int decimalDigits)
    {
        long factor = (long) Math.pow(10, decimalDigits);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
 public static boolean isWindowsPlatform()
    {
        String os = System.getProperty("os.name");
        if ( os != null && os.startsWith("Windows"))
            return true;
        else
            return false;
    }
    
    public static boolean isMacPlatform()
    {
        String os = System.getProperty("os.name");
        if ( os != null && os.startsWith("Mac"))
            return true;
        else
            return false;
    }
    
	public static void launchBrowser(String url)
	{
            Global.hostServices.showDocument(url);
	}
        
    //Build the query string given a set of query parameters
    public static String buildQueryString(HashMap<String, String> args, String encoding) {
        String result = new String();
        for (String hashkey : args.keySet()) {
            if (result.length() > 0) result += '&';
            try {
                result += URLEncoder.encode(hashkey, encoding) + "="
                        + URLEncoder.encode(args.get(hashkey), encoding);
            } catch (Exception ex) {
                Logger.getLogger(MtGoxTrading.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }  
    
    
     public static double getLastSavedPrice(String currency, String coin,String market)
        {
        double toReturn = -1;
        switch(market){
            case Constant.MTGOX : {
                if (currency.equals(Constant.USD))
                {
                    toReturn = Global.MtgoxMarket.getLastBTC_USD();
                }
                else if(currency.equals(Constant.EUR))
                {
                    toReturn =  Global.MtgoxMarket.getLastBTC_EUR();
                }
                else 
                {
                    log("currency "+ currency+" is not supported", LOG_ERR);
                }
                break;
            }
            case Constant.BTCE : {
                if (currency.equals(Constant.USD))
                {
                    if (coin.equals(Constant.BTC))
                        toReturn = Global.BtceMarket.getLastBTC_USD();   
                    else if (coin.equals(Constant.LTC))
                        toReturn = Global.BtceMarket.getLastLTC_USD();                      
                }
                else 
                {
                    log("currency "+ currency+" is not supported", LOG_ERR);
                }
                break;
            }
            case Constant.BITSTAMP : {
                if (currency.equals(Constant.USD))
                {
                     toReturn = Global.BitstampMarket.getLastBTC_EUR();          
                }
                else 
                {
                    log("currency "+ currency+" is not supported", LOG_ERR);
                }
                break;
            }
            default: {
                log("market "+ market+" is not supported", LOG_ERR);
                break;
            }
        }
       return toReturn;
    }
    

          
      
    public static String toHex(String arg) {
    return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
}
     
    public static boolean apiKeysSaved(Market market) {
        boolean exist=false;
        String secret_path= market.getPATH_TO_SECRET();
        String api_path= market.getPATH_TO_API();
         
        if((new File(secret_path).exists()) && (new File(api_path).exists()) )
        {
            exist = true;
        }           
        return exist;    
    }
      
    
    public static void eraseKeys(Market market)
    {
        market.setKeysValid(false);
        market.setKeys(null);
        market.setTrade(null);

        FileSystem.deleteFile(market.getPATH_TO_SECRET());
        FileSystem.deleteFile(market.getPATH_TO_API());
    }

    public static Market getMarketFromName(String marketString)
    {
        Market toReturn=null;
         switch(marketString){
                case Constant.MTGOX : {  return Global.MtgoxMarket; }
                case Constant.BTCE : {  return Global.BtceMarket; }
                case Constant.BITSTAMP : {  return Global.BitstampMarket; }
                default: { Utils.log("market "+ marketString+" is not supported", LOG_ERR); break ;  }
            }   
         return toReturn;
    }

    public static void initMarket(Market market,ApiKeys keys)
    {
        switch(market.getName()){
           case Constant.MTGOX : { 
               Global.MtgoxMarket.setKeys(keys);
               Global.MtgoxMarket.setKeysValid(true);
               Global.MtgoxMarket.setTrade(new MtGoxTrading(keys));
               break; 
               }
           case Constant.BTCE : {
               Global.BtceMarket.setKeys(keys);
               Global.BtceMarket.setKeysValid(true);
               Global.BtceMarket.setTrade(new BtceTrading(keys));
               break; 
               }
           case Constant.BITSTAMP :{
               Global.BitstampMarket.setKeys(keys);
               Global.BitstampMarket.setKeysValid(true);
               Global.BitstampMarket.setTrade(new BitStampTrading(keys));
                   break; 
               }
           default: { System.out.println("market "+ market+" is not supported"); break ;  }
       }            
    }
    
    public static String getTimestamp()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
