package tclass.pep;   
import tclass.*; 
import tclass.util.*; 
import java.util.*; 

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
    
    public String toString(){
        return "Line Segment: midTime = "+midTime+ " durn = " + duration 
            +" gradient = " + gradient + " avg = " + avg  ; 
    }
    public float getDuration(){ return duration; }
    public float getMidtime(){ return midTime; }
    public String printRawValues(){ return source.printRawValues(); }
    public String printFitValues(){ return source.printFitValues(); }
}
