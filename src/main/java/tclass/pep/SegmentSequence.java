
package tclass.pep;   
import tclass.*; 
import tclass.util.*; 
import java.util.*; 

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

