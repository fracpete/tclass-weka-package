/**
 *  Single classifier solution. 
 * 
 *  Superseded by ExpSingle
 * 
 * @author Waleed Kadous
 * @version $Id: ExpNB_Single.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import tclass.util.*; 
// import tclass.learnalg.*; 
import tclass.learnalg.*; 
import weka.attributeSelection.*; 
import weka.filters.*; 
import weka.core.*; 
import java.io.*; 

public class ExpNB_Single {
    // Ok. What we are going to do is to separate the learning task in 
    // an interesting way. 
    // First of all, though, the standard stuff
    
    String domDescFile = "sl.tdd"; 
    String trainDataFile = "sl.tsl"; 
    String testDataFile = "sl.ttl"; 
    // String globalDesc = "test._gc"; 
    // String evExtractDesc = "test._ee";
    String evClusterDesc = "test._ec"; 
    String settingsFile = "test.tal"; 
    boolean featureSel = false; 
    int numDivs = 10; 
    
    void parseArgs(String[] args){
        for(int i=0; i < args.length; i++){
            if(args[i].equals("-tr")){
                trainDataFile = args[++i]; 
            }
            if(args[i].equals("-te")){
                testDataFile = args[++i];
            }
            if(args[i].equals("-settings")){
                settingsFile = args[++i]; 
            }
            if(args[i].equals("-fs")){
                featureSel = true; 
            }
            if(args[i].equals("-numdivs")){
                numDivs = Integer.parseInt(args[++i]); 
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Debug.setDebugLevel(Debug.PROGRESS); 
        ExpSingle thisExp = new ExpSingle(); 
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
        Debug.dp(Debug.PROGRESS, eventClusterer.getMapping()); 
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
        eventClusterer = null; 
        trainEventSEV = null; 
        trainEventCV = null; 
        clusters = null; 
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
        Debug.dp(Debug.PROGRESS, "PROGRESS: Beginning format conversion for class "); 
        Instances  data = WekaBridge.makeInstances(trainAtts, "Train ");
        Debug.dp(Debug.PROGRESS, "PROGRESS: Conversion complete. Starting learning");    
        Debug.setDebugLevel(Debug.PROGRESS); 
        int[] selectedIndices = null;  
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
                featureString += selectedIndices[j] + ",";
            }
            featureString += ("last"); 
            System.err.println(featureString); 
            // Now cut from trainAtts. 
            // trainAtts.selectFeatures(selectedIndices); 
        }
        
        
        Debug.dp(Debug.PROGRESS, "Learning with Naive Bayes now ..."); 
        NaiveBayes nbLearner = new NaiveBayes(); 
        nbLearner.setDomDesc(domDesc);  
        nbLearner.setAttDescVec(trainAtts.getStreamAttValVec().getDescription()); 
        ClassifierI nbClassifier = nbLearner.learn(trainAtts); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: Learning complete. ");  

        System.out.println(">>> Testing stage <<<"); 
        // First, print the results of using the straight testers. 
        ClassificationVecI classns; 
        classns = (ClassificationVecI) testAtts.getClassVec().clone();
        StreamAttValVecI savvi = testAtts.getStreamAttValVec(); 
        /*
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
        */
        for(int j=0; j < numTestStreams; j++){
            nbClassifier.classify(savvi.elAt(j), classns.elAt(j));
        }
        System.out.println(">>> Learner <<<"); 
        int numCorrect = 0; 
        for(int j=0; j < numTestStreams; j++){
            System.out.print(classns.elAt(j).toString()); 
            if(classns.elAt(j).getRealClass() == classns.elAt(j).getPredictedClass()){
                numCorrect++; 
                String realClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getRealClass());                
                System.out.println("Class " + realClassName + " CORRECTLY classified."); 

            }
            else {

                String realClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getRealClass());
                String predictedClassName = domDesc.getClassDescVec().getClassLabel(classns.elAt(j).getPredictedClass());
                

                                System.out.println("Class " + realClassName + " INCORRECTLY classified as " + predictedClassName + "."); 

            }
        }
            System.out.println("Test accuracy for classifier: " + numCorrect + " of " + numTestStreams + " (" + 
                               numCorrect*100.0/numTestStreams + "%)"); 
            
    }
    
}
