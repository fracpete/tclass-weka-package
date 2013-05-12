/**
  * What this class does goes here
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Binner.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.*; 
import tclass.util.*; 

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
    
    public String toString(){
	return "Discretisation boundaries are: " + EqualFreqDiscretiser.d2str(boundaries); 
    }
}
