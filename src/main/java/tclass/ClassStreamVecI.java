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
  * Interface for a set of streams, including the classification. 
  * <p>
  * When you think about it, the classification of a set of instances
  * and the instances themselves are separate. For example, a different
  * representation of the stream still has the same class. 
  * This technique of having a representation for streams and classifications
  * separately simplifies representation conversion (which, as you can guess, 
  * we do in several places in the code, since representation conversion
  * is the basis of the learning algorithm). 
  * <p>
  * You should make sure that the classification vector and the vector of streams
  * are the same size. 
  * <p>
  * @author Waleed Kadous
  * @version $Id: ClassStreamVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */
public interface ClassStreamVecI extends Cloneable, Serializable 
{

    public boolean hasClassification();     

    public StreamVecI getStreamVec(); 
    
    public void setStreamVec(StreamVecI sv);

    public ClassificationVecI  getClassVec(); 

    public void setClassVec(ClassificationVecI classes); 
    
    /**
     * Adds an instance to both.
     */ 
  
    public void add(StreamI strm, ClassificationI classn); 

    /**
     * Get the size of the current stream
     */ 
    
    public int size(); 
    
}
