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
  * A prototype manager for DataTypes
  *
  * 
  * @author Waleed Kadous
  * @version $Id: DataTypeMgr.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.util.Enumeration;
import java.util.Hashtable;

import tclass.datatype.Continuous;
import tclass.datatype.Discrete;

public class DataTypeMgr {
    Hashtable registry = new Hashtable(); 
    static DataTypeMgr instance; 
    
    /**
     * The constructor. A good place to register the various 
     * datatypes. 
     * 
     *
     */

    public DataTypeMgr(){
	register((DataTypeI) new Continuous()); 
	register((DataTypeI) new Discrete()); 
    }

    public static DataTypeMgr getInstance(){ 
	if(instance == null){
	    instance = new DataTypeMgr(); 
	    return instance; 
	}
	else {
	    return instance; 
	}
    }

    public void register(DataTypeI prototype){
	registry.put(prototype.getName(), prototype); 
    }

    /** 
     * Gets a datatype by name. WARNING: This does
     * clone it. This is necessary for safety reasons. Otherwise, 
     * other people's code could modify the things stored in the
     * prototype. This is BAD. 
     * 
     * This is the default version of the object. 
     *
     * @param name name of the prototype to retrieve
     * @return A clone of the prototype. null 
     * if there is no such prototype known.
     */ 

    public DataTypeI getClone(String name){
	DataTypeI dt =  (DataTypeI) registry.get(name); 
	if(dt == null)
	    return null; 
	else
	    return (DataTypeI) dt.clone(); 
    }

    /**
     *  Gets a list of all the PEPs available by name
     *
     */

    public String[] getNames(){
	String[] retval = new String[registry.size()]; 
	int i = 0; 
	for(Enumeration e = registry.keys(); e.hasMoreElements(); i++){
	    retval[i] = (String) e.nextElement(); 
	}
	return retval; 
    }

    public static void main(String[] args) throws Exception {
	DataTypeMgr dtm = DataTypeMgr.getInstance(); 
	DataTypeI dt = dtm.getClone("continuous"); 
	DataTypeI dt2 = dtm.getClone("continuous"); 
	DataTypeI dt3 = dtm.getClone("discrete");
	dt.setParam("distance", "linear"); 
	dt2.setParam("distance", "square"); 
	dt3.setParam("costmetric", "complex"); 
	dt3.setParam("cost", "true false 5"); 
	dt3.setParam("cost", "false true 0.1"); 
	System.out.println("Distance linear = " + dt.distance((float) 7.0, (float) 5.0)); 
	System.out.println("Distance square = " + dt2.distance((float) 7.0, (float) 5.0)); 
	System.out.println("Distance disc = " 
			   + dt3.distance(dt3.read("true"), dt3.read("false"))); 
	System.out.println("Distance disc = " 
			   + dt3.distance(dt3.read("false"), 
					  dt3.read("true"))); 
	System.out.println(dt3.print((float) 0.0) +  " " + dt3.print((float) 1.0)); 
    }
}
