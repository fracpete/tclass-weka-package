/**
  * Stores all the information about a set of attributes. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: AttDesc.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public class AttDesc implements AttDescI {

    private String name; 
    private DataTypeI dt; 

    public AttDesc(String name, DataTypeI dt){
	this.name = name; 
	this.dt = dt;
    }

    /**
     * Gets the name of this attribute. 
     * 
     * @return Name of the attribute
     */ 

    public String getName(){
	return name; 
    }
    
    /**
     * Sets the name of this attribute
     *
     * @param name The new name of this attribute. 
     */ 
    
    public void setName(String name){
	this.name = name; 
    }
    

    /**
     * Tells us if two attribute descriptions are the same ...
     * i.e. can they be used interchangeably? 
     * 
     * @param att The attribute that you want to compare with this one. 
     * @return True if the attributes are interchangeable (i.e. can be used
     *          in a functionally similar way). 
     */ 

    public boolean equals(AttDescI att){
	return att.getDataType().getName().equals(dt.getName()); 
    }
    

    /**
     * Get the datatype of this attribute. 
     *
     * @return The current datatype
     */
    
    public DataTypeI getDataType(){
	return dt; 
    }
    
    /**
     * Set the data type for this attribute description
     *
     * @param data the new data type. 
     */
    
    public void setDataType(DataTypeI dt){
	this.dt = dt; 
    }
    
    public String toString(){
	return "AttDesc " + name + " " + dt.toString() + "\n"; 
    }
    
}
