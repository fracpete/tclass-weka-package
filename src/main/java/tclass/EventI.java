/**
  * The description of a generic event. For now, event parameters are 
  * represented exclusively as doubles. 
  * 
  * @author Waleed Kadous
  * @version $Id: EventI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface EventI {
    
    /**
     * Gets the value of the <i>i</i>th parameter. These really only
     * make sense when combined with an EventDescI object. 
     */

    public float valOf(int i); 
 

    public float getMidtime(); //  Why not? return -1 if not interested. 
    public float getDuration(); // Why not? return -1 if not interested. 

}
