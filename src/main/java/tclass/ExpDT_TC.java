/**
 * This is a "hack" class. It's just to help me test out some ideas. 
 * I'm running out of time for this conference and I'm really trying
 * to put together something stunning in terms of performance. 
 *
 * This one uses C4.5 with the same generation architecture. 
 *
 * @author Waleed Kadous
 * @version $Id: ExpDT_TC.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import tclass.util.*; 
import tclass.learnalg.*; 
import java.io.*; 


public class ExpDT_TC {
    // Ok. What we are going to do is to separate the learning task in 
    // an interesting way. 
    // First of all, though, the standard stuff
    
    String domDescFile = "sl.tdd"; 
    String trainDataFile = "sl.tsl"; 
    String testDataFile = "sl.ttl"; 
    String tmpPrefix = "/tmp/c45-sl-"; 
    String settingsFile = "test.tal"; 
    void parseArgs(String[] args){
        for(int i=0; i < args.length; i++){
            if(args[i].equals("-tr")){
                trainDataFile = args[++i]; 
            }
            if(args[i].equals("-te")){
                testDataFile = args[++i];
            }
            if(args[i].equals("-pr")){
                tmpPrefix = args[++i]; 
            }
            if(args[i].equals("-setings")){
                settingsFile = args[++i]; 
            }

        }      
    }
    public static void main(String[] args) throws Exception {
        Debug.setDebugLevel(Debug.PROGRESS); 
        ExpDT_TC thisExp = new ExpDT_TC(); 
        thisExp.parseArgs(args); 
        DomDesc domDesc = new DomDesc(thisExp.domDescFile); 
        Settings settings = new Settings(thisExp.settingsFile, domDesc); 
        ClassStreamVecI trainStreamData = new
            ClassStreamVec(thisExp.trainDataFile, domDesc); 
        ClassStreamVecI testStreamData = new
            ClassStreamVec(thisExp.testDataFile, domDesc); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Data read in");  


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
        EventClusterer[] eventClusterers = new
            EventClusterer[numClasses]; 
        // And now, initialise. 
        for(int i=0; i < numClasses; i++){
            eventClusterers[i] = settings.getEventClusterer(); 
        }

        // Segment the data. 


        ClassStreamEventsVec[] trainStreamsByClass = new
            ClassStreamEventsVec[numClasses]; 
        for(int i=0; i < numClasses; i++){
            trainStreamsByClass[i] = new ClassStreamEventsVec(); 
            trainStreamsByClass[i].setClassVec(new
                ClassificationVec()); 
            trainStreamsByClass[i].setStreamEventsVec(new
                StreamEventsVec()); 
    
        }

        Debug.dp(Debug.PROGRESS, "PROGRESS: Data rearranged.");  


        //And now load it up. 
        StreamEventsVecI trainEventSEV =
            trainEventData.getStreamEventsVec(); 
        ClassificationVecI trainEventCV = trainEventData.getClassVec();
        int numTrainStreams = trainEventCV.size(); 
        for(int i=0; i < numTrainStreams; i++){
            int currentClass = trainEventCV.elAt(i).getRealClass(); 
            trainStreamsByClass[currentClass].add(trainEventSEV.elAt(i), trainEventCV.elAt(i)); 
        }

        ClusterVecI[] clustersByClass = new ClusterVecI[numClasses]; 
        for(int i=0; i < numClasses; i++){
            clustersByClass[i] =
                eventClusterers[i].clusterEvents(trainStreamsByClass[i]); 
            Debug.dp(Debug.PROGRESS, "PROGRESS: Clustering of " + i + " complete"); 
            Debug.dp(Debug.PROGRESS, "Clusters for class: " + domDesc.getClassDescVec().getClassLabel(i) + " are:"); 
            Debug.dp(Debug.PROGRESS, eventClusterers[i].getMapping()); 
    
        }

        Debug.dp(Debug.PROGRESS, "PROGRESS: Clustering complete. ");  


        // But wait! There's more! There is always more. 
        // The first thing was only useful for clustering. 
        // Now attribution. We want to attribute all the data. So we are going 
        // to have one dataset for each learner. 
        // First set up the attributors. 

        Attributor[] attribsByClass = new Attributor[numClasses]; 
        for(int i=0; i < numClasses; i++){
            attribsByClass[i] = new Attributor(domDesc,
                                               clustersByClass[i], 
                                               eventClusterers[i].getDescription()); 

            Debug.dp(Debug.PROGRESS, "PROGRESS: AttributorMkr of " + i + " complete."); 
        }


        ClassStreamAttValVecI[] trainEventAtts = new
            ClassStreamAttValVec[numClasses]; 
        ClassStreamAttValVecI[] testEventAtts = new
            ClassStreamAttValVec[numClasses]; 

        for(int i=0; i < numClasses; i++){
            trainEventAtts[i] =
                attribsByClass[i].attribute(trainStreamData,
                                            trainEventData); 
            testEventAtts[i] = attribsByClass[i].attribute(testStreamData,
                                                           testEventData); 
            Debug.dp(Debug.PROGRESS, "PROGRESS: Attribution of " + i + " complete."); 

        }


        Debug.dp(Debug.PROGRESS, "PROGRESS: Attribution complete.");  


        // Combine all data sources. For now, globals go in every
        // one. 

        Combiner c = new Combiner(); 

        ClassStreamAttValVecI[] trainAttsByClass = new
            ClassStreamAttValVec[numClasses];

        ClassStreamAttValVecI[] testAttsByClass = new
            ClassStreamAttValVec[numClasses];

        for(int i=0; i < numClasses; i++){
            trainAttsByClass[i] = c.combine(trainGlobalData,
                                            trainEventAtts[i]); 

            testAttsByClass[i] = c.combine(testGlobalData,
                                           testEventAtts[i]); 
        }

        // Garbage collect

        trainStreamData = null; 
        testStreamData = null; 
        eventClusterers = null; 
        trainEventSEV = null; 
        trainEventCV = null; 
        clustersByClass = null; 
        attribsByClass = null; 

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

        C45Call[] c45Learners = new C45Call[numClasses]; 
        for(int i=0; i < numClasses; i++){
            c45Learners[i] = new C45Call(); 
            c45Learners[i].setDomDesc(domDesc); 
            c45Learners[i].setAttDescVec(trainAttsByClass[i].getStreamAttValVec().getDescription()); 
            try{
                c45Learners[i].setParam("prefix", thisExp.tmpPrefix + i); 
                // c45Learners[i].setParam("deleteFiles", "true"); 
                c45Learners[i].learn(trainAttsByClass[i]); 
                Debug.dp(Debug.PROGRESS, "PROGRESS: Data learnt"); 
            }
            catch(Exception e){
                e.printStackTrace(System.err); 
            } 
        }

        // Now learn. 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Learning complete. ");  

        // Now test on training data (each one)
        /*
          for(int i=0; i < numClasses; i++){
          String className =
          domDesc.getClassDescVec().getClassLabel(i); 
          ClassificationVecI classvi = (ClassificationVecI) trainAttsByClass[i].getClassVec().clone();
          StreamAttValVecI savvi =
          trainAttsByClass[i].getStreamAttValVec(); 
    
          // for(int j=0; j < trainAttsByClass[i].size(); j++){
          // nbClassifiers[i].classify(savvi.elAt(j), classvi.elAt(j));
          // }
    
          c45Learners[i].classifyAll(trainAttsByClass[i]); 

          System.out.println(">>> Learner for class " + className); 
          int numCorrect = 0; 
          for(int j=0; j < classvi.size(); j++){
          System.out.print(classvi.elAt(j).toString()); 
          if(classvi.elAt(j).getRealClass() == classvi.elAt(j).getPredictedClass()){
          numCorrect++; 
          }
    
          }
          System.out.println("Train accuracy for " + className + " classifier: " + numCorrect + " of " + numTrainStreams + " (" + 
          numCorrect*100.0/numTrainStreams + "%)"); 

    
          }
        */
        System.out.println(">>> Testing stage <<<"); 
        // First, print the results of using the straight testers. 
        ClassificationVecI[] classns = new ClassificationVecI[numClasses]; 
        for(int i=0; i < numClasses; i++){
            String className =
                domDesc.getClassDescVec().getClassLabel(i); 
            StreamAttValVecI savvi =
                testAttsByClass[i].getStreamAttValVec(); 
    
            // for(int j=0; j < numTestStreams; j++){
            // nbClassifiers[i].classify(savvi.elAt(j), classns[i].elAt(j));
            // }
    
            c45Learners[i].classifyAll(testAttsByClass[i]); 
            classns[i] = (ClassificationVecI) testAttsByClass[i].getClassVec().clone();

            System.out.println(">>> Learner for class " + className); 
            int numCorrect = 0; 
            for(int j=0; j < numTestStreams; j++){
                System.out.print(classns[i].elAt(j).toString()); 
                if(classns[i].elAt(j).getRealClass() == classns[i].elAt(j).getPredictedClass()){
                    numCorrect++; 
                }
    
            }
            System.out.println("Test accuracy for " + className + " classifier: " + numCorrect + " of " + numTestStreams + " (" + 
                               numCorrect*100.0/numTestStreams + "%)"); 

    
        }

        // Now do voting. This is a hack solution. 
        int numCorrect = 0; 
        for(int i=0; i < numTestStreams; i++){
            int[] votes = new int[numClasses]; 
            int realClass = classns[0].elAt(i).getRealClass();
            String realClassName = domDesc.getClassDescVec().getClassLabel(realClass); 
            for(int j=0; j < numClasses; j++){
                int thisPrediction = classns[j].elAt(i).getPredictedClass(); 

                // if(thisPrediction == j){
                // votes[thisPrediction] += 2; 
                // }
                // else {
                votes[thisPrediction]++; 
                // }

            }
            int maxIndex = -1;
            int maxVotes = 0; 
            String voteRes = "[ "; 
            for(int j=0; j <numClasses; j++){
                voteRes += votes[j] + " "; 
                if(votes[j] > maxVotes){
                    maxIndex = j;
                    maxVotes = votes[j]; 
                }
            } 
            voteRes += "]"; 
            // Now print the result: 
            String predictedClassName = domDesc.getClassDescVec().getClassLabel(maxIndex); 
            if(maxIndex == realClass){ 
                System.out.println("Class " + realClassName + " CORRECTLY classified with " + maxVotes + " votes. Votes: " + voteRes); 
                numCorrect++; 
            }
            else {
                System.out.println("Class " + realClassName + " INCORRECTLY classified as " + predictedClassName + " with " + maxVotes  + " votes. Votes: " + voteRes); 
            }
    
        }
        System.out.println("Final voted accuracy: " + numCorrect + " of " + numTestStreams + " (" + 
                           numCorrect*100.0/numTestStreams + "%)"); 
    }
    
}
