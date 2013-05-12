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
 * A vector of classifications. Usually designed to be coindexed with a StreamVec object. 
 * 
 * @author Waleed Kadous
 * @version $Id: ClassificationVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   
import java.util.Vector;


public class ClassificationVec implements ClassificationVecI {

    private Vector classvec = new Vector(); 
    private ClassDescVecI classdescvec; 

    public ClassificationVec(){
    }

    public ClassificationVec(ClassDescVecI cdv){
	classdescvec = cdv; 
    }

    /**
     * Get the number of streams in this Vector
     *
     * @return number of streams
     */
    public int size(){
	return classvec.size(); 
    }

    /**
     *  Add a stream to this vector
     *
     * @param s The stream to be added
     */ 
    public void add(ClassificationI c){
	classvec.addElement(c); 
    }

    /**
     * Ask for a particular classification
     *
     * @param i the index of the stream you want. 
     * @return the stream at the <em>i</em>th position of the vector. 
     */ 
    public ClassificationI elAt(int i){
	try {
	    ClassificationI classn = (ClassificationI) classvec.elementAt(i); 
	    return classn; 
	}
	catch(ArrayIndexOutOfBoundsException ae){
	    return null; 
	}
    }

    /**
     * Allows you to change the classification of an instance. 
     */ 

    public void setClassification(int i, ClassificationI classn){
	classvec.setElementAt(classn, i); 
    } 

    /**
     * Each classification vector refers to a ClassDescVecI that
     * describes its format.
     */
    
    public ClassDescVecI getClassDescVec(){
	return classdescvec; 
    }
    /**
     * Add a new classification description vector. WARNING: Do this
     * operation with extreme care. It tends to break things!
     * So this why it's not public. 
     */ 

    public void setClassDescVec(ClassDescVecI cdv){
	classdescvec = cdv; 
    }

    /**
     * A function that: 
     *
     * <ul>
     * <li> Changes the ClassDescVec so that it only contains two classes, 
     * true or false. 
     * <li> Changes the real class of the vector so that:
     *      if the classid is equal to the the true class, the new 
     *      classification is true. 
     *      otherwise the classification is false. 
     * </ul>
     */ 
    public void binarify(int trueclass){
	
	//Ok, first thing we do is we change the classification 
	//vector equal to true false. 
	
	ClassDescVec cdv = new ClassDescVec(); 
	int falseid = cdv.add("false"); 
	int trueid = cdv.add("true"); 
	this.classdescvec = (ClassDescVecI) cdv; 
	
	//Now go through and change all the classes. 
	//Just some good style. 
	
	
	int numClassn = size(); 
	for(int i=0; i< numClassn; i++){
	    ClassificationI currentElement = this.elAt(i); 
	    if(currentElement.getRealClass() == trueclass){
		currentElement.setRealClass(trueid); 
	    }
	    else {
		currentElement.setRealClass(falseid); 
	    }
		
	}
	
    }

    /**
     * Clone the current object. 
     * We have to do a deep copy. 
     */ 

    @Override
    public Object clone()
    {
	ClassificationVec retval = new ClassificationVec(); 
	for(int i=0; i < size(); i++){
	    retval.add((ClassificationI) elAt(i).clone()); 
	}
	return (Object) retval; 
	
    }
    
    @Override
    public String toString(){
	String retval = new String(); 
	retval += "Current class description: " + classdescvec.toString(); 
	retval += "\nCurrent classifications: \n"; 
	for(int i=0; i < size(); i++){
	    retval += elAt(i).toString() + "\n"; 
	}
	return retval; 
    }

    public static void main(String[] args) throws Exception {
	// Read in a domain description file. 
	
	DomDesc d = new DomDesc("tests/test.tdd"); 
	ClassificationVec cv = new ClassificationVec(d.getClassDescVec()); 
	
	// Now we really get to play. 
	cv.add(new Classification(0, ClassificationI.UNCLASSIFIED)); 
	cv.add(new Classification(1, ClassificationI.UNCLASSIFIED)); 
	cv.add(new Classification(2, ClassificationI.UNCLASSIFIED));
	cv.add(new Classification(3, ClassificationI.UNCLASSIFIED));
	cv.add(new Classification(0, ClassificationI.UNCLASSIFIED)); 
	cv.add(new Classification(0, ClassificationI.UNCLASSIFIED)); 
	cv.add(new Classification(1, ClassificationI.UNCLASSIFIED)); 
	System.out.println(cv); 
	
	//Let's try to binarify. 
	cv.binarify(0); 

	System.out.println(cv); 
	
	
	
    }
}
