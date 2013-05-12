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
import tclass.ChannelI;
import tclass.EventVec;
import tclass.util.Debug;

public class Segmenter {
    ChannelI c; 
    int numFrames; 
    /** Store best intermediate results
     */ 
    SegmentSequence bestSegs[]; 

    
    Segmenter(ChannelI channel){
        c = channel; 
        numFrames = c.numFrames();             
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
            variance = 1e-9f;
        }
        System.out.println("Variance is: " + variance); 
        return variance; 
    }
    
    void segment() {
        int oldDebugLevel = Debug.getDebugLevel(); 
        // Debug.setDebugLevel(Debug.EVERYTHING); 
        float minBits = Float.MAX_VALUE; 
        float varGlobal = computeVariance(c); 
        bestSegs = new SegmentSequence[numFrames]; 
        bestSegs[1] = new SegmentSequence(); 
        bestSegs[1].add(new Segment(0,1,c, varGlobal, (float) 1e-8, 200, (float) 1e-9));  
        for(int i=2; i < numFrames; i++){
            SegmentSequence currSS = new SegmentSequence(); 
            SegmentSequence bestSS; 
            // First we try to make the straight optimal line fit. . 
            currSS.add(new Segment(0, i, c,  varGlobal, (float) 1e-8, 200, (float) 1e-9)); 
            float currBits; 
            minBits = currSS.getNumBits(); 
            bestSS = currSS; 
            // Next, we try starting from any of the remaining
            // possible positions. 
                Debug.dp(Debug.EVERYTHING, "Current SS is: " +
                         currSS.toString()); 

            for(int j=1; j < i-1; j++){
                currSS = (SegmentSequence) bestSegs[j].clone(); 
                Debug.myassert(currSS.getLength() == j, "Ends do not match in line segmentation"); 
                currSS.add(new Segment(j+1, i, c,  varGlobal, (float) 1e-8, 200, (float) 1e-9)); 
                currBits = currSS.getNumBits(); 
                if(currBits < minBits){
                    bestSS = currSS; 
                    minBits = currBits; 
                }
                
                Debug.dp(Debug.EVERYTHING, "j = " + j + " i = " + i); 
                Debug.dp(Debug.EVERYTHING, "BestSegs[j] is: " +
                         bestSegs[j].toString()); 
                Debug.dp(Debug.EVERYTHING, "Current SS is: " +
                         currSS.toString()); 
                Debug.dp(Debug.EVERYTHING, "Best SS is: " +
                         bestSS.toString()); 

            }
            Debug.dp(Debug.EVERYTHING, "Final SS for i = " + i + " is: " +
                     bestSS.toString()); 
            bestSegs[i] = bestSS; 
        }
        Debug.setDebugLevel(oldDebugLevel); 
    }
    
    EventVec getEventVec(){
        Debug.dp(Debug.EMERGENCY, "Best (final) is:  " +
                 bestSegs[numFrames-1]); 

        return bestSegs[numFrames-1].toEventVec(); 
        
    }
}

