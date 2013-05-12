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
  * Extracts the mode of a sequence. This is tricky; because we want
  * it to work for every class. Recall that the mode is the most common value
  * in a sequence. 
  * 
  * Right now, the implementation's a little cruddy; using the JDK's Hashtable 
  * class; where the key is a float and the value is the count of instances. 
  * 
  * @author Waleed Kadous
  * @version $Id: First.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.global;   
import tclass.ChannelDesc;
import tclass.ChannelI;
import tclass.DataTypeI;
import tclass.DomDesc;
import tclass.GlobalExtractorI;
import tclass.InvalidParameterException;
import tclass.Param;
import tclass.ParamVec;
import tclass.StreamI;

public class First implements GlobalExtractorI {
    static final String baseName = "first"; 
    static final String desc = 
	"Grabs the first value of a specified channel"; 
    
    DomDesc domDesc = null; 
    
    // New fields for remembering the channel we are working on. 
    
    int chanIndex = 0; // Stores the current channel's index. By default, 
    // it's the first channel. 
    
    public First(){
	// Any special initialisation code goes here. 
	// Can't think of anything special. 
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
     * Gets the base name of this global extractor
     *
     * @return the Extractor's name
     */ 
    
    public String name(){
	return baseName; 
    }

    /**
     * Returns the datatype of this object. 
     */ 

    public DataTypeI getDataType(){
	// The datatype of this instance is the datatype of the channel
	// we are supposed to be analysing. 
	ChannelDesc cd = domDesc.getChannel(chanIndex); 
	return cd.getDataType(); 
    }

    /**
     * Provides a description for the Global Extractor. It explains the 
     * basic features the Extractor is looking for. 
     *
     * @return A simple description
     * 
     */

    public String description(){
	return desc; 
    }

    public void setDomDesc(DomDesc d){
	domDesc = d; 
    }

    /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */
    
    public ParamVec getParamList() {
	ParamVec p = new ParamVec();
	p.add(new Param("channel", "Channel you want the first value of", "First Channel")); 
	return p; 
    }
    
    /**
     * Configures this particular extractor so that parameter <i>p</i> 
     * has value <i>v</i>.
     *
     * @param p The parameter to set.
     * @param v The value of the parameter. 
     * @return True if the setting succeeded; false otherwise. 
     */
    
    public void setParam(String p, String v)  throws InvalidParameterException {
	// Well, let's accept any value at all for ex1, otherwise, 
	// spit the dum- ... err, ummm ... InvalidParameterException
	if(p.equals("channel")){
	    //Get the channel's index, from our DomDesc object. 
	    chanIndex = domDesc.getChanIndex(v); 
	    if(chanIndex == -1){
		throw new InvalidParameterException(p, v, "Unknown channel " + v); 
	    }
	}
	else {
	    throw new InvalidParameterException(p, v, "Unknown parameter."); 
	}
	
    }

    /**
     * Gets the feature that this global is supposed to extract. For
     * now, we assume that global extractors return a double. Note
     * that this float has a datatype associated with it; which
     * explains what it means. 
     * 
     * @param s the stream we want to extract the global feature 
     * from. 
     * @return The global feature's value. 
     *
     */


    public float extract(StreamI s){
	// Get the datatype. We are going to be good little boys 
	// and use the provided cmp function. 	
	// Grab a Channel interface. Oh joy!!

	ChannelI c = s.chanAt(chanIndex); 

	return c.valAt(0); 
    } 

}
