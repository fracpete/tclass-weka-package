/**
  * Yet another vector class. Pretty much standard. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: StreamAttValVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.*; 
import tclass.util.*; 

public class StreamAttValVec implements  StreamAttValVecI, Cloneable {

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

    public void add(StreamAttValI savi){
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

    public StreamAttValI elAt(int i){
	return (StreamAttValI) data.elementAt(i); 
    }
    
    /** 
     * Returns a copy of the current object, except only with certain
     * features as specified by AttDescVecI
     *
     */ 
    
    public StreamAttValVecI extractFeatures(AttDescVecI selectedFeatures){
	return null; 
    }

    public String toString(){
	String retval = "StreamAttVal vec has " + size() + " elements.\n"; 
	int numEls = size(); 
	for(int i=0; i < numEls; i++){
	    retval += "[" + elAt(i).toString() +"]"; 
	}
	return retval; 
    }

}
