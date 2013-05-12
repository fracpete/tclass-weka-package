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
  * A representation of all the attribute values associated with a particular 
  * stream. The format of the stream is given by an AttDescVecI object. 
  * 
  * @author Waleed Kadous
  * @version $Id: StreamAttValI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   


public interface StreamAttValI  {
    
    /**
     *  Set the value of the <i>i</i>th attribute to a particular 
     *  value. 
     *  
     *  @param att The attribute to change. More info on the attributes can 
     *              be found from the AttDescVecI object. 
     */ 

    public void setAtt(int att, float value); 

    /** 
     * Get the value of the <i>i</i>th attribute 
     *
     * @param att Attribute you want the value for.
     * @return the value of the <i>i</i>th attribute. 
     */

    public float getAtt(int att); 

   
}
