
package tclass;   

/**
  * Interface for a set of attribute value pairs, including the
  * classification.
  *<p>
  * When you think about it, the classification of a set of instances
  * and the instances themselves are separate. For example, a different
  * representation of the stream still has the same class. 
  * This technique of having a representation for streams and classifications
  * separately simplifies representation conversion (which, as you can guess, 
  * we do in several places in the code, since representation conversion
  * is the basis of the learning algorithm). 
  * <p>
  * So this class acts as little more than a wrapper of two classes, one which 
  * is the ClassificationVecI and the other which is the StreamAttValVecI. 
  * <p>
  * Note that the classification vector and the vector of attribute-value streams 
  * should be of equal size ... otherwise ... evil things will happen. 
  * <p>
  * @author Waleed Kadous
  * @version $Id: ClassStreamAttValVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ 
  */

public interface ClassStreamAttValVecI {
    /**
     * Returns true if the class has a ClassificationVecI associated with 
     * it. 
     */

    public boolean hasClassification();     

    /**
     * Get the vector of attribute-value pairs for this object
     * 
     */ 

    public StreamAttValVecI getStreamAttValVec(); 

    /** 
     * Set the vector of attribute-value pairs for this object. 
     * 
     * @param savv the new vector
     */ 

    public void setStreamAttValVec(StreamAttValVecI savv);
    
    /**
     * Get the classification vector associated with the <code>StreamAttValVec</code>
     */ 

    public ClassificationVecI getClassVec(); 

    public void setClassVec(ClassificationVecI classes); 
    
    /**
     * Adds an instance and its classification to this object. 
     */ 
  
    public void add(StreamAttValI savi, ClassificationI classn); 

    /** 
     * Return the number of elements in this vector
     */ 

    public int size(); 

}
