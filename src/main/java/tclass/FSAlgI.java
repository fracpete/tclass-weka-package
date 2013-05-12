/**
  * Interface description for feature selection algorithms
  *
  * 
  * @author Waleed Kadous
  * @version $Id: FSAlgI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface FSAlgI extends Cloneable {
    
    /** 
     * Gets the name of the FSA. Used by the prototype manager 
     * as a key. 
     *
     * @return A key representing this particular FSA
     */ 
    public String name(); 

    
    /** 
     * Provides a description of the FSAlg. This description explains
     * what the basic idea of the FSA is (i.e. the sort of shapes it
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

    public boolean setParam(String p, String v); 


    /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */    
    public ParamVec getParamList(); 

    /**
     * Apply the feature selection algorithm
     * 
     */ 
    
    public AttDescVecI featuresel(ClassStreamAttValVecI dataset);

    public ClassStreamAttValVecI select(ClassStreamAttValVecI dataset, 
					AttDescVecI atts); 
    
    
}
