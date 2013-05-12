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
  * A line segment
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Segment.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.pep;   

import java.io.Serializable;

import tclass.ChannelI;
import tclass.util.FastMath;

public class Segment implements Serializable {
  
    static final long serialVersionUID = 1768853445089886624L;
    static final float log4pidiv2 = (float) Math.log(4*Math.PI)/2; 
    static final float minsd = (float) 1e-6;
    int startFrame; 
    int endFrame; 
    //Start and end frames for this example. 
    
    ChannelI channel; 
    // The particular channel for which it is a segment. 
    
    float a;        
    // Value of a in the equation y = a + bt
    
    float b;        
    // Value of b in the equation y = a + bt
    
    float variance; 
    // The variance in this segment from the above equation. 
    
    float numBits; 
    
    float varGlobal;
    
    float noiseFactor;

    float maxBits;
    
    float varNoise;
    
    float totalError; 

    

    
    Segment(int startFrame, int endFrame, ChannelI channel){
        this.startFrame = startFrame; 
        this.endFrame = endFrame; 
        this.channel = channel; 
        linearFit(); 
    }

    float getTotalError(){
        return totalError; 
    }
    Segment(int startFrame, int endFrame, ChannelI channel, 
            float varGlobal, float noiseFactor, float maxBits, float varNoise){
        this.startFrame = startFrame; 
        this.endFrame = endFrame; 
        this.channel = channel; 
        this.varGlobal = varGlobal; 
        this.noiseFactor = noiseFactor; 
        this.maxBits = maxBits; 
        this.varNoise = varNoise; 
        // Debug.dp(Debug.EVERYTHING, "Channel is : " + this.channel.toString()); 
        calcVals(); 
    }
    
    public float getTCent(){ 
        return((endFrame+startFrame)/2); 
    }
  
    public float getYCent(){
        return(valAt(getTCent())); 
    }
    
    public float getGrad(){
        return b; 
    }
 
    public float getDuration(){
        return endFrame-startFrame +1; 
    }
    
    void calcVals(){
        // Completes setting up the values of the object, namely: 
        // a, b, variance and numBits

        linearFit(); 
        calcBits(); 
    
    }
    
    
    /**
       * Get the value of numBits.
       * @return Value of numBits.
       */
    public float getNumBits() {
        return numBits;
    }
    
    void calcBits(){
        // Returns the number of bits. 
        //Ok, this is based on Manganaris. 
        // int n = (endFrame-startFrame+1);
        // numBits = (float) (n*log4pidiv2+n/2.0*((float) Math.log(n))-((float) Sfun.logGamma(n/2.0)) + 
        // (n-1)*((float) Math.log((float) Math.sqrt(variance))));
        // 
        // Based on my previous research and approximations: 
        numBits = bitsHypothesis() + bitsObservation(); 
    }
    float bitsObservation(){
        // Calculates the bits required to express deviation of the
        // observations from the theory, given a normal assumption. 
        // Essentially, it assumes that any value within the range 
        // val-noiseFactor to val+noiseFactor is acceptable. 
        // Proceeds by converting it to a normal distribution and then
        // multiplying all probabilities. WARNING: In future, it might
        // make sense to do the calcs in logs. 
        
        float stddev = (float) Math.sqrt(variance) + 1e-6f; 
        float upperBnd, lowerBnd; 
        float retval = 0; 
        float probability = 1; 
        for(int i=startFrame; i <= endFrame; i++){
            upperBnd = (channel.valAt(i) - valAt(i) + noiseFactor)/stddev; 
            lowerBnd = (channel.valAt(i) - valAt(i) - noiseFactor)/stddev; 
            // System.err.println("Probability = " + probability + " ub = " + upperBnd + " lb =  " + lowerBnd); 
            probability += Math.log(FastMath.normalCDF(upperBnd) - FastMath.normalCDF(lowerBnd));
        }
        return (float) (-probability/Math.log(2)); 
    }
    
    float bitsHypothesis(){
        float retval = 0; 
        
        // Encode start and end values. Assume uniform distribution over
        // the length of the stream
        retval += Math.log(getDuration())/Math.log(2); 
        
        // Encode a and b.
        // Currently a fixed number of bits. Really doesn't affect much. 
        
        retval += 32;
        retval += bitsVariance(variance, varGlobal, maxBits, varNoise); 
        // System.out.println("Hyp = " + retval); 
        return retval;
    }
    
    static float  bitsVariance(float variance, float varglobal, float maxBits,
                               float window){
        
        // This is a weird and wacky function. 
        // The probability distribution function for variance is:
        // f=-maxBits*ln(2)/(varGlobal*(pow(2,-maxBits)-1))*pow(2,-maxBits/varGlobal)*x
        // It has the following properties:
        // 1. int(0, varGlobal)f = 1. 
        // 2. It  decays exponentially - net result: Smaller variances are 
        // preferred. 
        // 3. log2(int(x-varNoise*varGlobal, x+varNoise*varGlobal))
        // = maxBits*x/varGlobal - log2(pow(2, varNoise*maxBits)-pow(2,
        // -varNoise*maxBits))
        // Which is exactly what the below equation calculates. 
        
        if(varglobal != 0){
            float bits = (float) (maxBits*variance/varglobal 
                                  - 
                                  (Math.log(Math.pow(2,window*maxBits)
                                            -Math.pow(2, -window*maxBits)))
                                  /Math.log(2));
            return(bits); 
        }
        else {
            return(0); 
        }
    }
    
    @Override
    public String toString(){
        String retval = new String(); 
        retval = "Start: " + startFrame + " End: " + endFrame + " y = " + 
            a + " + " + b + "t variance = " + variance + " bits = " +
            numBits; 
        return retval; 
    }
    
    
    public float valAt(float t){
        //This returns the value of this segment at a time t. 
        return(a+b*t);
    }
    
    void linearFit(){
        // Calculates the best linear fit (and thus evaluates a and b),
        // but also computes the variance. The below calculations are
        // based on Walpole & Myers, "Probability and Statistics for
        // Engineers & Scientists", 5th Edn, pp365-373 and pp204-207. 

        float sxi, syi, sxiyi, sxi2, syi2;
        // Sum of xi (= timesteps)
        // Sum of yi (= values)
        // Sum of xi*yi
        // sum of xi*xi
        // sum of yi*yi
        
        float sse; 
        //Sum of square of errors. 
        
        float n; 
        // Number of timesteps. 
    
        sxi = syi = sxiyi = sxi2 = syi2 = 0; 
        for(int i = startFrame; i <= endFrame; i++){
            // Debug.dp(Debug.EVERYTHING, "Channel is : " + channel.toString()); 
            float val = channel.valAt(i);  
            sxi += i; 
            syi += val;
            syi2 += val*val;
            sxiyi += i*val;
            sxi2 += i*i; 
        }
        n = (endFrame-startFrame+1);
        b = (n*sxiyi-sxi*syi)/(n*sxi2-sxi*sxi); 
        a = (syi-b*sxi)/n; 
        
        sse = 0;
        
        for(int i = startFrame; i <= endFrame; i++){
            sse += Math.pow(channel.valAt(i)-valAt(i), 2); 
        }

        // Obviously, if the line of best fit is based on 2 points, it's
        // hardly a surprise that the variance should be 0. We need this
        // to avoid a div0 error. 
       
        totalError = (float) Math.sqrt(sse); 
        
        if(n > 2){
            variance = sse/(n-2);
        }
        
        else {
            variance = 0;
        }
    }
    public String printRawValues(){
        StringBuffer retval = new StringBuffer(); 
        for(int i = startFrame; i <= endFrame; i++){
            retval.append(channel.valAt(i) + "\n" ); 
        }
        return retval.toString(); 
    }
    public String printFitValues(){
        StringBuffer retval = new StringBuffer(); 
        for(int i = startFrame; i <= endFrame; i++){
            retval.append(valAt(i) + "\n" ); 
        }
        return retval.toString(); 
    }
}
