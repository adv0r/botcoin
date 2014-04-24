
package io.botcoin.api;

import io.botcoin.global.Constant;
import io.botcoin.utils.Utils;
import static io.botcoin.utils.Utils.LOG_ERR;


/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class ApiKeys {

    public static final String VALID_KEYS = "These keys are valid! Save them to complete the setup";
    
    public static String validate(String secret, String api, String clientID,String market) {
            TradeInterface trade = null;
            switch(market){
                case Constant.MTGOX : { trade = new MtGoxTrading(new ApiKeys(secret, api)) ;break; }
                case Constant.BTCE : { trade = new BtceTrading(new ApiKeys(secret, api)); break; }
                case Constant.BITSTAMP : { trade = new BitStampTrading(new ApiKeys(secret, api,clientID)); break; }
                default: { Utils.log("market "+ market+" is not supported", LOG_ERR); return "market"+ market+" not supported" ;  }
            }               
            
            ApiPermissions permissions = trade.getPermissions();
            String toReturn = "" ;
            if (!permissions.isValid_keys())
            {
                toReturn = "Authentication failed. Read the tutorial if the problem persist.";
            }
            else if(permissions.isGet_info() && permissions.isTrade())
            {
               toReturn = VALID_KEYS;
            }
            else{
              toReturn= "The keys of "+market+" works but some permission is missing. Activate at least : get_info and trade";
            }
            return toReturn;
    }

//Class Variables
private String secretKey,apiKey;

private String clientID;

//Constructor (private) use the static method loadKeysFromFile instead
public ApiKeys(String secretKey, String apiKey)
{
    this.secretKey=secretKey;
    this.apiKey=apiKey;
    this.clientID="";
}

//Constructor (private) use the static method loadKeysFromFile instead
public ApiKeys(String secretKey, String apiKey, String clientID)
{
    this.secretKey=secretKey;
    this.apiKey=apiKey;
    this.clientID=clientID;
}


//Methods



/**
 * @return the privateKey
 */
public String getClientID() {
    return clientID;
}

/**
 * @param privateKey the privateKey to set
 */
public void setClientID(String ClientID) {
    this.clientID = ClientID;
}


/**
 * @return the privateKey
 */
public String getPrivateKey() {
    return secretKey;
}

/**
 * @param privateKey the privateKey to set
 */
public void setPrivateKey(String privateKey) {
    this.secretKey = privateKey;
}

/**
 * @return the apiKey
 */
public String getApiKey() {
    return apiKey;
}

/**
 * @param apiKey the apiKey to set
 */
public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
}



public static ApiKeys createApiKeys(String passphrase, String apiKey, String secret, String market)
{
    String secret_path = Utils.getMarketFromName(market).getPATH_TO_SECRET(); 
    String api_path = Utils.getMarketFromName(market).getPATH_TO_API(); 
    
    Utils.encodeToFile(apiKey, passphrase, api_path);
    Utils.encodeToFile(secret, passphrase, secret_path);

    return new ApiKeys(secret,apiKey);
}


public static ApiKeys createApiKeys(String passphrase, String apiKey, String secret, String clientID , String market)
{
    String secret_path = Utils.getMarketFromName(market).getPATH_TO_SECRET(); 
    String api_path = Utils.getMarketFromName(market).getPATH_TO_API(); 
    String clientID_path = Utils.getMarketFromName(market).getPATH_TO_CLIENTID(); 

    Utils.encodeToFile(apiKey, passphrase, api_path);
    Utils.encodeToFile(secret, passphrase, secret_path);
    Utils.encodeToFile(clientID, passphrase, clientID_path);

    return new ApiKeys(secret,apiKey,clientID);
}


private static String decryptStringFromFile(String pathToFile, String passphrase)
{
    return Utils.decode(pathToFile, passphrase);
}

  public static ApiKeys loadKeysFromFile(String passphrase, String market)
{   
    ApiKeys toReturn = null;
    String secret_path= Utils.getMarketFromName(market).getPATH_TO_SECRET();
    String api_path= Utils.getMarketFromName(market).getPATH_TO_API();
    
    String secret = decryptStringFromFile(secret_path,passphrase);
    String api = decryptStringFromFile(api_path,passphrase);
    
    String clientID="";
    if(market.equals(Constant.BITSTAMP))
    {
        String clientID_path = Utils.getMarketFromName(market).getPATH_TO_CLIENTID();
        clientID = decryptStringFromFile(clientID_path,passphrase);
        if(!(secret.equals("-1") || api.equals("-1") || clientID.equals("-1") ))
            toReturn = new ApiKeys(secret, api,clientID); //Correct passphrase
    }
    
    else 
        if(!(secret.equals("-1") || api.equals("-1")))
        {
           toReturn = new ApiKeys(secret, api); //Correct passphrase
        }
    return toReturn; //Wrong passphrase default
}
  
  public static ApiPermissions getMtGoxApiPermissions(String secret, String api)
  {
      MtGoxTrading gox = new MtGoxTrading(new ApiKeys(secret, api));
      ApiPermissions permissions = gox.getPermissions();
     return permissions;
  }
    
    
}
