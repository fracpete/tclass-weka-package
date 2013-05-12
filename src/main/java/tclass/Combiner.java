/**
  * Combines input from the global and attributor stages. This shouldn't be
  * too complicated. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Combiner.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import tclass.util.*; 
import java.io.*; 


public class Combiner {

    public ClassStreamAttValVecI combine(ClassStreamAttValVecI globals, 
                                         ClassStreamAttValVecI synthetics){

    
        // Let's make sure the data hasn't been mangled. 

        Debug.myassert(globals.getClassVec() == synthetics.getClassVec(), 
                     "WARNING: Global and Synthetic classes are not identical!"); 


        ClassStreamAttValVec retval = new ClassStreamAttValVec(); 
    
        // Easy part: set the classification.
        retval.setClassVec(globals.getClassVec()); 

        //Ugly part: Combining the data sources. 
        //First, make the attribute description vector. 
        StreamAttValVecI globalSAVV = globals.getStreamAttValVec(); 
        StreamAttValVecI synthSAVV = synthetics.getStreamAttValVec(); 

        Debug.myassert(globalSAVV.size() == synthSAVV.size(),
                     "WARNING: Global and Synthetic data are not the same size!!"); 

        AttDescVecI globalDesc =  globalSAVV.getDescription(); 
        AttDescVecI synthDesc = synthSAVV.getDescription(); 
        AttDescVec newAdv = new AttDescVec(); 
        newAdv.add(globalDesc); 
        newAdv.add(synthDesc); 
        int numGlobal = globalDesc.size(); 
        int numSynth = synthDesc.size(); 

        StreamAttValVec savv =  new StreamAttValVec(); 
        savv.setDescription(newAdv); 

        int numStreams = globalSAVV.size(); 
        for(int i=0; i < numStreams; i++){
            StreamAttValI globalSAV = globalSAVV.elAt(i); 
            StreamAttValI synthSAV = synthSAVV.elAt(i); 
            MyStreamAttVal newSAV =  new MyStreamAttVal(globalSAV, numGlobal, synthSAV, numSynth); 
            savv.add(newSAV); 
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
            ClassStreamAttValVecI synthOut = a.attribute(csvi, csevi);
            System.out.println(" --- %%% --- Synth Attributes --- %%% ---"); 
            System.out.println(synthOut.toString()); 
            GlobalCalc gc = new GlobalCalc(new StreamTokenizer(
                                                               new FileReader("tests/test._gc")), d);
            System.out.println(gc.getDescription().toString()); 
            System.out.println(" --- %%% --- Glob Attributes --- %%% ---"); 
            ClassStreamAttValVecI globOut = gc.applyGlobals(csvi); 
            System.out.println(globOut.toString());
            System.out.println(" --- %%% --- Combined --- %%% ---");
            Combiner c = new Combiner(); 
            ClassStreamAttValVecI combOut = c.combine(globOut, synthOut); 
            System.out.println(combOut.toString());
    
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

            ClassStreamAttValVecI synthOut = a.attribute(csvi, csevi);
            System.out.println(" --- %%% --- Synth Attributes --- %%% ---"); 
            System.out.println(synthOut.toString()); 
            GlobalCalc gc = new GlobalCalc(new StreamTokenizer(
                                                               new FileReader("test._gc")), d);
            System.out.println(gc.getDescription().toString()); 
            System.out.println(" --- %%% --- Glob Attributes --- %%% ---"); 
            ClassStreamAttValVecI globOut = gc.applyGlobals(csvi); 
            System.out.println(globOut.toString());
            System.out.println(" --- %%% --- Combined --- %%% ---");
            Combiner c = new Combiner(); 
            ClassStreamAttValVecI combOut = c.combine(globOut, synthOut); 
            System.out.println(combOut.toString());
    
        }
    }    
    
}

class MyStreamAttVal implements StreamAttValI {
    private StreamAttValI globalSAV; 
    private StreamAttValI synthSAV; 
    private int numGlobal; 
    private int numSynth; 
    private int numTotal; 

    // Note: Globals always go first. 
    MyStreamAttVal(StreamAttValI globalSAV, int numGlobal, 
                   StreamAttValI synthSAV, int numSynth){

        this.globalSAV = globalSAV; 
        this.synthSAV = synthSAV; 
        this.numGlobal = numGlobal; 
        this.numSynth = numSynth; 
        numTotal = numGlobal + numSynth; 
    }

    public float getAtt(int att){
        if(att < 0){
            throw new ArrayIndexOutOfBoundsException(att); 
        }
        else if(att < numGlobal){
            return globalSAV.getAtt(att); 
        }
        else if(att < numTotal){
            return synthSAV.getAtt(att-numGlobal); 
        }
        else {
                     
            throw new ArrayIndexOutOfBoundsException(att); 
        }
    }
    public void setAtt(int att, float val){
        if(att < 0){
            throw new ArrayIndexOutOfBoundsException(att); 
        }
        else if(att < numGlobal){
            globalSAV.setAtt(att, val); 
        }
        else if(att < numTotal){
            synthSAV.setAtt(att-numGlobal, val); 
        }
        else {
            throw new ArrayIndexOutOfBoundsException(att); 
        }
    }

    public String toString(){
        return "Globals: " + globalSAV.toString() + " Synths: " + synthSAV.toString();
    }
 
    
}
    
