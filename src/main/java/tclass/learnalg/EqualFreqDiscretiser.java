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
  * What this class does goes here
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EqualFreqDiscretiser.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.AttDescVecI;
import tclass.ClassStreamAttValVecI;
import tclass.DataTypeI;
import tclass.StreamAttValVecI;
import tclass.datatype.Discrete;
import tclass.util.Debug;

public class EqualFreqDiscretiser implements DiscretiserI {
    int numBins; 
    boolean singular = false; 
    Binner myBinner; 

    static String d2str(float[] data){
	StringBuffer retval = new StringBuffer(data.length*12); 
	retval.append("[ "); 
	for(int i=0; i < data.length; i++){
	    retval.append(data[i] + " "); 	   
	}
	retval.append("]"); 
	return retval.toString(); 
    }
    
    static int findMinIndex(int minPos, float[] vals){
	int retval=0;
	float minVal = vals[minPos]; 
 	for(int i=minPos; i< vals.length; i++){
	    if(vals[i] <= minVal){
		minVal = vals[i]; 
		retval = i; 
	    }
	}
	return retval; 
    }
    
    public int size(){
	return numBins; 
    }

    
    static void swap(int a, int b, float[] vals){
	float tmp; 
	tmp = vals[a]; 
	vals[a] = vals[b];
	vals[b] = tmp; 
    }

    static void ssort(float[] vals){
	for(int i=0; i < vals.length; i++){
	    int correctOne = findMinIndex(i, vals); 
	    swap(i, correctOne, vals); 
	}
	// Debug.dp(Debug.EMERGENCY, "Sorted = " + d2str(vals)); 
    }
    public void makeDiscretisation(ClassStreamAttValVecI data, int
				   numBins, int attNum){
	StreamAttValVecI streams = data.getStreamAttValVec(); 
	DataTypeI dt = streams.getDescription().elAt(attNum).getDataType(); 
	Debug.myassert(dt.getName().equals("continuous"), "WARNING!! Attempt to discretise non-continuous value"); 
	this.numBins = numBins; 
	int numStreams = streams.size(); 
	float[] vals = new float[numStreams]; 	
	float min = Float.MAX_VALUE; 
	float max = -Float.MAX_VALUE; 
	for(int i=0; i < numStreams; i++){
	    vals[i]=streams.elAt(i).getAtt(attNum); 
	    if(vals[i] < min){
		min = vals[i]; 
	    }
	    if(vals[i] > max && vals[i] != Float.MAX_VALUE){
		max = vals[i]; 
	    }
	}
	
	ssort(vals);
	

	AttDescVecI advi = streams.getDescription(); 

	// And now make a binner for ourselves. 
	
	Debug.myassert(max!=min, "WARNING!!! max = min for " +
		     advi.elAt(attNum)); 
	if(max == min) singular = true; 
	if(!singular){
	    myBinner = new Binner(numBins); 
	    for(int i=1; i < numBins; i++){
		// A little cleaner to do things this way. Get rid of those 
		// horrible empty bins. 
		float breakpt = vals[i*numStreams/numBins]; 
		myBinner.setBound(i-1, breakpt);
	    }
	    	
	    Debug.dp(Debug.EVERYTHING, "For " + advi.elAt(attNum) + " range is : " + min + " to " + max + " " + myBinner.toString()); 

	}
    }
    
    public Discrete getDiscType(){
	Discrete retval = new Discrete(); 
	String classes = ""; 
	
	if(!singular){
	    for(int i=0; i<numBins; i++){
		classes += "bin"+i+" "; 
	    }
	    try {
		
		retval.setParam("values", classes); 
	    }
	    catch(Exception e){
		//Naughty! Naughty!! 
	    }
	    return retval; 
	}
	else {
	     try {
		 Debug.dp(Debug.EVERYTHING, "Setting class to singular"); 
		 retval.setParam("values", "singular"); 
	    }
	    catch(Exception e){
		//Naughty! Naughty!! 
	    }
	    return retval; 
	}
	
    }
    
    public int discretise(float val){
	if(!singular){
	    return myBinner.findBin(val); 
	}
	else {
	    return 0; 
	}
    }
    
}

