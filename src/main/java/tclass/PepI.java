/**
  * The interface class for all the PEP prototypes. 
  * PepI is short for Parametrised Event Primitive Interface
  * (Whoa! What a mouthful!). 
  * Note that we implement Cloneable, i.e. we can clone the prototypes. 
  * 
  * @author Waleed Kadous
  * @version $Id: PepI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface PepI extends Cloneable {

    /** 
     * Gets the name of the PEP. Used by the prototype manager 
     * as a key. 
     *
     * @return A key representing this particular PepPT
     */ 
    public String name(); 

    public Object clone(); 
    
    /**
     * Set the domain description that this object will use to interpret 
     * data. 
     *
     */ 
    
    public void setDomDesc(DomDesc d);
    
    /** 
     * Provides a description of the PepPTI. This description explains
     * what the basic idea of the PEP is (i.e. the sort of shapes it
     * tried to find). It should also explain any potential
     * configuration options that may
     * be used to configure the object, using the configure option. 
     * 
     * @return The description of this class. 
     */ 

    public String description(); 

    /**
     * Configures this instance so that parameter <i>p</i> has
     * value <i>v</i>. 
     *
     * @param p the parameter to set. 
     * @param v the value of the parameter. 
     * @return true if the operation succeeded. 
     *
     */

    public void setParam(String p, String v) throws InvalidParameterException; 


    /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */    
    public ParamVec getParamList(); 

    /** 
     * Now we get to the complicated stuff.
     *
     * Get a description of the events created by this prototype. 
     * This includes the description of the number of parameters, 
     * the names of the parameters and other info. Note also that
     * this is included free with any EventVec's we return. 
     * 
     */
    public EventDescI getEventDesc();

    /**
     * The finding function for this PEP. Returns all the events
     * of the form this PEP represents. Returns an EventVecI. 
     * 
     * @param c The channel that we want the finding function to
     * operate on. 
     * @return A vector of the events of the type extracted by this
     * PEP. 
     */

    public EventVecI findEvents(StreamI s); 
    
       
        
}
