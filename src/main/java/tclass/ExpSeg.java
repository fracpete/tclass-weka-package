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
 * Naive segmentation-based learning. Used for comparison. 
 * 
 * @author Waleed Kadous
 * @version $Id: ExpSeg.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import tclass.util.Debug;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ExpSeg {
    // Ok. What we are going to do is to separate the learning task in 
    // an interesting way. 
    // First of all, though, the standard stuff
    
    String domDescFile = "sl.tdd"; 
    String trainDataFile = "sl.tsl"; 
    String testDataFile = "sl.ttl"; 
    // String globalDesc = "test._gc"; 
    // String evExtractDesc = "test._ee";
    // String evClusterDesc = "test._ec"; 
    String settingsFile = "test.tal"; 
    String learnerStuff = weka.classifiers.trees.J48.class.getName(); 
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
            if(args[i].equals("-l")){
                learnerStuff = args[++i]; 
                learnerStuff = learnerStuff.replace(':', ' '); 
                System.err.println("Learner String is: " + learnerStuff); 
            }
            if(args[i].equals("-n")){
                numDivs = Integer.parseInt(args[++i]); 
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Debug.setDebugLevel(Debug.PROGRESS); 
        ExpSeg thisExp = new ExpSeg(); 
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


        // System.out.println(trainEventData.toString()); 


        // Now we want the clustering algorithms only to cluster
        // instances of each class. Make an array of clusterers, 
        // one per class. 

        int numTestStreams = testGlobalData.size(); 
        int numClasses = domDesc.getClassDescVec().size(); 
        TimeDivision td = new TimeDivision(domDesc, thisExp.numDivs); 
        ClassStreamAttValVecI trainDivData = td.timeDivide(trainStreamData); 
        ClassStreamAttValVecI testDivData = td.timeDivide(testStreamData); 
        Debug.dp(Debug.PROGRESS, "PROGRESS: Segmentation performed");  
        
        Combiner c = new Combiner(); 
        ClassStreamAttValVecI trainAtts = c.combine(trainGlobalData,
                                            trainDivData); 

        ClassStreamAttValVecI testAtts = c.combine(testGlobalData,
                                           testDivData); 

        trainStreamData = null; 
        testStreamData = null; 

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
        Classifier learner = AbstractClassifier.forName(classifierName, classifierSpec);
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
                Remove af = new Remove(); 
                af.setInvertSelection(true); 
                af.setAttributeIndices(featureString); 
                af.setInputFormat(data); 
                data = Filter.useFilter(data, af); 
            }
        learner.buildClassifier(data); 
        Debug.dp(Debug.PROGRESS, "Learnt classifier: \n" + learner.toString()); 
        
        WekaClassifier wekaClassifier; 
        wekaClassifier = new WekaClassifier(learner); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Learning complete. ");  

        System.err.println(">>> Testing stage <<<"); 
        // First, print the results of using the straight testers. 
        ClassificationVecI classns; 
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
            Remove af = new Remove(); 
            af.setInvertSelection(true); 
            af.setAttributeIndices(featureString); 
            af.setInputFormat(data); 
            data = Filter.useFilter(data, af); 
        }
        for(int j=0; j < numTestStreams; j++){
            wekaClassifier.classify(data.instance(j), classns.elAt(j));
        }
        System.err.println(">>> Learner <<<"); 
        int numCorrect = 0; 
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
