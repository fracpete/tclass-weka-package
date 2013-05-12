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
  * A simple PEP for doing RLE (run length encoding). 
  * It finds periods for which a value does not change for an elongated period
  * of time. 
  * 
  * @author Waleed Kadous
  * @version $Id: RLE.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.pep;   
import tclass.ChannelDesc;
import tclass.ChannelI;
import tclass.DataTypeI;
import tclass.DataTypeMgr;
import tclass.DomDesc;
import tclass.EventDesc;
import tclass.EventDescI;
import tclass.EventI;
import tclass.EventVec;
import tclass.EventVecI;
import tclass.InvalidParameterException;
import tclass.Param;
import tclass.ParamVec;
import tclass.PepI;
import tclass.StreamI;

public class RLE implements PepI {

    static final String name = "rle"; 
    static final String description = "Looks for values that are stable over time";

    DomDesc domDesc = null; 

    String limitVals = ""; 
    
    int minRun = 2; 
    
    int chanIndex = 0; 
    // Assume the correct channel is the first. 

    /** 
     * Gets the name of the PEP. Used by the prototype manager 
     * as a key. 
     *
     * @return A key representing this particular PepPT
     */ 
    public String name(){
	return name; 
    }

    /**
     * Set the domain description
     */ 
    
    public void setDomDesc(DomDesc d){
	domDesc = d; 
    }

        /**
     * Clone the current object. 
     *
     */ 

    @Override
    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }
    
    /** 
     * Provides a description of the PepI. This description explains
     * what the basic idea of the PEP is (i.e. the sort of shapes it
     * tried to find). It should also explain any potential
     * configuration options that may
     * be used to configure the object, using the configure option. 
     * 
     * @return The description of this class. 
     */ 

    public String description(){ 
	return description; 
    }

    /**
     * Configures this instance so that parameter <i>p</i> has
     * value <i>v</i>. 
     *
     * @param p the parameter to set. 
     * @param v the value of the parameter. 
     *
     */

    public void setParam(String p, String v) throws InvalidParameterException {
	if(p.equals("channel")){
	    // So they want us to do RLE on a channel. 
	    // Any channel will do, provided it exists. 
	    chanIndex = domDesc.getChanIndex(v); 
	    if(chanIndex == -1){
		throw new InvalidParameterException(p, v, "Unknown channel " + v); 
	    }

	}
	else if(p.equals("minrun")){
	    try {
		minRun = Integer.parseInt(v); 
	    }
	    catch(NumberFormatException nfe){
		throw new InvalidParameterException(p, v, v +  " is not a number"); 
	    }
	}
               else if(p.equals("limitvalues")){
                   limitVals = v; 
	}
	else {
	    throw new InvalidParameterException(p, v, "Unknown parameter"); 
	}
	
    }


    /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */    
    public ParamVec getParamList() {
	ParamVec pv = new ParamVec(); 
	pv.add(new Param("channel", "Channel to operate on", "First channel")); 
	pv.add(new Param("minrun", "Minimum stable period", "2")); 
	pv.add(new Param("limitvalues", "Limit interest only to these values", "All")); 
	return pv; 
    }

    /** 
     * Now we get to the complicated stuff.
     *
     * Get a description of the events created by this prototype. 
     * This includes the description of the number of parameters, 
     * the names of the parameters and other info. Note also that
     * this is included free with any EventVec's we return. 
     * 
     */
    public EventDescI getEventDesc(){
	// For run length encoding, each event consists of three pieces of 
	// information: 
	// - The value itself (depends on the channel)
	// - The start time (continuous)
	// - The duration  (continuous)
	
	EventDesc ed = new EventDesc(); 
	DataTypeMgr dtm = DataTypeMgr.getInstance(); 
	ChannelDesc cd = domDesc.getChannel(chanIndex); 
	ed.addParam("value", cd.getDataType()); 
	ed.addParam("start", dtm.getClone("continuous"));
	ed.addParam("durn",  dtm.getClone("continuous"));
	return (EventDescI) ed; 

    }

    
    
    /**
     * The finding function for this PEP. Returns all the events
     * of the form this PEP represents. Returns an EventVecI. 
     * 
     * @param s The stream we want to play with
     * @return A vector of the events of the type extracted by this
     * PEP. 
     */
    
    public EventVecI findEvents(StreamI s){
	EventVec ev = new EventVec(); 
	// Grab our channel. 
	ChannelI c = s.chanAt(chanIndex); 
	int numFrames = c.numFrames(); 
	DataTypeI d = domDesc.getChannel(chanIndex).getDataType(); 
	if(numFrames > 1){
	    RLEData curr = new RLEData(d, c.valAt(0), 0, 1); 
	    // Now loop through. 
	    for(int i = 1; i < numFrames; i++){
		//Check if the new value is the same as the previous. 
		//If so, increment the
		if(c.valAt(i) == curr.value){
		    curr.durn++; 
		}
		else {
		    // Otherwise, get rid of the old object, 
		    // and create a new one. 
		    if(curr.durn >= minRun){
                        // System.out.println("LV = " + limitVals + " look for >" + " " + curr.value + " " + "<"); 
                        if(limitVals.equals("") || (limitVals.indexOf(" " + d.print(curr.value) + " " ) != -1)){
			ev.add(curr); 
                        }
		    }
		    curr = new RLEData(d, c.valAt(i), i, 1); 
		}
	    }
	    // We need to handle the last case as well. 
	    if(curr.durn >= minRun){
                if(limitVals == "" || (limitVals.indexOf(" " + d.print(curr.value) + " " ) != -1)){
		ev.add(curr); 
                }
	    }
	}
	
	return ev; 
	
    }
    
}

    // A class for keeping track of the information we have about an RLE. 

class RLEData implements EventI {
    DataTypeI d; 
    float value = 0 ; 
    int start = 0; 
    int durn = 0; 

    RLEData(DataTypeI d){
	this.d = d; 
    }

    RLEData(DataTypeI d, float value, int start, int durn){
	this.d = d; 
	this.value = value; 
	this.start = start;
	this.durn = durn; 
    }

    // Note that this format matches the EventDesc given in 
    // RLE.getEventDesc(). If you modify that, you've got to modify
    // this too. 
	
    public float valOf(int i){
	if(i==0){
	    return value; 
	}
	if(i==1){
	    return start; 
	}
	if(i==2){
	    return durn; 
	}
	else return 0; 
    }

    @Override
    public String toString(){
	return "Run: val = " + d.print(value)
	    + " start = " + start + " durn = " + durn; 
    }
    
    public float getDuration(){ return durn; }
    public float getMidtime(){ return start + (float) durn/2.0f; }

}
