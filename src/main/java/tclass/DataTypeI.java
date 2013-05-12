package tclass;   

import java.io.*; 

/**
  * A data type is something that assists in the interpretation of
  * data. Internally, in tclass, all data are represented as doubles
  * in order to simplify the code and make things faster.  For
  * example, three common data types are continuous (which provides a
  * simple mapping to doubles), discrete and cyclic (for example,
  * angles). 
  * <p>
  * A data type is defined by the three methods discussed below. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: DataTypeI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ 
  */

public interface DataTypeI extends Cloneable, Serializable  {
    
    /** 
     * Get the names
     */ 

    public String getName(); 


    /**
     *  Clones the current object
     * 
     * @return A shallow copy of the current object. 
     */


    public Object clone(); 


    /**
     * Convert between a user-input value for the class and the
     * internal float representation. Can be complicated sometimes. 
     * For example, if this is a discrete class, then a hash table
     * mapping discrete values to a float must be maintained to do
     * the read.    
     *
     * @param s User input value.
     * @return A float representing the input value. 
     *  When <code>print()</code> is called with the returned value 
     *  it should return the same string that was given. 
     */

    public float read(String s);

    /**
     * Compute the distance between two instances belonging to the
     * same data type. Should meet all the criteria for a metric. 
     * i.e. distance(a,a)=0, distance (a,b) + distance(b,c) &lt;= distance(a,c) and
     * distance(a,b) &gt;= 0. 
     */ 

    public float distance(float a, float b); 

    /** 
     * Returns true if the data type is ordered. 
     */ 

    public boolean isOrdered(); 

    /**
     * A comparison function. If isOrdered() is false, just return 0. 
     * 
     * @param a first parameter 
     * @param b second parameter
     * @return -1 if a < b, 0 if a = b, 1 if a > b
     * 
     */ 
    
    public int cmp(float a, float b); 
    
    /**
     * Convert back from our internal representation back into a
     * more user-friendly one.
     * 
     */ 

    public String print(float a); 


    /**  
     * 
     * Describes any parameters this Data type can handle. 
     * 
     * @return A vector of parameters.  
     */     
    public ParamVec getParamList();  
    
    /** 
     * Configures this instance so that parameter <i>p</i> has 
     * value <i>v</i>.  
     * 
     * @param p the parameter to set.  
     * @param v the value of the parameter.  
     * @exception InvalidParameterException Occurs when the parameter can not
     * be set to the given value, or there is no such parameter. 
     * 
     */ 
 
    public void setParam(String p, String v) throws InvalidParameterException; 
}
