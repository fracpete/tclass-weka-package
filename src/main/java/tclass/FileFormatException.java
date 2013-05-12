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
  * Occurs when one of the input files has an incorrect format. 
  * It specified the file name, the StreamTokenizer, and the error. 
  * 
  * @author Waleed Kadous
  * @version $Id: FileFormatException.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.io.StreamTokenizer;

public class FileFormatException extends Exception {
    
  /** for serialization. */
  private static final long serialVersionUID = 894833276817138061L;

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
