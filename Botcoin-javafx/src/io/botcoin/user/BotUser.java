
package io.botcoin.user;

import io.botcoin.global.Settings;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 * email, email address
 * keep it updated with the database (update every x seconds)
 * 
 */
  
public class BotUser {
    private final String PATH_TO_PWD = Settings.PATH_TO_SAVED_PW;

    private boolean pro;
    private String email;
    private boolean password_saved;
    private double proportional_fee;
    private double fixed_fee;
    private int total_number_of_transaction;
    private double total_btc_executed;
    private double total_btc_paid_to_us;


    
//Class Variables

   

//Constructors
    public BotUser(){}
    
    public BotUser(boolean pro, String email, boolean password_saved, double proportional_fee, double fixed_fee, int total_number_of_transaction, double total_btc_executed, double total_btc_paid_to_us) {
        this.pro = pro;
        this.email = email;
        this.password_saved = password_saved;
        this.proportional_fee = proportional_fee;
        this.fixed_fee = fixed_fee;
        this.total_number_of_transaction = total_number_of_transaction;
        this.total_btc_executed = total_btc_executed;
        this.total_btc_paid_to_us = total_btc_paid_to_us;
    }
    


//Methods

    /**
     * @return the pro
     */
    public boolean isPro() {
        return pro;
    }

    /**
     * @param pro the pro to set
     */
    public void setPro(boolean pro) {
        this.pro = pro;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the PATH_TO_PWD
     */
    public String getPATH_TO_PWD() {
        return PATH_TO_PWD;
    }

    /**
     * @return the password_saved
     */
    public boolean isPassword_saved() {
        return password_saved;
    }

    /**
     * @param password_saved the password_saved to set
     */
    public void setPassword_saved(boolean password_saved) {
        this.password_saved = password_saved;
    }

    /**
     * @return the proportional_fee
     */
    public double getProportional_fee() {
        return proportional_fee;
    }

    /**
     * @param proportional_fee the proportional_fee to set
     */
    public void setProportional_fee(double proportional_fee) {
        this.proportional_fee = proportional_fee;
    }

    /**
     * @return the fixed_fee
     */
    public double getFixed_fee() {
        return fixed_fee;
    }

    /**
     * @param fixed_fee the fixed_fee to set
     */
    public void setFixed_fee(double fixed_fee) {
        this.fixed_fee = fixed_fee;
    }

    /**
     * @return the total_number_of_transaction
     */
    public int getTotal_number_of_transaction() {
        return total_number_of_transaction;
    }

    /**
     * @param total_number_of_transaction the total_number_of_transaction to set
     */
    public void setTotal_number_of_transaction(int total_number_of_transaction) {
        this.total_number_of_transaction = total_number_of_transaction;
    }

    /**
     * @return the total_btc_executed
     */
    public double getTotal_btc_executed() {
        return total_btc_executed;
    }

    /**
     * @param total_btc_executed the total_btc_executed to set
     */
    public void setTotal_btc_executed(double total_btc_executed) {
        this.total_btc_executed = total_btc_executed;
    }

    /**
     * @return the total_btc_paid_to_us
     */
    public double getTotal_btc_paid_to_us() {
        return total_btc_paid_to_us;
    }

    /**
     * @param total_btc_paid_to_us the total_btc_paid_to_us to set
     */
    public void setTotal_btc_paid_to_us(double total_btc_paid_to_us) {
        this.total_btc_paid_to_us = total_btc_paid_to_us;
    }
}
