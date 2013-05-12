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
  * @version $Id: Increasing.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
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

public class Increasing implements PepI {

    static final String name = "inc"; 
    static final String description = "Finds period of increase."; 
    private DomDesc domDesc = null; 
    private int chanIndex; 
    private int noiseIgnore = 1; 
    private int minDurn = 3; 
    private int smoothSize = 1; 
    private boolean useRelativeTime = false; 
    private boolean useRelativeHeight = false; // Compute heights + values relative
    // to the global mean. 
    private float offset = 0; // this is used as a hack. 
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
        else if(p.equals("mindurn")){
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
        else if(p.equals("smoothSize")){
            try {
                smoothSize = Integer.parseInt(v); 
            }
            catch(NumberFormatException ne){
                throw new InvalidParameterException(p, v, v + " must be an integer."); 
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
        pv.add(new Param("smoothSize", "Smoothing window size", "1")); 
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
        ed.addParam("gradient", dtm.getClone("continuous")); 
        ed.addParam("duration", dtm.getClone("continuous")); 
        return (EventDescI) ed; 
    }

    float[] makeDiff(ChannelI c){
        float[] retval = new float[c.numFrames()];
        float[] rawVals = new float[c.numFrames()]; 
        float avg = 0; 
        for(int i=0; i < retval.length; i++){
            rawVals[i] = c.valAt(i); 
        }
        if(smoothSize != 1){
            smoothDiff(rawVals); 
        }

        float prevVal = 0; 
        for(int i=0; i < retval.length; i++){
            retval[i] = rawVals[i]-prevVal; 
            prevVal = rawVals[i]; 
            avg+= rawVals[i]; 
        }
        avg /= c.numFrames(); 
        if(useRelativeHeight){
            offset = avg; // HACK!!! This is the hack we use to implement relative height. 
        }
        return retval; 
    }


    float endExtend(float[] input, int index){
        if(index < 0) return input[0]; 
        else if(index >= input.length) return input[input.length-1];
        else return input[index]; 
    }
    
    void smoothDiff(float[] input){
        // Smoothsize must be odd. 
        float windowSum=0; 
        // fill the window sum
        for(int i=-smoothSize/2; i <= smoothSize/2; i++){
            windowSum += endExtend(input, i); 
        }
        // And now run it along
        for(int i=0; i < input.length; i++){
            //    Debug.dp(Debug.PROGRESS, "Smoothing at " + i + " from " + input[i] + " to " + windowSum/smoothSize); 
            input[i] = windowSum/smoothSize; 
            windowSum-= endExtend(input, i-smoothSize/2); 
            windowSum+= endExtend(input, i+smoothSize/2); 
        }
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
        // So now, we try to make long chains. 
        int startPoint=0; 
        int currentPoint=0; 
        int endPoint=0; 
        int misses=0; 
        boolean shouldContinue = true; 
        while(currentPoint < difference.length){ 
            // In other words, loop over the whole signal. 
            Debug.dp(Debug.EVERYTHING, "Now at " + currentPoint); 
            if(difference[currentPoint] > 0){
                endPoint = currentPoint; 
                Debug.dp(Debug.EVERYTHING, "diff > 0 " + currentPoint);
                currentPoint++; 

            }
            else {
                currentPoint++; 
                misses++; 
            }
            if(misses > noiseIgnore){
                Debug.dp(Debug.EVERYTHING, "Attempting " + startPoint + " " + endPoint);                     
                // End of the run. So now dump. 
                if(endPoint-startPoint +1 > minDurn){
                    Debug.dp(Debug.EVERYTHING, "Adding " + startPoint + " " + endPoint); 
                    ev.add(makeInc(startPoint, endPoint-1, c, difference, useRelativeTime, offset)); 
                }
                currentPoint++; 
                startPoint = currentPoint; 
                endPoint =currentPoint; 
                misses = 0; 
            }
           
        }
        // But what if it is at the end? So we have to have a special case here too ... 
        if(endPoint-startPoint +1 > minDurn){
            Debug.dp(Debug.EVERYTHING, "Adding " + startPoint + " " + endPoint); 
            ev.add(makeInc(startPoint, endPoint-1, c, difference, useRelativeTime, offset)); 
        }

        Debug.dp(Debug.EVERYTHING , "findEvents on " + s.getComment() + " " + domDesc.getChannel(chanIndex).getName() +" returns " + ev + d2str(difference)); 
        Debug.setDebugLevel(oldDebugLevel); 
        return ev; 
    }

    EventI makeInc(int start, int end, ChannelI c, float[] diff, boolean useRelativeTime, float offset){
        int totlength = diff.length;
        float duration = end - start + 1; 
        float midTime = (end + start)/((float) 2.0); 
        // And now the messy bits ... gradients and averages. 
        // This stuff based on Walpole and myers. 
        float xave = midTime; 
        // Compute yave
        float ytotal = 0; 
        for(int i=start; i <= end; i++){
            ytotal += c.valAt(i); 
        }
        float yave = ytotal/duration; 
        // Eqtn for gradient is: sum(1, n)((x[i]-xave).(y[i]-yave))/sum(1,n)(x[i]-xave)
        float xiyi=0; 
        float xi2=0; 
        for(int i=start; i <= end; i++){
            float xid = i-xave;
            xiyi += xid*(c.valAt(i)-yave);
            xi2 += xid*xid;     
        }
        float gradient = xiyi/xi2; 
        
        if(useRelativeTime){
            duration /= totlength; 
            midTime /= totlength;
            gradient *= totlength; 
        }
        return new IncEvent(yave-offset, midTime, gradient, duration); 
    } 
}

class IncEvent implements EventI {
    float avg; 
    float midTime; 
    float gradient; 
    float duration; 
    
    IncEvent(float avg, float midTime, float gradient, float duration){
        this.avg = avg; 
        this.midTime = midTime; 
        this.gradient = gradient; 
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
            return gradient; 
        }
        if(i==3){
            return duration; 
        }
        else return 0; 
    }
    
    @Override
    public String toString(){
        return "Increasing: midTime = " + midTime +  " avg = " + avg + 
            " gradient = " + gradient + " durn = " + duration; 
    }

    public float getDuration(){ return duration; }
    public float getMidtime(){ return midTime; }
}
