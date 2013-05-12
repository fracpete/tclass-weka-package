/**
  * A class that provides the Event Extraction, Global Calculation, and Event clustering
  * settings. Why did I postpone working on this for so long when it's so simple? 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Settings.java,v 1.2 2002/08/02 04:28:38 waleed Exp $
  */

package tclass;   
import java.io.*; 
import java.util.*; 
import tclass.util.*; 

public class Settings {
    EventExtractor ee; 
    GlobalCalc gc; 
    EventClusterer ec; 
    DomDesc domDesc; 
   
    public Settings(String filename, DomDesc dd)
        throws FileFormatException, InvalidParameterException, IOException {
        StreamTokenizer st = new StreamTokenizer(new FileReader(filename));
        domDesc = dd; 
        parseStructure(st); 
    }
    
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
        st.nextToken(); 
        while(st.ttype != st.TT_EOF){
            if(st.ttype != st.TT_WORD){
                throw new FileFormatException("ArchDesc", st, "globalcalc, segmentation, metafeatures");                 
            }
            if(st.sval.equals("globalcalc")){
                gc = new GlobalCalc(st,domDesc); 
            }
            else if(st.sval.equals("segmentation")){
                if(ee == null){
                    throw new FileFormatException("ArchDesc", st, "metafeatures first");                 }
                ec =new EventClusterer(st,domDesc, ee.getDescription()); 
            }
            else if(st.sval.equals("metafeatures")){
                ee = new EventExtractor(st,domDesc); 
            }
            else {
                throw new FileFormatException("ArchDesc", st, "globalcalc, segmentation, metafeatures");                 

            }
            st.nextToken(); 
        }
    }
    
    /**
       * Get the value of ee.
       * @return Value of ee.
       */
    public EventExtractor getEventExtractor() {return ee;}
    
    /**
       * Set the value of ee.
       * @param v  Value to assign to ee.
       */
    public void setEventExtractor(EventExtractor  v) {this.ee = v;}
    
    
    
    /**
       * Get the value of gc.
       * @return Value of gc.
       */
    public GlobalCalc getGlobalCalc() {return gc;}
    
    /**
       * Set the value of gc.
       * @param v  Value to assign to gc.
       */
    public void setGlobalCalc(GlobalCalc  v) {this.gc = v;}
    
    
    
    /**
       * Get the value of ec.
       * @return Value of ec.
       */
    public EventClusterer getEventClusterer() {return (EventClusterer) ec.clone();}
    
    /**
       * Set the value of ec.
       * @param v  Value to assign to ec.
       */
    public void setEventClusterer(EventClusterer  v) {this.ec = v;}
    
    public static void main(String[] args) throws Exception {
        DomDesc d = new DomDesc("tests/test.tdd"); 
        Settings s = new Settings("tests/test.tal", d); 
        ClassStreamVecI csvi = (ClassStreamVecI) new ClassStreamVec("tests/test.tsl", d);
        System.out.println("Test of Settings System");
        System.out.println(s.getGlobalCalc().toString()); 
        GlobalCalc gc = s.getGlobalCalc();
        System.out.println(gc.getDescription().toString()); 
        System.out.println(gc.applyGlobals(csvi).toString());
        System.out.println(s.getEventExtractor().toString()); 
        EventExtractor ee = s.getEventExtractor(); 
        System.out.println(ee.getDescription().toString()); 
        ClassStreamEventsVecI csevi = ee.extractEvents(csvi);
        System.out.println(csevi.toString());
        System.out.println(s.getEventClusterer().toString()); 
        EventClusterer ec = s.getEventClusterer(); 
        System.out.println(ec.clusterEvents(csevi)); 
        System.out.println("Printing cluster -> values mapping ... "); 
        System.out.println(ec.getMapping()); 
        
    }
}
