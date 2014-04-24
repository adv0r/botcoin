/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.api;

import io.botcoin.models.Balance;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.utils.Utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author advanced
 */
public class BtceTrading implements TradeInterface{

    private ApiKeys keys;
    
    private final String SIGN_HASH_FUNCTION = "HmacSHA512";
    private final String ENCODING = "UTF-8";
        
    private final String API_BASE_URL="https://btc-e.com/tapi/";
    private final String API_GET_INFO = "getInfo";
    private final String API_TRADE = "Trade";

    
    private final String API_TICKER_USD = "https://btc-e.com/api/2/btc_usd/ticker";
    private final String API_TICKER_EUR = "https://btc-e.com/api/2/btc_eur/ticker";
    private final String API_TICKER_LTC = "https://btc-e.com/api/2/ltc_usd/ticker";

    private final String TOKEN_ERR = "error";
    private final String NO_CONNECTION = "NO CONNECTION WITH BTCE";
    
    private final String ERR_RATE = "Not executed. Error while getting last market price";


      public BtceTrading(ApiKeys keys) {
        this.keys = keys;
    }
      public BtceTrading()
      {
        //Use this constructur for public API calls (e.g. tikers, lag)
      }


    @Override
    public Balance getBalance() {
        String path = API_GET_INFO;
        HashMap<String, String> query_args = new HashMap<>();
         /*Params
         * 
         */
        
         Balance balance = new Balance();
         String queryResult = query(API_BASE_URL,API_GET_INFO, query_args); 
         if (queryResult.startsWith(TOKEN_ERR))
         {          
            return new Balance(-1,-1,-1,-1);
         }
        
        if (queryResult.equals(NO_CONNECTION))
         {
            return new Balance(-1,-1,-1,-1);
         }
         
         /*Sample result
         *{
	 *"success":1,
	 *"return":{
	 *	"funds":{
	 *		"usd":325,
	 *		"btc":23.998,
	 *		"sc":121.998,
	 *		"ltc":0,
	 *		"ruc":0,
	 *		"nmc":0
	 *	},
	 *	"rights":{
	 *		"info":1,
	 *		"trade":1
	 *	},
	 *	"transaction_count":80,
	 *	"open_orders":1,
	 *	"server_time":1342123547
         *      }
         *}
         */
         JSONParser parser=new JSONParser();
         try {
             JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             long success = (long)httpAnswerJson.get("success"); 
             if(success==0)
              {
                //error
                String error = (String)httpAnswerJson.get("error");
                Utils.log("Btce returned an error: "+error,Utils.LOG_ERR);
                balance.setBitcoins(-1);
                balance.setDollars(-1);
                balance.setLitecoins(-1);
                balance.setEuros(-1);
              }
             else
             {
                //correct
                JSONObject dataJson = (JSONObject)httpAnswerJson.get("return");  
                JSONObject funds = (JSONObject)dataJson.get("funds");  

                balance.setBitcoins((Double)funds.get("btc"));
                balance.setDollars((Double)funds.get("usd"));
                balance.setLitecoins((Double)funds.get("ltc"));
                balance.setEuros(-1);

             }
           } 
         catch (ParseException ex) {      
            Utils.log(ex,Utils.LOG_ERR);
            return new Balance(-1,-1,-1,-1);
        }
        
        return balance;
          
    }

    @Override
    public String sellBTC(double amount) {
        double rate = getLastRate("sell","btc","usd");
        if(rate!=-1)
            return enterOrder("sell","btc_usd",amount,rate);
        return ERR_RATE;
    }
    
     @Override
    public String sellLTC(double amount) {
        double rate = getLastRate("sell","ltc","usd");
        if(rate!=-1)
            return enterOrder("sell","ltc_usd",amount,rate);
        return ERR_RATE;
    }

    @Override
    public String buyLTC(double amount) {
        double rate = getLastRate("buy","ltc","usd");
        if(rate!=-1)        
            return enterOrder("buy","ltc_usd",amount,rate);
        return ERR_RATE;        
    }

    @Override
    public String buyBTC(double amount) {
        double rate = getLastRate("buy","btc","usd");
        if(rate!=-1)        
            return enterOrder("buy","btc_usd",amount,rate); 
        return ERR_RATE;
    }

    private String enterOrder(String type, String pair, double amount, double rate)
    {
        String order_id = "";

        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("pair", pair);
        query_args.put("type", type);
        query_args.put("rate", Double.toString(rate));
        query_args.put("amount", Double.toString(amount));

        String queryResult = query(API_BASE_URL,API_TRADE, query_args); 
 
        /* Sample Answer
         * {
	"success":1,
	"return":{
		"received":0.1,
		"remains":0,
		"order_id":0,
		"funds":{
			"usd":325,
			"btc":2.498,
			"sc":121.998,
			"ltc":0,
			"ruc":0,
			"nmc":0
		}
            }
          }	
         */
        
        JSONParser parser=new JSONParser();
         try {
             JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             long success = (long)httpAnswerJson.get("success"); 
             if(success==0)
              {
                //error
                String error = (String)httpAnswerJson.get("error");
                Utils.log("Btce returned an error: "+error,Utils.LOG_ERR);
                order_id = "Error : "+error;
              }
             else
             {
                //correct
                JSONObject dataJson = (JSONObject)httpAnswerJson.get("return");  
                order_id = ""+ (long)dataJson.get("order_id");  
             }
           } 
         catch (ParseException ex) {      
            Utils.log(ex,Utils.LOG_ERR);
        }
                
        return order_id;
    }
    
    private double getLastRate(String type, String coin, String currency) {
        double rate = -1; 
        
        //Switch coin
        String url="";
        if (coin.equalsIgnoreCase("BTC"))
        {
            url=API_TICKER_USD ;
        }
        else if (coin.equalsIgnoreCase("LTC"))
        {
            url=API_TICKER_LTC;
        }
        else 
        {          
            Utils.log("GetLastRate , not supported "+ coin,Utils.LOG_ERR);
            return -1;
        }
        
        //switch type
        if(type.equals("sell"))
        {
           //SELL
           rate=getSellPrice(url);
        }
        else 
        {
            //BUY
            rate=getBuyPrice(url);
        }
        return rate;
    }
   

    @Override
    public double getLastPriceUSD(String coin) {
        String url ="";
        if (coin.equalsIgnoreCase("BTC"))
        {
            url=API_TICKER_USD ;
        }
        else if (coin.equalsIgnoreCase("LTC"))
        {
            url=API_TICKER_LTC;
        }
        else
        {
            Utils.log("Btc-e API does not support "+coin);
            return -1;
        }
        return getLastPrice(url);       
    }

    @Override
    public double getLastPriceEUR(String coin) {
        String url ="";
        if (coin.equalsIgnoreCase("BTC"))
        {
            url=API_TICKER_EUR ;
        }
        else if (coin.equalsIgnoreCase("LTC"))
        {
            Utils.log("Btc-e API does not support "+coin+" in EUR");
            return -1;
        }
        else
        {
            Utils.log("Btc-e API does not support "+coin+" in EUR");
            return -1;
        }    
        return getLastPrice(url);
    }
   
    private double getLastPrice(String url) {
        String path = url;
        double last = -1;
        HashMap<String, String> query_args = new HashMap<>();
         /*Params
         * 
         */
        
        String queryResult = query(url, query_args); 

        /*Sample result 
         * {"ticker":{"high":103.6,"low":100.14944,"avg":101.87472,"vol":3571.71545,"vol_cur":35.02405,"last":102,"buy":103.26,"sell":101.77,"server_time":1369650996}}
         */
         JSONParser parser=new JSONParser();
         try {
             JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             JSONObject ticker = (JSONObject)httpAnswerJson.get("ticker"); 
             
            
             if(ticker.get("last").getClass().equals((new Long((long)10)).getClass())) //If the price is round (i.e. 100) the type will be parsed as Long
             {
                Long l = new Long((long) ticker.get("last"));
                last = l.doubleValue();
             }
             else
             {
                last = (Double)ticker.get("last");
             }
         }
         
        catch (ParseException ex) {     
            Utils.log(ex,Utils.LOG_ERR);
            return -1;
        }
        return last;
        
    }
    
    private double getBuyPrice(String url) {
    double buy = -1;
    HashMap<String, String> query_args = new HashMap<>();
     /*Params
     * 
     */

    String queryResult = query(url, query_args); 

    /*Sample result 
     * {"ticker":{"high":103.6,"low":100.14944,"avg":101.87472,"vol":3571.71545,"vol_cur":35.02405,"last":102,"buy":103.26,"sell":101.77,"server_time":1369650996}}
     */
     JSONParser parser=new JSONParser();
     try {
         JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
         JSONObject ticker = (JSONObject)httpAnswerJson.get("ticker"); 
         buy = (Double)ticker.get("buy");
     }

    catch (ParseException ex) {     
        Utils.log(ex,Utils.LOG_ERR);
        return -1;
    }
    return buy; 
    }
        

    private double getSellPrice(String url) {
     double sell = -1;
     HashMap<String, String> query_args = new HashMap<>();
      /*Params
      * 
      */

     String queryResult = query(url, query_args); 

     /*Sample result 
      * {"ticker":{"high":103.6,"low":100.14944,"avg":101.87472,"vol":3571.71545,"vol_cur":35.02405,"last":102,"buy":103.26,"sell":101.77,"server_time":1369650996}}
      */
      JSONParser parser=new JSONParser();
      try {
          JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
          JSONObject ticker = (JSONObject)httpAnswerJson.get("ticker"); 
          sell = (Double)ticker.get("sell");
      }

     catch (ParseException ex) {     
         Utils.log(ex,Utils.LOG_ERR);
         return -1;
     }
     return sell; 
    }
    
    
    @Override
    public boolean withdrawBTC(double amount, String dest_address) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public String getLag() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String query(String base,String method, HashMap<String, String> args) {
        BtceService query = new BtceService(base,method, args, keys);
        String queryResult=NO_CONNECTION;
        if(Global.BtceMarket.isConnected())
        {
            queryResult = query.executeQuery(true);
        }
        else 
        {
            Utils.log("The bot will not execute the query, there is no connection to btce", Utils.LOG_ERR);
        }
        return queryResult;
        //TODO should be done by a different thread ...
    }
     
     
    private String query(String url, HashMap<String, String> query_args) {
        BtceService query = new BtceService(url, query_args, keys);
        String queryResult=NO_CONNECTION;
        if(Global.BtceMarket.isConnected())
        {
            queryResult = query.executeQuery(false);
        }
        else 
        {
            Utils.log("The bot will not execute the query, there is no connection to btce", Utils.LOG_ERR);
        }
        return queryResult;
    }

    @Override
    public ApiPermissions getPermissions() {
        String path = API_GET_INFO;
        HashMap<String, String> query_args = new HashMap<>();
         /*Params
         * 
         */
         ApiPermissions permissions = new ApiPermissions(false, false, false, false, false, false);   
         String queryResult = query(API_BASE_URL,API_GET_INFO, query_args); 
         if (queryResult.startsWith(TOKEN_ERR))
         {          
            return permissions;
         }
        
        if (queryResult.equals(NO_CONNECTION))
         {
            return permissions;
         }
             /*Sample result
         *{
	 *"success":1,
	 *"return":{
	 *	"funds":{
	 *		"usd":325,
	 *		"btc":23.998,
	 *		"sc":121.998,
	 *		"ltc":0,
	 *		"ruc":0,
	 *		"nmc":0
	 *	},
	 *	"rights":{
	 *		"info":1,
	 *		"trade":1
	 *	},
	 *	"transaction_count":80,
	 *	"open_orders":1,
	 *	"server_time":1342123547
         *      }
         *}
         */
         JSONParser parser=new JSONParser();
         try {
             JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             long success = (long)httpAnswerJson.get("success"); 
             if(success==0)
              {
                //error
                String error = (String)httpAnswerJson.get("error");
                Utils.log("Btce returned an error: "+error,Utils.LOG_ERR);
              }
             else
             {
                //correct
                JSONObject dataJson = (JSONObject)httpAnswerJson.get("return");  
                JSONObject rightsJson = (JSONObject)dataJson.get("rights");  
                
                long info = (long)rightsJson.get("info");
                long trade = (long)rightsJson.get("trade");
                long withdraw = (long)rightsJson.get("withdraw");
                
                if (info==1)
                {
                    permissions.setGet_info(true);
                }
                if (trade==1)
                {
                    permissions.setTrade(true);
                }                
                if (withdraw==1)
                {
                    permissions.setWithdraw(true);
                }
                
                permissions.setValid_keys(true);
             }
           } 
         catch (ParseException ex) {      
            Utils.log(ex,Utils.LOG_ERR);
        }
        
        return permissions;
    }
    
    
     private class BtceService  {
        protected String base;
        protected String method;
        protected HashMap args;
        protected ApiKeys keys;
        protected String url;

        public BtceService(String base,String method,HashMap<String, String> args, ApiKeys keys)
        {
            this.base = base;
            this.method = method;
            this.args = args;
            this.keys = keys;
        }

        private BtceService(String url, HashMap<String, String> query_args, ApiKeys keys) {
            //Used for ticker, does not require auth
            this.url = url;
            this.args = args;
            this.keys = keys;
            this.base=url;
            this.method="";
        }
        

        
    private String signRequest(String secret, String hash_data) {
       String signature = "";
       
        Mac mac;
        SecretKeySpec key = null;
        
           // Create a new secret key
        try {
            key = new SecretKeySpec(secret.getBytes( "UTF-8"), "HmacSHA512" );
        } catch( UnsupportedEncodingException uee) {
            System.err.println( "Unsupported encoding exception: " + uee.toString());
            return null;
        }
 
        // Create a new mac
        try {
            mac = Mac.getInstance( "HmacSHA512" );
        } catch( NoSuchAlgorithmException nsae) {
            System.err.println( "No such algorithm exception: " + nsae.toString());
            return null;
        }
 
        // Init mac with key.
        try {
            mac.init( key);
        } catch( InvalidKeyException ike) {
            System.err.println( "Invalid key exception: " + ike.toString());
            return null;
        }
            try {
               signature= Hex.encodeHexString( mac.doFinal( hash_data.getBytes( "UTF-8")));
              
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(BtceTrading.class.getName()).log(Level.SEVERE, null, ex);
            }
        return signature;
    }

    private String executeQuery(boolean needAuth) {
                        String answer = "" ;
                        String signature = "" ;
                        String post_data = "" ;
                        boolean httpError = false;
                        HttpsURLConnection connection=null;
                        String nonce = String.valueOf(System.currentTimeMillis()/1000);
                       
                        try {
                            // add nonce and build arg list
                            if(needAuth){
                                args.put("nonce", nonce);  
                                args.put("method", method);  
                                                    
                                post_data= Utils.buildQueryString(args,ENCODING);
                                
                                // args signature with apache cryptografic tools
                                String toHash = post_data;

                                signature = signRequest(keys.getPrivateKey(), toHash);
                            }
                            // build URL
                              
                            URL queryUrl;
                              if(needAuth)
                                  queryUrl = new URL(base);
                              else 
                                  queryUrl = new URL(url);
                              
                            
                            connection = (HttpsURLConnection)queryUrl.openConnection();
                            connection.setRequestMethod("POST"); 
                            
                            // create and setup a HTTP connection

                            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                            connection.setRequestProperty("User-Agent", Settings.APP_TITLE);
                            
                            if(needAuth){
                                connection.setRequestProperty("Key", keys.getApiKey());
                                connection.setRequestProperty("Sign", signature);
                            }

                            connection.setDoOutput(true);
                            connection.setDoInput(true);

                            //Read the response

                            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                            os.writeBytes(post_data);
                            os.close();

                            BufferedReader br = null;
                            boolean toLog=false; 
                            if (connection.getResponseCode() >= 400) {
                                httpError = true;//TODO , if HTTP error, do something else with output!
                                br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
                                toLog = true;
                                }
                            else 
                                br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

                            String output;

                            int logLevel=Utils.LOG_LOW;
                            if(httpError)
                            {
                                logLevel=Utils.LOG_ERR;
                                Utils.log("Post Data: "+post_data,Utils.LOG_ERR);
                            }
                            Utils.log("Query to :" + base + "(method="+method+")" + " , HTTP response : \n",logLevel); //do not log unless is error > 400
                            while ((output = br.readLine()) != null) {
                                        Utils.log(output,logLevel);
                                        answer+=output;
                                    }
                            
                             if (httpError){
                                JSONParser parser=new JSONParser();
                                try {
                                   JSONObject obj2=(JSONObject)(parser.parse(answer));
                                   answer = (String)obj2.get(TOKEN_ERR);

                                } catch (ParseException ex) {
                                   Utils.log(ex,Utils.LOG_ERR);
                               }
                            }
                        } 

                        //Capture Exceptions
                        
                        catch (IllegalStateException ex) {
                             Utils.log(ex,Utils.LOG_ERR);
                        }
                        catch(NoRouteToHostException | UnknownHostException ex){
                            Global.BtceMarket.setConnected(false);
                            Utils.log(ex,Utils.LOG_ERR);
                            answer = NO_CONNECTION;
                        }
                        catch (IOException ex) {
                             Utils.log(ex,Utils.LOG_ERR);
                        }
                        
                            finally
                        {
                            //close the connection, set all objects to null
                            connection.disconnect();
                            connection = null;
                        }
                        return answer;        
        }

    }
    
    
}
