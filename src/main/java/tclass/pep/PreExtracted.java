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
  * A code for preextracted features that actually live in the
  * data file. A hack that allows pre-extracted metafeatures
  * to be included. Currently, limited by the fact that all 
  * streams have to have an equal number of channels. So you 
  * have to have them in such a way as they all have the same
  * type and has to be zero padded. Works only with a single metafeature
  * right now. 
  * 
  * @author Waleed Kadous
  * @version $Id: PreExtracted.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.pep;   
import tclass.ChannelDesc;
import tclass.DataTypeMgr;
import tclass.DomDesc;
import tclass.Event;
import tclass.EventDesc;
import tclass.EventDescI;
import tclass.EventVec;
import tclass.EventVecI;
import tclass.InvalidParameterException;
import tclass.Param;
import tclass.ParamVec;
import tclass.PepI;
import tclass.StreamI;

public class PreExtracted implements PepI {

    static final String name = "preex"; 
    static final String description = "A pre-extracted metafeature."; 
    DomDesc domDesc = null; 

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
        // Now that we have the domain description, we can use it 
        // to construct the features. It tells us the number and names of the channels.
        // This gives us the format of the event. 
        // 
    }

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
        if(p.equals("expar")){
            // Do nothing. 
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
        pv.add(new Param("expar", "Example Parameter", "who cares?")); 
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
        // In this case, let's assume the parameter is a very simple one. 
        // A single parameter that is always zero. 

        EventDesc ed = new EventDesc(); 
        DataTypeMgr dtm = DataTypeMgr.getInstance(); 
        int numChans = domDesc.numChans(); 
        for(int i = 0; i < numChans; i++){
            ChannelDesc cd = domDesc.getChannel(i);             
            ed.addParam(cd.getName(), cd.getDataType()); 
        }
        return (EventDescI) ed; 
    }

    /**
     * The finding function for this PEP. Returns all the events
     * of the form this PEP represents. Returns an EventVecI. 
     * 
     * @param s The channel that we want the finding function to
     * operate on. 
     * @return A vector of the events of the type extracted by this
     * PEP. 
     */

    public EventVecI findEvents(StreamI s){
        EventVec ev = new EventVec(); 
        int numFrames = s.numFrames(); 
        int numChans = domDesc.numChans(); 
        for(int i = 0; i < numFrames; i++){
            Event e = new Event(numChans); 
            for(int j = 0; j < numChans; j++){
                e.setVal(j, s.valAt(i,j)); 
            }
            ev.add(e); 
        }
        return ev;
    }
}
