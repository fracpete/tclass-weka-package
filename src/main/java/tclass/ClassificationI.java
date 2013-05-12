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

package tclass;   
import java.io.Serializable;

/**
  * Everything related to the classification of a particular stream or
  * anything else. Includes information about the classification
  * real or predicted. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClassificationI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClassificationI extends Serializable, Cloneable {

    /**
     * A special class, indicating that the current instance 
     * that this class is associated with doesn't actually have a class. 
     */ 

    static final int UNCLASSIFIED = -1; 

    /** 
     *  Returns true if the real class is known. 
     *  
     *  @return <code>true</code> if the class of this stream is known. 
     *
     */

    public boolean isRealClassDefined(); 
     /** 
     *  Returns true if the predicted class is known. 
     *  
     *  @return <code>true</code> if the class of this stream is known. 
     *
     */

    public  boolean isPredictedClassDefined();
    /** 
     * Gets the real class
     *
     * @return An int identifying this class which can be interpreted
     *          using the DomainDesc object. -1 if the class is not defined. 
     */

    public int getRealClass();
    
     /** 
     * Gets the predicted class
     *
     * @return An int identifying this class which can be interpreted
     *          using the DomainDesc object. -1 if not defined. 
     */

    public int getPredictedClass(); 

    /**
     * If the classifier provides further information about confidence
     * of classification, it can be accessed through here. 
     * 
     */ 
    
    public float getPredictedClassConfidence(); 

    /**
     * Set the real class 
     * 
     * @param classlabel The new real class 
     */ 
    
    public void setRealClass(int classlabel); 

    /**
     * Set the predicted class
     * 
     * @param classlabel The new predicted class
     */ 
    
    public void setPredictedClass(int classlabel); 

    /**
     * Some classifiers, when making a prediction about an instance's class
     * also give a confidence of that prediction. This allows the confidence
     * to be set 
     * 
     * @see #getPredictedClassConfidence()
     */ 

    public void setPredictedClassConfidence(float confidence); 

    public Object clone(); 
}
