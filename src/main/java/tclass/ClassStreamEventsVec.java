/**
  * Interface for a stream of events, including the classification. 
  *
  * When you think about it, the classification of a set of instances
  * and the instances themselves are separate. For example, a different
  * representation of the stream still has the same class. 
  * This technique of having a representation for streams and classifications
  * separately simplifies representation conversion (which, as you can guess, 
  * we do in several places in the code, since representation conversion
  * is the basis of the learning algorithm). 
  *
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClassStreamEventsVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.*; 


public class ClassStreamEventsVec implements ClassStreamEventsVecI {
    
    private boolean hasClassification = false; 
    private ClassificationVecI classnvec; 
    private StreamEventsVecI streams; 
    /*
    private MetafeatureDesc mfd; 

    
    public ClassStreamEventsVec(String filename, MetaFeatureDesc mfd) throws 
	FileNotFoundException, FileFormatException, IOException
    {
	this.mfd = mfd; 
	classVec = (ClassificationVecI) new ClassificationVec(dd.getClassDescVec()); 
	streamVec = (StreamVec) new StreamVec(); 
	addFromFile(filename); 
    }
    */

    /**
     * Returns true if the class has a ClassificationVecI associated with 
     * it. 
     */

    public boolean hasClassification(){
	return hasClassification; 
    }

    public StreamEventsVecI getStreamEventsVec(){
	return streams; 
    }
    
    public void setStreamEventsVec(StreamEventsVecI sevv){
	streams = sevv; 
    }

    public ClassificationVecI getClassVec(){
	return classnvec;
    }

    public void setClassVec(ClassificationVecI classes){
	hasClassification = true; 
	classnvec  = classes; 
    }
    
    /**
     * Adds an instance and its classification to this object. 
     */ 
  
    public void add(StreamEventsI sei, ClassificationI classn){
	classnvec.add(classn); 
	streams.add(sei); 
    }

    public int size(){
	return streams.size(); 

    }
    public String toString(){
	int numEls = size(); 
	String retval = "ClassStreamEvents has " + numEls + " elements\n"; 
	for(int i=0; i < numEls; i++){
	    retval += "Str: " + streams.elAt(i).toString() + 
		"Class: " + classnvec.elAt(i).toString(); 
	}
	return retval; 
    }
    
    
}
