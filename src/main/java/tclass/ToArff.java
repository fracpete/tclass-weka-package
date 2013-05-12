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
 * ToArff.java: Converts a dataset into a format palatable to 
 * Weka's learners. Makes it easier to explore the data. 
 * 
 * 
 * @author Waleed Kadous
 * @version $Id: ToArff.java,v 1.1 2002/08/02 05:07:52 waleed Exp $
 * $Log: ToArff.java,v $
 * Revision 1.1  2002/08/02 05:07:52  waleed
 * *** empty log message ***
 *
 */

package tclass;  

import java.io.FileWriter;

import tclass.util.Debug;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ToArff {
    // Ok. What we are going to do is to separate the learning task in 
    // an interesting way. 
    // First of all, though, the standard stuff
    
    String domDescFile = "sl.tdd"; 
    String inFile = "sl.tsl"; 
    // String globalDesc = "test._gc"; 
    // String evExtractDesc = "test._ee";
    String settingsFile = "test.tal"; 
    String learnerStuff = weka.classifiers.trees.J48.class.getName(); 
    String outFile = "default.arff";
    boolean featureSel = false; 
    boolean makeDesc = false; 
    boolean trainResults = false; 
    void parseArgs(String[] args){
        for(int i=0; i < args.length; i++){
            if(args[i].equals("-in")){
                inFile = args[++i]; 
            }
	    
	    if(args[i].equals("-out")){
		outFile = args[++i]; 
	    }
            if(args[i].equals("-dd")){
                domDescFile = args[++i]; 
            }
            if(args[i].equals("-settings")){
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
        ToArff thisExp = new ToArff(); 
        thisExp.parseArgs(args); 
        DomDesc domDesc = new DomDesc(thisExp.domDescFile); 
        ClassStreamVecI trainStreamData = new
            ClassStreamVec(thisExp.inFile, domDesc); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Data read in");  
        Settings settings = new Settings(thisExp.settingsFile, domDesc); 
        

        EventExtractor evExtractor = settings.getEventExtractor(); 
        // Global data is likely to be included in every model; so we
        // might as well calculated now
        GlobalCalc globalCalc = settings.getGlobalCalc(); 

        ClassStreamAttValVecI trainGlobalData =
            globalCalc.applyGlobals(trainStreamData);
        // And we might as well extract the events. 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Globals calculated.");  
        Debug.dp(Debug.PROGRESS, "Train: " + trainGlobalData.size());  


        ClassStreamEventsVecI trainEventData =
            evExtractor.extractEvents(trainStreamData); 

        Debug.dp(Debug.PROGRESS, "PROGRESS: Events extracted");  
        // System.out.println(trainEventData.toString()); 


        // Now we want the clustering algorithms only to cluster
        // instances of each class. Make an array of clusterers, 
        // one per class. 

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
        Debug.dp(Debug.PROGRESS, "PROGRESS: Attribution complete."); 


        // Combine all data sources. For now, globals go in every
        // one. 

        Combiner c = new Combiner(); 
        ClassStreamAttValVecI trainAtts = c.combine(trainGlobalData,
                                            trainEventAtts); 


        trainStreamData = null; 
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
	try {
	    FileWriter fw = new FileWriter(thisExp.outFile); 
	    fw.write(data.toString()); 
	    fw.close(); 
	}
	catch(Exception e){
	     throw new Exception("Could not write to output file. ");
	}
    }
}
