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
 * An implementation of a Class Stream Vector. 
 * 
 * Makes use of the concrete classes ClassificationVec, StreamVec and
 * Stream to provide the functionality. Still, it's important that
 * they export the interfaces rather than the objects
 * themselves. This makes it possible to implement alternatives to
 * ClassStreamVec.
 * 
 * 
 * @author Waleed Kadous
 * @version $Id: ClassStreamVec.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ 
 */

package tclass;   
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

import tclass.util.Debug;

public class ClassStreamVec implements ClassStreamVecI {
    
    private ClassificationVecI classVec;
    private StreamVecI streamVec; 
    private DomDesc domDesc; 

    public ClassStreamVec(String filename, DomDesc dd) throws 
    FileNotFoundException, FileFormatException, IOException
    {
	domDesc = dd; 
	classVec = (ClassificationVecI) new ClassificationVec(dd.getClassDescVec()); 
	streamVec = (StreamVec) new StreamVec(); 
	addFromFile(filename); 
    }
    
        /**
     * Clone the current object. 
     *
     */ 

    public ClassStreamVec(StreamVecI svi, DomDesc dd){
	domDesc = dd; 
	classVec = null; 
	streamVec = svi; 
    }

    @Override
    public String toString(){
	String retval = "Class Stream Vector\n"; 
	for(int i=0; i < size(); i++){
	    retval += "Stream " + i +"\n"; 
	    retval += "Classification: " +
		classVec.elAt(i).toString() + "\n"; 
	    retval += "Data: " + streamVec.elAt(i).toString(); 
	}
	return retval; 
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

    private void addFromFile(String filename) 
	throws IOException, FileNotFoundException, FileFormatException
    {
        BufferedReader br = new BufferedReader(new
					       FileReader(filename)); 
	StreamTokenizer st = new StreamTokenizer(br); 
	st.eolIsSignificant(true); 
	st.wordChars('/','/'); 
        st.wordChars('_', '_'); 
	st.commentChar('#'); 
	//Start pulling off pieces. 
	String streamfile; 
	String streamclass; 
	ClassDescVecI cdv = domDesc.getClassDescVec(); 

	st.nextToken(); 
	while(st.ttype != st.TT_EOF){
	    if(st.ttype == st.TT_EOL){
		st.nextToken(); 
		continue; 
	    }
	    if(st.ttype == st.TT_WORD){
		streamfile = st.sval; 
	    }
	    else {
		throw new FileFormatException(filename, st, "A file name"); 
	    }
	    st.nextToken(); 
	    if(st.ttype == st.TT_WORD){
		streamclass = st.sval; 
	    }
	    else {
		throw new FileFormatException(filename, st, "A class"); 
	    }
	    st.nextToken(); 
	    if(st.ttype != st.TT_EOL){
		throw new FileFormatException(filename, st, "A newline"); 
	    }
	    // Now construct the object. 
	    Stream s = new Stream(streamfile, domDesc); 
	    int classn = cdv.getId(streamclass);
	    if(classn == -1){
		throw new FileFormatException(filename, st, "A valid class"); 
	    }
	    Classification c = new Classification(classn); 
	    add(s, c); 
	    Debug.dp(Debug.EVERYTHING, "Added " + streamfile); 
	}
    }

    public boolean hasClassification(){
	return (classVec != null); 
    }

    public StreamVecI getStreamVec(){
	return (StreamVecI) streamVec; 
    }
    
    public void setStreamVec(StreamVecI sv){
	streamVec = sv; 
    }

    public ClassificationVecI  getClassVec(){
	return (ClassificationVecI) classVec; 
    }

    public void setClassVec(ClassificationVecI classes){
	classVec = classes; 
    }
    
    /**
     * Adds an instance to both.
     */ 
  
    public void add(StreamI strm, ClassificationI classn){
	classVec.add(classn); 
	streamVec.add(strm); 
    }

    /**
     * Get the size of the current stream
     */ 
    
    public int size(){
	if(classVec != null){
	    Debug.myassert(classVec.size() == streamVec.size(), 
			 "DANGER! ClassVec Size != StreamVec Size"); 
	}
	return streamVec.size(); 		 
    }

    public static void main(String[] args)throws Exception {
	Debug.setDebugLevel(Debug.EVERYTHING); 	
      	DomDesc d = new DomDesc("tests/test.tdd"); 
	ClassStreamVec csv = new ClassStreamVec("tests/test.tsl", d); 
	System.out.println(csv.toString()); 
	
    }
}
