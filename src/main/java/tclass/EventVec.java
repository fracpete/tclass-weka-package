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
 * Default implementation of EventVec. 
 * 
 * For convenience, a description of the events (something
 * implementing EventDescI), is included. 
 * 
 * @author Waleed Kadous
 * @version $Id: EventVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ */

package tclass;   
import java.util.Vector;

public class EventVec implements EventVecI {
    
    private Vector events = new Vector(); 
    private EventDescI description; 
    
    /**
     * Get the number of events in this Vector
     *
     * @return number of streams
     */

    public int size(){
	return events.size(); 
    }
    
    /**
     * Set the EventDescI for this vector. 
     * 
     * @param evd The new EventDescI for this vector. 
     * 
     */
    
    public void setEventDesc(EventDescI evd){
	description = evd; 
        // System.out.println("Event Description set to: " + evd.toString()); 
    }

    /** 
     * Get the EventDescI for this vector of events. 
     *
     * @return The event description for this object. 
     */
    public EventDescI getEventDesc(){
	return description; 
    }
    

    /**
     *  Add a stream to this vector
     *
     * @param e The event to be added
     */ 
    public void add(EventI e){
	events.addElement(e); 
    }

    public void add(EventVecI ev){
	int numEvents = ev.size(); 
	for(int i=0; i < numEvents; i++){
	    add(ev.elAt(i)); 
	}
    }
    
    public void remove(EventI e){
	events.removeElement(e); 
    }

    /**
     * Ask for a particular event
     *
     * @param i the index of the stream you want. 
     * @return the event at the <em>i</em>th position of the vector. 
     */ 

    public EventI elAt(int i){
	return (EventI) events.elementAt(i); 
    }

    @Override
    public String toString(){
	int numEvs = size(); 
	String retval = ""; 
	for(int i = 0; i < numEvs; i++){
	    retval += elAt(i).toString() + "\n"; 
	}
	return retval; 
    }
    
}
