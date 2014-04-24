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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author advanced
 * Implementation of MtGoxTrading Api V2
 */
public class MtGoxTrading implements TradeInterface{
    
private ApiKeys keys;

private final int USD_DIVIDE_FACTOR = 100000;  //In order to use the intvalue provided by the api
private final int EUR_DIVIDE_FACTOR = 100000;  //you should divide the intvalue by this number. Or vice versa
private final int BTC_DIVIDE_FACTOR = 100000000;
private final double USD_MULTIPLY_FACTOR = 0.00001; 
private final double EUR_MULTIPLY_FACTOR = 0.00001;  
private final double BTC_MULTIPLY_FACTOR = 0.00000001;

private final double MIN_ORDER = 0.1; //BTC

private final String API_BASE_URL_V2 = "https://data.mtgox.com/api/2/";
private final String API_BASE_URL_V1 = "https://data.mtgox.com/api/1/";

private final String API_GET_INFO = "MONEY/INFO";
private final String API_TICKER_USD = "BTCUSD/MONEY/TICKER";
private final String API_TICKER_EUR = "BTCEUR/MONEY/TICKER";
private final String API_TICKER_FAST_USD = "BTCUSD/MONEY/TICKER_FAST"; //NOT sure what it does
private final String API_TICKER_FAST_EUR = "BTCEUR/MONEY/TICKER_FAST"; //NOT sure what it does

private final String API_WITHDRAW = "MONEY/BITCOIN/SEND_SIMPLE";
private final String API_LAG = "MONEY/ORDER/LAG";
private final String API_ADD_ORDER = "BTCUSD/MONEY/ORDER/ADD";

private final String SIGN_HASH_FUNCTION = "HmacSHA512";
private final String ENCODING = "UTF-8";

//ERRORS 
private final String TOKEN_ERR = "ERR: ";
private final String NO_CONNECTION = "NO CONNECTION WITH MTGOX";

private final String TOKEN_ERR_INVALID_API = "login_error_invalid_rest_key";
private final String TOKEN_ERR_INVALID_METHOD ="invalid_request_method";

public MtGoxTrading(ApiKeys keys) {
        this.keys = keys;
    }
public MtGoxTrading() {
       //Use this constructur for public API calls (e.g. tikers, lag)
    this.keys = null;
    }

    @Override
    public String getLag()  {        
        String urlPath = API_LAG;
        HashMap<String, String> query_args = new HashMap<>();
         /*Params
         * 
         */
        String queryResult = query(urlPath, query_args,false);
          //TODO improve
        if (queryResult.startsWith(TOKEN_ERR))
            return "-1";
        if (queryResult.equals(NO_CONNECTION))
            return "-1";
        
         /*Sample result
         * the lag in milliseconds
         */
         JSONParser parser=new JSONParser();
         String lag="";
         try {
            JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
            JSONObject dataJson = (JSONObject)httpAnswerJson.get("data");
            lag = (String)dataJson.get("lag_text");                      
         } catch (ParseException ex) {
            Utils.log(ex,Utils.LOG_ERR);
        }   
        return lag;
    }

    @Override
    public boolean withdrawBTC(double amount, String dest_address) {
        /* 
        String urlPath = API_WITHDRAW;
        HashMap<String, String> query_args = new HashMap<>();
        /*Params
         * address : Target bitcoin address
         * amount_int : Amount of bitcoins to withdraw
         * fee_int : Fee amount to be added to transaction (optional), maximum 0.01 BTC
         * no_instant : Setting this parameter to 1 will prevent transaction from being processed internally, and force usage of the bitcoin blockchain even if receipient is also on the system
         * green : Setting this parameter to 1 will cause the TX to use MtGoxTrading’s green address
         
        query_args.put("amount_int", Long.toString(Math.round(amount*BTC_DIVIDE_FACTOR)));
        query_args.put("address",dest_address);
        String queryResult = query(urlPath, query_args,false);       
        
        if (queryResult.equals(NO_CONNECTION))
            return false;
        
         /*Sample result
         * On success, this method will return the transaction id (in offser trx ) which will contain either the bitcoin transaction id as hexadecimal or a UUID value in case of internal transfer.
         
        
         JSONParser parser=new JSONParser();
         try {
            JSONObject obj2=(JSONObject)(parser.parse(queryResult));
            JSONObject data = (JSONObject)obj2.get("data");

            //lastPriceArray[0] = (Double)obj2.get("last"); //USD
         } catch (ParseException ex) {
            Utils.log(ex,Utils.LOG_ERR);
        }
        
        return true; //TODO Edit
        */
        return false;
    }

    @Override
    public String sellBTC(double amount) {
       return placeOrder("sell", Math.round(amount*BTC_DIVIDE_FACTOR));
    }

    @Override
    public String buyBTC(double amount) {
       return placeOrder("buy", Math.round(amount*BTC_DIVIDE_FACTOR));
    }
    
    @Override
    public ApiPermissions getPermissions() {
        ApiPermissions permissions = new ApiPermissions(false, false, false, false, false, false);   
         
        String urlPath = API_GET_INFO;
        HashMap<String, String> query_args = new HashMap<>();
     
        String queryResult = query(urlPath, query_args,false); 
        if (queryResult.startsWith(TOKEN_ERR))
        {
            return permissions; //all false by default
        }
                   
        if (queryResult.equals(NO_CONNECTION))
            return permissions; //TODO non è vero. should be all false here, something should say no connection

         JSONParser parser=new JSONParser();
         
         //Example answer
         //"Rights":["deposit","get_info","merchant","trade","withdraw"]
         try {
            JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
            JSONObject dataJson = (JSONObject)httpAnswerJson.get("data");  
            JSONArray permissionsArray = (JSONArray)dataJson.get("Rights"); 
            
            for(int i=0; i<permissionsArray.size(); i++)
            {
                String tempElem = (String)permissionsArray.get(i);
                //System.out.println("Temp elem ("+i+") "+tempElem);
                switch(tempElem)
                {
                    case "deposit": permissions.setDeposit(true); break;
                    case "get_info": permissions.setGet_info(true); break;
                    case "merchant": permissions.setMerchant(true); break;
                    case "trade": permissions.setTrade(true); break;
                    case "withdraw": permissions.setWithdraw(true); break;
                    default : Utils.log("Error while parsing Api Keys permissions.",Utils.LOG_ERR);break;             
                }
            }
            
         } catch (ParseException ex) {
            Utils.log(ex,Utils.LOG_ERR);
            return permissions; 
        }

         permissions.setValid_keys(true); //I assume that if it didn't encounter an error before, then it has to be true

         return permissions;
    }
    
     public String placeOrder(String type, long amount_int) {
         
        String toReturn = "";
        String result = "" ;
        String data= "";
        String urlPath = API_ADD_ORDER;
        HashMap<String, String> query_args = new HashMap<>();
        /*Params
         * type : {ask (sell) | bid(buy) }
         * amount_int : amount of BTC to buy or sell, as an integer
         * price_int : The price per bitcoin in the auxiliary currency, as an integer, optional if you wish to trade at the market price
         */
        query_args.put("amount_int",Long.toString(amount_int));
        if (type.equals("sell"))
           query_args.put("type", "ask");
        else 
           query_args.put("type", "bid");
        
        String queryResult = query(urlPath, query_args,false); 
       
        if (queryResult.startsWith(TOKEN_ERR))
            return "-1";

        if (queryResult.equals(NO_CONNECTION))
            return "-1";
         /*Sample result
         * {"result":"success","data":"abc123-def45-.."} 
         */
         JSONParser parser=new JSONParser();
         try {
            JSONObject obj2=(JSONObject)(parser.parse(queryResult));
            result = (String)obj2.get("result");
            data = (String)obj2.get("data");

            //lastPriceArray[0] = (Double)obj2.get("last"); //USD
  
            
         } catch (ParseException ex) {
            Utils.log(ex,Utils.LOG_ERR);
        }
        
        if(result.equals("success"))
        {
            toReturn="executed : " +data; 
        }
        else 
            toReturn="not executed : " +data; //TODO test this branch
        
        return toReturn; //TODO change
    }

    @Override
    public Balance getBalance() {
        String urlPath = API_GET_INFO;
        HashMap<String, String> query_args = new HashMap<>();
        
        /*Params
         * 
         */
        Balance balance = new Balance();

        
       
        String queryResult = query(urlPath, query_args,false); 
        if (queryResult.startsWith(TOKEN_ERR))
        {       
            return new Balance(-1,-1,-1);
        }
        
        if (queryResult.equals(NO_CONNECTION) || !Global.MtgoxMarket.isConnected())
        {
            return new Balance(-1,-1,-1);
        }
                   
         /*Sample result
         * {
         *   "data": {
         *       "Created": "yyyy-mm-dd hh:mm:ss",
         *       "Id": "abc123",
         *       "Index": "123",
         *       "Language": "en_US",
         *       "Last_Login": "yyyy-mm-dd hh:mm:ss",
         *       "Login": "username",
         *       "Monthly_Volume":                   **Currency Object**,
         *       "Trade_Fee": 0.6,
         *       "Rights": ['deposit', 'get_info', 'merchant', 'trade', 'withdraw'],
         *       "Wallets": {
         *           "BTC": {
         *               "Balance":                  **Currency Object**,
         *               "Daily_Withdraw_Limit":     **Currency Object**,
         *               "Max_Withdraw":             **Currency Object**,
         *               "Monthly_Withdraw_Limit": null,
         *               "Open_Orders":              **Currency Object**,
         *               "Operations": 1,
         *           },
         *           "USD": {
         *               "Balance":                  **Currency Object**,
         *               "Daily_Withdraw_Limit":     **Currency Object**,
         *               "Max_Withdraw":             **Currency Object**,
         *               "Monthly_Withdraw_Limit":   **Currency Object**,
         *               "Open_Orders":              **Currency Object**,
         *               "Operations": 0,
         *           },
         *           "JPY":{...}, "EUR":{...},
         *           // etc, depends what wallets you have
         *       },
         *   },
         *   "result": "success"
         * }
         */
        
         JSONParser parser=new JSONParser();
         try {
            JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
            JSONObject dataJson = (JSONObject)httpAnswerJson.get("data");  
            JSONObject walletsJson = (JSONObject)dataJson.get("Wallets"); 
            
            JSONObject BTCwalletJson = (JSONObject)((JSONObject)walletsJson.get("BTC")).get("Balance");  
            String BTCBalance = (String)BTCwalletJson.get("value");

            boolean hasDollars = true;
            boolean hasEuros = true;
            JSONObject USDwalletJson,EURwalletJson;
            String USDBalance ="" , EURBalance ="";

            try{
                 USDwalletJson = (JSONObject)((JSONObject)walletsJson.get("USD")).get("Balance"); 
                 USDBalance = (String)USDwalletJson.get("value");
            }
            catch (Exception e)
            {
                hasDollars = false;
            }
            
            try{
                 EURwalletJson = (JSONObject)((JSONObject)walletsJson.get("EUR")).get("Balance");
                 EURBalance = (String)EURwalletJson.get("value");
            }
            catch (Exception e)
            {
                hasEuros = false;  
            }
            
            balance.setBitcoins(Double.parseDouble(BTCBalance)); //BTC
            
            if(hasDollars)
                balance.setDollars(Double.parseDouble(USDBalance)); //USD
            else 
                balance.setDollars(-1); //Account does not have USD wallet

            if(hasEuros)
                balance.setEuros(Double.parseDouble(EURBalance)); //EUR
            else 
                balance.setEuros(-1); //Account does not have EUR wallet

            
         } catch (ParseException ex) {
            Logger.getLogger(MtGoxTrading.class.getName()).log(Level.SEVERE, null, ex);
        }
        return balance;
    }
    
    @Override
    public double getLastPriceUSD(String coin) {
        //TODO Switch if coin is LTC ?
        return getLastPrice("USD");
    }

    @Override
    public double getLastPriceEUR(String coin) {
        //TODO Switch if coin is LTC ?
        return getLastPrice("EUR");
    }

    
    public String query(String path, HashMap<String, String> args,boolean isV1) {
        GoxService query = new GoxService(path, args, keys);
        String queryResult=NO_CONNECTION; //TODO dangerous
        if(Global.MtgoxMarket.isConnected())
        {
            if(keys==null)
            {
                queryResult = query.executeQuery();
            }
            
            else
            {
                if(isV1)
                {
                   queryResult = query.executeQueryAuth(true);
                }
                else
                {
                   queryResult = query.executeQueryAuth(false);
                }
            }
        }
        else
        {
                Utils.log("The bot will not execute the query, there is no connection to mtgox", Utils.LOG_ERR);

        }
        return queryResult;
        //TODO should be done by a different thread ...
    }
    

    public double getLastPrice(String currency) {    
  
        String urlPath="";
        long divideFactor;
        switch (currency) {
        case "USD":
            urlPath = API_TICKER_FAST_USD ;
            divideFactor = USD_DIVIDE_FACTOR;
            break;
        case "EUR":
            urlPath = API_TICKER_FAST_EUR ; //TODO When they will fix it change to ticker fast!! It is not working properly today 17Apr2013
            divideFactor = EUR_DIVIDE_FACTOR;
            break;
        default:
            throw new UnsupportedOperationException("MTGOX API ERROR: Currency - "+currency+ " - Not supported yet.");
           }
        HashMap<String, String> query_args = new HashMap<>();
        
        /*Params :
        * No params required
        */
        String queryResult = query(urlPath, query_args,false);
         //TODO improve
        if (queryResult.startsWith(TOKEN_ERR))
            return -1;
        if (queryResult.equals(NO_CONNECTION))
            return -1;
         /* Result sample :
         *{
         *   "result":"success",
         *   "data": {
         *       "high":       **Currency Object - USD**,
         *       "low":        **Currency Object - USD**,
         *       "avg":        **Currency Object - USD**,
         *       "vwap":       **Currency Object - USD**,
         *       "vol":        **Currency Object - BTC**,
         *       "last_local": **Currency Object - USD**,
         *       "last_orig":  **Currency Object - ???**,
         *       "last_all":   **Currency Object - USD**,
         *       "last":       **Currency Object - USD**,
         *       "buy":        **Currency Object - USD**,
         *       "sell":       **Currency Object - USD**,
         *       "now":        "1364689759572564"
         *   }
         *}
         */
        JSONParser parser=new JSONParser();
        double last=0;
        try {
            JSONObject httpAnswerJson=(JSONObject)(parser.parse(queryResult));
            JSONObject dataJson = (JSONObject)httpAnswerJson.get("data");
            JSONObject lastJson = (JSONObject)dataJson.get("last");
            String last_String = (String)lastJson.get("value");
            last = Double.parseDouble(last_String);
            
         } catch (ParseException ex) {
            Utils.log(ex,Utils.LOG_ERR);
        }
        return last;
    }

    @Override
    public String sellLTC(double amount) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO change body of generated methods, choose Tools | Templates.
    }
    

    @Override
    public String buyLTC(double amount) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO change body of generated methods, choose Tools | Templates.
    }
   
    private class GoxService  {
        protected String path;
        protected HashMap args;
        protected ApiKeys keys;
        protected boolean publicAPI;

        public GoxService(String path,HashMap<String, String> args, ApiKeys keys)
        {
            this.path = path;
            this.args = args;
            this.keys = keys;
            
            if (keys==null)
                publicAPI=true;
            else 
                publicAPI =false;
        }
       
    
    private String signRequest(String secret, String hash_data) {
       String signature = "";
        try{
          Mac mac = Mac.getInstance(SIGN_HASH_FUNCTION);
          SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(secret), SIGN_HASH_FUNCTION);
          mac.init(secret_spec);
          signature = Base64.encodeBase64String(mac.doFinal(hash_data.getBytes()));
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e){
          Logger.getLogger(MtGoxTrading.class.getName()).log(Level.SEVERE, null, e);            
        }
        return signature;
    }

    
      private String executeQuery()
   {
        //Used for API call which do NOT require authorization
        URL queryUrl = null;
        String answer = "" ;

        try {
        
            queryUrl = new URL(API_BASE_URL_V2 + path);
            
            URLConnection con = queryUrl.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            answer = IOUtils.toString(in, encoding);    
        }
        catch(Exception e){
            Utils.log("Error with MtGox Query:"+e.getStackTrace(),Utils.LOG_ERR);
        }
        return answer;
   }
    
    
    private String executeQueryAuth(boolean v1) {
            String answer = "";
            boolean httpError = false;
            HttpsURLConnection connection=null;
            String signature = "";
            String post_data ="";
            String nonce = String.valueOf(System.currentTimeMillis())+"000";
            try {
                // add nonce and build arg list
                if(!publicAPI)
                {
                    args.put("nonce", nonce);     
                    post_data= Utils.buildQueryString(args,ENCODING);
                    String hash_data = path + "\0" + post_data; //Should be correct

                    // args signature with apache cryptografic tools
                    String toHash = hash_data;
                    if(v1)
                        toHash=post_data;
                    signature = signRequest(keys.getPrivateKey(), toHash);
                }
                // build URL
                String base = API_BASE_URL_V2;
                if(v1)
                    base= API_BASE_URL_V1;
                URL queryUrl = new URL(base + path); 

                if(v1)
                {
                  connection = (HttpsURLConnection)(HttpURLConnection)queryUrl.openConnection();

                }
                else
                {
                  connection = (HttpsURLConnection)queryUrl.openConnection();
                }
                
                if(!publicAPI)
                {
                      connection.setRequestMethod("POST");  
                      connection.setRequestProperty("Rest-Key", keys.getApiKey());
                      connection.setRequestProperty("Rest-Sign", signature.replaceAll("\n", ""));
                }
                else 
                {
                      connection.setRequestMethod("GET");
                }
                
                // create and setup a HTTP connection

                //TODO remove 

                connection.setRequestProperty("User-Agent", Settings.APP_TITLE);


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
                Utils.log("Query to :" + base + path + " , HTTP response : \n",logLevel); //do not log unless is error > 400
                while ((output = br.readLine()) != null) {
                            Utils.log(output,logLevel);
                            answer+=output;
                        }

                if (httpError){
                    JSONParser parser=new JSONParser();
                    try {
                       JSONObject obj2=(JSONObject)(parser.parse(answer));
                       String result = (String)obj2.get("result");
                       String error = (String)obj2.get("error");
                       String token = (String)obj2.get("token");

                       if(token.equals(TOKEN_ERR_INVALID_API)) 
                       {
                           Utils.log("Invalid API Keys",Utils.LOG_ERR);
                           answer = TOKEN_ERR+TOKEN_ERR_INVALID_API;
                       }
                       else if(token.equals(TOKEN_ERR_INVALID_METHOD))
                       {
                           Utils.log("Invalid Method",Utils.LOG_ERR);
                           answer = TOKEN_ERR+TOKEN_ERR_INVALID_METHOD;
                       }

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
                Global.MtgoxMarket.setConnected(false);
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