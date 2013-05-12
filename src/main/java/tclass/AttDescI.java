
package tclass;   

/**
  * Stores the description of a single attribute. 
  * Each attribute consists of a name of the attribute and its
  * datatype.<p> 
  * Pretty simple, straightforward object.
  *
  * @author Waleed Kadous
  * @version $Id: AttDescI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface AttDescI {

    /**
     * Gets the name of this attribute. 
     * 
     * @return Name of the attribute
     */ 

    public String getName(); 
    
    /**
     * Sets the name of this attribute
     *
     * @param name The new name of this attribute. 
     */ 
    
    public void setName(String name);
    

    /**
     * Tells us if two attribute descriptions are the same ...
     * i.e. can they be used interchangeably? 
     * 
     * @param att The attribute that you want to compare with this one. 
     * @return True if the attributes are interchangeable (i.e. can be used
     *          in a functionally similar way). 
     */ 

    public boolean equals(AttDescI att); 
    

    /**
     * Get the datatype of this attribute. 
     *
     * @return The current datatype
     */
    
    public DataTypeI getDataType(); 
    
    /**
     * Set the data type for this attribute description
     *
     * @param data the new data type. 
     */
    
    public void setDataType(DataTypeI data); 
    
}
