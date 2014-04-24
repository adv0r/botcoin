/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.botcoin.global;

/**
 
 * @author advanced
 * 
 * This class contains the settings for transaction
 */
public class Fee {
    
    //Payment
    public final String PAYMENT_WALLET_ADDRESS = "1N7XxSvek1xVnWEBFGa5sHn1NhtDdMhkA7" ;  //The wallet where fees
    public final double OUR_FEE = 0.02 ; // 2% , Our take on each executed transaction (percentage)
    public final double OUR_FEE_BUY = OUR_FEE ; //Our take on each executed sell order (percentage) 
    public final double OUR_FEE_SELL = OUR_FEE ; //Our take on each executed sell order (percentage)
    
    //Constructor    
    public Fee(){
        
    }
}
