package tclass;   
import java.io.*; 

/**
  * Represents a vector of clusters. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClusterVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClusterVecI extends Serializable, Cloneable { 

    /** Get the number of clusters described in this set. 
     *
     * @return number of clusters. 
     */ 

    
    public int size(); 
    
    /** 
     * Creates a copy of this object
     *
     * @return A shallow copy of the current object. 
     */ 

    public Object clone(); 

    /**
     * Get a cluster by index.
     *
     * @param i Tndex of the cluster you want.  <code>0 &lt;= i &lt; this.size()</code>
     * @return The <i>i</i>th element if i is valid, and null otherwise. 
     *
     */ 

    public ClusterI elAt(int i); 
    
    /**
     * Call findMatch on each cluster to find the whole best thingie 
     *
     */ 
    /*    public StreamAttValI matchAll(StreamI si, StreamEventsI sei); */


    public ClusterMem findBestLabel(EventI event); 

}
