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
  * A vector of events. Also encodes the appropriate EventDescI for this
  * class. 
  * 
  * @author Waleed Kadous
  * @version $Id: EventVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface EventVecI {
    
    /**
     * Get the number of events in this Vector
     *
     * @return number of events
     */
    public abstract int size(); 

    /**
     *  Add an event to this vector
     *
     * @param s The stream to be added
     */ 
    public abstract void add(EventI s); 


    /**
     * Set the EventDescI for this vector. 
     * 
     * @param evd The new EventDescI for this vector. 
     * 
     */
    
    public void setEventDesc(EventDescI evd); 

    /** 
     * Get the EventDescI for this vector of events. 
     *
     * @return The event description for this object. 
     */
    
    public EventDescI getEventDesc(); 
    
    /**
     * Ask for a particular stream
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 
    public EventI elAt(int i);

}
