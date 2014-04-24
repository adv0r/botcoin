
package io.botcoin.models;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class Balance {


//Class Variables
   private double euros;
   private double dollars;
   private double bitcoins;
   private double litecoins;
   
   private boolean haveEuros;
   private boolean haveDollars;
   private boolean haveLitecoins;
   private boolean haveBitcoins;

//Constructor
public Balance(){
    
}


public Balance(double bitcoins,double dollars)
{ 
    setBitcoins(bitcoins);
    setDollars(dollars);
}

public Balance(double bitcoins,double euros,double dollars)
{
    
    setBitcoins(bitcoins);
    setEuros(euros);
    setDollars(dollars);
   
}

public Balance(double bitcoins,double litecoins,double euros,double dollars)
{
    setBitcoins(bitcoins);
    setEuros(euros);
    setDollars(dollars);
    setLitecoins(litecoins);
}
//Methods

   @Override
   public String toString()
{
    String toReturn="Balance : ";
    if(this.haveBitcoins)
        toReturn+=getBitcoins()+" BTC; ";
    if(this.haveLitecoins)
        toReturn+=getBitcoins()+" LTC; ";
    if(this.haveDollars)
        toReturn+=getDollars()+" USD; "; 
    if(this.haveEuros)
        toReturn+=getDollars()+" EUR ";     
    return toReturn;
    
}

    /**
     * @return the euros
     */
    public double getEuros() {
        return euros;
    }

    /**
     * @param euros the euros to set
     */
    public void setEuros(double euros) {
        if(euros==-1)
            this.haveEuros=false;
        else 
            this.haveEuros=true;
        this.euros = euros;
    }

    /**
     * @return the dollars
     */
    public double getDollars() {
        return dollars;
    }

    /**
     * @param dollars the dollars to set
     */
    public void setDollars(double dollars) {
        if(dollars==-1)
            this.haveDollars=false;
        else 
            this.haveDollars=true;
        this.dollars = dollars;
    }

    /**
     * @return the bitcoins
     */
    public double getBitcoins() {
        return bitcoins;
    }

    /**
     * @param bitcoins the bitcoins to set
     */
    public void setBitcoins(double bitcoins) {
        if(bitcoins==-1)
            this.haveBitcoins=false;
        else 
            this.haveBitcoins=true;
        this.bitcoins = bitcoins;
    }

    /**
     * @return the haveEuros
     */
    public boolean haveEuros() {
        return haveEuros;
    }

    /**
     * @param haveEuros the haveEuros to set
     */
    private void setHaveEuros(boolean haveEuros) {
        this.haveEuros = haveEuros;
    }

    /**
     * @return the haveDollars
     */
    public boolean haveDollars() {
        return haveDollars;
    }

    /**
     * @param haveDollars the haveDollars to set
     */
    private void setHaveDollars(boolean haveDollars) {
        this.haveDollars = haveDollars;
    }

    /**
     * @return the litecoins
     */
    public double getLitecoins() {
        return litecoins;
    }

    /**
     * @param litecoins the litecoins to set
     */
    public void setLitecoins(double litecoins) {
        if(litecoins==-1)
            this.haveLitecoins=false;
        else 
            this.haveLitecoins=true;
        this.litecoins = litecoins;
    }

    /**
     * @return the haveLitecoins
     */
    public boolean haveLitecoins() {
        return haveLitecoins;
    }

    /**
     * @param haveLitecoins the haveLitecoins to set
     */
    public void setHaveLitecoins(boolean haveLitecoins) {
        this.haveLitecoins = haveLitecoins;
    }

 
}
