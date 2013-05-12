
package tclass;   

/**
  * Interface for a stream of events, including the classification. 
  * <p>
  * When you think about it, the classification of a set of instances
  * and the instances themselves are separate. For example, a different
  * representation of the stream still has the same class. 
  * This technique of having a representation for streams and classifications
  * separately simplifies representation conversion (which, as you can guess, 
  * we do in several places in the code, since representation conversion
  * is the basis of the learning algorithm). 
  * <p>
  * Note that the classification vector and the stream events vector should be the 
  * same size. Otherwise ... BAD!!
  * <p>
  * Should be straightforward. 
  * 
  * @author Waleed Kadous
  * @version $Id: ClassStreamEventsVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClassStreamEventsVecI {

    /**
     * Returns true if the class has a ClassificationVecI associated with 
     * it. 
     */

    public boolean hasClassification();     


    public StreamEventsVecI getStreamEventsVec(); 
    
    public void setStreamEventsVec(StreamEventsVecI savv);

    public ClassificationVecI getClassVec(); 

    public void setClassVec(ClassificationVecI classes); 
    
    /**
     * Adds an instance and its classification to this object. 
     */ 
  
    public void add(StreamEventsI savi, ClassificationI classn); 

    public int size(); 
    
}
