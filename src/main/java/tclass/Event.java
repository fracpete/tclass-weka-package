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
  * The description of a generic event. 
  *
  * For now, event parameters are represented exclusively as
  * doubles. This is a simple default implementation, which
  * essentially is little more than a wrapper for an array of
  * doubles. This is more for convenience. It's unlikely that you'll
  * want to use this class yourself; you can do nifty tricks like
  * reformatting the internal representation used in your PEP to
  * provide just one extra method.
  * 
  * @author Waleed Kadous
  * @version $Id: Event.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ */

package tclass;   

public class Event implements EventI {
    
    float[] data = null; 

    
    /**
     *  Creates an event with <i>i</i> parameters. 
     *
     *  @param i Number of parameters for this type of event. 
     *
     */
    
    public Event(int i){
	data = new float[i]; 
    }

    public Event(float[] data){
	this.data = data; 
    }

    /**
     * Sets the <i>i</i>th parameter to <i>d</i>
     *
     * @param i The parameter to set. 
     * @param d the new value for the parameter. 
     */ 

    public void setVal(int i, float d){
	data[i] = d; 
    }

    /**
     * Gets the value of the <i>i</i>th parameter. These really only
     * make sense when combined with an EventDescI object. 
     *
     * @param i The parameter to retrieve. 
     * @return The value of the <i>i</i>th parameter. 
     */

    public float valOf(int i){
	return data[i]; 
    }
    
    @Override
    public String toString(){
	String retval = "( "; 
	for(int i=0; i < (data.length-1); i++){
	    retval += data[i] + ", "; 
	}
	retval += data[data.length-1] + " )"; 
	return retval; 
    }
    
    public float getMidtime(){
        return 0; 
    }

    public float getDuration(){
        return 0; 
    }
 
}
