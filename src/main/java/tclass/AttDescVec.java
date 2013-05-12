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
  * A vector of attribute descriptions. Has an additional feature over
  * other vector classes that it can search for a particular attribute 
  * description by name. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: AttDescVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.Vector;

import tclass.util.StringMap;

public class AttDescVec implements AttDescVecI, Cloneable {

    private Vector atts = new Vector(); 
    private StringMap attNames = new StringMap(); 

    public int size(){
	return attNames.size(); 
    }
    
    public void add(AttDescI ad){
	atts.addElement(ad); 
	attNames.add(ad.getName()); 
    }

    public void add(AttDescVecI adv){
	for(int i=0; i < adv.size(); i++){
	    add(adv.elAt(i)); 
	}
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

    public AttDescI elAt(int i){
	return (AttDescI) atts.elementAt(i); 
    }


    
    public AttDescI elCalled(String name){
	return (AttDescI) atts.elementAt(attNames.getInt(name)); 
    }
    
    @Override
    public String toString(){
	String retval = "AttDescVec has " + size() + " elements. \n"; 
	
	int numEls = size(); 
	for(int i=0; i < numEls; i++){
	    retval += "[" + elAt(i).toString() + "]\n"; 
	}
	return retval; 
    }

}
