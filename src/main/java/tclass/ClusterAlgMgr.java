/**
  * This is the prototype manager for clustering algorithms. 
  * this is a singleton class. For discussion of singleton classes,
  * and a clearer example of prototype managers, see the DataTypeMgr 
  * class. 
  * 
  * @author Waleed Kadous
  * @version $Id: ClusterAlgMgr.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ */

package tclass;   


import java.io.*; 
import java.util.*; 
import tclass.util.*; 

import tclass.clusteralg.*; //The prototypes live here. 

public class ClusterAlgMgr {
    Hashtable registry = new Hashtable(); 
    static ClusterAlgMgr instance; // This is the singleton. 
    
    /**
     * The constructor. A good place to register the various 
     * global extractors. 
     *
     */
    
    public ClusterAlgMgr(){
        //Registering looks like this: 
        //register((ClusterAlgI) new yourClusterAlgClassHere());
        register((ClusterAlgI) new KMeans()); 
        register((ClusterAlgI) new EMClusterer()); 
        register((ClusterAlgI) new GainCluster()); 
    }
    
    
    /** 
     * Gets the instance of the Global Extractor Manager. 
     * If it doesn't exist yet, it makes it; otherwise,
     * it just returns the existing one.
     */ 

    public static ClusterAlgMgr getInstance(){ 
        if(instance == null){
            instance = new ClusterAlgMgr(); 
            return instance; 
        }
        else {
            return instance; 
        }
    }
    
    public void register(ClusterAlgI prototype){
        registry.put(prototype.name(), prototype); 
    }
   
    /** 
     * Gets a clone of the Global Extractor by name. WARNING: This does
     * clone it. This is necessary for safety reasons. Otherwise, 
     * other people's code could modify the things stored in the
     * prototype. This is BAD. 
     *
     * Note that the domain description object is necessary; since
     * global extractors need to have access to the underlying data
     * they are extracting from. 
     * 
     * This is the default version of the object. 
     *
     * @param name name of the prototype to retrieve
     * @param d Domain description that this global is going to be 
     *   used for. 
     * @return A clone of the prototype. null 
     * if there is no such prototype known.  */

    public ClusterAlgI getClone(String name, DomDesc d, EventDescVecI edv){
        ClusterAlgI ca =  (ClusterAlgI) registry.get(name); 
        if(ca == null)
            return null; 
        else {
            ClusterAlgI caclone = (ClusterAlgI) ca.clone(); 
            caclone.setDomDesc(d); 
            caclone.setEventDescVec(edv); 
            return caclone;  
        }
    }

    /**
     *  Gets a list of all the Global Extractors available by name
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

        //How do I debug thee?
        Debug.setDebugLevel(Debug.EVERYTHING); 
        //Let's see ... first let's load a domain description:
        DomDesc d = new DomDesc("tests/test.tdd"); 
        // And now some data ... 
        StreamI s = (StreamI) new Stream("tests/test.tsd", d); 
        //Get ourselves the Manager: 
        ClusterAlgMgr cam = ClusterAlgMgr.getInstance();
    }    
}
