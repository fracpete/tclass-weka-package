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

  /** for serialization. */
  private static final long serialVersionUID = -7110455352271801196L;
    int realClass = ClassificationI.UNCLASSIFIED; 
    int predictedClass = ClassificationI.UNCLASSIFIED; 
    float predictedClassConfidence = 0; 

    /**
     * Clone the current object. 
     *
     */ 

    @Override
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
    

    @Override
    public String toString(){
	return "RealClass = " + realClass + " PredClass = " 
	    + predictedClass + " Confidence = " 
	    + predictedClassConfidence + "\n"; 
    }
    
}
