/**
  * Yet another vector class. Pretty much standard. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: StreamAttValVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface StreamAttValVecI {
    
    /** Get the number of Streams described in this set. 
     *
     * @return number of streams. 
     */ 

    public int size(); 
    
    /** Add a stream. 
     */ 

    public void add(StreamAttValI savi); 

    /**
     *  Get the description of this vector 
     * 
     */ 

    public AttDescVecI getDescription();  
    
    /**
     * Get a single single stream. 
     */ 

    public StreamAttValI elAt(int i); 
    
    /** 
     * Returns a copy of the current object, except only with certain
     * features as specified by AttDescVecI
     *
     */ 
    
    public StreamAttValVecI extractFeatures(AttDescVecI selectedFeatures); 

}
