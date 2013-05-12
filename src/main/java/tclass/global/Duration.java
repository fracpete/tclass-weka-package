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
  * A very simple global extractor. Finds the duration of the 
  * stream. 
  * 
  * @author Waleed Kadous
  * @version $Id: Duration.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.global;   
import tclass.DataTypeI;
import tclass.DataTypeMgr;
import tclass.DomDesc;
import tclass.GlobalExtractorI;
import tclass.InvalidParameterException;
import tclass.ParamVec;
import tclass.StreamI;

public class Duration implements GlobalExtractorI {
    static final String baseName = "duration"; 
    static final String desc = 
	"Finds the duration of the stream"; 
    
    DomDesc domDesc = null; 
    
    // New fields for remembering the channel we are working on. 
    
    int chanIndex = 0; // Stores the current channel's index. By default, 
    // it's the first channel. 
    
    public Duration(){
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
	// The duration is always a continuous value. So let's ask for it. 
	// From the DataTypeMgr. 
	
	DataTypeMgr dtm = DataTypeMgr.getInstance(); 
	return (dtm.getClone("continuous")); 
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
	// No parameters. 
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
	throw new InvalidParameterException(p, v, "Unknown parameter."); 
    }

    /**
     * Gets the feature that this global is supposed to extract. For
     * now, we assume that global extractors return a float. Note
     * that this float has a datatype associated with it; which
     * explains what it means. 
     * 
     * @param s the stream we want to extract the global feature 
     * from. 
     * @return The global feature's value. 
     *
     */

    public float extract(StreamI s){
	return s.numFrames(); 
    } 

}
