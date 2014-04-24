
package io.botcoin.utils;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class Response {

    //Class Variables

private String message;
private boolean positive;


//Constructor

    public Response(String message, boolean positive) {
        this.message = message;
        this.positive = positive;
    }

//Methods

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the positive
     */
    public boolean isPositive() {
        return positive;
    }

    /**
     * @param positive the positive to set
     */
    public void setPositive(boolean positive) {
        this.positive = positive;
    }
}
