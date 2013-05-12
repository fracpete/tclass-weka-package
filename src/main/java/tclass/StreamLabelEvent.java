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
  * A representation of all the attribute values associated with a particular 
  * stream. The format of the stream is given by an AttDescVecI object. 
  * 
  * This is little more than an array wrapper. 
  * @author Waleed Kadous
  * @version $Id: StreamLabelEvent.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.Vector;

public class StreamLabelEvent {
    Vector labelEvents = new Vector(); 

    /**
     *  Set the value of the <i>i</i>th attribute to a particular 
     *  value. 
     *  
     *  @param cm The attribute to change. More info on the attributes can 
     *              be found from the AttDescVecI object. 
     */ 

    public void add(ClusterMem cm){
	labelEvents.addElement(cm); 
    }

    /** 
     * Get the value of the <i>i</i>th attribute 
     *
     * @param index Attribute you want the value for.
     * @return the value of the <i>i</i>th attribute. 
     */

    public ClusterMem getLabelEvent(int index){
	return (ClusterMem) labelEvents.elementAt(index); 
    }

    public int size(){
        return labelEvents.size(); 
    }
    
    @Override
    public String toString(){
        
        String retval = "[ "; 
        for(int i=0; i < labelEvents.size(); i++){
            retval += labelEvents.elementAt(i)  + " "; 
        }
        retval += "]\n"; 
        return retval; 
    }
   
}
