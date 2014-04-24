
package io.botcoin.UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class MyMenuBar extends MenuBar {

//Class Variables



//Constructor

public MyMenuBar()
    {
        // --- Menu items for botcoin
        Menu menuBotcoin = new Menu("Botcoin");
        
        MenuItem strategyItem = new MenuItem("Export strategy");
            strategyItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Export strategy");
                }
            });
        
        
        MenuItem passphraseItem = new MenuItem("Reset passphrase");
            passphraseItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Reset encryuption passphrase");
                }
            });
        
            
        MenuItem apiItem = new MenuItem("Set API keys");
            apiItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Set API keys");
                }
            });
            
        MenuItem upgradeItem = new MenuItem("Get pro");
            upgradeItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Upgrade button pressed");
                }
            });
            
        MenuItem shareItem = new MenuItem("Get share link");
            shareItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Get share link");
                }
            });
        menuBotcoin.getItems().add(strategyItem);
        menuBotcoin.getItems().add(passphraseItem);
        menuBotcoin.getItems().add(apiItem);
        menuBotcoin.getItems().add(upgradeItem);
        menuBotcoin.getItems().add(shareItem);

         // --- Menu items for help
        Menu menuHelp = new Menu("Help");
         
        MenuItem supportItem = new MenuItem("Contact support");
            supportItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Contact support");
                }
            });
            
        MenuItem faqItem = new MenuItem("Read the FAQ");
            faqItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Read the FAQ");
                }
            });
                       
        MenuItem websiteItem = new MenuItem("Go to website");
            websiteItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("Read the FAQ");
                }
            });  
        MenuItem updateItem = new MenuItem("Check for updates");
            updateItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    System.out.println("check for updates");
                }
            });
            
        menuHelp.getItems().add(faqItem);
        menuHelp.getItems().add(websiteItem);
        menuHelp.getItems().add(supportItem);
        menuHelp.getItems().add(updateItem);
        setId("menu-bar");
        
        //Add menus to the menubar
        
        this.getMenus().addAll(menuBotcoin, menuHelp);
        
        
        //this.setUseSystemMenuBar(true); //Uncomment if you want to use the menubar of macosx
    }

//Methods
}
