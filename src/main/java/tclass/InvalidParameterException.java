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
  * An exception which occurs if somewhere in the program, the user tries
  * to set a parameter value to a silly value. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: InvalidParameterException.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public class InvalidParameterException extends Exception {

  /** for serialization. */
  private static final long serialVersionUID = -1286882661113641141L;

    public InvalidParameterException(String param, String value, String error){
	super("P: " + param + " V: " + value + " error: " + error); 
    }
    

}
