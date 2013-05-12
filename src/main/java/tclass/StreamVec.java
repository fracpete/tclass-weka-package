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
 * Default implementation of StreamVecI
 * 
 * @author Waleed Kadous
 * @version $Id: StreamVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import java.util.Vector;

public class StreamVec implements StreamVecI {
    
  /** for serialization. */
  private static final long serialVersionUID = 5701524033478394961L;
    private Vector streams = new Vector(); 
    
    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */

    public int size(){
	return streams.size(); 
    }

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */ 
    public void add(StreamI s){
	streams.addElement(s); 
    }

    /**
     * Ask for a particular stream
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 

    public StreamI elAt(int i){
	return (StreamI) streams.elementAt(i); 
    }

}
