/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

 /**
  * A main class designed to take some info and extract all the events
  * from a file. For monitoring purposes. 
  * 
  * @author Waleed Kadous
  * @version $Id: ExtractEvents.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import tclass.util.Debug;

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
