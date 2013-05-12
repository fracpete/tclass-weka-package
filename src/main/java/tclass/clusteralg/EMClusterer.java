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
  * This is an example of a clustering algorithm, in other words, 
  * what we expect our clustering algorithms to be able to do. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: EMClusterer.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.clusteralg;   
import tclass.ClassStreamEventsVecI;
import tclass.ClusterAlgI;
import tclass.ClusterI;
import tclass.ClusterMem;
import tclass.ClusterVecI;
import tclass.DomDesc;
import tclass.EventDescI;
import tclass.EventDescVecI;
import tclass.EventI;
import tclass.EventVecI;
import tclass.InvalidParameterException;
import tclass.Param;
import tclass.ParamVec;
import tclass.StreamEventsI;
import tclass.StreamI;
import tclass.WekaBridge;
import weka.core.Instance;
import weka.core.Instances;

public class  EMClusterer implements ClusterAlgI { 
    
    String baseName = "em";
    String description = "Implements an EM clustering algorithm by using WEKA to do everything";
    EventDescVecI edvi = null; 
    DomDesc domDesc = null; 
    private int pepIndex = 0; 
    private boolean ignoreClasses = true; 
    private boolean ignoreTime = false; 
    private boolean autoNumClusters = true; 
    int numClusters = 0; 

    /**
     * Name of this clustering algorithm. 
     */ 
    
    public String name(){
	return baseName;
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

    
    public void setDomDesc(DomDesc dd){
	domDesc = dd; 
    }

    /**
     * Set the description of the incoming Class Stream Events Vector
     * Note that we need this first ... for the parsing of values,
     * before we do any actual processing. I expect that the
     * ClusterAlgMgr should have a copy of it and passes it through in
     * the constructor, pretty much like the Domain description for the 
     * GlobalExtrMgr
     */

    public void setEventDescVec(EventDescVecI events){
	edvi = events; 
    }
    
     /** 
     * Provides a description of the clustering algorithm.

     * This description explains what the basic idea of the clustering
     * algorihtm is (i.e. the sort of shapes it tried to find). It
     * should also explain any potential configuration options that
     * may be used to configure the object, using the configure
     * option.
     * 
     * @return The description of this class.  
     */
    
    public String description(){
	return description; 
    }


        /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */    
    public ParamVec getParamList(){
        ParamVec pv = new ParamVec(); 
        pv.add(new Param("metafeature", "The name of the metafeature to apply this to", "First")); 
        pv.add(new Param("numclusters", "Number of clusters. Possible values: auto or a number", "auto")); 
        pv.add(new Param("ignoreclass", "Completely ignore class information. Can be true or false", "false")); 
        pv.add(new Param("ignoretime", "Ignore time (assuming the first attribute of any metafeature is time).. Can be true or false", "false")); 
	return pv; 
    }
    
    public void setParam(String p, String v) throws InvalidParameterException {
        if(p.equals("metafeature")){
	    // So they want to use a particular metafeature. 
            //Try to find the metafeature. 
            pepIndex = edvi.elIndex(v); 
            if(pepIndex == -1){
                throw new InvalidParameterException(p, v, "Unknown metafeature " + v); 
            }
        }
        else if(p.equals("numclusters")){
            if(v.equals("auto")){
                autoNumClusters = true; 
            }
            else {
                try {
                    numClusters = Integer.parseInt(v); 
                }
                catch(NumberFormatException nfe){
                    throw new InvalidParameterException(p, v, "Could not understand number of exceptions."); 
                    
                }
            }
        }
        else if(p.equals("ignoretime")){
            if(v.equals("true")){
                ignoreTime = true; 
            }
            else if(v.equals("false")){
                ignoreTime = false; 
            }
            else {
                throw new InvalidParameterException(p, v, "Must be either true or false"); 
            }
        }
        else if(p.equals("ignoreclass")){
            if(v.equals("true")){
                ignoreClasses = true;
            }
            else if(v.equals("false")){
                ignoreClasses = false; 
            }
            else {
                throw new InvalidParameterException(p, v, "Must be either true or false"); 
            }
            
        }
        else {
            throw new InvalidParameterException(p, v, "Unknown parameter "+p); 
        }
    }
    
    
    public ClusterVecI cluster(ClassStreamEventsVecI csevi){
        try {
            // System.out.println("Ok, here's the stuff ..."); 
            // System.out.println(csevi); 
            // System.out.println(edvi);
            // System.out.println("PI = " + pepIndex); 
            // System.out.println("IgnoreTime = " + ignoreTime); 
            Instances stuff = WekaBridge.makeInstances(csevi, domDesc.getClassDescVec(), edvi, pepIndex, ignoreClasses, ignoreTime);
            EM myClusterer = new EM();        
            // myClusterer.setDebug(true);
            if(stuff.numInstances() > 10){
                myClusterer.setNumClusters(-1);
                myClusterer.setNumCVs(10); 
            }
            else {
                myClusterer.setNumClusters(2); 
            }
            myClusterer.buildClusterer(stuff); 
            return new EMClusterVec(myClusterer, pepIndex, edvi.elAt(pepIndex), stuff, ignoreTime);          
        }
        catch(Exception e){
            System.err.println("AAAARGH!! Clustering failed."); 
            e.printStackTrace(); 
        }
        return null; 
    }

    

}

class EMClusterVec implements ClusterVecI {
    EM em; 
    int pepIndex; 
    boolean ignoreTime; 
    EventDescI edi; 
    Instances stuff; 
    EMCluster[] myClusters; 
    EMClusterVec(EM em, int pepIndex, EventDescI edi, Instances stuff, boolean ignoreTime){
        this.em = em; 
        this.pepIndex = pepIndex; 
        this.edi = edi; 
        this.stuff = stuff;
        myClusters = new EMCluster[size()]; 
        for(int i=0; i < myClusters.length; i++){
            myClusters[i] =  new EMCluster(this, pepIndex, i, edi, ignoreTime); 
        }
        this.ignoreTime = ignoreTime; 
    }

   

    public float prob(Instance inst, int cluster){
        try {
            inst.setDataset(stuff);
            float retval = (float) em.probInstanceInCluster(inst, cluster); 
            return retval; 
        }
        catch(Exception e){
            System.err.println("AAAARGH!!! Could not get distribution f for instance!"); 
            e.printStackTrace(); 
        }
        return 0; 
    }

    public ClusterMem findBestLabel(EventI ev){
        // System.out.println("Trying to find best label for " + ev); 
        
        Instance inst = WekaBridge.makeInstance(ev, edi, ignoreTime);  
        inst.setDataset(stuff);
        try {
            int cluster = em.clusterInstance(inst); 
            float conf = (float) em.probInstanceInCluster(inst, cluster); 
            // if(conf > 1){
            //    System.out.println("WTF?? Label is " + (new ClusterMem(elAt(cluster), conf)).toString() );               //  }
            return new ClusterMem(elAt(cluster), conf); 
            

        }
        catch(Exception e){
            System.out.println("Label finding failed. AAAAAARGH!!!"); 
            e.printStackTrace(); 
            return null;
        }
    }


    public String getClusterDesc(int cluster){
        return em.describeCluster(cluster); 
    }

    public int size(){
          try {
              return em.numberOfClusters(); 
          }
          catch(Exception e){
            System.err.println("AAAARGH!!! Could not get number of instances!"); 
        }
        return 0;
    }
   
    public ClusterI elAt(int i){
        return myClusters[i]; 
        
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
    

    
}

class EMCluster implements ClusterI {
    EMClusterVec parent; 
    int pepIndex; 
    int clusterIndex; 
    boolean ignoreTime;
    EventDescI edi; 
    String myName = "unnamed"; 


    public String getName(){
        return myName; 
    }
    
    public void setName(String newname){
        // System.out.println("Name set to  " + newname); 
        myName = newname; 
    }
    public EMCluster(EMClusterVec parent, int pepIndex, int clusterIndex, EventDescI edi, boolean ignoreTime){
        this.parent = parent; 
        this.pepIndex = pepIndex; 
        this.clusterIndex = clusterIndex; 
        this.edi = edi;
        this.ignoreTime = ignoreTime; 
    }

    public String getDescription() {
        return parent.getClusterDesc(clusterIndex); 
    }

    @Override
    public String toString(){
        return myName + " " + parent.getClusterDesc(clusterIndex); 
    }

    public float  findMatch(StreamI si, StreamEventsI sei){
        EventVecI events = sei.getEvents(pepIndex); 
        int numEvents = events.size(); 
        float bestEventDistance = Float.MAX_VALUE; 
        for(int i=0; i < numEvents; i++){
            Instance inst = WekaBridge.makeInstance(events.elAt(i), edi, ignoreTime);  
            float dist = parent.prob(inst, clusterIndex); 
            if(dist < bestEventDistance){
                bestEventDistance = dist; 
            }
        }
        return bestEventDistance; 
    }


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
    
}
