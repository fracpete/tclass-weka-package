/**
  * Is a Labeller, which relabels instances with their most probable cluster. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventLabeller.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import tclass.util.*; 
import java.io.*; 
import java.util.*; 

public class EventLabeller {
    
    private DomDesc domDesc; 
    private ClusterVecI[] clusterVecs; 
    private boolean ignoreTime = false; 
    
    boolean inclEvent;
    
    /**
       * Get the value of inclEvent.
       * @return Value of inclEvent.
       */
    public boolean getInclEvent() {return inclEvent;}
    
    /**
       * Set the value of inclEvent.
       * @param v  Value to assign to inclEvent.
       */
    public void setInclEvent(boolean  v) {this.inclEvent = v;}
    
    
    public EventLabeller(DomDesc d, ClusterVecI[] cvi){
        domDesc = d; 
        clusterVecs = cvi; 
    }

    public void setIgnoreTime(boolean ignoreTime){
        this.ignoreTime = ignoreTime; 
    }

    
    /** 
     * Do the attribution
     */ 



    public ClassStreamLabelEventVec label(ClassStreamVecI rawData, 
                                          ClassStreamEventsVecI eventData, EventDescVecI edvi, double minConf){

        /* Note that StreamVecI and StreamEventsVecI should have
           a similar number of entries. So let's do some sanity checks. 
        */ 
 
        Debug.myassert(rawData.size() == eventData.size(), 
                     "DANGER WILL! Event and raw data sizes are different!"); 

        Debug.myassert(rawData.getClassVec() == eventData.getClassVec(), 
                     "DANGER WILL! Raw and Event classifications are different!!"); 

        // Object we'll be returning. 

        ClassStreamLabelEventVec retval = new ClassStreamLabelEventVec(); 

        //Copy the classification. 
        retval.setClassVec(eventData.getClassVec()); 

        //And now let's start on the Str

        StreamLabelEventVec sevv = new StreamLabelEventVec(); 
        // savv.setDescription(attDescVec); 

        // Ok, let's boogie with some for loops!! 
        int numStreams = rawData.size(); 
        int numChannels = clusterVecs.length; 
        StreamVecI svi = rawData.getStreamVec(); 
        StreamEventsVecI sevi = eventData.getStreamEventsVec(); 
        for(int i=0; i < numStreams; i++){
            StreamEventsI sei = sevi.elAt(i); 
            StreamLabelEvent slev = new StreamLabelEvent(); 
            for(int j=0; j < numChannels; j++){
                System.out.println("numChannels = " + numChannels); 

                EventVecI events = sei.getEvents(j); 
                int numParams = edvi.elAt(j).numParams(); 
                int numEvents = events.size(); 
                // This is a little hack that "flattens" the effect of different numbers of 
                // event parameters. This is necessary to make fair comparisons of confidence
                // about things. Effectively, we are using the geometric mean. 
                
                for(int k=0; k < numEvents; k++){
                    ClusterMem bestLabel = clusterVecs[j].findBestLabel(events.elAt(k));
                    bestLabel.setEventName(edvi.elName(j)); 
                    bestLabel.setEventNum(k); 
                    bestLabel.setConf((float) Math.pow(bestLabel.getConf(), 1.0/(numParams - (ignoreTime ? 1: 0))));
                    // Above is a hack. If we are ignoring time, there
                    // appears to be one extra attribute which is not
                    // really there.  
                    // Below is added to add time information to the
                    // cluster info. Again; useful for relational
                    // implementations. 
                    bestLabel.setMidtime(events.elAt(k).getMidtime()); 
                    bestLabel.setDuration(events.elAt(k).getDuration()); 
                    if(inclEvent){
                        bestLabel.setOrigEvent(events.elAt(k));                         
                    }
                    if(bestLabel.getConf() > minConf){
                        slev.add(bestLabel); 
                    }
                }
            }
            sevv.add(slev); 
        }
        retval.setStreamLabelEventVec(sevv); 
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
            ClusterVecI[] cvi = ec.getClusters(csevi);
            System.out.println(cvi.toString()); 
            System.out.println("Printing cluster -> values mapping ... "); 
            System.out.println(ec.getMapping());
            System.out.println("And the other way now ..."); 
            for(int i=0; i < cvi.length; i++){
                System.out.println(cvi[i].toString()); 
                
            }
            System.out.println("..... Drumroll please ... Labelling info"); 
            EventLabeller a = new EventLabeller(d, cvi); 
            System.out.println(a.label(csvi, csevi, ee.getDescription(),0).toString()); 
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
