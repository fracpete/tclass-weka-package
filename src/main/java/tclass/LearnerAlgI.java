/**
  * Interface description for learning algorithms
  *
  * 
  * @author Waleed Kadous
  * @version $Id: LearnerAlgI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import weka.core.*; 

public interface LearnerAlgI extends Cloneable {
    
    /** 
     * Gets the name of the Learner algorithm. Used by the prototype manager 
     * as a key. 
     *
     * @return A key representing this particular Learner Algorithm
     */ 
    public String name(); 

    
    public Object clone(); 

    /** 
     * Provides a description of the LearnerAlg. This description explains
     * what the basic idea of the learner is (i.e. the sort of shapes it
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
     * Apply the feature selection algorithm
     * 
     */ 
    
    public ClassifierI learn(ClassStreamAttValVecI input); 
    
}
