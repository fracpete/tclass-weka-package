/**
  * Going back to old habits ... this is an attempt to reimplement the
  * new-fangled MDL-based line segmentation. 
  * 
  * @author Waleed Kadous
  * @version $Id: LineSeg.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.pep;   
import tclass.*; 
import tclass.util.*; 
import java.util.*; 

/** 
 * Assumptions in this model: 
 *  - Noise is assumed to follow independent Gaussian distribution
 * with zero mean. 
 * - Variance is assumed to be identical within a region, but variable 
 * between regions. 
 *
 * This stuff is based on Manganaris' work. 
 * For now, we will only allow Linear modelling, but may later 
 * switch to 0th, 1st, 2nd degree polynomial. 
 * Basically, we sum three parts: 
 * - L(M) = length of encoded model structure
 * - L(M(p)) = length of encoded model parameters. 
 * - L(y|M(p)) = length of encoded deviation from model M using
 * parameters p. 
 * For now, we assume L(M) is constant (since we're only going to use
 * 1st order stuff). 
 */

public class LineSeg implements PepI {

    static final String name = "lineseg"; 
    static final String description = "A line segmentation approach"; 
    DomDesc domDesc = null; 
    private int chanIndex; 

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
     * @return true if the operation succeeded. 
     *
     */

    public void setParam(String p, String v) throws InvalidParameterException {
        if(p.equals("channel")){
            chanIndex = domDesc.getChanIndex(v); 
            if(chanIndex == -1){
                throw new InvalidParameterException(p, v, "Unknown channel " + v); 
    
            }
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
        pv.add(new Param("channel", "Channel to apply line seg to",
                         "first channel")); 
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
        ed.addParam("midtime", dtm.getClone("continuous")); 
        ed.addParam("avg", dtm.getClone("continuous")); 
        ed.addParam("gradient", dtm.getClone("continuous")); 
        ed.addParam("duration", dtm.getClone("continuous")); 
        return (EventDescI) ed; 
    }


    

    /**
     * The finding function for this PEP. Returns all the events
     * of the form this PEP represents. Returns an EventVecI. 
     * 
     * @param c The channel that we want the finding function to
     * operate on. 
     * @return A vector of the events of the type extracted by this
     * PEP. 
     */
    public EventVecI findEvents(StreamI s){
        int oldDebugLevel = Debug.getDebugLevel(); 
        // Debug.setDebugLevel(Debug.EVERYTHING); 
        DataTypeI d = domDesc.getChannel(chanIndex).getDataType(); 
        Segmenter seg = new Segmenter(s.chanAt(chanIndex)); 
        seg.segment(); 
        EventVec ev = seg.getEventVec(); 
        ev.setEventDesc(getEventDesc()); 
        Debug.setDebugLevel(oldDebugLevel); 
        return ev; 
    }
}

