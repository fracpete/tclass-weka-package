
package tclass;   
import java.io.*; 


/**
  * A store of all the classes relevant for this domain. Includes methods
  * for adding classes, converting between integer and String representations
  * and finding the total number of classes. 
  * 
  * @author Waleed Kadous
  * @version $Id: ClassDescVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClassDescVecI extends Cloneable, Serializable {

    /** Add a new class label to this class. 
     *
     */ 

    public int add(String label); 
    
    /**
     * Converts from a ClassLabel to an integer using this ClassDescVec. 
     *
     * @param classlabel The String you want to convert to an int. 
     * @return The corresponding int. -1 if there is no such string in this
     * mapping.
     */

    public int getId(String classlabel); 
    
    /**
     * Get the string corresponding to a particular class. 
     * 
     */ 
    
    public String getClassLabel(int classid); 

    /**
     * Get the number of strings stored in this mapping. 
     */ 
    
    public int size();   
    
}
