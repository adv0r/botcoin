
package io.botcoin.task;

import io.botcoin.global.Global;
import io.botcoin.global.Settings;
import io.botcoin.utils.Utils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */

public class ConnectionThread implements Runnable
{
        public static final String ONLINE_CHECK_MTGOX = "http://data.mtgox.com";
        public static final String ONLINE_CHECK_BTCE ="http://btc-e.com";
        public static final String ONLINE_CHECK_BITSTAMP ="https://www.bitstamp.net";
	private Thread th = null;
	
	public ConnectionThread(){
		start();
	}
	
	public void start(){
		if(th==null){
			th = new Thread(this);
			th.start();
		}
	}
	
	public void run()
	{  
		while(th!=null){
			Global.connectionDetection = false;
                        checkConnection();
            
		}
	 }
        
        public void forceCheck()
        {
            checkConnection();
        }

    private void checkConnection() {
            //Test for conections
        
           //TODO ... not sure whether this is always necessary
           Global.MtgoxMarket.setConnected(isConnectedTo(ONLINE_CHECK_MTGOX));
           Global.BtceMarket.setConnected(isConnectedTo(ONLINE_CHECK_BTCE));
           Global.BitstampMarket.setConnected(isConnectedTo(ONLINE_CHECK_BITSTAMP));
            
            Global.connectionDetection = true;

            try {
                  th.sleep(Settings.PING_TIME);
            } 
            catch (InterruptedException e) {
                  e.printStackTrace();
           }    
    }
    
    
    private boolean isConnectedTo(String url)
    {
        boolean connected = false;
        HttpURLConnection connection=null;
        URL query = null;
        try { 
               query = new URL(url);
        } catch (MalformedURLException ex) {
               Utils.log(ex,Utils.LOG_ERR);
        }
        try{
               connection = (HttpURLConnection)query.openConnection();
               connection.setRequestMethod("POST"); 
               connection.setDoOutput(true);
               connection.setDoInput(true);
               connection.getOutputStream();   
               connected=true;
        }
        catch( NoRouteToHostException | UnknownHostException ex){
               connected = false;
               //TODO I suspect here I should reset the connection somehow (?=null?)
               }
        catch(IOException ex){
               Utils.log(ex,Utils.LOG_ERR);
                }
        return connected;
    }
}	

