/**
 * Fast vector of ints class. Meant to allow extension, but with
 * maximum performance. Uses growing arrays. 
 * @author Waleed Kadous
 * @version $Id: FVint.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass.util; 

import java.io.*; 
import java.util.*;

public class FVint {
    int[] data; 
    int currentSize; // The number of defined elements in the array. 
    int origSize; 
    static final int DEFAULT_SIZE=8; 
    int maxGrowth = 512; 

    public FVint(int size){
        data = new int[size]; 
        currentSize = 0; 
        origSize = size; 
    }
    
    public FVint(){
        this(DEFAULT_SIZE); 
    }
    
    public int elAt(int index){
        return data[index]; 
    }

    public int size(){
        return currentSize; 
    }

    public void set(int index, int value){
        if(index >= data.length){
            makeBigger(index+1); 
            currentSize = index+1; 
        }
        data[index] = value; 
    }

    void makeBigger(int minNewSize){
        int newSize = Math.min(data.length*2, data.length+maxGrowth); 
        newSize = Math.max(newSize, minNewSize); 
        int[] newdata = new int[newSize]; 
        System.arraycopy(data, 0, newdata, 0, data.length); 
        data = newdata; 
    }
    
    public void append(int value){
        if(currentSize >= data.length){
            makeBigger(0); 
        }
        data[currentSize] = value; 
        currentSize++; 
    }

    public String toString(){
        StringBuffer rv = new StringBuffer("[ "); 
        for(int i=0; i < currentSize; i++){
            rv.append(data[i]); 
            rv.append(" "); 
        }
        rv.append("]"); 
        return rv.toString(); 
    }

    public static void main(String[] args) throws Exception{
        System.out.println("Allocating and adding " + args[0] + " elements " + args[1] + " times. "); 
        int arraySize = Integer.parseInt(args[0]); 
        int numLoops = Integer.parseInt(args[1]); 
        System.out.println("Going to sleep for JIT to kick in ... "); 
        Thread.sleep(30000); 
        System.out.println("First FVint ..."); 
        long startTime = System.currentTimeMillis(); 
        for(int i=0; i < numLoops; i++){
            FVint fvint = new FVint(arraySize); 
            for(int j=0; j < arraySize; j++){
                fvint.append(j-arraySize/2); 
            }
            // System.out.println(fvint); 

            int sum = 0; 
            for(int j=0; j < arraySize; j++){
                sum += fvint.elAt(j); 
            }
            // System.out.println(fvint); 
            // System.out.println("Total is: " + sum); 
        }
        System.out.println("Total time: " + (System.currentTimeMillis()-startTime)); 
        System.out.println("And now to compare to Vectors ..."); 
        startTime = System.currentTimeMillis(); 
        for(int i=0; i < numLoops; i++){
            Vector v = new Vector(arraySize); 
            for(int j=0; j < arraySize; j++){
                v.addElement(new Integer(j-arraySize/2)); 
            }
            int sum = 0; 
            for(int j=0; j < arraySize; j++){
                sum += ((Integer) v.elementAt(j)).intValue(); 
            }
            // System.out.println("Total is: " + sum); 
        }
        System.out.println("Total time: " + (System.currentTimeMillis()-startTime)); 
    }

}

