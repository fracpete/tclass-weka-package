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
  * @version $Id: LineDisplay.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.io.FileWriter;
import java.io.PrintWriter;

import tclass.pep.LineEvent;
import tclass.util.Debug;

public class LineDisplay {
    String domDescFile = "sl.tdd"; 
    String dataFile = ""; 
    String segmenter = "";
    String chanName = ""; 
    String rawFile = "raw.plot"; 
    String fitFile = "fit.plot"; 
    
    void parseArgs(String[] args){
        for(int i=0; i < args.length; i++){
            if(args[i].equals("-d")){
                domDescFile = args[++i]; 
            }
            if(args[i].equals("-f")){
                dataFile = args[++i];
            }
            if(args[i].equals("-s")){
                segmenter = args[++i]; 
            }
            if(args[i].equals("-c")){
                chanName = args[++i]; 
            }
            if(args[i].equals("-r")){
                rawFile = args[++i]; 
            }
            if(args[i].equals("-l")){
                fitFile = args[++i]; 
            }
        }
    }
    public static void main(String args[]) throws Exception {
        LineDisplay  main = new LineDisplay(); 
        main.parseArgs(args); 
        Debug.setDebugLevel(Debug.PROGRESS); 
        DomDesc domDesc = new DomDesc(main.domDescFile); 
        StreamI data = new Stream(main.dataFile, domDesc); 
        PepMgr pm = PepMgr.getInstance(); 
        PepI segger =pm.getClone(main.segmenter, domDesc); 
        segger.setParam("channel", main.chanName);  
        EventVecI events = segger.findEvents(data); 
        // Now cast it to a SegmentSequence. 
        int numEvents = events.size(); 
        PrintWriter raw = new PrintWriter(new FileWriter(main.rawFile));
        PrintWriter fit = new PrintWriter(new FileWriter(main.fitFile));
        for(int i = 0; i < numEvents; i++){
            raw.print(((LineEvent) events.elAt(i)).printRawValues()); 
            fit.println(((LineEvent) events.elAt(i)).printFitValues()); 
        }
        raw.close(); 
        fit.close(); 
    }
    
}
