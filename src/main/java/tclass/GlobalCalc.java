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
  * Given a set of global extractors, applies them to a set of streams. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: GlobalCalc.java,v 1.2 2002/08/02 04:28:38 waleed Exp $
  */

package tclass;   
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;

import tclass.util.Debug;
import tclass.util.StringMap;

public class GlobalCalc {

    private Vector extractors = new Vector(); 
    private StringMap extractorNames = new StringMap(); 
    private DomDesc domDesc; 
    
    public GlobalCalc(DomDesc d){
        domDesc = d; 
    }

    public GlobalCalc(String structure, DomDesc d)
        throws FileFormatException, InvalidParameterException, IOException {
        StreamTokenizer st = new StreamTokenizer(new StringReader(structure));
        domDesc = d; 
        parseStructure(st); 
    }

    public GlobalCalc(StreamTokenizer st, DomDesc d) 
        throws FileFormatException, InvalidParameterException, IOException {
        domDesc = d; 
        parseStructure(st); 
    }

    /* Remember, this bit of architecture description is meant to look
     * something like: 
     * globalcalc {
     *   global <name> <type> {
     *     <parameter> <value>
     *     ..
     *     <parameter> <value>
     *    }
     *   global <name> <type> {
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
        GlobalExtrMgr gem = GlobalExtrMgr.getInstance(); 
        while(st.ttype != '}' && st.ttype != st.TT_EOF ){ // i.e. while there's still more ... 
            if(st.ttype == st.TT_WORD && st.sval.equals("global")){
                //So we have a global declaration. 
                st.nextToken(); 
                String geName = st.sval;
                // The name of the global extractor
                st.nextToken(); 
                String geType = st.sval; 
                // The type of the global extractor
                // Now we can create it. 
                GlobalExtractorI ge = gem.getClone(geType, domDesc); 
                // Check the return value. 
                if(ge == null){
                    throw new FileFormatException("ArchDesc", st, "an existent global extractor."); 
    
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
                    ge.setParam(param, value); 
                    st.nextToken();
                }
                // And finally, add to the database. 
                addExtractor(geName, ge); 
            }
            else {
                throw new FileFormatException("ArchDesc", st, "global"); 
            }
            st.nextToken(); 
        }
    }

    /** 
     * Add a global extractor to this global calculator. 
     * @param gex The extractor to be added. By this time, the extractor
     * should have all of its parameters to set. 
     * 
     */ 

    public void addExtractor(String name, GlobalExtractorI gex){
        extractors.addElement(gex); 
        extractorNames.add(name); 
    }

    public GlobalExtractorI extractorAt(int i){
        return (GlobalExtractorI) extractors.elementAt(i); 
    }

    /**
     * Applies this set of global extractors to the Stream vector given.
     * 
     * @param sv The stream vector that we wish to apply the particular 
     * global extractors to. 
     *
     * @return A StreamAttValVecI that contains the result of the
     * application of the global features.  
     */ 

    public ClassStreamAttValVecI applyGlobals(ClassStreamVecI csv){
        int numVecs = csv.size(); 
        int numExtr = numExtractors(); 
        StreamVecI svi = csv.getStreamVec(); 
        ClassStreamAttValVecI csavv = new ClassStreamAttValVec(); 
        
        csavv.setClassVec(csv.getClassVec()); 
        // And now, let's make the StreamAttValVec. 
        StreamAttValVec savv = new StreamAttValVec(); 
        savv.setDescription(getDescription());
        for(int i=0; i<numVecs; i++){
            StreamAttVal sav = new StreamAttVal(numExtr); 
            for(int j=0; j<numExtr; j++){
                sav.setAtt(j, extractorAt(j).extract(svi.elAt(i))); 
            }
            savv.add((StreamAttValI) sav); 
        }
        csavv.setStreamAttValVec((StreamAttValVecI) savv); 
        return csavv; 
    } 

    public int numExtractors(){
        return extractors.size(); 
    }

    /**
     * Gets a description of the data returned by these globals. 
     */ 
    public AttDescVecI getDescription(){
        AttDescVec retval = new AttDescVec(); 
        // And now we iterate through the loop. 
        int numExtractors = extractorNames.size(); 
        for(int i=0; i < numExtractors; i++){
            String attname = extractorNames.getString(i); 
            DataTypeI dt = extractorAt(i).getDataType(); 
            AttDesc ad = new AttDesc(attname, dt); 
            retval.add(ad); 
        }
        return (AttDescVecI) retval; 
    }

    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            //How do I debug thee?
            Debug.setDebugLevel(Debug.EVERYTHING); 
            //Let's see ... first let's load a domain description:
            DomDesc d = new DomDesc("tests/test.tdd"); 
            // And now some data ...
            ClassStreamVecI csvi = (ClassStreamVecI) new ClassStreamVec("tests/test.tsl", d);
            GlobalCalc gc = new GlobalCalc(new StreamTokenizer(
                                                               new FileReader("tests/test._gc")), d);
            System.out.println("---%%%-- Results ---%%%---"); 
            System.out.println(gc.getDescription().toString()); 
            System.out.println(gc.applyGlobals(csvi).toString());
        }
        //Ok, option 2. 
        else {
            //How do I debug thee?
            Debug.setDebugLevel(Debug.FN_PARAMS); 
            //Let's see ... first let's load a domain description:
            DomDesc d = new DomDesc("sl.tdd"); 
            // And now some data ...
            ClassStreamVecI csvi = (ClassStreamVecI) new ClassStreamVec("sl.tsl", d);
            GlobalCalc gc = new GlobalCalc(new StreamTokenizer(
                                                               new FileReader("test._gc")), d);
            System.out.println("---%%%-- Results ---%%%---"); 
            System.out.println(gc.getDescription().toString()); 
            System.out.println(gc.applyGlobals(csvi).toString());
        }
 
    }
}
