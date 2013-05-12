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
 *  Single classifier solution. That is to say, we cluster all the instances
 *  using the same clustering algorithms. 
 * 
 * 
 * @author Waleed Kadous
 * @version $Id: GetPoints.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import java.io.FileWriter;

import tclass.util.Debug;

public class GetPoints {

    // Ok. What we are going to do is to separate the learning task in 
    // an interesting way. 
    // First of all, though, the standard stuff
    
    String domDescFile = "sl.tdd"; 
    String trainDataFile = "sl.tsl";  
    String settingsFile = "test.tal"; 
    String outputFile = "allEvents.dat"; 
    boolean classLabels = false; 

    void parseArgs(String[] args){
        for(int i=0; i < args.length; i++){
            if(args[i].equals("-tr")){
                trainDataFile = args[++i]; 
            }
            if(args[i].equals("-settings")){
                settingsFile = args[++i]; 
            }
            if(args[i].equals("-o")){
                outputFile = args[++i]; 
            }
            if(args[i].equals("-c")){
                classLabels = true; 
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Debug.setDebugLevel(Debug.PROGRESS); 
        GetPoints thisExp = new GetPoints(); 
        thisExp.parseArgs(args); 
        DomDesc domDesc = new DomDesc(thisExp.domDescFile); 
        ClassStreamVecI trainStreamData = new
            ClassStreamVec(thisExp.trainDataFile, domDesc); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Data read in");  
        Settings settings = new Settings(thisExp.settingsFile, domDesc); 
        

        EventExtractor evExtractor = settings.getEventExtractor(); 
        // Global data is likely to be included in every model; so we
        // might as well calculated now

        ClassStreamEventsVecI trainEventData =
            evExtractor.extractEvents(trainStreamData); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Events extracted");  
        // System.out.println(trainEventData.toString()); 


        // Now we want the clustering algorithms only to cluster
        // instances of each class. Make an array of clusterers, 
        // one per class. 

        EventDescVecI eventDescVec = evExtractor.getDescription(); 
        EventClusterer eventClusterer = settings.getEventClusterer(); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: Data rearranged.");  


        //And now load it up. 
        
        FileWriter fw = new FileWriter(thisExp.outputFile); 
        fw.write(eventClusterer.printAllData(trainEventData,thisExp.classLabels)); 
    }
}
