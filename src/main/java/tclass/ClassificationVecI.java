
package tclass;   
import java.io.*;

/**
 * A vector of classifications. Usually designed to be coindexed with a StreamVecI object. 
 * 
 * @author Waleed Kadous
 * @version $Id: ClassificationVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClassificationVecI extends Cloneable, Serializable  {
    
    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */
    public int size(); 

    public Object clone(); 

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */ 

    public void add(ClassificationI s); 

    /**
     * Ask for a particular classification
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 
    public ClassificationI elAt(int i);

    /**
     * Allows you to change the classification of an instance. 
     */ 

    public void setClassification(int i, ClassificationI classn); 

    /**
     * Each classification vector refers to a ClassDescVecI that
     * describes its format.
     */
    
    public ClassDescVecI getClassDescVec(); 
    
    /**
     * Add a new classification description vector. WARNING: Do this
     * operation with extreme care. It tends to break things!
     */ 

    public void setClassDescVec(ClassDescVecI cdv); 

    /**
     * Converts a multiclass problem to a two-class learning problem. 
     * A function that: 
     *
     * <ul>
     * <li> Changes the ClassDescVec so that it only contains two classes, 
     * true or false. 
     * <li> Changes the real class of the vector so that:
     *      if the classid is equal to the the true class, the new 
     *      classification is true. 
     *      otherwise the classification is false. 
     * </ul>
     *
     * @param trueclass The class that is to become the "true" class. 
     *                   All other classes are set to "false". 
     */ 

    public void binarify(int trueclass);

}
