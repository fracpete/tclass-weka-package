/**
  * An exception which occurs if somewhere in the program, the user tries
  * to set a parameter value to a silly value. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: InvalidParameterException.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public class InvalidParameterException extends Exception {

    public InvalidParameterException(String param, String value, String error){
	super("P: " + param + " V: " + value + " error: " + error); 
    }
    

}
