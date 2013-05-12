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
 * Interface for Stream objects. 
 * Allows user to ask about the raw data. 
 * 
 * @author Waleed Kadous
 * @version $Id: StreamI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 *
 *
 */

package tclass; 
import java.io.Serializable;
  
public interface StreamI extends Cloneable, Serializable  {

    /**
     * Gets the comment associated with this object 
     * 
     * @return the comment of this label 
     */
    public String getComment(); 

    /**
     * Set the label of this stream. 
     *
     * @param comment the comment associated with this stream
     */
    public void setComment(String comment); 
    

    /** 
     * Gets the number of frames
     *
     * @return Number of frames in this stream. 
     */

    public int numFrames(); 

    /**
     * Gets the value for the frame f and the channel c
     *
     * @param f Frame of interest
     * @param c Channel of interest
     * @return A float representing the information 
     *          stored for this channel
     */
         
    public float valAt(int f, int c); 

    /**
     * Gets a requested channel. 
     * Note: The number of channels can be retrieved from the DomDescI object. 
     *
     * @param c Channel of interest
     * @return A channel
     *         
     */

    public ChannelI chanAt(int c); 
    
}
