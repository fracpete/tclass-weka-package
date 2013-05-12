/**
  * Yet another vector class. Pretty much standard. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: StreamLabelEventVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.*; 
import tclass.util.*; 

public class StreamLabelEventVec implements Cloneable {

    private Vector data = new Vector(); 
    private AttDescVecI adv; 
    
    
    /** Get the number of Streams described in this set. 
     *
     * @return number of streams. 
     */ 

    public int size(){
	return data.size(); 
    }

        /**
     * Clone the current object. 
     *
     */ 

    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }

    /** Add a stream. 
     */ 

    public void add(StreamLabelEvent savi){
	data.addElement(savi); 
    }

        /**
     * Clone the current object. 
     *
     */ 

    /**
     *  Get the description of this vector 
     * 
     */ 

    public AttDescVecI getDescription(){
	return adv; 
    }
    
    public void setDescription(AttDescVecI adv){
	this.adv = adv; 
    }
    
    /**
     * Get a single single stream. 
     */ 

    public StreamLabelEvent elAt(int i){
	return (StreamLabelEvent) data.elementAt(i); 
    }
    
    public String toString(){
        int numEls = data.size(); 
        
	String retval = "StreamLabelEventVec vec has " + numEls + " elements.\n"; 
	for(int i=0; i < numEls; i++){
	    retval += "[" + data.elementAt(i).toString() +"]"; 
	}
	return retval; 
    }

}
