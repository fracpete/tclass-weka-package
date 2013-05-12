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
import java.io.*; 
import tclass.util.*; 
  
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
