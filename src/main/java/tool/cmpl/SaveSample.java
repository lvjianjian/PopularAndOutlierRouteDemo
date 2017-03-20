package tool.cmpl;

import tool.Properties;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by JQ-Cao on 2016/6/16.
 */
public class SaveSample {
    public static boolean saveSample(long startId,long endId){
        try{
            FileWriter writer = new FileWriter(Properties.samplePath,true);
            writer.write(startId+"\t"+endId+"\n");
            writer.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
        return true;
    }
}
