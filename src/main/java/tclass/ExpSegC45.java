/**
 * Another "hack class" that will be used to test how well C4.5 
 * does on straight time division. 
 * 
 * @author Waleed Kadous
 * @version $Id: ExpSegC45.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import tclass.util.*; 
import tclass.learnalg.*; 
import java.io.*; 

// Divides each channel into 10 equal values and then averages them. 
// A useful baseline for comparison. I'm hoping that this one does not work
// too well. But I bet you it will.

public class ExpSegC45 {
    // Ok. What we are going to do is to separate the learning task in 
    // an interesting way. 
    // First of all, though, the standard stuff
    
    String domDescFile = "sl.tdd"; 
    String trainDataFile = "sl.tsl"; 
    String testDataFile = "sl.ttl"; 
    String globalDesc = "test._gc"; 

    void parseArgs(String[] args){
	for(int i=0; i < args.length; i++){
	    if(args[i].equals("-tr")){
		trainDataFile = args[++i]; 
	    }
	    if(args[i].equals("-te")){
		testDataFile = args[++i];
	    }
	}      
    }
    public static void main(String[] args) throws Exception {
	Debug.setDebugLevel(Debug.PROGRESS); 
	ExpSegC45 thisExp = new ExpSegC45(); 
	thisExp.parseArgs(args); 
	DomDesc domDesc = new DomDesc(thisExp.domDescFile); 
	ClassStreamVecI trainStreamData = new
	    ClassStreamVec(thisExp.trainDataFile, domDesc); 	
	ClassStreamVecI testStreamData = new
	    ClassStreamVec(thisExp.testDataFile, domDesc); 
	
	Debug.dp(Debug.PROGRESS, "PROGRESS: Data read in");  

	// Global data is likely to be included in every model; so we
	// might as well calculated now
	GlobalCalc globalCalc = new GlobalCalc(new StreamTokenizer(
			    new FileReader(thisExp.globalDesc)), domDesc);
	
	ClassStreamAttValVecI trainGlobalData =
	    globalCalc.applyGlobals(trainStreamData);
	ClassStreamAttValVecI testGlobalData =
	    globalCalc.applyGlobals(testStreamData);
	// And we might as well extract the events. 

	Debug.dp(Debug.PROGRESS, "PROGRESS: Globals calculated.");  
	Debug.dp(Debug.PROGRESS, "Train: " + trainGlobalData.size() +
		 " Test: " + testGlobalData.size());  
	
	
	int numTestStreams = testGlobalData.size(); 
	int numTrainStreams = trainGlobalData.size(); 
	int numClasses = domDesc.getClassDescVec().size(); 
	
	//Now do the time division
	TimeDivision td = new TimeDivision(domDesc, 10); 
	ClassStreamAttValVecI trainDivData = td.timeDivide(trainStreamData); 
	ClassStreamAttValVecI testDivData = td.timeDivide(testStreamData); 
	
	Combiner c = new Combiner(); 
	ClassStreamAttValVecI trainAllData = c.combine(trainGlobalData, trainDivData); 
	ClassStreamAttValVecI testAllData = c.combine(testGlobalData, testDivData); 
	Debug.dp(Debug.PROGRESS, "PROGRESS: Data now combined."); 
	// System.out.println("TRAINING DATA: \n" + trainAllData); 
	// System.out.println("TESTING DATA: \n" + testAllData); 

	// Now do some learning. 
	C45Call c45Learner = new C45Call(); 
	c45Learner.setDomDesc(domDesc); 
	c45Learner.setAttDescVec(trainAllData.getStreamAttValVec().getDescription()); 
	try{
	    c45Learner.setParam("prefix", "c45-sl"); 
	    c45Learner.learn(trainAllData); 
	    Debug.dp(Debug.PROGRESS, "PROGRESS: Data learnt"); 
	}
	catch(Exception e){
	    e.printStackTrace(System.err); 
	}
	
	// ClassifierI nbClassifier = nbLearner.learn(trainAllData); 
	
	// ClassificationVecI classvi = trainAllData.getClassVec(); 
	// StreamAttValVecI savvi = trainAllData.getStreamAttValVec(); 

	// And now dfo the training data. 
	// for(int i=0; i < numTrainStreams; i++){
	//    nbClassifier.classify(savvi.elAt(i), classvi.elAt(i));
	//  }

	// Now we use ctester

	c45Learner.classifyAll(trainAllData); 
	
	ClassificationVecI classvi = trainAllData.getClassVec(); 
	
	int numCorrect = 0; 
	ClassDescVecI  cdvi = domDesc.getClassDescVec(); 
	for(int i=0; i < numTrainStreams; i++){
	    int predictedClass = classvi.elAt(i).getPredictedClass(); 
	    int realClass =  classvi.elAt(i).getRealClass(); 
	    String predictedClassName = cdvi.getClassLabel(predictedClass); 
	    String realClassName = cdvi.getClassLabel(realClass); 
	    if(realClass == predictedClass){
		numCorrect++; 
		System.out.println("Class " + realClassName + " CORRECTLY classified.\n");
	    }
	    else {
		System.out.println("Class " + realClassName + " INCORRECTLY classified as " + predictedClassName + ".\n");	
	    }
	
	    
	}
	System.out.println("Final train accuracy: " + numCorrect + " of " + numTrainStreams + " (" + 
			       numCorrect*100.0/numTrainStreams + "%)"); 

	c45Learner.classifyAll(testAllData);

	classvi = testAllData.getClassVec(); 
	numCorrect = 0; 
	for(int i=0; i < numTestStreams; i++){
	    int predictedClass = classvi.elAt(i).getPredictedClass(); 
	    int realClass =  classvi.elAt(i).getRealClass(); 
	    String predictedClassName = cdvi.getClassLabel(predictedClass); 
	    String realClassName = cdvi.getClassLabel(realClass); 
	    if(realClass == predictedClass){
		numCorrect++; 
		System.out.println("Class " + realClassName + " CORRECTLY classified.");
	    }
	    else {
		System.out.println("Class " + realClassName + " INCORRECTLY classified as " + predictedClassName + ".");	
	    }
	
	    
	}
	System.out.println("Final test accuracy: " + numCorrect + " of " + numTestStreams + " (" + 
			       numCorrect*100.0/numTestStreams + "%)"); 
    }
}
