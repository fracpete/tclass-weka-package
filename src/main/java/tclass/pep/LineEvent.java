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
import tclass.EventI;

public class LineEvent implements EventI {
    Segment source; 
    float avg; 
    float midTime; 
    float gradient; 
    float duration; 
    
    LineEvent(float avg, float midTime, float gradient, float duration){
        this.source = null; 
        this.avg = avg; 
        this.midTime = midTime; 
        this.gradient = gradient; 
        this.duration = duration; 
    }

    LineEvent(Segment s){
        this.source = s; 
        this.duration  = s.getDuration(); 
        this.gradient = s.getGrad(); 
        this.avg = s.getYCent(); 
        this.midTime = s.getTCent(); 
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
        return "Line Segment: midTime = "+midTime+ " durn = " + duration 
            +" gradient = " + gradient + " avg = " + avg  ; 
    }
    public float getDuration(){ return duration; }
    public float getMidtime(){ return midTime; }
    public String printRawValues(){ return source.printRawValues(); }
    public String printFitValues(){ return source.printFitValues(); }
}
