/**
  * Interface for discretisers. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: DiscretiserI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   
import tclass.*; 
import tclass.util.*; 
import tclass.datatype.*; 


public interface DiscretiserI {
    //Note if numBins < 0 means use auto bin count. 
    void makeDiscretisation(ClassStreamAttValVecI classes, int numBins,
			    int attNum);
    Discrete getDiscType();
    
    int discretise(float val); 
    
    int size(); 
    
}
