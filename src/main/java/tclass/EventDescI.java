/**
  * Describes a particular type of event. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventDescI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface EventDescI {
    
    /** 
     * Get the number of parameters for this particular type of 
     * event. 
     *
     * @return The number of parameters. 
     */ 

    public int numParams(); 

    /**
     * Returns the name of a particular parameter by index. 
     * Useful for creating labels, plots etc. 
     * 
     * @param index The parameter index. Starts at 0, up to numParams()-1. 
     * @return The name of that parameter. Returns null if 
     * index is unreasonable (i.e. &lt; 0 or &gt;= numParams()). 
     * 
     */ 

    public String paramName(int index); 

    /** 
     * Does the opposite of the above. Returns the index of the particular
     * parameter from its name. 
     *
     * @param name The name of the parameter. 
     * @return The index of the parameter. -1 if there is no such parameter. 
     * 
     */
    
    public int paramNum(String name);
    
    /** 
     * Get the data type of the ith parameter.  
     * 
     */ 

    public DataTypeI getDataType(int i); 

}
