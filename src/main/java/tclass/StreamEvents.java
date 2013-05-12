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
  * A store of all the events associated with a stream. For each channel, 
  * an EventVecI can be requested. Note!! Stream events are defined by 
  * an EventDescVecI, which contains all the information about the various 
  * types of events. At this stage, the channel-based representation has 
  * disappeared to be replaced by this event-based representation. This 
  * is useful for future expansion, since it is likely that at some
  * point, we are going to want to switch to multi-channel events. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: StreamEvents.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public class StreamEvents implements StreamEventsI {

    private String comment; 
    private EventVecI eventVecs[]; 

    public StreamEvents(int size){
	eventVecs = new EventVecI[size]; 
    }

    /**
     * Gets the comment associated with this object 
     * 
     * @return the comment of this label 
     */
    public String getComment(){
	return comment; 
    }

    /**
     * Set the label of this stream. 
     *
     * @param comment the comment associated with this stream
     */
    public void setComment(String comment){
	this.comment = comment; 
    }
    
    /** 
     *  Returns true if the real class of this stream is known. 
     *  
     *  @return <code>true</code> if the class of this stream is known. 
     *
     */
 
    public EventVecI getEvents(int index){
        return eventVecs[index]; 
    }


    public void setEvents(int index, EventVecI ev){
	eventVecs[index] = ev; 
	
    }

    public String prettyPrint(EventDescVecI edvi){
	int size = eventVecs.length; 
	String retval = ""; 
	for(int i = 0; i < size; i++){
	    retval += "-- Events of type " + edvi.elName(i) +" -- \n"; 
	    retval += eventVecs[i].toString(); 
	    	retval += "\n"; 
	}
	return retval; 
    }
    
    @Override
    public String toString(){
	int size = eventVecs.length; 
	String retval = "StreamEvents has " + size + " elements. They are: \n";
	for(int i=0; i < size; i++){
	    retval += "[" + eventVecs[i].toString() + "]\n"; 
	}
	return retval; 
	
    }
    
}
