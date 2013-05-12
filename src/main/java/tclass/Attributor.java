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
  * Does the attribution. Attribution involves taking a set of clusters and
  * takes the data (in both raw and event representations) and evaluates
  * the presence or absence of each synthetic event. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Attributor.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.io.FileReader;
import java.io.StreamTokenizer;

import tclass.util.Debug;

public class Attributor {
    
    private DomDesc domDesc; 
    private ClusterVecI clusterVec; 
    private AttDescVecI attDescVec; 
    

    
    public Attributor(DomDesc d, ClusterVecI cvi, AttDescVecI  advi){
	domDesc = d; 
	clusterVec = cvi; 
	attDescVec = advi; 
    }


    /** 
     * Do the attribution
     */ 



    public ClassStreamAttValVecI attribute(ClassStreamVecI rawData, 
					   ClassStreamEventsVecI eventData){

	/* Note that StreamVecI and StreamEventsVecI should have
	   a similar number of entries. So let's do some sanity checks. 
	*/ 
	 
	Debug.myassert(rawData.size() == eventData.size(), 
		     "DANGER WILL! Event and raw data sizes are different!"); 

	Debug.myassert(rawData.getClassVec() == eventData.getClassVec(), 
		     "DANGER WILL! Raw and Event classifications are different!!"); 
	
	// Object we'll be returning. 

	ClassStreamAttValVec retval = new ClassStreamAttValVec(); 
	
	//Copy the classification. 
	retval.setClassVec(eventData.getClassVec()); 

	//And now let's start on the Str
	
	StreamAttValVec savv = new StreamAttValVec(); 
	savv.setDescription(attDescVec); 
	
	// Ok, let's boogie with some for loops!! 
	int numStreams = rawData.size(); 
	int numAtts = clusterVec.size(); 
	StreamVecI svi = rawData.getStreamVec(); 
	StreamEventsVecI sevi = eventData.getStreamEventsVec(); 
	for(int i=0; i < numStreams; i++){

            StreamAttVal sav = new StreamAttVal(numAtts); 
	    for(int j=0; j < numAtts; j++){
		sav.setAtt(j, clusterVec.elAt(j).findMatch(svi.elAt(i), sevi.elAt(i))); 
	    }
	    savv.add(sav); 
	}
	retval.setStreamAttValVec(savv); 
	return retval; 
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
	    ClassStreamEventsVecI csevi = ee.extractEvents(csvi);
	    System.out.println(csevi.toString());
	    // And then the event clusterer. 
	    System.out.println("Ok ... now testing clustering."); 
	    StreamTokenizer ecst = new StreamTokenizer(new FileReader("tests/test._ec")); 
	    EventClusterer ec = new EventClusterer(ecst, d, ee.getDescription()); 
	    ClusterVecI cvi = ec.clusterEvents(csevi);
	    System.out.println(cvi.toString()); 
	    System.out.println("Printing cluster -> values mapping ... "); 
	    System.out.println(ec.getMapping()); 
	    System.out.println("..... Drumroll please ... Attribution"); 
	    Attributor a = new Attributor(d, cvi, ec.getDescription()); 
	    System.out.println(a.attribute(csvi, csevi).toString()); 
	}
	else {
	    //How do I debug thee?
	    //Let's see ... first let's load a domain description:
	    DomDesc d = new DomDesc("sl.tdd"); 
	    // And now some data ...
	    ClassStreamVecI csvi = (ClassStreamVecI) new ClassStreamVec("sl.tsl", d);
	    Debug.setDebugLevel(Debug.EVERYTHING); 
	    EventExtractor ee = new EventExtractor(new StreamTokenizer(
								       new FileReader("test._ee")), d);
	    System.out.println("---%%%-- Results ---%%%---"); 
	    System.out.println(ee.getDescription().toString()); 
	    ClassStreamEventsVecI csevi = ee.extractEvents(csvi);
	    System.out.println(csevi.toString());
	    // And then the event clusterer. 
	    System.out.println("Ok ... now testing clustering."); 
	    StreamTokenizer ecst = new StreamTokenizer(new FileReader("test._ec")); 
	    EventClusterer ec = new EventClusterer(ecst, d, ee.getDescription()); 
	    ClusterVecI cvi = ec.clusterEvents(csevi);
	    System.out.println(cvi); 
	    System.out.println("Printing cluster -> values mapping ... "); 
	    System.out.println(ec.getMapping()); 
	    System.out.println("..... Drumroll please ... Attribution"); 
	    Attributor a = new Attributor(d, cvi, ec.getDescription()); 
	    System.out.println(a.attribute(csvi, csevi).toString()); 

	}
    }

}
