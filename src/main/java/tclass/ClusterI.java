
package tclass;   
import java.io.*; 

/**
  * A cluster; which in this particular case acts as a weird sort of 
  * prototype. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClusterI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */


public interface ClusterI extends Serializable, Cloneable {
    

    /** 
     * Describes this cluster. 
     * For example, if this was the results of k-means clustering, 
     * it might print the centroid and the standard deviations. 
     */ 

    public String getName(); 
    
    public void setName(String name); 
    
    public Object clone(); 

    /** 
     * Produces a description of this cluster. 
     */ 
    

    public String getDescription(); 


    /** 
     * Try to determine if a stream has an event that belongs to this
     * cluster or not. This function will return 0 if it is 100 per
     * cent confident that there is nothing in the stream that could
     * belong to this cluster, and 1 if it is 100 per cent confident
     * there is something in the stream belonging to this cluster. 
     *
     * Right now, attribution is done by the cluster. This may change
     * in future, but it's how it is right now. Note that a particular
     * clusterer may not use both sources of information -- it may
     * only use the events themselves (that have already been
     * processed), or it may alternatively use the raw data representation. 
     *
     */
    
    public float findMatch(StreamI stream, StreamEventsI events); 
    
}
