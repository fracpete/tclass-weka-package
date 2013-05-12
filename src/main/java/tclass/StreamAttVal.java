/**
  * A representation of all the attribute values associated with a particular 
  * stream. The format of the stream is given by an AttDescVecI object. 
  * 
  * This is little more than an array wrapper. 
  * @author Waleed Kadous
  * @version $Id: StreamAttVal.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.io.*; 

public class StreamAttVal implements StreamAttValI  {
    float[] data; 
    
    public StreamAttVal(int size){
	data = new float[size]; 
    }

    /**
     *  Set the value of the <i>i</i>th attribute to a particular 
     *  value. 
     *  
     *  @param att The attribute to change. More info on the attributes can 
     *              be found from the AttDescVecI object. 
     */ 

    public void setAtt(int att, float value){
	data[att] = value; 
    }

    /** 
     * Get the value of the <i>i</i>th attribute 
     *
     * @param att Attribute you want the value for.
     * @return the value of the <i>i</i>th attribute. 
     */

    public float getAtt(int att){
	return data[att]; 
    }
    
    public String toString(){
	String retval = "[ "; 
	for(int i=0; i < data.length; i++){
	    retval += data[i] + " "; 
	}
	retval += "]\n"; 
	return retval; 
    }
   
}
