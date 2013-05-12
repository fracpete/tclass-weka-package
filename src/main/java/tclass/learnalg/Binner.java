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
  * @version $Id: Binner.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.util.Debug;

// Messiness note. To make four bins, i need 3 values. So ten bins needs nine values. 
public class Binner {
    float[] boundaries;
    Binner(int numBins){
	boundaries = new float[numBins-1]; 
    }
    void setBound(int num, float val){
	boundaries[num] = val; 
	if(num > 0){
	    Debug.myassert(boundaries[num-1] < boundaries[num], 
			 "DANGER!!! Bin boundaries are not sequential!!"
			 + boundaries[num-1] + " > " + boundaries[num]); 
	    
	}
	
    }
    int findBin(float value){
	for(int i=0; i < boundaries.length; i++){
	    if(value < boundaries[i]){
		return i; 
	    }
	}
	return boundaries.length; 
    }
    
    @Override
    public String toString(){
	return "Discretisation boundaries are: " + EqualFreqDiscretiser.d2str(boundaries); 
    }
}
