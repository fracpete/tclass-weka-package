/**
  * Interface for sets of streams.
  *
  * @author Waleed Kadous
  * @version $Id: StreamEventsVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  * $Log$
  */

package tclass;
import java.util.*;
import java.io.*;

public class StreamEventsVec implements StreamEventsVecI {
    private EventDescVecI edv;
    private Vector data = new Vector();

    /*
    public StreamEventsVec(String filename, MetafeatureDesc mfd){
    	
    }
    */
    /**
     * Get the current Event Description Vector that describes the
     * format of each of the Stream Events that are part of this
     * vector.
     */

    public EventDescVecI getEventDescVec(){
	return edv;
    }

     /**
     * Set the current Event Description Vector that describes the
     * format of each of the Stream Events that are part of this
     * vector.
     */

    public void setEventDescVec(EventDescVecI newEventDesc){
	edv = newEventDesc;
    }


    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */
    public int size(){
	return data.size();
    }

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */
    public void add(StreamEventsI s){
	data.addElement(s);
    }

    /**
     * Ask for a particular stream
     *
     * @param i the index of the stream you want.
     * @return the stream at the <em>i</em>th position of the vector.
     */
    public StreamEventsI elAt(int i){
	return (StreamEventsI) data.elementAt(i);
    }


}
