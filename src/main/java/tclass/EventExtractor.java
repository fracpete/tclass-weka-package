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
  * The Event Extractor is responsible for applying a sequence of PEPs
  * to a ClassStreamVec and returns a few things. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventExtractor.java,v 1.2 2002/08/02 04:28:38 waleed Exp $
  */

package tclass;   

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;

import tclass.util.Debug;
import tclass.util.StringMap;

public class EventExtractor {
    private Vector peps = new Vector(); 
    private StringMap pepNames = new StringMap(); 
    private DomDesc domDesc; 

    public EventExtractor(DomDesc d){
	domDesc = d; 
    }

    
    public EventExtractor(String structure, DomDesc d)
	throws FileFormatException, InvalidParameterException, IOException {
	StreamTokenizer st = new StreamTokenizer(new StringReader(structure));
	domDesc = d; 
	parseStructure(st); 
    }
    
    public EventExtractor(StreamTokenizer st, DomDesc d) 
	throws FileFormatException, InvalidParameterException, IOException {
	domDesc = d; 
	parseStructure(st); 
    }
    
    
    
    /* Remember, this bit of architecture description is meant to look
     * something like: 
     *  metafeatures {
     *   metafeature <name> <type> {
     *     <parameter> <value>
     *     ..
     *     <parameter> <value>
     *    }
     *   metafeature <name> <type> {
     *     <parameter> <value>
     *     ..
     *     <parameter> <value>
     *   }
     * }
     * We get it just after the globalcalc; i.e. if we do a st.nextToken(), 
     * then an st.ttype it should be '{'. 
     */ 

    public void parseStructure(StreamTokenizer st) throws 
    FileFormatException, InvalidParameterException, IOException {
	// We assume that the following has been done before: 
	st.commentChar('#'); 
	// We want to treat numbers as parts of a word, as well as full
	// stops and - signs.

	st.ordinaryChars('0','9'); 
        st.ordinaryChar('.'); 
        st.ordinaryChar('-');
        st.wordChars('0','9'); 
	st.wordChars('.','.'); 
        st.wordChars('-','-'); 
        st.quoteChar('"'); 
	
	// So let's get rid of the leading {
	st.nextToken(); 
	if(st.ttype != '{'){
	    throw new FileFormatException("ArchDesc", st, "{"); 
	}
	st.nextToken(); 
	// And now the loop that takes parameters and tests them. 
	// But wait; we should get the local GlobalExtrMgr. 
	PepMgr pmgr = PepMgr.getInstance(); 
	while(st.ttype != '}' && st.ttype != st.TT_EOF ){ // i.e. while there's still more ... 
	    if(st.ttype == st.TT_WORD && st.sval.equals("metafeature")){
		//So we have a global declaration. 
		st.nextToken(); 
		String pepName = st.sval;
		// The name of the global extractor
		st.nextToken(); 
		String pepType = st.sval; 
		// The type of the global extractor
	        // Now we can create it. 
		PepI pep = pmgr.getClone(pepType, domDesc); 
		// Check the return value. 
		if(pep == null){
		    throw new FileFormatException("ArchDesc", st, "an existent PEP."); 
		    
		}
		// Now, let's grab parameters and feed them to our lovely new 
		// global extractor. 
		st.nextToken(); 
		if(st.ttype != '{'){
		    throw new FileFormatException("ArchDesc", st, "{"); 
		}
		st.nextToken(); 
		while(st.ttype != '}'){
		    String param = st.sval; 
		    st.nextToken(); 
		    //Here are the possibilities: It's a number, but we
		    //treat numbers like words anyway; it's a straight string
		    //or it's quote-delimited string - " 
		    if(!(st.ttype == st.TT_WORD || st.ttype == '"'))
			throw new FileFormatException("ArchDesc", st, "Parameter value");
		    String value = st.sval; 
		    pep.setParam(param, value); 
		    st.nextToken();
		}
		// And finally, add to the database. 
		addPep(pepName, pep); 
	    }
	    else {
		throw new FileFormatException("ArchDesc", st, "metafeature"); 
	    }
	    st.nextToken(); 
	}
    }
    
    public void addPep(String name, PepI pep){
	peps.addElement(pep); 
	pepNames.add(name); 
    }
    
    public PepI pepAt(int i){
	return (PepI) peps.elementAt(i); 
    }
    
    public EventDescVecI getDescription(){
	EventDescVec retval = new EventDescVec(); 
	// And now we iterate through the loop. 
	int numPeps = pepNames.size(); 
	for(int i=0; i < numPeps; i++){
	    String pepName = pepNames.getString(i); 
	    EventDescI ed = pepAt(i).getEventDesc(); 
	    retval.add(pepName, ed); 
	    // Note the
	}
	return (EventDescVecI) retval; 
    }
    
    public int numPeps(){
	return peps.size(); 
    }

    
    // Now the bigun ... this one could be a real headache. 

    public ClassStreamEventsVecI extractEvents(ClassStreamVecI csvi){
	int numVecs = csvi.size(); 
	int numPeps = numPeps(); 
	StreamVecI svi = csvi.getStreamVec(); 
	ClassStreamEventsVec csev = new ClassStreamEventsVec(); 
	csev.setClassVec(csvi.getClassVec()); 
	StreamEventsVec sev = new StreamEventsVec(); 
	sev.setEventDescVec(getDescription()); 
	// Ok, this is where it gets confusing. For each vector, we
	// want to get the events from applying 
	for(int i=0; i < numVecs; i++){
	    StreamEvents se = new StreamEvents(numPeps); 
	    StreamI s = svi.elAt(i); 
	    for(int j=0; j < numPeps; j++){
		se.setEvents(j, pepAt(j).findEvents(s)); 
	    }
	    sev.add(se); 
	}
	csev.setStreamEventsVec(sev); 
	return (ClassStreamEventsVecI) csev; 
    }
    
    public static void main(String[] args) throws Exception {
	if(args.length == 0){
	    //How do I debug thee?
	    Debug.setDebugLevel(Debug.EVERYTHING); 
	    //Let's see ... first let's load a domain description:
	    DomDesc d = new DomDesc("tests/test.tdd"); 
	    // And now some data ...
	    ClassStreamVecI csvi = (ClassStreamVecI) new ClassStreamVec("tests/test.tsl", d);
	    EventExtractor ee = new EventExtractor(new StreamTokenizer(
							       new FileReader("tests/test._ee")), d);
	    System.out.println("---%%%-- Results ---%%%---"); 
	    System.out.println(ee.getDescription().toString()); 
	    System.out.println(ee.extractEvents(csvi).toString());
	}
	//Ok, option 2. 
	else {
	    //How do I debug thee?
	    Debug.setDebugLevel(Debug.FN_PARAMS); 
	    //Let's see ... first let's load a domain description:
	    DomDesc d = new DomDesc("sl.tdd"); 
	    // And now some data ...
	    ClassStreamVecI csvi = (ClassStreamVecI) new ClassStreamVec("sl.tsl", d);
	    EventExtractor ee = new EventExtractor(new StreamTokenizer(
							       new FileReader("test._ee")), d);
	    System.out.println("---%%%-- Results ---%%%---"); 
	    System.out.println(ee.getDescription().toString()); 
	    System.out.println(ee.extractEvents(csvi).toString());
	}
 
    }
    
}
