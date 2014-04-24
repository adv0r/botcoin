
package io.botcoin.strategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.botcoin.UI.DashboardStatusController;
import io.botcoin.UI.DashboardStrategyController;
import io.botcoin.global.Constant;
import io.botcoin.global.Global;
import io.botcoin.utils.FileSystem;
import io.botcoin.utils.Response;
import io.botcoin.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 */
public class Strategy {
    //Class Variables

    /** Prototype entities lists**/
    private String author;
    private String pathToStrategy;
    
    private ArrayList<Rule> rulesList = new ArrayList<>();
    private ObservableList<Rule> ruleObservableList = FXCollections.observableArrayList(rulesList);
    
    private ArrayList<Rule> executedRulesList = new ArrayList<>();
    private ObservableList<Rule> executedRulesObservableList = FXCollections.observableArrayList(executedRulesList);
    
    private ArrayList<Rule> activeRulesList = new ArrayList<>();
    private ObservableList<Rule> activeRulesObservableList = FXCollections.observableArrayList(activeRulesList);

    
    //Constructor
    public Strategy(String pathToStrategy){
        this.pathToStrategy = pathToStrategy;
        ruleObservableList.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
               updateOtherLists();
               
               /*if(Global.startupComplete) 
               {
                   DashboardStatusController dsc = (DashboardStatusController)Global.getController(Constant.STATUS);
                   dsc.updateTitles();
               }
               */ 
                //System.out.println("Detected a change! ");
            }
        });
        parseStrategy();	
    }
    
    private void updateOtherLists() {
        updateActiveRulesList();
        updateExecutedRulesList();
    }

    //Methods
    public long getNextRuleId()
    {
        //I'm assuming that the last element has always the largest id
        //this will break if the cofig is broken
        if(getRulesList().size()!=0)    
            return ((getRulesList().get(getRulesList().size()-1)).getId())+1;
        else return 1;
    }
    
    
    //Call this method *ONLY* to add new rules with app open
    public Response addRule(String operation,double amount, String coin, String direction, 
            double target, String currency, String comment,String orderResponse, boolean active,
            boolean executed, String market)
    {
        long id = getNextRuleId();
        Rule newRule = new Rule(id, operation, amount,coin, direction, target, currency, 
                comment,orderResponse,active,executed,market,"",false,true);
        Response resp = RuleValidator.validate(newRule);
        if (resp.isPositive())
        {
            getRulesList().add(newRule);
            Utils.log("Rule added: "+ newRule.toString());
            writeStrategyToFile(pathToStrategy);
        }
        else
        {
            Utils.log("Error(s) while trying to add a rule : " + resp.getMessage()+"\nRule = "+newRule.toString(),Utils.LOG_ERR);            
        }
        return resp;
    }
     
    
    //Call this method *ONLY* to add initial rules from file
    public void addInitialRuleFromFile(long id,String operation,double amount,String coin, String direction, double target, 
            String currency, String comment,String orderResponse, boolean active,boolean executed, 
            String market, String executedTimestamp,boolean seen, boolean visualize)
    {
        Rule newRule = new Rule(id, operation, amount, coin, direction, target, currency, 
                comment,orderResponse,active,executed,market,executedTimestamp,seen,visualize);
        Response resp = RuleValidator.validate(newRule);
        if (resp.isPositive())
        {    
            getRulesList().add(newRule);
            Utils.log("Initial Rule added:" + newRule.toString(),Utils.LOG_MID);
        }
        else 
        {
            Utils.log("Error(s) while trying to add an initial rule : " + resp.getMessage()+"\nRule = "+newRule.toString(),Utils.LOG_ERR);
        }
    }
    
    public boolean delRuleById(long id)
    {   
        boolean found = false;
        for (int j=0; j< getRulesList().size(); j++)
        {
            Rule temp = getRulesList().get(j);
            if(id == temp.getId())
            {
                found = true;
                getRulesList().remove(j);
                Utils.log("Rule deleted . Id : "+id+ "Comment : "+temp.getComment());
                writeStrategyToFile(pathToStrategy);
            }
        }
        if(!found)
           Utils.log("I can't find any rule with id "+id,Utils.LOG_ERR);
        return found;
    }
    
    public void deleteAllRules()
    {
       int numberOfRules = getRulesList().size();
       for (int j=0; j< numberOfRules; j++)
        {
           Rule temp = getRulesList().get(0);
           delRuleById(temp.getId());
           System.out.println(j+": calling delRuleById "+temp.getId());
        }  
    }
    
    public Rule getRuleByID(long id)
    {
        Rule toReturn = null;
        boolean found = false;
        for (int j=0; j< getRulesList().size(); j++)
        {
            Rule temp = getRulesList().get(j);
            if(id == temp.getId())
            {
                found = true;
                toReturn = temp;
            }
        }
        
        if(!found)
           Utils.log("I can't find any rule with id "+id,Utils.LOG_ERR);
        return toReturn;
    }
    
    private void parseStrategy() {
        JSONParser parser=new JSONParser();
        String strategyString = FileSystem.readFromFile(this.pathToStrategy);
        try
        {
           JSONObject strategyJSON=(JSONObject)(parser.parse(strategyString));
           this.author = (String)strategyJSON.get("author");
           
           JSONArray rules = (JSONArray)strategyJSON.get("rules");
            for (int i = 0; i < rules.size(); i++) {
                JSONObject tempRule = (JSONObject)rules.get(i);
                long id = (long)tempRule.get("id");

                double amount = Double.parseDouble((String)tempRule.get("amount"));
                double target = Double.parseDouble((String)tempRule.get("target"));

                boolean executed = (boolean)tempRule.get("executed");
                boolean active = (boolean)tempRule.get("active");
                boolean seen = (boolean)tempRule.get("seen");
                boolean visualize = (boolean)tempRule.get("visualize");
                
                String coin = (String)tempRule.get("coin");
                String direction = (String)tempRule.get("direction");
                String comment = (String)tempRule.get("comment");
                String operation = (String)tempRule.get("operation");
                String currency = (String)tempRule.get("currency");
                String executedOrderId = (String)tempRule.get("executed-order-id");
                String market = (String)tempRule.get("market");
                String executedTimestamp = (String)tempRule.get("executed-timestamp");



                addInitialRuleFromFile(id,operation, amount, coin, direction, target, 
                        currency, comment, executedOrderId, active, 
                        executed,market,executedTimestamp,seen,visualize);            
            }
        }
        catch(ParseException | NumberFormatException e)
        {
            Utils.log("Error while parsing the strategy file : "+e, Utils.LOG_ERR);
        }
    }
    
    public ArrayList<Rule> getListOfActiveRules()
    {
        ArrayList<Rule> activeRules = new ArrayList<>();
        for(int i=0; i<getRulesList().size();i++)
        {
            if (getRulesList().get(i).isActive())
                activeRules.add(getRulesList().get(i));
        }
        return activeRules;   
    }
    
        
    public int countActiveRules()
    {
        int count=0;
        for(int i=0; i<getRulesList().size();i++)
        {
            if (getRulesList().get(i).isActive())
                count++;
        }
        return count;   
    }
    
    public int countExecutedRules()
    {
        int count=0;
        for(int i=0; i<getRulesList().size();i++)
        {
            if (getRulesList().get(i).isExecuted() && getRulesList().get(i).isVisualize())
                count++;
        }
        return count;   
    }
            
    public int countRules()
    {
        int count=0;
        for(int i=0; i<getRulesList().size();i++)
        {
                count++;
        }
        return count;   
    }
    
    
    public String getIdAndComment(ArrayList<Rule> list)
    {
        String toReturn="";
        for (int i=0;i<list.size();i++)
            toReturn+= "Id="+list.get(i).getId()+
                    " : "+list.get(i).getComment()+"\n";
        return toReturn;
    }
    
    public void writeStrategyToFile(String where)
    {
        //update lists to refresh view
        refreshLists();
        
        String toWrite="";
        JSONObject toWriteJ=new JSONObject();
        toWriteJ.put("author", author);
       
          
        JSONArray ruleListJ = new JSONArray();
        for (int i = 0; i < getRulesList().size(); i++) {
            JSONObject tempRuleJ=new JSONObject();
            Rule tempRule = getRulesList().get(i);
            tempRuleJ.put("id", tempRule.getId());
            tempRuleJ.put("operation", tempRule.getOperation());
            tempRuleJ.put("amount", String.valueOf(tempRule.getAmount()));
            tempRuleJ.put("coin", tempRule.getCoin());
            tempRuleJ.put("direction", tempRule.getDirection());
            tempRuleJ.put("target", String.valueOf(tempRule.getTarget()));
            tempRuleJ.put("comment", tempRule.getComment());
            tempRuleJ.put("executed", tempRule.isExecuted());
            tempRuleJ.put("active", tempRule.isActive());
            tempRuleJ.put("currency", tempRule.getCurrency());
            tempRuleJ.put("executed-order-id", tempRule.getExecutedOrderResponse());
            tempRuleJ.put("market", tempRule.getMarket());
            tempRuleJ.put("executed-timestamp", tempRule.getExecutedTimestamp());
            tempRuleJ.put("seen", tempRule.isSeen());
            tempRuleJ.put("visualize", tempRule.isVisualize());
            
            ruleListJ.add(tempRuleJ);      
        }
        toWriteJ.put("rules", ruleListJ);
        
        toWrite+=toWriteJ.toString();
        //prettify it
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(toWrite);
        String toWritePretty = gson.toJson(je);

        //Utils.msg("toWrite= "+toWrite);
        
        try {
            FileUtils.writeStringToFile(new File(where), toWritePretty);
            Utils.log("updated strategy file",Utils.LOG_LOW);
        } catch (IOException ex) {
            Utils.log(ex,Utils.LOG_ERR);
        }
       
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the ruleObservableList
     */
    public ObservableList<Rule> getRulesList() {
        return ruleObservableList;
    }

    /**
     * @return the ruleObservableList
     */
    public ObservableList<Rule> getExecutedRulesList() {
        return executedRulesObservableList;
    }
    
        /**
     * @return the ruleObservableList
     */
    public ObservableList<Rule> getActiveRulesList() {
        return activeRulesObservableList;
    }
    /**
     * @param ruleObservableList the ruleObservableList to set
     */
    public void setRulesList(ObservableList<Rule> ruleObservableList) {
        this.ruleObservableList = ruleObservableList;
    }

    private void updateExecutedRulesList() {
        executedRulesList.clear();
        executedRulesObservableList.clear();
        for(int i=0; i<getRulesList().size();i++)
        {
            if (getRulesList().get(i).isExecuted() && getRulesList().get(i).isVisualize())
                executedRulesObservableList.add(getRulesList().get(i));
        }           
    }

    private void updateActiveRulesList() {
        activeRulesList.clear();
        activeRulesObservableList.clear();
        for(int i=0; i<getRulesList().size();i++)
        {
            if (getRulesList().get(i).isActive())
                activeRulesObservableList.add(getRulesList().get(i));
        }        
    }      
    
    public void clearExecuted()
    {
        for(int i=0; i<getRulesList().size();i++)
        {
            if (getRulesList().get(i).isExecuted())
                getRulesList().get(i).setVisualize(false);
        }   
        
        updateExecutedRulesList();
    }

    private void refreshLists() {
        if(Global.startupComplete)
        {
            Platform.runLater(new Runnable() {
        @Override
        public void run() {
            DashboardStrategyController strategyController = (DashboardStrategyController) Global.getController(Constant.STRATEGY);
            DashboardStatusController statusController = (DashboardStatusController) Global.getController(Constant.STATUS);

            //refresh table strategy
            TableView tableview = strategyController.getTableView();
            TableColumn tc = (TableColumn)tableview.getColumns().get(0);
            tc.setVisible(false);
            tc.setVisible(true);

            //refresh titles status
            statusController.updateTitles();

            //refresh lists status
            updateExecutedRulesList();
            updateActiveRulesList();
            statusController.updateLists();
            }
         });   
        }
    }

}
