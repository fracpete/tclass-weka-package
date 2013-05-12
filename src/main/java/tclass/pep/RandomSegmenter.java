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

/** Class responsible for applying the dynamic programming approach to 
 * get the line segmentation. 
 */

package tclass.pep;   
import java.util.Enumeration;
import java.util.Vector;

import tclass.ChannelI;
import tclass.EventVec;

public class RandomSegmenter {
    ChannelI c; 
    int numFrames; 
    /** Store best intermediate results
     */ 
    SegmentSequence bestSeg;
    double bestError= Double.MAX_VALUE; 
    int maxLines = 8; 
    int numTries = 1000; 
    
    RandomSegmenter(ChannelI channel, int numTries, int maxLines){
        c = channel; 
        numFrames = c.numFrames();             
        maxLines = maxLines; 
        numTries = numTries; 
    }
    
    float computeVariance(ChannelI channel){
        // Calculates the best linear fit (and thus evaluates a and b),
        // but also computes the variance. The below calculations are
        // based on Walpole & Myers, "Probability and Statistics for
        // Engineers & Scientists", 5th Edn, pp365-373 and pp204-207. 
          // Computes the Global Variance for this sign. 
        float syi2 = 0;
        float syi = 0;
        float variance = 0; 
        int n = numFrames; 
        //Calculate global variance
        for(int i=0; i < n; i++){
            float val = channel.valAt(i); 
            syi2 += val*val;
            syi += val; 
        }
        if(n > 2){
            variance = ((n+1)*syi2-syi*syi)/((n+1)*n);
        }
        else {
            variance = 1e-20f;
        }
        System.out.println("Variance is: " + variance); 
        return variance; 
    }

    boolean insertInto(int breakpoint, Vector breakpoints){
        int numElements = breakpoints.size(); 
        if(numElements == 0){
            breakpoints.addElement(new Integer(breakpoint)); 
            return true; 
        }
        for(int i=0; i < numElements; i++){
            int currentElement = ((Integer) breakpoints.elementAt(i)).intValue(); 
            if(Math.abs(breakpoint - currentElement) < 3){
                return false; 
            }
            if(breakpoint < currentElement){
                breakpoints.insertElementAt(new Integer(breakpoint), i); 
                return true; 
            }
        }
        // if we're still here ...
        breakpoints.addElement(new Integer(breakpoint)); 
        return true; 
    }
    
    void segment() {
        float sd = (float)Math.sqrt(computeVariance(c)); 
        if(sd == 0){
            bestSeg = new SegmentSequence(); 
            bestSeg.add(new Segment(0, numFrames -1, c));
            return; 
        }
        for(int i=0; i < numTries; i++){
            SegmentSequence currentSeg = new SegmentSequence(); 
            // Decide on number of segments ... a number between 0 and maxLine-1. 
            int numLines = (int) Math.round(Math.floor(Math.random()*maxLines));
            // Now generate the random number, using insertion sort into a list of vectors. 
            if(numFrames < numLines*3){
                numLines = numFrames/3; 
            }
            Vector breakpoints = new Vector(numLines); 
            for(int j=0; j < numLines; j++){
                // Random number from 1 to the length of the list. 
                int breakpoint = (int) Math.round(Math.ceil(Math.random()*(numFrames-3)));
                if(!insertInto(breakpoint, breakpoints)){
                    // Insertion did not succeed, generated value was
                    // already in vector. 
                    // So, try again
                    j--; 
                    // System.out.println("Coincidence on " + j + " for " + breakpoint); 
                }
            }
            // System.out.println("Vector is: " + breakpoints); 
            // Now, make the line segments. 
            int startpoint = 0; 
            for(Enumeration e = breakpoints.elements(); e.hasMoreElements();){
                int endpoint = ((Integer) e.nextElement()).intValue(); 
                currentSeg.add(new Segment(startpoint, endpoint, c)); 
                startpoint = endpoint + 1; 
            }
            currentSeg.add(new Segment(startpoint, numFrames-1, c)); 
            double currentError = currentSeg.getTotalError(sd); 
            // System.out.println("Current attempt: " + currentSeg + " has error " + currentError); 
            if(currentError < bestError){
                bestError = currentError; 
                bestSeg = currentSeg; 
            }
        }
    }
    
    EventVec getEventVec(){
        // Debug.dp(Debug.EMERGENCY, "Best (final) is:  " +
        //         bestSeg); 

        System.out.println("Best error is: " + bestError); 
        return bestSeg.toEventVec(); 
    }
}

