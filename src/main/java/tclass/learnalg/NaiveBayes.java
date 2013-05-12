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
 * Implements a naive Bayes learning algorithm. 
 *
 * 
 * @author Waleed Kadous
 * @version $Id: NaiveBayes.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass.learnalg;   

import tclass.AttDescVecI;
import tclass.ClassDescVecI;
import tclass.ClassStreamAttValVecI;
import tclass.ClassificationI;
import tclass.ClassificationVecI;
import tclass.ClassifierI;
import tclass.DomDesc;
import tclass.InvalidParameterException;
import tclass.LearnerAlgI;
import tclass.Param;
import tclass.ParamVec;
import tclass.StreamAttValI;
import tclass.StreamAttValVecI;
import tclass.util.Debug;

//Note: We only discretise continuous values, I guess
 
class NBClassifier implements ClassifierI {
    String name = "nbayes"; 
    String description = "Naive Bayes Classifier"; 
    BayesTable bt; 

    NBClassifier(BayesTable bt){
	this.bt = bt; 
    }
    
    public String getName(){
	return name; 
    }

    public String getDescription(){
	return description; 
    }

    public void classify(StreamAttValI instance, ClassificationI
			 classn){
	// Now find most probable class. 
	int numClasses = bt.classAttValProb.length; 
	int bestClass = -1; 
	float maxProb = -Float.MAX_VALUE; 
	
	for(int i=0; i<numClasses; i++){
	    float prob = bt.evaluateProb(instance, i); 
	    Debug.dp(Debug.FN_PARAMS, "For class " +
		     classn.getRealClass() + " against " + i +
		     " has log proby " + prob); 
	    if(prob > maxProb){
		maxProb = prob; 
		bestClass = i;
	    }
	    
	}
	//And now update the Classification object we were given. 
	classn.setPredictedClass(bestClass); 
	classn.setPredictedClassConfidence(maxProb); 

    }

    @Override
    public String toString(){
	return ("Naive Bayes Learner. Table: \n" + bt.toString()); 
    }
}

class BayesTable {
    // We need one entry for each attribute for each value of each
    // attribute of each class. 
    int numStreams; 
    float[] classProb; 
    float[][][] classAttValProb; 
    DiscretiserI[] attDiscer;
    int numAtts; 
    int numClasses; 
    DomDesc domDesc; 
    // Evaluate probability. 

    BayesTable(DomDesc d, DiscretiserI[] attDiscer,
	       ClassStreamAttValVecI csavvi){
	// Do we do laplace correction on class? No. That's pretty
	// silly. 
	// So let's start off by making some assignments. 
	this.domDesc = d; 
	this.attDiscer = attDiscer; 
	ClassDescVecI classes = domDesc.getClassDescVec(); 
	numClasses = classes.size(); 
	ClassificationVecI cvi = csavvi.getClassVec(); 
	numStreams = cvi.size(); 
	classProb =  new float[numClasses]; 
	float increment = (float) 1.0/numStreams; 
	numAtts = attDiscer.length; 
	Debug.dp(Debug.EVERYTHING, "NumStreams = " + numStreams + " inc " + increment); 
	for(int i=0; i < numStreams; i++){
	    ClassificationI classn = cvi.elAt(i);
	    // Debug.dp(Debug.IMPORTANT, "i=" + i + classn); 
	    classProb[classn.getRealClass()]+=increment; 
	    //Convert to logs. 
	}

	// Ok, now we have to deal with the stuff for the
	// probabilities. The first issue is construction. 
	classAttValProb = new float[numClasses][numAtts][]; 
	for(int j=0; j < numAtts; j++){
	    int size = attDiscer[j].getDiscType().size(); 
	    for(int i=0; i < numClasses; i++){
		classAttValProb[i][j] = new float[size]; 
	    }
	}
	// Now initialise for laplace correction. 
	for(int i=0; i < numClasses; i++){
	    for(int j=0; j < numAtts; j++){
		float[] vals = classAttValProb[i][j];
		for(int k=0; k < vals.length; k++){
		    vals[k] = 1; 
		}
	    }
	}
	//And now, we add the instances from the data. 
	StreamAttValVecI savvi = csavvi.getStreamAttValVec(); 
	for(int i=0; i < numStreams; i++){
	    int thisClass = cvi.elAt(i).getRealClass(); 
	    StreamAttValI thisStream = savvi.elAt(i); 
	    for(int j=0; j < numAtts; j++){
		int value =
		    attDiscer[j].discretise(thisStream.getAtt(j));
		Debug.dp(Debug.EVERYTHING, "AttDiscer is " + attDiscer[j].getDiscType()); 
		// Debug.dp(Debug.EVERYTHING, "Boundaries are " + ((EqualFreqDiscretiser) attDiscer[j]).myBinner.toString()); 
		// Debug.dp(Debug.EVERYTHING, "Discretiser is: " attDiscer[j].d2str()
		Debug.dp(Debug.EVERYTHING, "Array has a size of " + 	classAttValProb[thisClass][j].length);
		Debug.dp(Debug.EVERYTHING, "Looking at attribute " + j + " for class " + thisClass); 
		classAttValProb[thisClass][j][value]++; 
	    }
	}
	
	// And now renormalise all probabilities. And take logs. 
	for(int i=0; i < numClasses; i++){
	    for(int j=0; j < numAtts; j++){
		// We know the number of instances in this line of the 
		// array. It should be the results of the laplace
		// correction which is = to the size of this array; 
		// plus the number of instances of this class. 
		int numVals = classAttValProb[i][j].length; 
		float denominator = classProb[i]*numStreams +
		    numVals;
		Debug.dp(Debug.EVERYTHING, "denominator = " +
			 denominator); 
		for(int k=0; k < numVals; k++){
		    
		    classAttValProb[i][j][k] /= denominator; 
		    classAttValProb[i][j][k] = (float) Math.log(classAttValProb[i][j][k]); 
		    
		}
		
	    }
	}

	// And I think that's it. 
    }
    
    
    float evaluateProb(StreamAttValI savi, int qClass){
	float retval = classProb[qClass]; 
	if(retval == 0){
	    return -Float.MAX_VALUE;
	}
	else {
	    retval = (float) Math.log(retval); 
	}
	for(int i=0; i < numAtts; i++){
	    int value = attDiscer[i].discretise(savi.getAtt(i)); 
	    retval += classAttValProb[qClass][i][value]; 
	}
	return retval; 
    }
    
    @Override
    public String toString(){
	StringBuffer rvBuff = new StringBuffer(1000); 
	ClassDescVecI cdv = domDesc.getClassDescVec(); 
	rvBuff.append("Class Probs: \n"); 
	for(int i=0; i < numClasses; i++){
	    rvBuff.append(cdv.getClassLabel(i) + " " + classProb[i] +
			  "\n"); 
	}
	rvBuff.append("Att-Val log Probs (total atts = " + numAtts + "): "); 
	for(int i=0; i < numClasses; i++){
	    rvBuff.append("For class: " + cdv.getClassLabel(i)+"\n"); 
	    for(int j=0; j < numAtts; j++){
		rvBuff.append("Att " + j + " [ "); 
		float[] vals =  classAttValProb[i][j];
		for(int k=0; k < vals.length; k++){
		    rvBuff.append(vals[k] + " "); 
		}
		rvBuff.append("]\n"); 
	    }
	    
	}
	return rvBuff.toString(); 
    }
    
}
public class NaiveBayes implements LearnerAlgI {
    
    private String baseName = "nbayes"; 
    private String description = "Implements naive Bayes learning algorithm"; 
    private AttDescVecI advi = null;
    private DomDesc domDesc = null; 
    static final int EQFREQ = 1; 
    static final int EQWIDTH = 2; 
    private int discretisation = EQFREQ; 
    private int numDivs = 5; 
    
    
    /** 
     * Gets the name of the Learner algorithm. Used by the prototype manager 
     * as a key. 
     *
     * @return A key representing this particular Learner Algorithm
     */ 
    public String name(){
	return baseName; 
    }
    
    /**
     * Clone the current object. 
     *
     */ 

    @Override
    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }

    /** 
     * Provides a description of the LearnerAlg. This description explains
     * what the basic idea of the learner is (i.e. the sort of shapes it
     * tried to find). It should also explain any potential
     * configuration options that may
     * be used to configure the object, using the configure option. 
     * 
     * @return The description of this class. 
     */ 

    public String description(){
	return description; 
    }

    public void setDomDesc(DomDesc dd){
	domDesc = dd; 
    }
    
    public void setAttDescVec(AttDescVecI adv){
	advi = adv; 
    }

    /**
     * Configures this instance so that parameter <i>p</i> has
     * value <i>v</i>. 
     *
     * @param p the parameter to set. 
     * @param v the value of the parameter. 
     *
     */

    public void setParam(String p, String v) throws InvalidParameterException
    {
	// Let's just use a simple parameter as an example. 
	if(p.equals("numDivs")){
	    try {
		numDivs = Integer.parseInt(v); 
	    }
	    catch(NumberFormatException nfe){
		throw new InvalidParameterException(p, v, "Could not parse num divs"); 
	    }
	}
	else if(p.equals("discretisation")){
	    if(v.equals("equalFreq")){
		discretisation = EQFREQ; 
	    }
	    else if(v.equals("equalWidth")){
		discretisation = EQWIDTH; 
	    }
	}
	else {
	    throw new InvalidParameterException(p, v, "I was expecting exp"); 
	}
    }

    /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */    
    public ParamVec getParamList(){
	ParamVec pv = new ParamVec(); 
	pv.add(new Param("numDivs", "Number of divisions for continuous variables", "5")); 
	pv.add(new Param("discretisation", "The way to discretise continuous values. Either equalWidth or equalFreq", "equalWidth")); 
	return pv; 
    }

    /**
     * Apply the feature selection algorithm
     * 
     */ 
    
    public ClassifierI learn(ClassStreamAttValVecI input){
	//Now, let's see. 
	Debug.dp(Debug.FN_CALLS, "Learning with Naive Bayes ..."); 
	StreamAttValVecI streams = input.getStreamAttValVec(); 
	AttDescVecI atts = streams.getDescription(); 
	int numAtts = atts.size(); 
	DiscretiserI[] discretisers = new DiscretiserI[atts.size()]; 
	//This code likely to change in future. 
	for(int i=0; i < numAtts; i++){
	    if(atts.elAt(i).getDataType().getName().equals("continuous")){
		if(discretisation == EQWIDTH){
		    discretisers[i] = new EqualWidthDiscretiser(); 
		}
		else {
		    discretisers[i] = new EqualFreqDiscretiser(); 
		}
		discretisers[i].makeDiscretisation(input, numDivs, i); 
	    }
	    else if(atts.elAt(i).getDataType().getName().equals("discrete")){
		discretisers[i] = new DiscreteDiscretiser(); 
		discretisers[i].makeDiscretisation(input, numDivs, i);
	    }
	    else {
		Debug.dp(Debug.EMERGENCY, "AAARRGH! Naive bayes with wrong values"); 
	    }
		
	    
	}
	// Now we have the discretisation, let's create a Bayes Table
	BayesTable bt = new BayesTable(domDesc, discretisers, input); 
	return new NBClassifier(bt); 
	
    }
    
}
