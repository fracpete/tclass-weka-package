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
  * A vector of event descriptions. Has an additional feature over
  * other vector classes that it can search for a particular event
  * description by name. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventDescVec.java,v 1.2 2002/08/02 05:01:43 waleed Exp $
  * $Log: EventDescVec.java,v $
  * Revision 1.2  2002/08/02 05:01:43  waleed
  * Added log header
  *
  */

package tclass;   

import java.util.Vector;

import tclass.util.StringMap;

public class EventDescVec implements EventDescVecI {

    private Vector eds = new Vector(); 
    private StringMap edNames = new StringMap(); 
    
    public int size(){
	return edNames.size(); 
    }
    
    public void add(String name, EventDescI ed){
	eds.addElement(ed); 
	edNames.add(name); 
    }

    public EventDescI elAt(int i){
	return (EventDescI) eds.elementAt(i); 

    }
    
    public EventDescI elCalled(String name){
	return (EventDescI) eds.elementAt(edNames.getInt(name)); 
    }
    
    public String elName(int i){
	return edNames.getString(i); 
    }

    public int elIndex(String name){
	return edNames.getInt(name); 
    }

    @Override
    public String toString(){
	String retval = "EventDescVec has " + size() + " elements. \n"; 
	
	int numEls = size(); 
	for(int i=0; i < numEls; i++){
	    retval += "[" + elAt(i).toString() + "]\n"; 
	}
	return retval; 
    }


}
