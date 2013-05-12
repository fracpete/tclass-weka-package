/**
  * This file is now slightly misnamed ... it is now
  * is an algorithm for gain-based clustering. 
  * However it is more accurately described as
  * a directed segmentation approach. 
  * 
  * @author Waleed Kadous
  * @version $Id: GClust.java,v 1.2 2002/08/02 05:02:21 waleed Exp $
  * $Log: GClust.java,v $
  * Revision 1.2  2002/08/02 05:02:21  waleed
  * Added better handling of discrete parameters of metafeatures.
  * Standard deviation is no longer applied.
  *
  */

package tclass.clusteralg; 

import tclass.*; 
public class GClust implements ClusterI {
    GClustVec parent; 
    EventI origEvent; 
    String myName; 
    int myNum; 

    public GClust(GClustVec parent, EventI origEvent, int myNum){
        this.parent = parent; 
        this.origEvent = origEvent; 
        this.myNum = myNum; 
    }

    /** 
     * Describes this cluster. 
     * For example, if this was the results of k-means clustering, 
     * it might print the centroid and the standard deviations. 
     */ 
    
    public String getName(){
        return myName; 
    }

    public void setName(String name){
        // System.out.println("Name set to: " + name); 
       myName = name; 
    }

    EventI cent(){
        return origEvent; 
    }
    

    public EventDescI eventDesc(){
        return parent.eventDesc; 
    }

    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }

    
    /** 
     * Produces a description of this cluster. 
     */ 
    

    public String getDescription(){
        String retval = "Cluster centroid is: " + origEvent.toString() +" SDs are [ "; 
        for(int i=0; i < parent.numParams; i++){
            retval += parent.clustSDs[myNum][i] + " "; 
        }
        retval +=  "] Global SDs are [ "; 
        for(int i=0; i < parent.numParams; i++){
            retval += parent.sds[i] + " "; 
        }
        retval +=  "]\n";

        return retval; 
    }


    public float findMatch(StreamI stream, StreamEventsI events){
        // This uses the parent's functions. 
        // Uses the log of the ratio between the distance to this instance
        // and the next nearest instance. 
        return parent.findMatch(myNum, events); 
    }
    

    public EventI[] getBounds(float minConf){
        ClusterMem currClus; 
        EventI[] origEvents = parent.origEvents; 
        int numParams = parent.numParams; 
        float[] mins = new float[numParams]; 
        float[] maxs = new float[numParams]; 
        for(int i=0; i < numParams; i++){
            mins[i] = Float.MAX_VALUE; 
            maxs[i] = -Float.MAX_VALUE; 
        }
        for(int i=0; i < origEvents.length; i++){
            currClus = parent.findBestLabel(origEvents[i]);
            if(currClus.cluster == this){
                for(int j=0; j < numParams; j++){
                    if(origEvents[i].valOf(j) < mins[j]){
                        mins[j] = origEvents[i].valOf(j); 
                    }
                    if(origEvents[i].valOf(j) > maxs[j]){
                        maxs[j] = origEvents[i].valOf(j);
                    }
                }
            }
        }
        EventI[] retval = new EventI[3]; 
        retval[0] = new Event(mins); 
        retval[1] = new Event(maxs);
        retval[2] =  origEvent; 
        return retval; 
    }
       
}

