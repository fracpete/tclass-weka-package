 /**
  * A main class designed to take some info and extract all the events
  * from a file. For monitoring purposes. 
  * 
  * @author Waleed Kadous
  * @version $Id: ExtractEvents.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.util.*; 
import java.io.*; 

import tclass.util.*; 

public class ExtractEvents {
    String domDescFile = ""; 
    String dataFile = ""; 
    String eventExtractFile = "";
    
    void parseArgs(String[] args){
	for(int i=0; i < args.length; i++){
	    if(args[i].equals("-d")){
		domDescFile = args[++i]; 
	    }
	    if(args[i].equals("-f")){
		dataFile = args[++i];
	    }
	    if(args[i].equals("-e")){
		eventExtractFile = args[++i]; 
	    }
	}
    }
    public static void main(String args[]) throws Exception {
	ExtractEvents  main = new ExtractEvents(); 
	main.parseArgs(args); 
	Debug.setDebugLevel(Debug.PROGRESS); 
	DomDesc domDesc = new DomDesc(main.domDescFile); 
	StreamI data = new Stream(main.dataFile, domDesc); 
        Settings settings = new Settings(main.eventExtractFile, domDesc); 
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
