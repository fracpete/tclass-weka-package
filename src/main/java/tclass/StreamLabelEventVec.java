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
  * Yet another vector class. Pretty much standard. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: StreamLabelEventVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.Vector;

public class StreamLabelEventVec implements Cloneable {

    private Vector data = new Vector(); 
    private AttDescVecI adv; 
    
    
    /** Get the number of Streams described in this set. 
     *
     * @return number of streams. 
     */ 

    public int size(){
	return data.size(); 
    }

        /**
     * Clone the current object. 
     *
     */ 

    @Override
    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }

    /** Add a stream. 
     */ 

    public void add(StreamLabelEvent savi){
	data.addElement(savi); 
    }

        /**
     * Clone the current object. 
     *
     */ 

    /**
     *  Get the description of this vector 
     * 
     */ 

    public AttDescVecI getDescription(){
	return adv; 
    }
    
    public void setDescription(AttDescVecI adv){
	this.adv = adv; 
    }
    
    /**
     * Get a single single stream. 
     */ 

    public StreamLabelEvent elAt(int i){
	return (StreamLabelEvent) data.elementAt(i); 
    }
    
    @Override
    public String toString(){
        int numEls = data.size(); 
        
	String retval = "StreamLabelEventVec vec has " + numEls + " elements.\n"; 
	for(int i=0; i < numEls; i++){
	    retval += "[" + data.elementAt(i).toString() +"]"; 
	}
	return retval; 
    }

}
