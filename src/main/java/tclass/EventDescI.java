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
