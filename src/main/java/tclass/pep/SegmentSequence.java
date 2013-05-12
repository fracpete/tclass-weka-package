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


package tclass.pep;   
import java.util.Vector;

import tclass.EventVec;
import tclass.util.Debug;
import tclass.util.FastMath;

public class SegmentSequence implements Cloneable {
    private static final float ln2 = (float) Math.log(2.0); 
    private Vector segs = new Vector(); 
    private float numBitsLines=0;
    private int length = 0; 

    int getLength(){
        return length; 
    }
    
    void add(Segment s){
        segs.addElement(s);
        length = s.endFrame; 
                                                                             
    }

    /**
     * Clone the current object. 
     *
     */ 
    @Override
    public Object clone()
    {
        try {
            SegmentSequence retval = (SegmentSequence) super.clone(); 
            // But now we add our own magic to fix up the little problem ...
            retval.segs = (Vector) segs.clone(); 
            return retval; 
        }
        catch (CloneNotSupportedException e){
            // Can't happen, or so the java programming book says
            throw new InternalError(e.toString()); 
        }
    }

    /** Compute the number of bits used in a segment sequence. 
     * The formula is: 
     *
     * L_ppm(y) = log2(n-1) + log2(Choose(n-1,k)) + sum{i=0..k} Lc(y_i)
     * 
     * This is from Manganaris, eqn 3.6 p 31. 
     *
     * In our case: 
     * n = endFrame
     * k = number of segments = size(); 
     * Lc(y_i) = elAt(i).numBits(); 
     */

    public float getNumBits(){
        int size = size(); 
        float numBitsSegs = 0; 
        float numBitsPPM; 
        for(int i=0; i < size; i++){
            numBitsSegs += elAt(i).getNumBits(); 
        }

        numBitsPPM = (float) (Math.log(length-1)/FastMath.ln2 
            + FastMath.log2Choose(length-1,size()));
        Debug.dp(Debug.EVERYTHING, "length = " + length + " size = " + size + " l2c = "
                 + FastMath.log2Choose(length,size()));
        return (numBitsSegs + numBitsPPM); 
        
    }
    
    @Override
    public String toString(){
        int size = size(); 
        String retval = "SegSeq has " + size + " segments. Total bits: " + getNumBits() +". These  are: "; 
        for(int i=0; i < size; i++){
            retval += elAt(i).toString() +"\n"; 
        }
        return retval; 
    }

    Segment elAt(int i){
        return (Segment) segs.elementAt(i); 
    }

    float getTotalError(float sd){
        float x = 0.1f; 
        float totalError = 0; 
        for(int i=0; i < size(); i++){
            totalError += elAt(i).getTotalError(); 
        }
        return (float) Math.log(totalError/sd)+x*size(); 
    }
    
    int size(){
        return segs.size(); 
    }
    EventVec toEventVec(){
        EventVec retval = new EventVec(); 
        int size = size(); 
        for(int i=0; i < size; i++){
            retval.add(new LineEvent(elAt(i))); 
        }
        return retval; 
    }
}

