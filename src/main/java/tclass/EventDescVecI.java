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
  * A vector of event descriptions. Has an additional feature over
  * other vector classes that it can search for a particular event
  * description by name. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventDescVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface EventDescVecI {
    
    public int size(); 
    
    public void add(String name, EventDescI ad); 

    public EventDescI elAt(int i); 
    
    public EventDescI elCalled(String name); 
    
    public String elName(int i); 

    public int elIndex(String name); 
    
}
