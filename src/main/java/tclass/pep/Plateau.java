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
  * First attempt to look at difference signal. LocalMax and Min will
  * proboably be rewritten after this. 
  * 
  * @author Waleed Kadous
  * @version $Id: Plateau.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.pep;   
import tclass.ChannelDesc;
import tclass.ChannelI;
import tclass.DataTypeI;
import tclass.DataTypeMgr;
import tclass.DomDesc;
import tclass.EventDesc;
import tclass.EventDescI;
import tclass.EventI;
import tclass.EventVec;
import tclass.EventVecI;
import tclass.InvalidParameterException;
import tclass.Param;
import tclass.ParamVec;
import tclass.PepI;
import tclass.StreamI;
import tclass.util.Debug;

public class Plateau implements PepI {

    static final String name = "plat"; 
    static final String description = "Finds period of flatness."; 
    private DomDesc domDesc = null; 
    private int chanIndex; 
    private int noiseIgnore = 2; 
    private int minDurn = 4;
    private float max=-Float.MAX_VALUE; 
    private float min=Float.MAX_VALUE; 
    private float mGR = (float) 0.2; 
    private boolean useRelativeTime = false;
    private boolean useRelativeHeight = false; 
    private float offset = 0; 
    /** 
     * Gets the name of the PEP. Used by the prototype manager 
     * as a key. 
     *
     * @return A key representing this particular PepPT
     */ 
    public String name(){
        return name; 
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
     * Set the domain description
     */ 
    
    public void setDomDesc(DomDesc d){
        domDesc = d; 
    }

    
    /** 
     * Provides a description of the PepI. This description explains
     * what the basic idea of the PEP is (i.e. the sort of shapes it
     * tried to find). It should also explain any potential
     * configuration options that may
     * be used to configure the object, using the configure option. 
     * 
     * @return The description of this class. 
     */ 

    public String description(){ 
        return description; 
    }

    /**
     * Configures this instance so that parameter <i>p</i> has
     * value <i>v</i>. 
     *
     * @param p the parameter to set. 
     * @param v the value of the parameter. 
     *
     */

    public void setParam(String p, String v) throws InvalidParameterException {
        if(p.equals("noise")){
            try {
                noiseIgnore = Integer.parseInt(v); 
       
            }
            catch(NumberFormatException ne){
                throw new InvalidParameterException(p, v, v + " must be an integer."); 
            }
        }
        if(p.equals("mindurn")){
            try {
                minDurn = Integer.parseInt(v); 
       
            }
            catch(NumberFormatException ne){
                throw new InvalidParameterException(p, v, v + " must be an integer."); 
            }
        }
        else if(p.equals("channel")){
            // So they want us to do max on a channel. 
            // Any channel will do, provided it exists. 
            chanIndex = domDesc.getChanIndex(v); 
            if(chanIndex == -1){
                throw new InvalidParameterException(p, v, "Unknown channel " + v); 
            }
        }
        else if(p.equals("useRelativeTime")){
            if(v.equals("true")){
                useRelativeTime = true; 
            }
            else if(v.equals("false")){
                useRelativeTime = false; 
            }
            else {
                throw new InvalidParameterException(p, v, v + " must be true or false."); 
            }
        }
         else if(p.equals("useRelativeHeight")){
            if(v.equals("true")){
                useRelativeHeight = true; 
            }
            else if(v.equals("false")){
                useRelativeHeight = false; 
            }
            else {
                throw new InvalidParameterException(p, v, v + " must be true or false."); 
            }
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
    public ParamVec getParamList() {
        ParamVec pv = new ParamVec(); 
        pv.add(new Param("noise", "Number of errors before discontinuing", "1")); 
        pv.add(new Param("channel", "Channel to work on", "First")); 
        pv.add(new Param("useRelativeTime", "Use time relative to total length", "false")); 
        return pv; 
    }

    /** 
     * Now we get to the complicated stuff.
     *
     * Get a description of the events created by this prototype. 
     * This includes the description of the number of parameters, 
     * the names of the parameters and other info. Note also that
     * this is included free with any EventVec's we return. 
     * 
     */
    public EventDescI getEventDesc(){
        // The first parameter is the value, which is of
        // course the same as the type of the channel. 
       

        EventDesc ed = new EventDesc(); 
        ChannelDesc cd = domDesc.getChannel(chanIndex); 
        DataTypeMgr dtm = DataTypeMgr.getInstance();
        ed.addParam("midtime", dtm.getClone("continuous")); 
        ed.addParam("avg", dtm.getClone("continuous")); 
        ed.addParam("duration", dtm.getClone("continuous")); 
        return (EventDescI) ed; 
    }

    float[] makeDiff(ChannelI c){
        float[] retval = new float[c.numFrames()];
        float prevVal = c.valAt(0); 
        float avg = 0; 
        for(int i=0; i < retval.length; i++){
            avg += c.valAt(i); 
            retval[i] = c.valAt(i)-prevVal; 
            prevVal = c.valAt(i); 
            if(c.valAt(i) > max){
                max = c.valAt(i); 
            }
            if(c.valAt(i) < min){
                min = c.valAt(i); 
            }
        }
        avg /= c.numFrames(); 
         if(useRelativeHeight){
            offset = avg; // HACK!!! This is the hack we use to implement relative height. 
        }
        return retval; 
    }
    
    static String d2str(float[] data){
        StringBuffer retval = new StringBuffer(data.length*12); 
        retval.append("[ "); 
        for(int i=0; i < data.length; i++){
            retval.append(data[i] + " ");    
        }
        retval.append("]"); 
        return retval.toString(); 
    }

    /**
     * The finding function for this PEP. Returns all the events
     * of the form this PEP represents. Returns an EventVecI. 
     * 
     * @param s The channel that we want the finding function to
     * operate on. 
     * @return A vector of the events of the type extracted by this
     * PEP. 
     */

    public EventVecI findEvents(StreamI s){
        int oldDebugLevel = Debug.getDebugLevel(); 
        // Debug.setDebugLevel(Debug.EVERYTHING); 
        EventVec ev = new EventVec(); 
        ev.setEventDesc(getEventDesc()); 
        // Grab our channel. 
        ChannelI c = s.chanAt(chanIndex); 
        int numFrames = c.numFrames(); 
        DataTypeI d = domDesc.getChannel(chanIndex).getDataType(); 
        float[] difference = makeDiff(c); 
        //Compute average gradient magnitude.
        float sum=0;
        for(int i=0; i < difference.length; i++){
            sum += Math.abs(difference[i]); 
        }
        float avgGradient = sum/difference.length; 
        float maxAvgGradient = mGR*avgGradient; 
        Debug.dp(Debug.EVERYTHING, "mAG = " + maxAvgGradient); 
        // So now, we try to make long chains. 
        int startPoint=0; 
        int currentPoint=0; 
        int endPoint=0; 
        int misses=0;
        boolean shouldContinue = true; 
        float sumSoFar=0; 
        while(currentPoint < difference.length){ 
            sumSoFar += difference[currentPoint]; 
            Debug.dp(Debug.EVERYTHING, "Now at " + currentPoint + " with avgG" + sumSoFar/(currentPoint-startPoint+1)); 

            if(Math.abs(sumSoFar/(currentPoint-startPoint+1)) > maxAvgGradient){
                currentPoint++; 
                misses++; 
            }
            else {
                // This is the good case; 
                currentPoint++; 
                endPoint++; 
            }
            if(misses > noiseIgnore){
                // End of the run. So now dump. 
                Debug.dp(Debug.EVERYTHING, "Finishing line."); 
                if(endPoint-startPoint +1 > minDurn){
                    ev.add(makePlateau(startPoint, endPoint-1, c, difference, useRelativeTime, offset)); 
                }
                currentPoint++; 
                startPoint = currentPoint; 
                endPoint =currentPoint; 
                misses = 0; 
                sumSoFar = 0; 
            }
        }
        // Just in case there is one event still waiting to go through. 
        Debug.dp(Debug.EVERYTHING, "Finishing line."); 
        if(endPoint-startPoint +1 > minDurn){
            ev.add(makePlateau(startPoint, endPoint-1, c, difference, useRelativeTime, offset)); 
        }

        Debug.dp(Debug.EVERYTHING , "findEvents on " + s.getComment() + " (maxAvgGradient = " + maxAvgGradient +") " + domDesc.getChannel(chanIndex).getName() +" returns " + ev + d2str(difference)); 
        Debug.setDebugLevel(oldDebugLevel); 
        return ev; 
    }

    EventI makePlateau(int start, int end, ChannelI c, float[] diff, boolean useRelativeTime, float offset){
        int totlength = diff.length; 
        Debug.dp(Debug.FN_PARAMS, "Making PlatEvent " + start + " to " +  end); 
        float duration = end - start + 1; 
        float midTime = (end + start)/((float) 2.0); 
        // And now the messy bits ... gradients and averages. 
        // This stuff based on Walpole and myers. 
        // Compute yave
        float ytotal = 0; 
        for(int i=start; i <= end; i++){
            ytotal += c.valAt(i); 
        }
        float yave = ytotal/duration;
        if(useRelativeTime){
            midTime /= totlength; 
            duration /= totlength; 
        }
        return new PlatEvent(yave-offset, midTime, duration); 
    } 
}

class PlatEvent implements EventI {
    float avg; 
    float midTime; 
    float gradient; 
    float duration; 
    
    
    PlatEvent(float avg, float midTime, float duration){
        this.avg = avg; 
        this.midTime = midTime; 
        this.duration = duration; 
    }
    
    public float valOf(int i){
        if(i==0){
            return midTime; 
        }
        if(i==1){
            return avg; 
        }
        if(i==2){
            return duration; 
        }
        else return 0; 
    }
    
    @Override
    public String toString(){
        return "Plateau: midTime = "+midTime
            +   " avg = " + avg + " durn = " + duration; 
    }

    public float getDuration(){ return duration; }
    public float getMidtime(){ return midTime; }


}
