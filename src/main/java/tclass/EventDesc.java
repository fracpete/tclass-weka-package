/**
  * Describes a particular type of event. This is the default
  * implementation.  Not surprising, it also includes methods for
  * setting up the vector, as well as the reading stuff you'd expect
  * for EventDesc.
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventDesc.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ */

package tclass;   

import java.util.*; 
import tclass.util.*; 

public class EventDesc implements EventDescI {
    
    private Vector params = new Vector(); 
    private StringMap names = new StringMap(); 

    public EventDesc(){
	// Can't see anything to put in the constructor, myself.
    }

    // This is the writing interface. 

    /**
     * Adds a parameter to the list
     *
     * @param name The name of the parameter. 
     * @param dt   The DataTypeI of the parameter. 
     */ 
    
    public void addParam(String name, DataTypeI dt){
	//Add to the StringMap
	names.add(name); 
	params.addElement(dt); 
    }
    

    /** 
     * Get the number of parameters for this particular type of 
     * event. 
     *
     * @return The number of parameters. 
     */ 

    public int numParams(){
	return names.size(); 
	// Could equally have done params.size(), no diff. 
    }

    /**
     * Returns the name of a particular parameter by index. 
     * Useful for creating labels, plots etc. 
     * 
     * @param index The parameter index. Starts at 0, up to numParams()-1. 
     * @return The name of that parameter. Returns null if 
     * index is unreasonable (i.e. &lt; 0 or &gt;= numParams()). 
     * 
     */ 

    public String paramName(int index){
	return names.getString(index); 
    }

    
    public String toString(){
       int numParams = numParams(); 
       String retval = "EventDesc\n"; 
       for(int i=0; i < numParams; i++){
	   retval += "Name: " + names.getString(i) + " " + params.elementAt(i) + "\n"; 
       }
       return retval;

    }
    
    /** 
     * Does the opposite of the above. Returns the index of the particular
     * parameter from its name. 
     *
     * @param name The name of the parameter. 
     * @return The index of the parameter. -1 if there is no such parameter. 
     * 
     */
    
    public int paramNum(String name){
	return names.getInt(name); 
    }
    
    /** 
     * Get the data type of the ith parameter.  
     * 
     */ 

    public DataTypeI getDataType(int i){
	return (DataTypeI) params.elementAt(i); 
    }

}
