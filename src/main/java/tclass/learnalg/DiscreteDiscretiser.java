/**
  * What this class does goes here
  *
  * 
  * @author Waleed Kadous
  * @version $Id: DiscreteDiscretiser.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.*; 
import tclass.util.*; 
import tclass.datatype.*; 

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

