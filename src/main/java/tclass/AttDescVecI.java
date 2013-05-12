
package tclass;   

/**
  * A vector of attribute descriptions. Has an additional feature over
  * other vector classes that it can search for a particular attribute 
  * description by name.<p>
  * Such a vector might be produced, for instance as the result of the
  * construction of synthetic attributes, or by the global constructor. 
  * 
  * 
  * @author Waleed Kadous
  * @version $Id: AttDescVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface AttDescVecI {
    
    public int size(); 
    
    public void add(AttDescI ad); 

    public AttDescI elAt(int i); 
    
    public AttDescI elCalled(String name); 
    
}
