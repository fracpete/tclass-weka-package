/**
  * An executable that creates a .tso file from the .tsl file
  * 
  * Options: 
  * <ul>
  * <li>-p <prefix>: Prefix of the object. We assume that you thus want to convert
  * from a <prefix>.tsl to <prefix>.tso using <prefix>.tdd as the description. 
  * <li>-i <filename>: Use <filename> as input. 
  * <li>-o <filename>: Use <filename> as output. 
  * <li>-c <filename>: Use <filename> as domain description. 
  * <li>-f: Force conversion, even if .tso file modification 
  * is newer than .tsl file modification time. 
  * <li>-t: Do the test files as well. Also converts ttl to tto. 
  * <li>-ti: Choose the test input file. 
  * <li>-to: Choose the test output file. 
  * <li>-v: Verify output by reading back in. 
  * <li>-d <num>: debug level. 
  * </ul>
  * 
  * @author Waleed Kadous
  * @version $Id: MakeTSO.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.io.*; 
import tclass.util.*; 

public class MakeTSO {
    private static String trainInFile = "DF.tsl"; 
    private static String trainOutFile = "DF.tso"; 
    private static String domDescFile = "DF.tdd";
    private static String testInFile = "DF.ttl"; 
    private static String testOutFile = "DF.tto"; 
    private static boolean forceConversion = false; 
    private static boolean doTest = false; 
    private static boolean verify = false; 
    
    public static void parseInput(String[] args) throws Exception {
	int currentArg = 0; 
	for(int i=0; i < args.length; i++){
	    if(args[i].equals("-p")){
		String prefix = args[++i]; 
		trainInFile = prefix + ".tsl"; 
		trainOutFile = prefix + ".tso"; 
		domDescFile = prefix + ".tdd"; 
		testInFile = prefix + ".ttl"; 
		testOutFile = prefix + ".tto"; 
	    }
	    else if(args[i].equals("-o")){
		trainOutFile = args[++i]; 
	    }
	    else if(args[i].equals("-i")){
		trainInFile = args[++i]; 
	    }
	    else if(args[i].equals("-c")){
		domDescFile = args[++i]; 
	    }
	    else if(args[i].equals("-d")){
		Debug.setDebugLevel(Integer.parseInt(args[++i])); 
	    }
	    else if(args[i].equals("-v")){
		verify = true; 
	    }
	    else if(args[i].equals("-f")){
		forceConversion = true; 
	    }
	    else if(args[i].equals("-t")){
		doTest = true; 
	    }
	    else if(args[i].equals("-ti")){
		testInFile = args[++i]; 
	    }
	    else if(args[i].equals("-to")){
		testOutFile = args[++i]; 
	    }
	    else {
		throw new Exception("Unknown option"); 
	    }	    
	}	
    }

    
    public static boolean checkTrainFiles() throws FileNotFoundException {
	if(forceConversion) return true; 
	else {
	    // Get the file creation times. If the time of modification
	    // of the tsl is after that of the tso, return true. 
	    // If the tso does not exist return true. 
	    // IF the tsl throw a FileNotFoundException
	    File inF = new File(trainInFile); 
	    File outF = new File(trainOutFile); 
	    if(!inF.exists()){
		throw new FileNotFoundException("File " + trainInFile + " does not exist"); 
	    }
	    if(!outF.exists() || outF.length() == 0 ){
		return true; 
	    }
	    else if(inF.lastModified() > outF.lastModified()){
		return true; 
	    }
	    else {
		return false; 
	    }
	}
    }

    public static boolean checkTestFiles() throws FileNotFoundException {
	if(forceConversion) return true; 
	else {

	    // Get the file creation times. If the time of modification
	    // of the tsl is after that of the tso, return true. 
	    // If the tso does not exist return true. 
	    // If the tsl throw a FileNotFoundException

	    File inF = new File(testInFile); 
	    File outF = new File(testOutFile); 
	    if(!inF.exists()){
		throw new FileNotFoundException("File " + testInFile + " does not exist"); 
	    }
	    if(!outF.exists()){
		return true; 
	    }
	    else if(inF.lastModified() > outF.lastModified()){
		return true; 
	    }
	    else {
		return false; 
	    }
	}
    }
     
    public static void main(String[] args) throws Exception {
	try{
	    parseInput(args); 
	}
	catch(Exception e){
	    System.err.println("Usage: blah blah blah"); 
	    return; 
	}
	DomDesc dd = new DomDesc(domDescFile); 
	if(checkTrainFiles()){
	    Debug.dp(Debug.PROGRESS, "Now writing training set ..."); 
	    ClassStreamVec csv = new ClassStreamVec(trainInFile, dd); 
	    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(trainOutFile))); 
	    oos.writeObject(csv); 
	    oos.close(); 
	    if(verify){
		Debug.dp(Debug.PROGRESS, "Now verifying ... "); 
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(trainOutFile)));
		ClassStreamVecI csvi = (ClassStreamVecI) ois.readObject(); 
		Debug.dp(Debug.PROGRESS, "Read CSVI is: "); 
		Debug.dp(Debug.PROGRESS, csvi.toString()); 
	    }
	}
	if(doTest && checkTestFiles()){
	    Debug.dp(Debug.PROGRESS, "Now writing testing set ..."); 
	     ClassStreamVec csv = new ClassStreamVec(testInFile, dd); 
	    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(testOutFile))); 
	    oos.writeObject(csv); 
	    oos.close(); 
	    if(verify){
		Debug.dp(Debug.PROGRESS, "Now verifying ... "); 
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(testOutFile)));
		ClassStreamVecI csvi = (ClassStreamVecI) ois.readObject(); 
		Debug.dp(Debug.PROGRESS, "Read CSVI is: "); 
		Debug.dp(Debug.PROGRESS, csvi.toString()); 
	    }

	}
    }

}
