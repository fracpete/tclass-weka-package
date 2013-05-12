/**
  * A vector of attribute descriptions. Has an additional feature over
  * other vector classes that it can search for a particular attribute 
  * description by name. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: AttDescVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.*; 
import tclass.util.*; 

public class AttDescVec implements AttDescVecI, Cloneable {

    private Vector atts = new Vector(); 
    private StringMap attNames = new StringMap(); 

    public int size(){
	return attNames.size(); 
    }
    
    public void add(AttDescI ad){
	atts.addElement(ad); 
	attNames.add(ad.getName()); 
    }

    public void add(AttDescVecI adv){
	for(int i=0; i < adv.size(); i++){
	    add(adv.elAt(i)); 
	}
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

    public AttDescI elAt(int i){
	return (AttDescI) atts.elementAt(i); 
    }


    
    public AttDescI elCalled(String name){
	return (AttDescI) atts.elementAt(attNames.getInt(name)); 
    }
    
    public String toString(){
	String retval = "AttDescVec has " + size() + " elements. \n"; 
	
	int numEls = size(); 
	for(int i=0; i < numEls; i++){
	    retval += "[" + elAt(i).toString() + "]\n"; 
	}
	return retval; 
    }

}
