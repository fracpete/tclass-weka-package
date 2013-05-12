/**
  * An implementation of an Example data type. Very simple. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Continuous.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.datatype;   
import tclass.*; 
import tclass.util.*; 

public class Continuous implements DataTypeI {
    
    private static final int LINEAR = 1; 
    private static final int SQUARE = 2; 
    
    int distance = SQUARE; 
    
    /** 
     * Get the name of this datatype. 
     */ 

    public String getName(){
	return "continuous";
    }

    /**
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
    public float read(String s){
	return Float.valueOf(s).floatValue();  
    }

    /**
     *
     */ 

    public float distance(float a, float b){
	if(distance == LINEAR){
	    return Math.abs(a-b); 
	}
	else if(distance == SQUARE){
	    return (a - b)*(a-b); 
	}
	else {
	    Debug.dp(0, "ACK! In continuous datatype unknown metric used!!");
	    return -1; 
	}
    }

    public boolean isOrdered(){
	return true; 
    }
    
    public int cmp(float a, float b){
	if(a < b) return -1; 
	if(a == b) return 0; 
	if(a > b) return 1; 
	return 0; 
    }
    
    /**
     * Convert back from our internal representation back into a
     * more user-friendly one. 
     */ 
    
    public String print(float a){
	return String.valueOf(a);
    }

    /**  
     * 
     * Describes any parameters this Data type can handle. 
     * 
     * @return A vector of parameters.  
     */     
    public ParamVec getParamList(){
	ParamVec p =  new ParamVec(); 
	p.add(new Param("distance", 
			"Distance metric to apply. Valid values are: " 
			+ "linear, square", "square"));
	return p; 
    }
    
    /** 
     * Configures this instance so that parameter <i>p</i> has 
     * value <i>v</i>.  
     * 
     * @param p the parameter to set.  
     * @param v the value of the parameter.  
     * @return true if the operation succeeded.  
     * 
     */ 

    public String toString(){
	String retval = "cts: metric = "; 
	if(distance == SQUARE)
	    retval += "square";
	else 
	    retval += "linear"; 
	return retval; 
    }
 
    public void setParam(String p, String v) throws InvalidParameterException {
	// We don't handle any parameters, so return false
	if(p.equals("distance")){
	    if(v.equals("square")){
		distance = SQUARE; 
	    }
	    else if(v.equals("linear")){
		distance = LINEAR; 
	    }
	    else
		throw new InvalidParameterException(p, v, "Acceptable values for distance: linear, square"); 
		
	}
	else {
	    throw new InvalidParameterException(p, v, "No such parameter"); 
	}
      
    }
    
    
}
