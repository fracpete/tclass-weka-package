
/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */


/**
 * 
 *
 *  Single classifier solution. That is to say, we cluster all the instances
 *  using the same clustering algorithms. 
 * 
 * 
 * @author Waleed Kadous
 * @version $Id: TClass.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;  

import java.util.StringTokenizer;

import tclass.clusteralg.GClust;
import tclass.util.Debug;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.AttributeFilter;

public class TClass {
    // Ok. What we are going to do is to separate the learning task in 
    // an interesting way. 
    // First of all, though, the standard stuff
    
    String domDescFile = "tclass.tdd"; 
    String trainDataFile = "tclass.tsl"; 
    String testDataFile = "tclass.ttl"; 
    String settingsFile = "tclass.tal"; 
    String learnerStuff = "weka.classifiers.j48.J48"; 
    boolean featureSel = false; 
    boolean makeDesc = false; 
    boolean trainResults = false; 
    void parseArgs(String[] args){
        for(int i=0; i < args.length; i++){
            if(args[i].equals("-tr")){
                trainDataFile = args[++i]; 
            }
            if(args[i].equals("-dd")){
                domDescFile = args[++i]; 
            }
            if(args[i].equals("-te")){
                testDataFile = args[++i];
            }
            if(args[i].equals("-s")){
                settingsFile = args[++i]; 
            }
            if(args[i].equals("-fs")){
                featureSel = true; 
            }
            if(args[i].equals("-md")){
                makeDesc = true; 
            }
            if(args[i].equals("-trainres")){
                trainResults = true; 
            }
            if(args[i].equals("-l")){
                learnerStuff = args[++i]; 
                learnerStuff = learnerStuff.replace(':', ' '); 
                System.err.println("Learner String is: " + learnerStuff); 
            }
        }
    }

    // Alright. This is downright funky hacky stuff. 
    
    public static void main(String[] args) throws Exception {
        Debug.setDebugLevel(Debug.PROGRESS); 
        TClass thisExp = new TClass(); 
        thisExp.parseArgs(args); 
        DomDesc domDesc = new DomDesc(thisExp.domDescFile); 
        ClassStreamVecI trainStreamData = new
            ClassStreamVec(thisExp.trainDataFile, domDesc); 
        ClassStreamVecI testStreamData = new
            ClassStreamVec(thisExp.testDataFile, domDesc); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Data read in");  
        Settings settings = new Settings(thisExp.settingsFile, domDesc); 
        

        EventExtractor evExtractor = settings.getEventExtractor(); 
        // Global data is likely to be included in every model; so we
        // might as well calculated now
        GlobalCalc globalCalc = settings.getGlobalCalc(); 

        ClassStreamAttValVecI trainGlobalData =
            globalCalc.applyGlobals(trainStreamData);
        ClassStreamAttValVecI testGlobalData =
            globalCalc.applyGlobals(testStreamData);
        // And we might as well extract the events. 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Globals calculated.");  
        Debug.dp(Debug.PROGRESS, "Train: " + trainGlobalData.size() +
                 " Test: " + testGlobalData.size());  


        ClassStreamEventsVecI trainEventData =
            evExtractor.extractEvents(trainStreamData); 
        ClassStreamEventsVecI testEventData =
            evExtractor.extractEvents(testStreamData); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Events extracted");  
        // System.out.println(trainEventData.toString()); 


        // Now we want the clustering algorithms only to cluster
        // instances of each class. Make an array of clusterers, 
        // one per class. 

        int numTestStreams = testEventData.size(); 

        int numClasses = domDesc.getClassDescVec().size(); 
        EventDescVecI eventDescVec = evExtractor.getDescription(); 
        EventClusterer eventClusterer = settings.getEventClusterer(); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: Data rearranged.");  


        //And now load it up. 
        StreamEventsVecI trainEventSEV =
            trainEventData.getStreamEventsVec(); 
        ClassificationVecI trainEventCV = trainEventData.getClassVec();
        int numTrainStreams = trainEventCV.size(); 
        ClusterVecI clusters = eventClusterer.clusterEvents(trainEventData); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: Clustering complete"); 
        Debug.dp(Debug.PROGRESS, "Clusters are:"); 
        Debug.dp(Debug.PROGRESS, "\n" + eventClusterer.getMapping()); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: Clustering complete. ");  

        // But wait! There's more! There is always more. 
        // The first thing was only useful for clustering. 
        // Now attribution. We want to attribute all the data. So we are going 
        // to have one dataset for each learner. 
        // First set up the attributors. 

        Attributor attribs = new Attributor(domDesc, clusters, 
                                            eventClusterer.getDescription()); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: AttributorMkr complete."); 
        

        ClassStreamAttValVecI trainEventAtts =attribs.attribute(trainStreamData, trainEventData); 
        ClassStreamAttValVecI testEventAtts = attribs.attribute(testStreamData,
                                                    testEventData); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: Attribution complete."); 


        // Combine all data sources. For now, globals go in every
        // one. 

        Combiner c = new Combiner(); 
        ClassStreamAttValVecI trainAtts = c.combine(trainGlobalData,
                                            trainEventAtts); 

        ClassStreamAttValVecI testAtts = c.combine(testGlobalData,
                                           testEventAtts); 

        trainStreamData = null; 
        testStreamData = null; 
        trainEventSEV = null; 
        trainEventCV = null; 
        if(!thisExp.makeDesc){
            clusters = null; 
            eventClusterer = null; 
        }
        attribs = null; 

        System.gc(); 


        // So now we have the raw data in the correct form for each
        // attributor. 
        // And now, we can construct a learner for each case. 
        // Well, for now, I'm going to do something completely crazy. 
        // Let's run each classifier nonetheless over the whole data
        // ... and see what the hell happens. Maybe some voting scheme 
        // is possible!! This is a strange form of ensemble
        // classifier. 
        // Each naive bayes algorithm only gets one 

        Debug.setDebugLevel(Debug.PROGRESS); 
        int[] selectedIndices = null;  
        String [] classifierSpec = Utils.splitOptions(thisExp.learnerStuff);
        if (classifierSpec.length == 0) {
            throw new Exception("Invalid classifier specification string");
        }        
        String classifierName = classifierSpec[0];
        classifierSpec[0] = "";
        Classifier learner = Classifier.forName(classifierName, classifierSpec);
        Debug.dp(Debug.PROGRESS, "PROGRESS: Beginning format conversion for class "); 
        Instances  data = WekaBridge.makeInstances(trainAtts, "Train ");
        Debug.dp(Debug.PROGRESS, "PROGRESS: Conversion complete. Starting learning");    

        if(thisExp.featureSel){
                Debug.dp(Debug.PROGRESS, "PROGRESS: Doing feature selection");    
                BestFirst bfs = new BestFirst();
                CfsSubsetEval cfs = new CfsSubsetEval(); 
                cfs.buildEvaluator(data); 
                selectedIndices = bfs.search(cfs, data); 
                // Now extract the features. 
                System.err.print("Selected features: ");
                String featureString = new String(); 
                for(int j=0; j < selectedIndices.length; j++){
                    featureString += (selectedIndices[j] +1)+ ",";
                }
                featureString += ("last"); 
                System.err.println(featureString); 
               // Now apply the filter. 
                AttributeFilter af = new AttributeFilter(); 
                af.setInvertSelection(true); 
                af.setAttributeIndices(featureString); 
                af.inputFormat(data); 
                data = af.useFilter(data, af); 
            }
        learner.buildClassifier(data); 
        Debug.dp(Debug.PROGRESS, "Learnt classifier: \n" + learner.toString()); 
        
        WekaClassifier wekaClassifier; 
        wekaClassifier = new WekaClassifier(learner); 

        if(thisExp.makeDesc){
            // Section for making description more readable. Assumes that 
            // learner.toString() returns a string with things that look like 
            // feature names. 
            String concept = learner.toString(); 
            StringTokenizer st = new StringTokenizer(concept, " \t\r\n", true);
            while (st.hasMoreTokens()) {
                boolean appendColon = false; 
                String curTok = st.nextToken(); 
                GClust clust = (GClust) ((ClusterVec) clusters).elCalled(curTok);
                if(clust != null){
                    // Skip the spaces
                    st.nextToken(); 
                    // Get a < or >
                    String cmp = st.nextToken(); 
                    String qual = ""; 
                    if(cmp.equals("<=")){
                        qual = " HAS NO "; 
                    }
                    else {
                        qual = " HAS "; 
                    }
                    // skip spaces
                    st.nextToken(); 
                    // Get the number. 
                    String conf = st.nextToken(); 
                    if(conf.endsWith(":")){
                        conf = conf.substring(0, conf.length()-1); 
                        appendColon = true; 
                    }
                    float minconf = Float.valueOf(conf).floatValue(); 
                    EventI[] res = clust.getBounds(minconf);
                    String name = clust.getName(); 
                    int dashPos = name.indexOf('-'); 
                    int undPos = name.indexOf('_'); 
                    String chan = name.substring(0, dashPos);
                    String evType = name.substring(dashPos+1, undPos); 
                    EventDescI edi = clust.eventDesc(); 
                    System.out.print("Channel " + chan + qual + evType + " "); 
                    int numParams = edi.numParams(); 
                    for(int i=0; i < numParams; i++){
                        System.out.print(edi.paramName(i) + " in [" + res[0].valOf(i) + "," + res[1].valOf(i) + "] "); 
                    }
                    if(appendColon){
                        System.out.print(":"); 
                    }
                }
                else {
                    System.out.print(curTok);
                }
            }
 

            // Now this is going to be messy as fuck. Really. What do we needs? Well, 
            // we need to read in the data; look up some info, that we 
            // assume came from a GainClusterer ... 
            // Sanity check. 
            //            GClust clust =  (GClust) ((ClusterVec) clusters).elCalled("alpha-inc_0"); 
            // System.out.println("INSANE!: " + clust.getDescription()); 
            // EventI[] res = clust.getBounds(1); 
            // System.out.println("For clust settings: min event = " + res[0].toString() + " and max event = " + res[1].toString()); 
        }
        Debug.dp(Debug.PROGRESS, "PROGRESS: Learning complete. ");  
        int numCorrect = 0; 
        ClassificationVecI classns; 
        if(thisExp.trainResults){
            System.err.println(">>> Training performance <<<"); 
            classns = (ClassificationVecI) trainAtts.getClassVec().clone();
            for(int j=0; j < numTrainStreams; j++){
                wekaClassifier.classify(data.instance(j), classns.elAt(j));
            }
            for(int j=0; j < numTrainStreams; j++){
                // System.out.print(classns.elAt(j).toString()); 
                if(classns.elAt(j).getRealClass() == classns.elAt(j).getPredictedClass()){
                    numCorrect++; 
                    String realClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getRealClass());                
                    System.err.println("Class " + realClassName + " CORRECTLY classified."); 
                    
                }
                else {
                    
                    String realClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getRealClass());
                    String predictedClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getPredictedClass());
                                System.err.println("Class " + realClassName + " INCORRECTLY classified as " + predictedClassName + "."); 
                                
                }
            }
            System.err.println("Training results for classifier: " + numCorrect + " of " + numTrainStreams + " (" + 
                               numCorrect*100.0/numTrainStreams + "%)"); 
        }
            
        System.err.println(">>> Testing stage <<<"); 
        // First, print the results of using the straight testers. 
        classns = (ClassificationVecI) testAtts.getClassVec().clone();
        StreamAttValVecI savvi = testAtts.getStreamAttValVec(); 
        data = WekaBridge.makeInstances(testAtts, "Test "); 
        if(thisExp.featureSel){
            String featureString = new String(); 
            for(int j=0; j < selectedIndices.length; j++){
                featureString += (selectedIndices[j]+1) + ",";
            }
            featureString += "last"; 
            // Now apply the filter. 
            AttributeFilter af = new AttributeFilter(); 
            af.setInvertSelection(true); 
            af.setAttributeIndices(featureString); 
            af.inputFormat(data); 
            data = af.useFilter(data, af); 
        }
        for(int j=0; j < numTestStreams; j++){
            wekaClassifier.classify(data.instance(j), classns.elAt(j));
        }
        System.err.println(">>> Learner <<<"); 
        numCorrect = 0; 
        for(int j=0; j < numTestStreams; j++){
            // System.out.print(classns.elAt(j).toString()); 
            if(classns.elAt(j).getRealClass() == classns.elAt(j).getPredictedClass()){
                numCorrect++; 
                String realClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getRealClass());                
                System.err.println("Class " + realClassName + " CORRECTLY classified."); 

            }
            else {

                String realClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getRealClass());
                String predictedClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getPredictedClass());
                

                                System.err.println("Class " + realClassName + " INCORRECTLY classified as " + predictedClassName + "."); 

            }
        }
            System.err.println("Test accuracy for classifier: " + numCorrect + " of " + numTestStreams + " (" + 
                               numCorrect*100.0/numTestStreams + "%)"); 
            
    }
    
}
