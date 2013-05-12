
package tclass;   

/**
  * The interface for a classifier. These objects are produced by 
  * learners. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClassifierI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClassifierI {

    /** 
     * Gets the name of the classifier. 
     */ 

    public String getName(); 

    /**
     * Gets a description of the classifier. 
     */ 
    
    public String getDescription(); 

    /** 
     * Takes a vector of attribute-value pairs and updates the given
     * ClassificationI object. 
     *
     * @param instance The instance to classify. This object must be 
     * created in the same format (i.e. be described by the same
     * <code>AttDescVecI</code> as those used by the trainer. 
     * 
     * @param classn The classification to be updated. The
     *      <code>ClassificationI</code> methods 
     * <code>setPredictedClass()</code> and possibly
     * <code>setPredictedClassConfidence()</code> may be called in
     * this method. 
     */ 

    public void classify(StreamAttValI instance, ClassificationI classn); 
    
}
