/**
 * Default implementation of StreamVecI
 * 
 * @author Waleed Kadous
 * @version $Id: StreamVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import java.util.*;

public class StreamVec implements StreamVecI {
    
    private Vector streams = new Vector(); 
    
    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */

    public int size(){
	return streams.size(); 
    }

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */ 
    public void add(StreamI s){
	streams.addElement(s); 
    }

    /**
     * Ask for a particular stream
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 

    public StreamI elAt(int i){
	return (StreamI) streams.elementAt(i); 
    }

}
