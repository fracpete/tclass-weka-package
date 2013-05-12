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
  * A vector of attribute descriptions. Has an additional feature over
  * other vector classes that it can search for a particular attribute 
  * description by name.<p>
  * Such a vector might be produced, for instance as the result of the
  * construction of synthetic attributes, or by the global constructor. 
  * 
  * 
  * @author Waleed Kadous
  * @version $Id: AttDescVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface AttDescVecI {
    
    public int size(); 
    
    public void add(AttDescI ad); 

    public AttDescI elAt(int i); 
    
    public AttDescI elCalled(String name); 
    
}
