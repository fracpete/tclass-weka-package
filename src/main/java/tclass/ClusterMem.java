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
  * All the information associated with the cluster information. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClusterMem.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public class ClusterMem {
    public ClusterI cluster; 
    String eventName; 
    float confidence; 
    float midtime = -1;  // Added late in the piece to support a more temporal model. 
    float duration = -1; 
    int eventNum;
    EventI origEvent; 

    
    /**
       * Get the value of duration.
       * @return Value of duration.
       */
    public float getDuration() {return duration;}
    
    /**
       * Set the value of duration.
       * @param v  Value to assign to duration.
       */
    public void setDuration(float  v) {this.duration = v;}
    
    
    /**
       * Get the value of midtime.
       * @return Value of midtime.
       */
    public float getMidtime() {return midtime;}
    
    /**
       * Set the value of midtime.
       * @param v  Value to assign to midtime.
       */
    public void setMidtime(float  v) {this.midtime = v;}
    

    /**
       * Get the value of origEvent.
       * @return Value of origEvent.
       */
    public EventI getOrigEvent() {return origEvent;}
    
    /**
       * Set the value of origEvent.
       * @param v  Value to assign to origEvent.
       */
    public void setOrigEvent(EventI  v) {this.origEvent = v;}
    
    public ClusterMem(ClusterI cluster, float confidence){
        this.cluster = cluster; 
        this.confidence = confidence; 
    }

    /**
       * Get the value of eventNum.
       * @return Value of eventNum.
       */
    public int getEventNum() {
        return eventNum;
    }
    
    /**
       * Set the value of eventNum.
       * @param v  Value to assign to eventNum.
       */
    public void setEventNum(int  v) {this.eventNum = v;}
    
    
    /**
       * Get the value of eventName.
       * @return Value of eventName.
       */
    public String getEventName() {
        return eventName.replace('-','_');
    }
    
    /**
       * Set the value of eventName.
       * @param v  Value to assign to eventName.
       */
    public void setEventName(String  v) {this.eventName = v;}
    
    public ClusterI getCluster(){
        return cluster; 
    }
    
    public float getConf(){
        return confidence; 
    }
    
    public void setConf(float newconf){
        confidence = newconf; 
    }
    
    @Override
    public String toString(){
        String retval = new String("(" + cluster.getName() + " conf: " + confidence + " time: " + midtime  + " dur: " + duration + ")\n"); 
        if(origEvent != null){
            retval += origEvent.toString() + "\n"; 
        }
        return retval; 
    }
    
    public float startTime(){
        return midtime - duration/2.0f; 
    }
     public float endTime(){
        return midtime + duration/2.0f; 
    }
}
