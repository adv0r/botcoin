package io.botcoin.utils;
 
import static io.botcoin.utils.Utils.LOG_ERR;
import static io.botcoin.utils.Utils.log;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author advanced
 */
public class FileSystem {
     
    
     public static void deleteFile(String path) {
       try{
            File file = new File(path);
            if(file.delete()){
                    log("file "+file.getName() + " deleted!");
            }else{
                    log("Delete operation is failed for : "+ path,LOG_ERR);
            }
    	}
       catch(Exception e){
                log(e,LOG_ERR);
    		e.printStackTrace();
    	}    
    }
        
   public static String readFromFile(String path) {
         
        File file = new File(path);
         
        StringBuilder fileContent = new StringBuilder();
        BufferedReader bufferedReader = null;
         
        try {
 
            bufferedReader = new BufferedReader(new FileReader(file));
             
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                fileContent.append(text);
            }
 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
                 
        return fileContent.toString();
    }
}