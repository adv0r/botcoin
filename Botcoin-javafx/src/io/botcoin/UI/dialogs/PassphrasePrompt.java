
package io.botcoin.UI.dialogs;

import io.botcoin.global.Settings;
import io.botcoin.utils.Utils;
import javafx.geometry.Insets;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class PassphrasePrompt {
    private Stage myStage;
    private boolean isFirstTime;
//Class Variables
    private static PasswordField passphraseEnterField_;



//Constructor
public PassphrasePrompt(boolean isFirstTime){
    this.isFirstTime=isFirstTime;
};


//Methods
    
public String show(Stage stage) {
     GridPane grid = new GridPane();
     myStage = stage;
    //myStage.getScene().getStylesheets().add(Settings.CSS_FILE_PATH);
     grid.setHgap(10);
     grid.setVgap(10);
     grid.setPadding(new Insets(0, 10, 0, 10));

     passphraseEnterField_ = new PasswordField(); passphraseEnterField_.setPromptText("Insert your encription passphrase");
     //TODO add an Icon here
     double minWidth = 700;

     passphraseEnterField_.setMinWidth(minWidth);
        String label = "";
     if (isFirstTime)
         label= "Passphrase";
     else
         label = "Current passphrase";

     grid.add(new Label(label), 0, 0);
     grid.add(passphraseEnterField_, 0, 1);


     Callback<Void, Void> myCallback = new Callback<Void, Void>() {
                 @Override
                 public Void call(Void param) {
                     //maybe TODO
                         return null;
                 }
         };

     String message = "";
     if (isFirstTime)
         message= "Please insert the passphrase to decrypt your data\n";
     else
         message = "Please insert the old passphrase";
     Dialogs.DialogResponse resp = Dialogs.showCustomDialog(myStage,
             grid,
             message,
             "The bot needs a passphrase",
             Dialogs.DialogOptions.OK_CANCEL,
             myCallback);
     if (resp == resp.CLOSED || resp == resp.CANCEL)
     {
      if(isFirstTime)
      {
         Dialogs.showErrorDialog(myStage,
                 "To run the bot you need to enter the passphrase",
                 "Missimg passphrase",
                 "Error");
         Utils.log("To run the bot you need to enter the passphrase ",Utils.LOG_ERR);
         System.exit(0);
      }
      else
      {
          
      }
      
          
     }
     //You must check the resp, since input fields' texts are returned regardless of what button was pressed. 
     //(ie. If user clicked 'Cancel' disregard the input) 
     return passphraseEnterField_.getText();
    }
}
