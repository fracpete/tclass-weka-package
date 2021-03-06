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
 * Another "hack class" that will be used to test how well a Naive Bayes 
 * does on straight time division. 
 * 
 * @author Waleed Kadous
 * @version $Id: ExpSegment.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   
import java.io.FileReader;
import java.io.StreamTokenizer;

import tclass.learnalg.NaiveBayes;
import tclass.util.Debug;

public class ExpSegment {
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
	ExpSegment thisExp = new ExpSegment(); 
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
	System.out.println("TRAINING DATA: \n" + trainAllData); 
	System.out.println("TESTING DATA: \n" + testAllData); 

	// Now do some learning. 
	NaiveBayes nbLearner = new NaiveBayes(); 
	nbLearner.setDomDesc(domDesc); 
	nbLearner.setAttDescVec(trainAllData.getStreamAttValVec().getDescription()); 
	ClassifierI nbClassifier = nbLearner.learn(trainAllData); 
	
	ClassificationVecI classvi = trainAllData.getClassVec(); 
	StreamAttValVecI savvi = trainAllData.getStreamAttValVec(); 

	// And now dfo the training data. 
	for(int i=0; i < numTrainStreams; i++){
	    nbClassifier.classify(savvi.elAt(i), classvi.elAt(i));
	}
	
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

	classvi = testAllData.getClassVec(); 
	savvi = testAllData.getStreamAttValVec(); 

	// And now dfo the test data. 
	System.out.println("Results on test data: \n"); 
	for(int i=0; i < numTestStreams; i++){
	    nbClassifier.classify(savvi.elAt(i), classvi.elAt(i));
	}
	
	numCorrect = 0; 
	for(int i=0; i < numTestStreams; i++){
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
	System.out.println("Final test accuracy: " + numCorrect + " of " + numTestStreams + " (" + 
			       numCorrect*100.0/numTestStreams + "%)"); 
    }
}
