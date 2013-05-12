/**
  * Interface for sets of streams. Note that there is no classification
  * information stored with these records. If you want classifications
  * associated with the stream, use a ClassStreamVecI. 
  * 
  * @author Waleed Kadous
  * @version $Id: StreamVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ 
  */

package tclass;   

import java.io.*; 

public interface StreamVecI extends Serializable {
    
    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */
    public abstract int size(); 

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */ 
    public abstract void add(StreamI s); 

    /**
     * Ask for a particular stream
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 
    public StreamI elAt(int i);

}
