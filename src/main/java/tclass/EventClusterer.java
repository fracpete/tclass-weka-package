/**
  * The algorithm applies a clustering algorithm to each type of
  * PEP. 
  * The input is a vector of M streams, each stream having N Event
  * Vectors.
  * The output is a vector of N cluster vectors. 
  * 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EventClusterer.java,v 1.2 2002/08/02 04:28:38 waleed Exp $
  */

package tclass;   

import java.util.*; 
import tclass.util.*; 
import java.io.*; 


public class EventClusterer {
    
    private Vector clusterAlgs = new Vector();
    private StringMap caNames = new StringMap(); 
    private AttDescVec atts; 
    private ClusterVecI[] clusters; 
    private EventDescVecI evDesc;
    private DomDesc domDesc; 
    private boolean ignoreTime; 

    public EventClusterer(DomDesc d, EventDescVecI edvi){
        domDesc = d; 
        evDesc = edvi; 
    }

    /**
     * Clone the current object. 
     *
     */ 

    public Object clone()
    {
        EventClusterer retval = new EventClusterer(domDesc, evDesc);
        if(clusterAlgs != null){
            retval.clusterAlgs = (Vector) clusterAlgs.clone(); 
        }
        if(caNames != null){
            retval.caNames = (StringMap) caNames.clone(); 
        }
        if(clusters != null){
            retval.clusters = (ClusterVecI[]) clusters.clone(); 
        }
        if(atts != null){
            retval.atts = (AttDescVec) atts.clone(); 
        }
        return retval; 
    }

    public EventClusterer(String structure, DomDesc d, EventDescVecI edvi)
        throws FileFormatException, InvalidParameterException, IOException {
        StreamTokenizer st = new StreamTokenizer(new StringReader(structure));
        domDesc = d; 
        evDesc = edvi; 
        parseStructure(st); 
    }
    
    public EventClusterer(StreamTokenizer st, DomDesc d, EventDescVecI edvi) 
        throws FileFormatException, InvalidParameterException, IOException {
        domDesc = d; 
        evDesc = edvi; 
        parseStructure(st); 
    }
    
    public EventClusterer(StreamTokenizer st, DomDesc d) 
        throws FileFormatException, InvalidParameterException, IOException {
        domDesc = d; 
        parseStructure(st); 
    }

    public void setEventDescVec(EventDescVecI edvi){
        evDesc = edvi; 
    }

    /* Remember, this bit of architecture description is meant to look
     * something like: 
     *  segmentation {
     *   segmenter <name> <type> {
     *     <parameter> <value>
     *     ..
     *     <parameter> <value>
     *    }
     *   clusterer <name> <type> {
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
        ClusterAlgMgr camgr = ClusterAlgMgr.getInstance(); 
        while(st.ttype != '}' && st.ttype != st.TT_EOF ){ // i.e. while there's still more ... 
            if(st.ttype == st.TT_WORD && st.sval.equals("segmenter")){
                //So we have a global declaration. 
                st.nextToken(); 
                String caName = st.sval;
                // The name of the global extractor
                st.nextToken(); 
                String caType = st.sval; 
                // The type of the global extractor
                // Now we can create it. 
                ClusterAlgI clustAlg = camgr.getClone(caType, domDesc, evDesc); 
                // Check the return value. 
                if(clustAlg == null){
                    throw new FileFormatException("ArchDesc", st, "an existent Clustering Algorithm."); 
    
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
                    clustAlg.setParam(param, value); 
                    st.nextToken();
                }
                // And finally, add to the database. 
                addClustAlg(caName, clustAlg); 
            }
            else {
                throw new FileFormatException("ArchDesc", st, "pep"); 
            }
            st.nextToken(); 
        }
    }

    public void addClustAlg(String name, ClusterAlgI clustAlg){
        clusterAlgs.addElement(clustAlg); 
        caNames.add(name); 
    }

    public ClusterAlgI caAt(int i){
        return (ClusterAlgI) clusterAlgs.elementAt(i); 
    }

    public AttDescVecI getDescription(){
        // Come back to this one later. 
        // Actually, not that hard. 
        return atts; 
    }

    public String printAllData(ClassStreamEventsVecI csvi, boolean classLabels){
        // Ok, should be easy. 
        // Each algorithm takes 
        StreamEventsVecI sevi = csvi.getStreamEventsVec(); 
        EventDescVecI edvi = sevi.getEventDescVec(); 
        ClassificationVecI cvi = csvi.getClassVec(); 
        int numChannels = edvi.size(); 
        int numInstances = sevi.size();
        ClassDescVecI cdv = domDesc.getClassDescVec(); 
        Vector[] classList = new Vector[numChannels]; 
        if(classLabels){
            for(int i=0; i < numChannels; i++){
                classList[i] = new Vector(); 
            }
        }
        EventVec[] eventsByPEP = new EventVec[numChannels]; 
        for(int i=0; i < numChannels; i++){
            eventsByPEP[i] = new EventVec(); 
        }
        for(int i=0; i < numInstances; i++){
            StreamEventsI currentStream = sevi.elAt(i); 
            String currentClass = cdv.getClassLabel(cvi.elAt(i).getRealClass()); 
            for(int j=0; j < numChannels; j++){
                EventVec currEv = (EventVec) currentStream.getEvents(j); 
                eventsByPEP[j].add(currEv); 
                if(classLabels){
                    int numEvents = currEv.size(); 
                    for(int k=0; k < numEvents; k++){
                        classList[j].add(currentClass); 
                    }
                }
            }
        }
        // Ok, now all the data is in place. Pretty print!! 
        StringBuffer retval = new StringBuffer(); 
        for(int i=0; i < numChannels; i++){
            retval.append("-- " + edvi.elName(i) + " --\n"); 
            EventDescI edi = edvi.elAt(i); 
            int numParams = edi.numParams();
            for(int j=0; j < numParams; j++){
                retval.append(edi.paramName(j) + "\t"); 
            }
            if(classLabels){
                retval.append("class"); 
            }
            retval.append("\n"); 
            int numEvents = eventsByPEP[i].size(); 
            for(int j=0; j < numEvents; j++){
                for(int k=0; k < numParams; k++){
                    retval.append(eventsByPEP[i].elAt(j).valOf(k) + "\t"); 
                }
                if(classLabels){
                    retval.append(classList[i].elementAt(j)); 
                }
                retval.append("\n");
            }
        }
        return retval.toString(); 
    }
        
    public ClusterVecI clusterEvents(ClassStreamEventsVecI csvi){
        // Ok, should be easy. 
        // Each algorithm takes 
        int numCAs = clusterAlgs.size(); 
        clusters = new ClusterVecI[numCAs]; 
        DataTypeMgr dtm = DataTypeMgr.getInstance(); 
        for(int i=0; i < numCAs; i++){
            try {
                if(ignoreTime){
                    System.out.println("Setting ignoreTime ... "); 
                    caAt(i).setParam("ignoretime", "true"); 
                }
            }
            catch(Exception e){
                System.err.println("WARNING! WARNING! WARNING! IgnoreTime not supported"); 
            }
            clusters[i] = caAt(i).cluster(csvi); 
        }
        // Now create the attribute description: 
        atts = new AttDescVec(); 
        for(int i=0; i < numCAs; i++){
            ClusterVecI theseClusters = clusters[i]; 
            

            int numClusters = theseClusters.size(); 

            
            for(int j=0; j < numClusters; j++){
                // For now, we'll assume that all clustering outcomes
                // are continuous. If this turns out to be fixable,
                // we'll fix it later. 

                String name = caNames.getString(i) + "_" + j;
                clusters[i].elAt(j).setName(name); 
                DataTypeI dt = dtm.getClone("continuous"); 
                atts.add(new AttDesc(name, dt)); 
            }
        }
        // And now we flatten all the vectors into one. 
        return (ClusterVecI) new ClusterVec(clusters);  
    }
    
    
    // Ok, slightly different code from above for getting the clusters as an array

    public ClusterVecI[] getClusters(ClassStreamEventsVecI csvi){
        // Ok, should be easy. 
        // Each algorithm takes 
        int numCAs = clusterAlgs.size(); 
        clusters = new ClusterVecI[numCAs]; 
        DataTypeMgr dtm = DataTypeMgr.getInstance(); 
        for(int i=0; i < numCAs; i++){
            try {
                if(ignoreTime){
                    // System.out.println("Setting ignoreTime ... "); 
                    caAt(i).setParam("ignoretime", "true"); 
                }
            }
            catch(Exception e){
                System.err.println("WARNING! WARNING! WARNING! IgnoreTime not supported"); 
            }
            
            clusters[i] = caAt(i).cluster(csvi); 
        }
        // Now create the attribute description: 
        atts = new AttDescVec(); 
        for(int i=0; i < numCAs; i++){
            ClusterVecI theseClusters = clusters[i]; 
            int numClusters = theseClusters.size(); 
            
            for(int j=0; j < numClusters; j++){
                // For now, we'll assume that all clustering outcomes
                // are continuous. If this turns out to be fixable,
                // we'll fix it later. 

                String name = caNames.getString(i) + "_" + j;
                clusters[i].elAt(j).setName(name); 
                DataTypeI dt = dtm.getClone("continuous"); 
                atts.add(new AttDesc(name, dt)); 
            }
        }
        return clusters;  
    }

    public String getMapping(){
        // Prints a mapping from cluster names to descriptions. 
        StringBuffer sb = new StringBuffer(); 
        int numCAs = clusters.length; 
        for(int i=0; i < numCAs; i++){
            ClusterVecI theseClusters = clusters[i]; 
            int numClusters = theseClusters.size(); 
            for(int j=0; j < numClusters; j++){
                // For now, we'll assume that all clustering outcomes
                // are continuous. If this turns out to be fixable,
                sb.append(caNames.getString(i) + "_" + j); 
                sb.append(": " + theseClusters.elAt(j).getDescription()); 
            }
        }
        return sb.toString(); 
    }

    // Ok ... now main. 
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
            System.out.println(ec.clusterEvents(csevi)); 
            System.out.println("Printing cluster -> values mapping ... "); 
            System.out.println(ec.getMapping()); 
        }
        else {
            //How do I debug thee?
            //Let's see ... first let's load a domain description:
            DomDesc d = new DomDesc("sl.tdd"); 
            // And now some data ...
            ClassStreamVecI csvi = (ClassStreamVecI) new ClassStreamVec("sl2.tsl", d);
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
            System.out.println(ec.clusterEvents(csevi)); 
            System.out.println("Printing cluster -> values mapping ... "); 
            System.out.println(ec.getMapping()); 
        }
    }
    public void forceIgnoreTime(){
        ignoreTime = true; 
    }
}
