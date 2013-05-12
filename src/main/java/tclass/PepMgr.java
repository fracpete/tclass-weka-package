/**
  * A prototype manager for PEPs (parametrised event primitives). 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: PepMgr.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.util.*; 
import tclass.pep.*; 
import tclass.util.*; 
public class PepMgr {
    Hashtable registry = new Hashtable(); 
    static PepMgr instance; 
    
    /**
     * The constructor. A good place to register the various 
     * datatypes. 
     * 
     *
     */

    public PepMgr(){
	// Registrations go here. 
	// Usually of the form: 
	// register((PepI) new YourPep()); 
	register((PepI) new RLE()); 
	register((PepI) new LocalMax()); 
	register((PepI) new LocalMin()); 
	register((PepI) new Increasing()); 
	register((PepI) new Decreasing()); 
	register((PepI) new Plateau()); 
	register((PepI) new LineSeg()); 
	register((PepI) new RandomLineSeg()); 
        register((PepI) new PreExtracted()); 
    }

    public static PepMgr getInstance(){ 
	if(instance == null){
	    instance = new PepMgr(); 
	    return instance; 
	}
	else {
	    return instance; 
	}
    }

    public void register(PepI prototype){
	registry.put(prototype.name(), prototype); 
    }
    
   /** 
     * Gets a clone of the PEP by name. 
     *
     * WARNING: This does clone the PEP. This is necessary for safety
     * reasons. Otherwise, other people's code could modify the things
     * stored in the prototype. This is BAD.
     *
     * Note that the domain description object is necessary; since
     * global extractors need to have access to the underlying data
     * they are extracting from. 
     * 
     * This is the default version of the object. 
     *
     * @param name name of the prototype to retrieve
     * @param d Domain description that this global is going to be 
     *           used for. 
     * @return A clone of the prototype. null 
     * if there is no such prototype known.  */

    public PepI getClone(String name, DomDesc d){
	PepI p =  (PepI) registry.get(name); 
	if(p == null)
	    return null; 
	else {
	    PepI pclone = (PepI) p.clone(); 
	    pclone.setDomDesc(d); 
	    return pclone;  
	}
    }


    /**
     *  Gets a list of all the PEPs available by name
     *
     */

    public String[] getNames(){
	String[] retval = new String[registry.size()]; 
	int i = 0; 
	for(Enumeration e = registry.keys(); e.hasMoreElements(); i++){
	    retval[i] = (String) e.nextElement(); 
	}
	return retval; 
    }

    public static void main(String[] args) throws Exception {
        // Ok, added to print  out data for all the parameters and so on. 
        Debug.setDebugLevel(Debug.EVERYTHING); 
        PepMgr pm = PepMgr.getInstance();
        String[] peps = pm.getNames(); 
        for(int i=0; i < peps.length; i++){
            System.out.println("\nMetafeature: " + peps[i]); 
            System.out.println("-----------------------------------"); 
            ParamVec pv =  ((PepI) pm.registry.get(peps[i])).getParamList(); 
            for(int j=0; j < pv.size(); j++){
                System.out.println(pv.elAt(j).toString()); 
            }
        }

        /*
	
	//How do I debug thee?
	Debug.setDebugLevel(Debug.EVERYTHING); 
	//Let's see ... first let's load a domain description:
	DomDesc d = new DomDesc("tests/test.tdd"); 
	// And now some data ... 
	StreamI s = (StreamI) new Stream("tests/test.tsd", d); 
	//Get ourselves the Manager: 
	PepMgr pm = PepMgr.getInstance();
	PepI rle = pm.getClone("rle", d); 
	rle.setParam("channel", "Y"); 
	rle.setParam("minrun", "3"); 
	
	//Now apply them. 
	System.out.println("---- %%% Test results %%% ----"); 

	// Print out the data format. 
	System.out.println(rle.getEventDesc()); 

	// And now let's print out the returned events.  
	System.out.println("Results are: "); 
	System.out.println(rle.findEvents(s).toString()); 
        */
    }
}
