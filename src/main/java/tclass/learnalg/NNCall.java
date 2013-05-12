/**
  * Instance based caller. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: NNCall.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.learnalg;   

import tclass.*; 
import tclass.util.*; 
import tclass.datatype.*; 
//This data needs direct access at times.
import java.io.*; 
import java.util.*; 

public class NNCall implements LearnerAlgI {
    
    String baseName = "c45call"; 
    String description = "Runs c4.5 on the data"; 
    String nnbinary = "/home/waleed/bin/instance"; 
    String specialParams = ""; 
    AttDescVecI advi = null;
    DomDesc domDesc = null; 
    boolean deleteFiles = false; 
    String prefix = "DF"; 
    
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
     * @return true if the operation succeeded. 
     *
     */

    public void classifyAll(ClassStreamAttValVecI input){
	makeData(prefix + ".test", input); 
	//Grab the data. 
	ClassificationVecI classns = input.getClassVec(); 
	int numStreams = classns.size(); 
	ClassDescVecI cdv = domDesc.getClassDescVec(); 
	String execstring = nnbinary + " -n 1 -f " + prefix; 
	Debug.dp(Debug.PROGRESS, "Calling exec with >" + execstring +"<");
	try{
	    long startTime = System.currentTimeMillis(); 
	    Runtime runTime = Runtime.getRuntime(); 
	    Process p = runTime.exec(execstring); 
	    BufferedReader results = new BufferedReader(new InputStreamReader(p.getInputStream())); 
	    // Now we can read the data back. 
	    String line; 
	    while(!(line = results.readLine()).equals("--RESULTS START--")){
		Debug.dp(Debug.PROGRESS, "Skipped: " + line);  
	    }
	    //Skip all the crap. 

	    for(int i=0; i < numStreams; i++){
		StringTokenizer st = new StringTokenizer(results.readLine(), ",", false);
		Debug.myassert(st.countTokens() == 3, "AARGH! ctester is behaving weirdly!"); 
		String predictedClassName = st.nextToken(); 
		String realClassName = st.nextToken(); 
		float predictedClassConfidence = (new Float(st.nextToken())).floatValue();  
		int realClass = cdv.getId(realClassName); 
		int predictedClass = cdv.getId(predictedClassName); 
		// Sanity check time. 
		Debug.myassert(realClass != -1, "AARGH! Unknown real class coming from ctester"); 
		Debug.myassert(predictedClass != -1, "AARGH! Unknown predicted class coming from ctester"); 
		Debug.myassert(realClass == classns.elAt(i).getRealClass(), "AARGH! Real class doesn't match from ctester"); 
		// Cool. Let's go ahead. 
		classns.elAt(i).setPredictedClass(predictedClass); 
		classns.elAt(i).setPredictedClassConfidence(predictedClassConfidence); 
	    }
	    p.waitFor(); 
	    long endTime = System.currentTimeMillis(); 
	    Debug.dp(Debug.PROGRESS, "Process ran for " + (endTime-startTime) + "ms"); 
	}
	catch(Exception e){
	    e.printStackTrace(System.err); 
	    System.err.println("Ugh!! Can't run nnbinary!!"); 
	}
	
    }

    public void setParam(String p, String v) throws InvalidParameterException
    {
	// Let's just use a simple parameter as an example. 
	if(p.equals("deleteFiles")){
	    if(v.equals("true")){
		deleteFiles = true; 
	    }
	    else if(v.equals("false")){
		deleteFiles = false; 
	    }
	    else {
		throw new InvalidParameterException(p, v, "deleteFiles must be true or false");
	    }
		
	}
	else if(p.equals("prefix")){
	    prefix = v; 
	}
	else if(p.equals("nnbinary")){
	    nnbinary = v; 
	}
	else if(p.equals("opt")){
	    specialParams = v; 
	}
	else {
	    throw new InvalidParameterException(p, v, "Unknown parameter"); 
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
	pv.add(new Param("deleteFiles", "Deletes all files after first time Classifier is called", "false")); 
	pv.add(new Param("prefix", "Prefix for generated C4.5 files", "DF")); 
	pv.add(new Param("nnbinary", "Path for c4.5 binary", nnbinary)); 
	pv.add(new Param("opts", "Additional options to C4.5", "None")); 
	return pv; 
    }

    /**
     * Apply the feature selection algorithm
     * 
     */ 
    
    public ClassifierI learn(ClassStreamAttValVecI input){
	// Ok. So let's see. First we generate the files. 
	String namesFile = prefix + ".names"; 
	makeNames(namesFile); 
	// Ok, Now we make the data. 
	String dataFile = prefix + ".data"; 
	makeData(dataFile, input); 
	return null; 
	
    }
    
    
    void makeNames(String fileName){
	// Ok, so lets make the file
	// Let's scribble all over the files ... let's live dangerously, huh? 
	try{
	    PrintWriter pw = new PrintWriter(new FileWriter(fileName), true);
	    // Ok, let's write out the classes. 
	    ClassDescVecI classes = domDesc.getClassDescVec(); 
	    int limit = classes.size()-1; 
	    
	    pw.print("|Generated by TClass " + (new Date()).toString() + "\n|Classes are:\n"); 
	    for(int i=0; i < limit; i++){
		pw.print(classes.getClassLabel(i) +", "); 
	    }
	    pw.print(classes.getClassLabel(limit) + ".\n"); 
	    // And now the attributes. This could get messy. 
	    // For now, we'll spit the dummy if it's not cantinuous
	    // or discrete. 
	    int numAtts = advi.size(); 

	    pw.print("|Attributes are: (total #atts=" + numAtts + ")\n"); 
	    for(int i=0; i < numAtts; i++){
		pw.print(printAtt(advi.elAt(i))); 
	    }
	    pw.close(); 
	}
	catch(IOException ioe){
	    System.err.println("AARGH!! Could not write names file"); 
	}
	
    }
    
    String printAtt(AttDescI adi){
	DataTypeI dti = adi.getDataType(); 
	if(dti.getName().equals("continuous")){
	    //Easy. 
	    return adi.getName() +": continuous.\n";
	}
	else if(dti.getName().equals("discrete")){
	    // Uh oh. This gets ugly. 
	    String retval = adi.getName() + ": "; 
	    Discrete disc = (Discrete) adi; 
	    // Very dodgy ... grrr. But will have to do for now. 
	    // Can not thing of a better way to do it. 
	    int numVals = disc.size(); 
	    for(int i=0; i < numVals-1; i++){
		retval += disc.elAt(i) +", "; 
	    }
	    retval += disc.elAt(numVals-1) +".\n"; 
	    return retval; 
	}
	else {
	    System.err.println("AARGH!! Tried to call C4.5 with non cts or discrete attributes"); 
	    return ""; 
	}
	
    }
    void makeData(String filename, ClassStreamAttValVecI data){
	try {
	    Debug.dp(Debug.PROGRESS, "makeData called"); 
	    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename))); 
	    ClassificationVecI csvi = data.getClassVec(); 
	    StreamAttValVecI savvi = data.getStreamAttValVec(); 
	    int numStreams = savvi.size(); 
	    for(int i=0; i < numStreams; i++){
		String line = printInstance(savvi.elAt(i), csvi.elAt(i)); 
		pw.print(line); 
		// Debug.dp(Debug.PROGRESS, "Added: " + line); 

	    }
	    pw.close(); 
        } 
	catch(IOException ioe){
	    System.err.println("AARGH!! Could not write data file"); 
	}
    }
    
    String printInstance(StreamAttValI savi, ClassificationI ci){
	// System.out.println("Printing " + savi + " c " + ci); 
	// System.out.println("DomDesc is " + domDesc); 
	int numAtts = advi.size(); 
	String retval = "";
	for(int i=0; i < numAtts; i++){
	    float thisVal = savi.getAtt(i);
	    if(thisVal < 100000.0){
		retval += advi.elAt(i).getDataType().print(thisVal);
	    }
	    else {
		retval += 100000.0;
	    }
	    retval += ", "; 
	}
	retval += domDesc.getClassDescVec().getClassLabel(ci.getRealClass()) +"\n"; 
	return retval; 
    }
}