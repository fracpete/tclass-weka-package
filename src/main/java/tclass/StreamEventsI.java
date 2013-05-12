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
  * @version $Id: StreamEventsI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface StreamEventsI {
   
    /**
     * Gets the comment associated with this object 
     * 
     * @return the comment of this label 
     */
    public abstract String getComment(); 

    /**
     * Set the label of this stream. 
     *
     * @param comment the comment associated with this stream
     */
    public abstract void setComment(String comment); 
    
    /** 
     *  Returns true if the real class of this stream is known. 
     *  
     *  @return <code>true</code> if the class of this stream is known. 
     *
     */
 
    public EventVecI getEvents(int index); 

    public String prettyPrint(EventDescVecI edvi); 

    /**
     * Get the classification for this stream 
     */ 

    /**
     * Set the classification of this stream 
     */ 


}
