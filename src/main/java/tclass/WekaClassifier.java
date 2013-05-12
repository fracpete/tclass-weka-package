package tclass;  

import tclass.clusteralg.*; 
import tclass.util.*; 
// import tclass.learnalg.*; 
import weka.classifiers.*; 
import weka.classifiers.j48.*; 
import weka.attributeSelection.*; 
import weka.filters.*; 
import weka.core.*; 
import java.io.*; 
import java.util.*; 

public class WekaClassifier {
    weka.classifiers.Classifier classifier; 
    String name = "weka"; 
    String description = "Weka Classifier"; 
    public WekaClassifier(weka.classifiers.Classifier classifier){
        this.classifier = classifier; 
    } 
      public String getName(){
	return name; 
    }

   public String getDescription(){
	return description; 
    }

    public void classify(Instance inst, ClassificationI
			 classn) throws Exception {
        double bestClass = classifier.classifyInstance(inst); 
        classn.setPredictedClass((int) bestClass); 
        classn.setPredictedClassConfidence(1); 
    }
}

