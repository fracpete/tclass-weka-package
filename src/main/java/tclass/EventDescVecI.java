/**
  * A vector of event descriptions. Has an additional feature over
  * other vector classes that it can search for a particular event
  * description by name. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventDescVecI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface EventDescVecI {
    
    public int size(); 
    
    public void add(String name, EventDescI ad); 

    public EventDescI elAt(int i); 
    
    public EventDescI elCalled(String name); 
    
    public String elName(int i); 

    public int elIndex(String name); 
    
}
