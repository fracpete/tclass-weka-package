/**
  * A simple debug class, implemented as a static class. Controls
  * where debugging output goes as well as the level of debugging
  * output. The lower the debug level, the fewer debug messages are
  * printed. 
  *
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Debug.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.util;   
import java.io.*; 
import java.util.*; 
public class Debug {
    static private int debugLevel = 0; 
    static private PrintStream debugOut = System.err; 

    /** 
     * Now some definition of typical error printing levels 
     *
     */

    public static final int EMERGENCY = 0; 
    public static final int IMPORTANT = 1;
    public static final int INFORMATION = 2; 
    public static final int PROGRESS = 3; 
    public static final int FN_CALLS = 4; 
    public static final int FN_PARAMS = 5; 
    public static final int EVERYTHING = 20; 


    /**
     * Set the maximum debug levels. Any <code>dp(dl, s)</code> 
     * method call will only print if it is less than or equal to this 
     * level. As a convention, debugLevel 0 should not print any
     * extraneous messages. 
     *
     * @param debugLvl New debug level. 
     */

    public static void setDebugLevel(int debugLvl){
	debugLevel = debugLvl; 
    }

    /** 
     * Get the current debug level
     *
     * @return Current debug level. 
     */ 

    public static int getDebugLevel(){
	return debugLevel; 
    }

    /** 
     *
     * Sets the place where all the debug messages go. 
     * 
     * @param p The PrintStream used to print outputs to. 
     *
     */
    public static void setDebugOut(PrintStream p){
	debugOut = p; 
    }
    
    /**
     *
     * Gets the current debug output. 
     *
     */

    public static PrintStream getDebugOut(){
	return debugOut; 
    }

    /**
     * Prints a debug message, depending on the value of debug
     * level. If the given debug level is less than or equal to the
     * current debug level, the message is printed. 
     * Convention: 0 means this should always be printed; as the
     * number of level gets higher, means it should not be printed
     * unless that level of detail is required. 
     *
     * @param level The level of this debug message. 
     * @param str The string to be printed. 
     
     */ 
    

    public static void dp(int level, String str){
	if(level <= debugLevel)
	    debugOut.println(new Date() + ": " + str); 
    }

    /**
     * An assertion-based error print. If the assertion is true, do nothing. 
     * If it is false, print it out (at the zero debug level). If 
     * If you want level-based assertion, then use the method below. 
     */ 
    public static void myassert(boolean assertion, String str){
	if(!assertion) dp(0, str); 
    }
    
    /**
     * An assertion-based error print. If the assertion is true, do nothing. 
     * If it is false, print it out at the debug level l. 
     */ 
    
    public static void myassert(boolean assertion, int l, String str){
	if(!assertion) dp(l, str); 
    }

    public static void main(String args[]){
	if(args.length < 1){
	    System.out.println("Debug Level: " +
			       Debug.getDebugLevel()); 
	}
	else {
	    Debug.setDebugLevel(Integer.parseInt(args[0])); 
	}
	for(int i=0; i < 10; i++){
	    Debug.dp(i, "This is a level " + i + " debug statement."); 
	}
	    
    }


}
    
