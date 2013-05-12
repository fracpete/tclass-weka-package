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
  * Interface for discretisers. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: DiscretiserI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.ClassStreamAttValVecI;
import tclass.datatype.Discrete;


public interface DiscretiserI {
    //Note if numBins < 0 means use auto bin count. 
    void makeDiscretisation(ClassStreamAttValVecI classes, int numBins,
			    int attNum);
    Discrete getDiscType();
    
    int discretise(float val); 
    
    int size(); 
    
}
