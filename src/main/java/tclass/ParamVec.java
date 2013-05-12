/**
  * A collection of parameters. Simple enough not to require an interface. 
  * 
  * @author Waleed Kadous
  * @version $Id: ParamVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ 
  */

package tclass;  
import java.util.*;  

public class ParamVec {

    Vector params = new Vector(); 

    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */
    public int size() {
	return params.size(); 
    }

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */ 
    public void add(Param p) {
	params.addElement(p);
    } 

    /**
     * Ask for a particular stream
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 

    public Param elAt(int i){
	return (Param) params.elementAt(i); 
    }

}
