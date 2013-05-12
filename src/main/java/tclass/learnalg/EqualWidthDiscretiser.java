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
  * @version $Id: EqualWidthDiscretiser.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.AttDescVecI;
import tclass.ClassStreamAttValVecI;
import tclass.DataTypeI;
import tclass.StreamAttValVecI;
import tclass.datatype.Discrete;
import tclass.util.Debug;

public class EqualWidthDiscretiser implements DiscretiserI {
    int numBins;
    boolean singular = false; 
    Binner myBinner; 


    public int size()
    {
	return numBins; 
    }


    
    public void makeDiscretisation(ClassStreamAttValVecI data, int
				   numBins, int attNum){
	//Well, this is easy 
	this.numBins = numBins; 
	StreamAttValVecI streams = data.getStreamAttValVec(); 
	DataTypeI dt = streams.getDescription().elAt(attNum).getDataType(); 
	Debug.myassert(dt.getName().equals("continuous"), "WARNING!! Attempt to discretise non-continuous value"); 
	float min = Float.MAX_VALUE; 
	float max = -Float.MAX_VALUE; 
	int numStreams = streams.size(); 
	for(int i=0; i < numStreams; i++){
	    float val = streams.elAt(i).getAtt(attNum); 
	    if(val < min){
		min = val; 
	    }
	    if(val > max && val != Double.MAX_VALUE){
		max = val; 
	    }
	}
	AttDescVecI advi = streams.getDescription(); 
	Debug.dp(Debug.EMERGENCY, "For " + advi.elAt(attNum) + " range is : " + min + " to " + max); 

	// And now make a binner for ourselves. 
	
	Debug.myassert(max!=min, "WARNING!!! max = min for " +
		     advi.elAt(attNum)); 
	if(max == min) singular = true; 
	if(!singular){
	    myBinner = new Binner(numBins); 
	    float increment = (max-min)/numBins; 
	    for(int i=1; i < numBins; i++){
		myBinner.setBound(i-1, min + i*increment); 
	    }
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
		
		retval.setParam("classes", classes); 
	    }
	    catch(Exception e){
		//Naughty! Naughty!! 
	    }
	    return retval; 
	}
	else {
	    try {
		retval.setParam("classes", "singular"); 
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
