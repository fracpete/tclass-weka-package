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
  * Maps from Strings to ints and vice versa
  * 
  * A commonly occurring situation (at least in this project) is the situation
  * where a string maps to an integer and vice versa and a mapping must 
  * frequently be made between these two. This class handles this situation
  * in an efficient (i.e. all non-iterative ops are O(1)) manner. 
  *
  * 
  * Note that the integer represented by the string is allocated by
  * us.  What's the use, then? Well, this method offers consistency
  * from call to call, so that given the same string, the number is
  * the same.  It's just strings are such a pain to handle. In
  * addition, it is safe to assume that the numbers are allocated in
  * order, starting from 0. This makes it easy to construct other 
  * concurrent structures with the same indexing system. 
  *
  * Also note that there is no mechanism for deleting anything from
  * this mapping. It gets a bit messy retaining 
  * 
  * @author Waleed Kadous
  * @version $Id: StringMap.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ */

package tclass.util;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public class StringMap implements Cloneable, Serializable {
  /** for serialization. */
  private static final long serialVersionUID = -2015202023191511926L;
    Hashtable str2num; 
    Vector num2str; 
    int numEntries; 
    
    /**
     * Construct a String map with the default capacity 
     */ 

    public StringMap(){
	str2num = new Hashtable(); 
	num2str = new Vector(); 
	numEntries = 0; 
    }

    /**
     * Construct a StringMap with the specified capacity. 
     * 
     * @param capacity The initial capacity. Note that this is not an
     * upper limit, but rather an expected size. If the capacity is
     * the same as the actual number of entries, then the algorithm is
     * likely to be efficient in both space and time. 
     *
     */
    
    public StringMap(int capacity){
	str2num = new Hashtable(capacity); 
	num2str = new Vector(capacity);
	numEntries = 0; 
    }
    
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
    
    /**
     * Clear all entries from the stringmap
     *
     */ 
    
    public void clear(){
	str2num.clear(); 
	num2str.removeAllElements(); //Why can't the java libraries call them 
	//the same thing? 
	numEntries = 0; 
    }

    /**
     * Add an entry to the String map. Note: No integer is given. We allocate 
     * it. As an extra bonus, for no extra charge, we throw in a return value!
     * It save you a function call!!
     * 
     * @param label The label you want to add to this string map. 
     * @return An integer, greater than or equal to 0 that if you call
     * getString with, gives you back the label you gave us. 
     *
     * Note: DON'T put the same string in twice. Verily, therein lies
     * core dumps and exceptions unbounded in number.
     *  
     */

    public int add(String label){
	str2num.put(label, new Integer(numEntries)); 
    	num2str.addElement(label); 
	//Note that this means that num2str.elementAt(i) == label
	int retval = numEntries; 
	numEntries++; 
	return retval; 
    }

    /**
     * Converts from an integer to a String using this StringMap. 
     * 
     * @param num The integer you want to convert.  
     * @return The string represented by the number. Null if the number 
     * isn't valid. 
     */ 
    
    public String getString(int num){
	try {
	    return (String) num2str.elementAt(num); 
	}
	catch(ArrayIndexOutOfBoundsException ae){
	    return null; 
	}
    }
    
    /**
     * Converts from a String to an integer using this StringMap. 
     *
     * @param label The String you want to convert to an int. 
     * @return The corresponding int. -1 if there is no such string in this
     * mapping.
     */
    
    public int getInt(String label) {
	Integer retInt = (Integer) str2num.get(label); 
	if(retInt == null)
	    return -1; 
	else
	    return retInt.intValue(); 
    }
    
    /**
     * Get the number of strings stored in this mapping. 
     */ 
    
    public int size(){
	return numEntries; 
    }
    

    public static void main(String[] args){

	//Let's take this baby for a spin, shall we? 
	
	StringMap s = new StringMap(); 
	System.out.println("Let's add foo: " + s.add("foo")); 
	System.out.println("Let's add bar: " + s.add("bar")); 
	System.out.println("Let's add baz: " + s.add("baz")); 
	
	// Now let's try some retrieval, doing everything in reverse
	
	System.out.println(s.getString(s.getInt("foo")) + " == " + s.getInt("foo")); 
	System.out.println(s.getString(s.getInt("bar")) + " == " + s.getInt("bar")); 
	System.out.println(s.getString(s.getInt("baz")) + " == " + s.getInt("baz")); 
	
	//And now let's try some error results. 

	System.out.println("Retrieving blue returns " + s.getInt("blue")); 
	System.out.println("Retrieving 3 returns " + s.getString(3)); 
	System.out.println("Retrieving -1 returns " + s.getString(-1)); 
	
	
	
			   

    }
}
