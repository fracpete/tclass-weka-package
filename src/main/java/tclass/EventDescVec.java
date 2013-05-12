/**
  * A vector of event descriptions. Has an additional feature over
  * other vector classes that it can search for a particular event
  * description by name. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventDescVec.java,v 1.2 2002/08/02 05:01:43 waleed Exp $
  * $Log: EventDescVec.java,v $
  * Revision 1.2  2002/08/02 05:01:43  waleed
  * Added log header
  *
  */

package tclass;   

import java.io.*; 
import java.util.*; 
import tclass.util.*; 

public class EventDescVec implements EventDescVecI {

    private Vector eds = new Vector(); 
    private StringMap edNames = new StringMap(); 
    
    public int size(){
	return edNames.size(); 
    }
    
    public void add(String name, EventDescI ed){
	eds.addElement(ed); 
	edNames.add(name); 
    }

    public EventDescI elAt(int i){
	return (EventDescI) eds.elementAt(i); 

    }
    
    public EventDescI elCalled(String name){
	return (EventDescI) eds.elementAt(edNames.getInt(name)); 
    }
    
    public String elName(int i){
	return edNames.getString(i); 
    }

    public int elIndex(String name){
	return edNames.getInt(name); 
    }

    public String toString(){
	String retval = "EventDescVec has " + size() + " elements. \n"; 
	
	int numEls = size(); 
	for(int i=0; i < numEls; i++){
	    retval += "[" + elAt(i).toString() + "]\n"; 
	}
	return retval; 
    }


}
