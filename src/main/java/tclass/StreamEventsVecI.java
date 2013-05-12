/**
  * Interface for sets of streams.
  * 
  * @author Waleed Kadous
  * @version $Id: StreamEventsVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface StreamEventsVecI {

    /** 
     * Get the current Event Description Vector that describes the
     * format of each of the Stream Events that are part of this
     * vector.
     */

    public EventDescVecI getEventDescVec(); 

     /** 
     * Set the current Event Description Vector that describes the
     * format of each of the Stream Events that are part of this
     * vector.
     */

    public void setEventDescVec(EventDescVecI newEventDesc); 


    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */
    public abstract int size(); 

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */ 
    public abstract void add(StreamEventsI s); 

    /**
     * Ask for a particular stream
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 
    public StreamEventsI elAt(int i);

}
