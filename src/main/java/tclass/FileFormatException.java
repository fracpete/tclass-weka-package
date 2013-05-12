/**
  * Occurs when one of the input files has an incorrect format. 
  * It specified the file name, the StreamTokenizer, and the error. 
  * 
  * @author Waleed Kadous
  * @version $Id: FileFormatException.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.io.*; 

public class FileFormatException extends Exception {
    
    public FileFormatException(){
	super();
    }
    
    public FileFormatException(StreamTokenizer tokstrm, String expected){
	super("On line " + tokstrm.lineno() + " Got: "+tokstrm.toString() +
			 " Was expecting: "+ expected);
    }

    public FileFormatException(String filename, StreamTokenizer tokstrm, String expected){ 
        super("In file \"" + filename + "\" on line " + tokstrm.lineno() + " Got: "+tokstrm.toString() + 
                         " Was expecting: "+ expected); 
    } 

    public FileFormatException(String filename, StreamTokenizer tokstrm, 
			       InvalidParameterException ipex){
	super("In file \"" + filename + "\" on line " + tokstrm.lineno() + 
	      "Invalid Parameters " + ipex.toString()); 
    }

    public FileFormatException(String error){
	super(error);
    }
    
}    
