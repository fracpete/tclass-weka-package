 /**
  * A main class designed to take some info and extract all the events
  * from a file. For monitoring purposes. 
  * 
  * @author Waleed Kadous
  * @version $Id: ExtractEvents2.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.util.*; 
import java.io.*; 

import tclass.util.*; 

public class ExtractEvents2 {
    String domDescFile = ""; 
    String dataFile = ""; 
    String eventExtractFile = "";
    
    void parseArgs(String[] args){
        System.out.println("args is" + args[0] + " " + args[1]); 
        domDescFile = args[0]; 
        eventExtractFile = args[1]; 
    }
    public static void main(String args[]) throws Exception {
        System.out.println("WTF"); 
        ExtractEvents2  main = new ExtractEvents2(); 
        main.parseArgs(args); 
        Debug.setDebugLevel(Debug.PROGRESS); 
        DomDesc domDesc = new DomDesc(main.domDescFile); 
        Settings settings = new Settings(main.eventExtractFile, domDesc); 
        for(int i=2; i < args.length; i++){
            main.dataFile = args[i]; 
            StreamI data = new Stream(main.dataFile, domDesc); 
            EventExtractor ee =  settings.getEventExtractor(); 
            StreamVecI svi = new StreamVec(); 
            svi.add(data); 
            ClassStreamVec csvi = new ClassStreamVec(svi,domDesc); 
            ClassStreamEventsVecI csevi= ee.extractEvents(csvi); 
            StreamEventsI se = csevi.getStreamEventsVec().elAt(0); 
            System.out.println("Events for file " + main.dataFile); 
            System.out.println(se.prettyPrint(csevi.getStreamEventsVec().getEventDescVec())); 
        }
    }
    
}
