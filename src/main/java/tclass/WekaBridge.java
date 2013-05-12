package tclass;   
import java.util.*; 
import weka.core.*; 
import tclass.util.*; 
/**
  * Class for converting from our interna formats to Weka's. 
  * I'd rather keep things as separate as possible. 
  * 
  * @author Waleed Kadous
  * @version $Id: WekaBridge.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public class WekaBridge {
    
    /** 
     *  Makes an instances objects. 
     */
 
    public static Instances makeInstances(ClassStreamEventsVecI
                                          csevi, ClassDescVecI cdvi, EventDescVecI edvi, int evPos, boolean
                                          ignoreClasses, boolean ignoreTime) throws
        Exception {
        // System.out.println("Asked to ignore classes."); 
        
        Instances retval; 
        // Ok, first convert attributes. 
        StreamEventsVecI sevi = csevi.getStreamEventsVec(); 
        ClassificationVecI cvi = csevi.getClassVec(); 
        // System.out.println("Event Desc Vec = " + edvi); 
        int numAtts = edvi.elAt(evPos).numParams(); 
        if(ignoreTime){
            numAtts = numAtts-1; 
        }
        FastVector atts = makeAttVector(edvi.elAt(evPos), ignoreTime); 
        if(!ignoreClasses){
            int size = cdvi.size(); 
            FastVector classes = new FastVector(size); 
            for(int i=0; i < size; i++){
                classes.addElement(cdvi.getClassLabel(i)); 
            }
            atts.addElement(new Attribute("class", classes)); 
        }
        int size = csevi.size(); 
        retval = new Instances(edvi.elName(evPos), atts, size); 
        if(!ignoreClasses){
            retval.setClassIndex(numAtts); 
        }
        else {
            retval.setClassIndex(-1); 
        }
        for(int i=0; i < size; i++){
            // Get events of this type: 
            EventVecI evi = sevi.elAt(i).getEvents(evPos); 
            int numEvents = evi.size(); 
            for(int j=0; j < numEvents; j++){
                // System.out.println("Adding event " + j + " of stream " + i); 
                Instance thisInst = new Instance(atts.size()); 
                thisInst.setDataset(retval); 
                EventI thisEvent = evi.elAt(j); 
                
                for(int k= (ignoreTime ? 1: 0); k < edvi.elAt(evPos).numParams(); k++){
                    thisInst.setValue(k-(ignoreTime ? 1: 0), thisEvent.valOf(k)); 
                }
                if(!ignoreClasses){
                    thisInst.setValue(numAtts,
                                      cdvi.getClassLabel(cvi.elAt(i).getRealClass())); 
                }
                retval.add(thisInst); 
            }
        }
        return retval; 
    }    

    public static Instance makeInstance(EventI ei, EventDescI edi, boolean ignoreTime){
        // A speed hack. My code is getting pretty f*ing ugly. 
        int numParams = edi.numParams(); 
        double[] eventdbl = new double[numParams]; 
        for(int i=(ignoreTime ? 1: 0); i < numParams; i++){
            eventdbl[i-(ignoreTime ? 1: 0)] = (double) ei.valOf(i) ; 
        }
        Instance retval = new Instance(1.0, eventdbl); 
        return retval; 
    }
    
    public static Instances makeInstances(ClassStreamAttValVecI csavvi, String name) throws Exception{
        StreamAttValVecI origData = csavvi.getStreamAttValVec(); 
        AttDescVecI format = origData.getDescription(); 
        ClassificationVecI classes = csavvi.getClassVec();
        ClassDescVecI classInfo = classes.getClassDescVec(); 
        FastVector instanceDesc =  makeAttVector(format, classInfo); 
        int numInstances = origData.size(); 
        int numAtts = format.size(); 
        Instances retval = new Instances(name, instanceDesc, numInstances); 
        retval.setClassIndex(numAtts); // Set class to last attribute. 

        for(int i=0; i < numInstances; i++){
            Instance thisInst = new Instance(numAtts+1); // To include the class.  
            thisInst.setDataset(retval); 
            StreamAttValI thisStream = origData.elAt(i); 
            for(int j=0; j < numAtts; j++){
                thisInst.setValue(j, thisStream.getAtt(j)); 
            }
            thisInst.setValue(numAtts,
                                      classInfo.getClassLabel(classes.elAt(i).getRealClass())); 
            retval.add(thisInst); 
        }
        return retval; 
    }


    public static FastVector makeAttVector(EventDescI edi, boolean ignoreTime){
        int size = edi.numParams(); 
        if(ignoreTime){
            size--; 
        }
        FastVector retval = new FastVector(size); 
        int i; 
        for(i= (ignoreTime? 1: 0); i <  edi.numParams(); i++){
            retval.addElement(makeAtt(edi.paramName(i), edi.getDataType(i))); 
        }
        return retval; 
    }

    public static FastVector makeAttVector(AttDescVecI advi, ClassDescVecI classInfo){
        int numAtts = advi.size(); 
        FastVector retval = new FastVector(numAtts+1); 
        for(int i=0; i < numAtts; i++){
            retval.addElement(makeAtt(advi.elAt(i))); 
        }
        int numClasses = classInfo.size(); 
        FastVector classes = new FastVector(numClasses); 
        for(int i=0; i < numClasses; i++){
            classes.addElement(classInfo.getClassLabel(i)); 
        }
        retval.addElement(new Attribute("class", classes)); 
        return retval; 
    }

    public static Attribute makeAtt(AttDescI adi){
        return makeAtt(adi.getName(), adi.getDataType()); 
    }

    /** Hacked version!! Only handles numeric attributes in events,
     * which is dodgy. 
     */ 
    public static Attribute makeAtt(String name, DataTypeI dt){
        return new Attribute(name); 
    }
}

