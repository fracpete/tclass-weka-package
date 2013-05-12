/**
  * A representation of all the attribute values associated with a particular 
  * stream. The format of the stream is given by an AttDescVecI object. 
  * 
  * This is little more than an array wrapper. 
  * @author Waleed Kadous
  * @version $Id: StreamLabelEvent.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.io.*; 
import java.util.*; 

public class StreamLabelEvent {
    Vector labelEvents = new Vector(); 

    /**
     *  Set the value of the <i>i</i>th attribute to a particular 
     *  value. 
     *  
     *  @param att The attribute to change. More info on the attributes can 
     *              be found from the AttDescVecI object. 
     */ 

    public void add(ClusterMem cm){
	labelEvents.addElement(cm); 
    }

    /** 
     * Get the value of the <i>i</i>th attribute 
     *
     * @param att Attribute you want the value for.
     * @return the value of the <i>i</i>th attribute. 
     */

    public ClusterMem getLabelEvent(int index){
	return (ClusterMem) labelEvents.elementAt(index); 
    }

    public int size(){
        return labelEvents.size(); 
    }
    
    public String toString(){
        
        String retval = "[ "; 
        for(int i=0; i < labelEvents.size(); i++){
            retval += labelEvents.elementAt(i)  + " "; 
        }
        retval += "]\n"; 
        return retval; 
    }
   
}
