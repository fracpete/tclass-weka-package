/**
  * Everything related to the classification of a particular stream or
  * anything else. Includes information about the classification
  * real or predicted. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Classification.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public class Classification implements ClassificationI {

    int realClass = ClassificationI.UNCLASSIFIED; 
    int predictedClass = ClassificationI.UNCLASSIFIED; 
    float predictedClassConfidence = 0; 

    /**
     * Clone the current object. 
     *
     */ 

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

    public Classification(int realClass, int predictedClass){
	this.realClass = realClass; 
	this.predictedClass = predictedClass; 
    }
    
    public Classification(int realClass){
	this.realClass = realClass; 
    }
    
    /** 
     *  Returns true if the real class is known. 
     *  
     *  @return <code>true</code> if the class of this stream is known. 
     *
     */

    public boolean isRealClassDefined(){ 
	return realClass == ClassificationI.UNCLASSIFIED; 
    }

     /** 
     *  Returns true if the predicted class is known. 
     *  
     *  @return <code>true</code> if the class of this stream is known. 
     *
     */

    public  boolean isPredictedClassDefined(){
	return predictedClass == ClassificationI.UNCLASSIFIED; 
    }

    /** 
     * Gets the real class
     *
     * @return An int identifying this class which can be interpreted
     *          using the DomainDesc object. -1 if the class is not defined. 
     */

    public  int getRealClass(){
	return realClass; 
    }
    
     /** 
     * Gets the predicted class
     *
     * @return An int identifying this class which can be interpreted
     *          using the DomainDesc object. -1 if not defined. 
     */

    public int getPredictedClass(){
	return predictedClass; 
    }

    public float getPredictedClassConfidence(){
	return predictedClassConfidence; 
    }

    /**
     * Set the real class 
     * 
     * @param classlabel The new real class 
     */ 
    
    public void setRealClass(int classlabel){
	realClass = classlabel; 
    }

    /**
     * Set the predicted class
     * 
     * @param classlabel The new predicted class
     */ 
    
    public void setPredictedClass(int classlabel){
	predictedClass = classlabel; 
    }

    public void setPredictedClassConfidence(float confidence){
	predictedClassConfidence = confidence; 
    }
    

    public String toString(){
	return "RealClass = " + realClass + " PredClass = " 
	    + predictedClass + " Confidence = " 
	    + predictedClassConfidence + "\n"; 
    }
    
}
