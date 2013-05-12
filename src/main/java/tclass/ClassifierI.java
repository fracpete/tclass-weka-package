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

/**
  * The interface for a classifier. These objects are produced by 
  * learners. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClassifierI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClassifierI {

    /** 
     * Gets the name of the classifier. 
     */ 

    public String getName(); 

    /**
     * Gets a description of the classifier. 
     */ 
    
    public String getDescription(); 

    /** 
     * Takes a vector of attribute-value pairs and updates the given
     * ClassificationI object. 
     *
     * @param instance The instance to classify. This object must be 
     * created in the same format (i.e. be described by the same
     * <code>AttDescVecI</code> as those used by the trainer. 
     * 
     * @param classn The classification to be updated. The
     *      <code>ClassificationI</code> methods 
     * <code>setPredictedClass()</code> and possibly
     * <code>setPredictedClassConfidence()</code> may be called in
     * this method. 
     */ 

    public void classify(StreamAttValI instance, ClassificationI classn); 
    
}
