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
  * This is the prototype manage for global extractors. Note that
  * this is a singleton class. For discussion of singleton classes,
  * and a clearer example of prototype managers, see the DataTypeMgr 
  * class. 
  * 
  * @author Waleed Kadous
  * @version $Id: GlobalExtrMgr.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ */

package tclass;   


import java.util.Enumeration;
import java.util.Hashtable;

import tclass.global.Duration;
import tclass.global.First;
import tclass.global.Max;
import tclass.global.Mean;
import tclass.global.Min;
import tclass.global.Mode;
import tclass.util.Debug;

public class GlobalExtrMgr {
    Hashtable registry = new Hashtable(); 
    static GlobalExtrMgr instance; // This is the singleton. 
    
    /**
     * The constructor. A good place to register the various 
     * global extractors. 
     *
     */
    
    public GlobalExtrMgr(){
	//Registering looks like this: 
	//register((GlobalExtractorI) new yourGlobalExtractorClassHere());
	register((GlobalExtractorI) new Max()); 
	register((GlobalExtractorI) new Min()); 
	register((GlobalExtractorI) new Mean());
 	register((GlobalExtractorI) new Duration()); 
	register((GlobalExtractorI) new Mode()); 
	register((GlobalExtractorI) new First()); 
    }
    
    
    /** 
     * Gets the instance of the Global Extractor Manager. 
     * If it doesn't exist yet, it makes it; otherwise,
     * it just returns the existing one.
     */ 

    public static GlobalExtrMgr getInstance(){ 
	if(instance == null){
	    instance = new GlobalExtrMgr(); 
	    return instance; 
	}
	else {
	    return instance; 
	}
    }
    
    public void register(GlobalExtractorI prototype){
	registry.put(prototype.name(), prototype); 
    }
   
    /** 
     * Gets a clone of the Global Extractor by name. WARNING: This does
     * clone it. This is necessary for safety reasons. Otherwise, 
     * other people's code could modify the things stored in the
     * prototype. This is BAD. 
     *
     * Note that the domain description object is necessary; since
     * global extractors need to have access to the underlying data
     * they are extracting from. 
     * 
     * This is the default version of the object. 
     *
     * @param name name of the prototype to retrieve
     * @param d Domain description that this global is going to be 
     *           used for. 
     * @return A clone of the prototype. null 
     * if there is no such prototype known.  */

    public GlobalExtractorI getClone(String name, DomDesc d){
	GlobalExtractorI ge =  (GlobalExtractorI) registry.get(name); 
	if(ge == null)
	    return null; 
	else {
	    GlobalExtractorI geclone = (GlobalExtractorI) ge.clone(); 
	    geclone.setDomDesc(d); 
	    return geclone;  
	}
    }

    /**
     *  Gets a list of all the Global Extractors available by name
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

	//How do I debug thee?
	Debug.setDebugLevel(Debug.EVERYTHING); 
	//Let's see ... first let's load a domain description:
	DomDesc d = new DomDesc("tests/test.tdd"); 
	// And now some data ... 
	StreamI s = (StreamI) new Stream("tests/test.tsd", d); 
	//Get ourselves the Manager: 
	GlobalExtrMgr gem = GlobalExtrMgr.getInstance();
	
	// Now let's create some Global extractors. 
	GlobalExtractorI max = gem.getClone("max", d); 
	max.setParam("channel", "Z"); 
	DataTypeI maxType = max.getDataType();	
	GlobalExtractorI mean = gem.getClone("mean", d); 
	mean.setParam("channel", "Y"); 
	DataTypeI meanType = mean.getDataType(); 

	GlobalExtractorI durn = gem.getClone("duration", d); 
	DataTypeI durnType = durn.getDataType(); 
	GlobalExtractorI mode = gem.getClone("mode", d); 
	mode.setParam("channel", "X"); 
	GlobalExtractorI first = gem.getClone("first", d); 
	first.setParam("channel", "X"); 
	DataTypeI firstType = first.getDataType(); 

	DataTypeI modeType = mode.getDataType(); 
	Debug.dp(Debug.EVERYTHING, modeType.print((float) 1.0)); 
	//And apply them. 
	System.out.println("---- %%% Test results %%% ----"); 
	float maxVal = max.extract(s); 
	System.out.println("Max is: " + maxType.print(maxVal)); 
	float meanVal = mean.extract(s); 
	System.out.println("Mean is: " + meanType.print(meanVal)); 
	float durnVal = durn.extract(s); 
	System.out.println("Durn is: " + durnType.print(durnVal)); 
	float modeVal = mode.extract(s); 
	System.out.println("Mode is: " + modeType.print(modeVal));
	float firstVal = first.extract(s); 
	System.out.println("First is: " + firstType.print(firstVal)); 
	
    }
	    
}
