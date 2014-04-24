
package io.botcoin.api;

import io.botcoin.models.Balance;
import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.utils.Utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 * https://www.bitstamp.net/api/
 */
public class BitStampTrading implements TradeInterface {
    
   private ApiKeys keys;
    
    private final String SIGN_HASH_FUNCTION = "HmacSHA512";
    private final String ENCODING = "UTF-8";
    
    
    private final String API_BASE_URL="https://www.bitstamp.net/api/";
    private final String API_GET_INFO = API_BASE_URL+"balance/";
    private final String API_SELL = API_BASE_URL+"sell/";
    private final String API_BUY = API_BASE_URL+"buy/";
    private final String API_CONVERSION_RATE = API_BASE_URL+"eur_usd/";
    private final String API_TICKER = API_BASE_URL+"ticker/";
   
    private final String TOKEN_ERR = "error";
    private final String NO_CONNECTION = "NO CONNECTION WITH BITSTAMP";
    
    private final String ERR_RATE = "Not executed. Error while getting last market price";


    public BitStampTrading(ApiKeys keys) {
        this.keys = keys;
    }
    
    public BitStampTrading()
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
         String queryResult = query(path, query_args,true); 
         if (queryResult.startsWith(TOKEN_ERR))
         {          
            return new Balance(-1,-1);
         }
        
        if (queryResult.equals(NO_CONNECTION))
         {
            return new Balance(-1,-1);
         }
         /*Sample result
         *{"btc_reserved": "0", 
         * "fee": "0.5000", 
         * "btc_available": "0",
         * "usd_reserved": "0",
         * "btc_balance": "0", 
         * "usd_balance": "0.00", 
         * "usd_available": "0.00"}
         */ 
        JSONParser parser=new JSONParser();
         try {
             JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             balance.setBitcoins((Double.parseDouble((String)httpAnswerJson.get("btc_balance"))));
             balance.setDollars((Double.parseDouble((String)httpAnswerJson.get("usd_balance"))));
         }
        catch (ParseException ex) {      
            Utils.log(ex,Utils.LOG_ERR);
            return new Balance(-1,-1);
        }        
      return balance; 
    }

    @Override
    public double getLastPriceUSD(String coin) {
        double last = -1; 
        HashMap<String, String> query_args = new HashMap<>();
        String queryResult = query(API_TICKER, query_args,false);
        //TODO improve
        if (queryResult.startsWith(TOKEN_ERR))
            return -1;
        if (queryResult.equals(NO_CONNECTION))
            return -1;  
         /* Result sample :
         * {"high": "130.60", "last": "126.18", "bid": "126.18", "volume": "8559.02897491", "low": "120.33", "ask": "126.20"}
         */
        JSONParser parser=new JSONParser();

        try {
             JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             last = Double.parseDouble((String)httpAnswerJson.get("last"));
          } 
        catch (ParseException ex) {
            Utils.log(ex,Utils.LOG_ERR);
        }
        return last;
    }

    @Override
    public double getLastPriceEUR(String coin) {
        double lastEUR = -1;
        double lastUSD = getLastPriceUSD("BTC");
        if (lastUSD==-1)
            return -1;
        else
        {
            double rate = getConversionRate("sell");//TODO see ticket on bitstamp, why is sell?
            if (rate!=0)
                lastEUR= lastUSD / rate;
            else 
            {
                lastEUR=-1;
                Utils.log("Bitstamp error while getting conversion rate", Utils.LOG_ERR);
            }
        }
            
        return lastEUR;
    }
    
    private double getConversionRate(String type)
    {
        if (!type.equalsIgnoreCase("sell") && !type.equalsIgnoreCase("buy") )
        {   
            Utils.log("Bitstamp API error conversion rate type can only be 'buy' or 'sell'",Utils.LOG_ERR);
            return 0;
        }
        double rate=0;
        HashMap<String, String> query_args = new HashMap<>();
        String queryResult = query(API_CONVERSION_RATE, query_args,false);
        //TODO improve
        if (queryResult.startsWith(TOKEN_ERR))
            return 0;
        if (queryResult.equals(NO_CONNECTION))
            return 0;  
         /* Result sample :
         * {"sell": "1.2885", "buy": "1.2963"}
         */
        JSONParser parser=new JSONParser();

        try {
             JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             rate = Double.parseDouble((String)httpAnswerJson.get(type));
          } 
        catch (ParseException ex) {
            Utils.log(ex,Utils.LOG_ERR);
        }        
        return rate;
    }

   
    @Override
    public String sellBTC(double amount) {
        double price = getLastPrice("sell");
        if(price!=-1)
            {
            return ""+placeOrder("sell",amount,price); 
            }
        return ERR_RATE;
    }

    @Override
    public String buyBTC(double amount) {
        double price = getLastPrice("buy");
        if(price!=-1)
            {
            return ""+placeOrder("buy",amount,price);
            }
        return ERR_RATE;
    }

    
    private long placeOrder(String type,double amount, double price) {
        long order_id = -1;
        String url;
        if (type.equals("sell"))
            url = API_SELL;
        else
            url = API_BUY;

        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("price", Double.toString(price));
        query_args.put("amount", Double.toString(amount));
        
        
        String queryResult = query(url, query_args,true); 

          /* Sample Answer
         * 
         */
        JSONParser parser=new JSONParser();
        try {
            JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
            order_id = (long)httpAnswerJson.get("id");
         }
          catch (Exception ex) {      
            Utils.log("Bitstamp error while placing order. Check the response : "
                    +queryResult,Utils.LOG_ERR);
            return -1;
        }         
        return order_id;
    }

    private double getLastPrice(String type ) {
        double price; 
        //switch type
        if(type.equals("sell"))
        {
           price=getBuyPrice();
        }
        else  //buy
        {
            price=getSellPrice();
        }
        return price;
    }
    
    @Override
    public ApiPermissions getPermissions() {
    ApiPermissions permissions = new ApiPermissions(false, false, false, false, false, false);   

    final String ERROR_STRING = "API access disabled";
    String path = API_GET_INFO;
    HashMap<String, String> query_args = new HashMap<>();
     /*Params
     * 
     */

     String queryResult = query(path, query_args,true); 
     /*Sample result
         *GOOD:
         *
         *{"btc_reserved": "0", 
         * "fee": "0.5000", 
         * "btc_available": "0",
         * "usd_reserved": "0",
         * "btc_balance": "0", 
         * "usd_balance": "0.00", 
         * "usd_available": "0.00"}
         *
         *BAD:
         * {"error": "API access disabled. Check your user settings."}
         */ 
     if (queryResult.equals(NO_CONNECTION))
     {
        Utils.log("BitStamp, error while getting permissions: no connection");
        return permissions;
     }
      JSONObject httpAnswerJson=null;
      JSONParser parser=new JSONParser();
         try {
             httpAnswerJson=(JSONObject)(parser.parse(queryResult));
             String temp = (String)httpAnswerJson.get("usd_balance");
             if(temp!=null)
             {
                permissions.setGet_info(true);
                permissions.setTrade(true);
                permissions.setValid_keys(true);
                permissions.setWithdraw(true);
                permissions.setDeposit(true);
             }
             else
             {
                String error = (String)httpAnswerJson.get("error");
                if (error.contains(ERROR_STRING))
                {
                    Utils.log("Bitstamp API access is disabled in user account",Utils.LOG_MID);
                    return permissions;
                }                 
             }
         }
        catch (ParseException ex) {      
                Utils.log(ex,Utils.LOG_ERR);
            }
   
        return permissions;
    }
    
      
   
    
    
    
     private double getSellPrice() {
     String path = API_TICKER;
     double sell = -1;
     HashMap<String, String> query_args = new HashMap<>();
      /*Params
      * 
      */

     String queryResult = query(path, query_args,false); 

     /*Sample result 
      * {"high": "130.60", "last": "126.18", "bid": "126.18", "volume": "8559.02897491", "low": "120.33", "ask": "126.20"}
      */
      JSONParser parser=new JSONParser();
      try {
          JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
          sell = Double.parseDouble((String)httpAnswerJson.get("ask"));
      }

     catch (ParseException ex) {     
         Utils.log(ex,Utils.LOG_ERR);
         return -1;
         }
     return sell;
     }
      
  
     private double getBuyPrice() {
     String path = API_TICKER;
     double buy = -1;
     HashMap<String, String> query_args = new HashMap<>();
      /*Params
      * 
      */

     String queryResult = query(path, query_args,false); 

     /*Sample result 
      * {"high": "130.60", "last": "126.18", "bid": "126.18", "volume": "8559.02897491", "low": "120.33", "ask": "126.20"}
      */
      JSONParser parser=new JSONParser();
      try {
          JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
          buy = Double.parseDouble((String)httpAnswerJson.get("bid"));
      }

     catch (ParseException ex) {     
         Utils.log(ex,Utils.LOG_ERR);
         return -1;
     } 
     return buy; 
    }
    

    @Override
    public String getLag() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean withdrawBTC(double amount, String dest_address) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String sellLTC(double amount) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String buyLTC(double amount) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO change body of generated methods, choose Tools | Templates.
    }
    
    public String query(String url, HashMap<String, String> args, boolean auth) {
        BitstampService query = new BitstampService(url, args, keys,auth);
        String queryResult=NO_CONNECTION;
        if(Global.BitstampMarket.isConnected())
        {
            if(auth)
                queryResult=query.executeQueryAuth();
            else
                queryResult=query.executeQuery();
        }
        else 
        {
            Utils.log("The bot will not execute the query, there is no connection to Bitstamp", Utils.LOG_ERR);
        }
        return queryResult;
        //TODO should be done by a different thread ...
    }

    
 private class BitstampService  {
        protected HashMap args;
        protected ApiKeys keys;
        protected String url;
        protected boolean auth;
        private Mac mac ;
        private SecretKeySpec keyspec;

        public BitstampService(String url,HashMap<String, String> args, ApiKeys keys,boolean auth)
        {
            this.url = url;
            this.auth = auth;
            this.args = args;
            this.keys = keys;
        }    
        
        
        
   private String executeQuery()
   {
        //Used for API call which do NOT require authorization
        URL queryUrl = null;
        String answer = "" ;
        String params ="";

        try {
            if(args.size()>0)
                {
                params= Utils.buildQueryString(args,ENCODING);
                queryUrl = new URL(url+params);
                }
            else
                queryUrl = new URL(url);
            
            URLConnection con = queryUrl.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            answer = IOUtils.toString(in, encoding);    
        }
        catch(Exception e){
            Utils.log("Error with Bitstamp Query:"+e.getStackTrace(),Utils.LOG_ERR);
        }
        return answer;
   }
   
   
   private String executeQueryAuth() {
        //Used for API call which require authorization

        URL queryUrl = null;
        String answer = "" ;
        String post_data = "" ;
        String params ="";
        boolean httpError = false;
        HttpsURLConnection connection=null;

        //System.out.println("signature : "+ signature);

        try {
            // add nonce and build arg list

            initSign();
            String nonce = String.valueOf(System.currentTimeMillis());
            String message = nonce + keys.getClientID() + keys.getApiKey();
            //System.out.println("message : "+ message);

            String toHash = message;
            String signature = signRequest(keys.getPrivateKey(), toHash);
            args.put("key", keys.getApiKey());
            args.put("signature", signature);
            args.put("nonce", nonce);
            post_data= Utils.buildQueryString(args,ENCODING);
            queryUrl = new URL(url);


            connection = (HttpsURLConnection)queryUrl.openConnection();

            connection.setRequestMethod("POST");

            // create and setup a HTTP connection

            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", Settings.APP_TITLE);

            connection.setDoInput(true);
            connection.setDoOutput(true);

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
            Utils.log("Query to :" +url +" ! HTTP response : \n",logLevel); //do not log unless is error > 400
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
            Global.BitstampMarket.setConnected(false);
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
   
      private void initSign()
      {
            keyspec = null ;
            try {
                    keyspec = new SecretKeySpec(keys.getPrivateKey().getBytes("UTF-8"), SIGN_HASH_FUNCTION) ;
            } catch (UnsupportedEncodingException uee) {
                Utils.log(uee,Utils.LOG_ERR);
            }

            try {
                    mac = Mac.getInstance("HmacSHA256") ;
            } catch (NoSuchAlgorithmException nsae) {
                Utils.log(nsae,Utils.LOG_ERR);
            }

            try {
                    mac.init(keyspec) ;
            } catch (InvalidKeyException ike) {
                Utils.log(ike,Utils.LOG_ERR);
            }
      }
      
      
      private String signRequest(String secret, String hash_data) {
            mac.update(hash_data.getBytes()) ;
            return String.format("%064x", new BigInteger(1, mac.doFinal())).toUpperCase() ;
    }
 }
}
