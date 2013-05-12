/**
  * Interface for a set of attribute value pairs, including the
  * classification.
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
  * @version $Id: ClassStreamLabelEventVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ 
  */

package tclass;   

public class ClassStreamLabelEventVec {
    /**
     * Returns true if the class has a ClassificationVecI associated with 
     * it. 
     */

    private boolean hasClassification = false; 
    private ClassificationVecI classnvec; 
    private StreamLabelEventVec streams; 
    
    public ClassStreamLabelEventVec(){
    }
    
    public boolean hasClassification(){
	return hasClassification; 
    }

    public StreamLabelEventVec getStreamLabelEventVec(){
	return streams; 
    }
    
    public void setStreamLabelEventVec(StreamLabelEventVec savv){
	streams = savv; 
    }

    public ClassificationVecI getClassVec(){
	return classnvec; 
    }

    public void setClassVec(ClassificationVecI classes){
	hasClassification = true; 
	classnvec = classes; 
    }
    
    /**
     * Adds an instance and its classification to this object. 
     */ 
  
    public void add(StreamLabelEvent savi, ClassificationI classn){
	classnvec.add(classn); 
	streams.add(savi); 
    }

    public int size(){
	return streams.size(); 
    }
    
    public String toString(){
	int numEls = size(); 
	String retval = "ClassStream has " + numEls + " elements\n"; 
	for(int i=0; i < numEls; i++){
	    retval += "Str: \n" + streams.elAt(i).toString() + 
		"Class: " + classnvec.elAt(i).toString(); 
	}
	return retval; 
    }

}
