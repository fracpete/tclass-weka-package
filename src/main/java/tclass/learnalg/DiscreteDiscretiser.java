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
  * What this class does goes here
  *
  * 
  * @author Waleed Kadous
  * @version $Id: DiscreteDiscretiser.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.ClassStreamAttValVecI;
import tclass.DataTypeI;
import tclass.StreamAttValVecI;
import tclass.datatype.Discrete;

//A dummy discretiser

public class DiscreteDiscretiser implements DiscretiserI {
    DataTypeI dt; 
    
    public void makeDiscretisation(ClassStreamAttValVecI data, 
			    int numBins, int attNum){
	StreamAttValVecI streams = data.getStreamAttValVec(); 
	dt = streams.getDescription().elAt(attNum).getDataType(); 
    }
    
    public Discrete getDiscType (){
	return (Discrete) dt; 
    }
    
    public int discretise(float val){
	return (int) val; 
    }
    
    public int size(){
	return ((Discrete) dt).size(); 
    }
}

