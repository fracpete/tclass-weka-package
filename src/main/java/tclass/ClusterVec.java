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
  * Represents a vector of clusters. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClusterVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.util.Vector;

import tclass.util.StringMap;

public class ClusterVec implements ClusterVecI { 

    private Vector clusters = new Vector(); 
    private StringMap clusSM = new StringMap(); 
    public ClusterVec(){
    }

    public ClusterVec(ClusterVecI[] cvs){
	int numVecs = cvs.length; 
	for(int i = 0; i < numVecs; i++){
	    int numClusters = cvs[i].size(); 
	    for(int j=0; j < numClusters; j++){
		add(cvs[i].elAt(j)); 
	    }
	}
    }


    public ClusterMem findBestLabel(EventI event){
        return null; 
    }
    
    /** Get the number of clusters described in this set. 
     *
     * @return number of clusters. 
     */ 

    public int size(){
	return clusters.size(); 
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
    /** Add a stream. 
     */ 

    public void add(ClusterI cluster){
        clusters.addElement(cluster); 
    }
    
    /**
     * Get a single cluster. 
     */ 

    public ClusterI elAt(int i){
	return (ClusterI) clusters.elementAt(i); 
    }

    public ClusterI elCalled(String name){
        for(int i=0; i < size(); i++){
            // System.out.println("Name is: " + elAt(i).getName()); 
            if(elAt(i).getName().equals(name)){
                return elAt(i); 
            }
        }
        return null; 
    }

    @Override
    public String toString(){
	String retval = "Cluster Vector is size: " + size() + "\n";
	for(int i=0; i < size(); i++){
           retval += elAt(i).toString(); 
	}
	return retval; 
    }

    public StreamAttValI matchAll(StreamI si, StreamEventsI sei){
        return null; 
    }
    
}
