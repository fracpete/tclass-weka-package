/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


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
