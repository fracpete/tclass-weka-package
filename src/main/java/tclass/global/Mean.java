/**
  * A mean extractor. Works directly on the doubles. I can't see a better 
  * way to do this. 
  * 
  * @author Waleed Kadous
  * @version $Id: Mean.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.global;   
import tclass.*; 

public class Mean implements GlobalExtractorI {
    static final String baseName = "mean"; 
    static final String desc = 
	"Finds the mean of a specified channel"; 
    
    DomDesc domDesc = null; 
    
    // New fields for remembering the channel we are working on. 
    
    int chanIndex = 0; // Stores the current channel's index. By default, 
    // it's the first channel. 
    
    public Mean(){
	// Any special initialisation code goes here. 
	// Can't think of anything special. 
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
	// The mean is always a continuous value. So let's ask for it. 
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
	p.add(new Param("channel", "Channel you want the maximum for", "First Channel")); 
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
	
	// And now iterate. 
	
	float sum = 0; 
	int numFrames = c.numFrames(); 

	for(int i=0; i < numFrames; i++){
	    sum += c.valAt(i); 
	}
	return (sum/numFrames); 
    } 

}
