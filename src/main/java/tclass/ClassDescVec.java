/**
  * Encodes all the features of classes available. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClassDescVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import tclass.util.*; 

public class ClassDescVec implements ClassDescVecI {
    private StringMap classmap; 

    /** 
     * Default constructor
     */ 

    public ClassDescVec(){
	classmap = new StringMap(); 
    }

    /** Add a new class label to this class. 
     *
     */ 

    public int add(String classlabel){
	return classmap.add(classlabel); 
    }

    public String toString(){
	String retval = "[ "; 
	for(int i=0; i < classmap.size(); i++){
	    retval += classmap.getString(i) + " " ; 
	}
	retval += "]"; 
	return retval; 
    }
    
    /**
     * Converts from a ClassLabel to an integer using this ClassDescVec. 
     *
     * @param classlabel The String you want to convert to an int. 
     * @return The corresponding int. -1 if there is no such string in this
     * mapping.
     */

    public int getId(String classlabel){
	return classmap.getInt(classlabel); 
    }
    
    /**
     * Get the string corresponding to a particular class. 
     * 
     */ 
    
    public String getClassLabel(int classid){
	return classmap.getString(classid); 
    }

    /**
     * Get the number of strings stored in this mapping. 
     */ 
    
    public int size(){
	return classmap.size(); 
    }
    
}
