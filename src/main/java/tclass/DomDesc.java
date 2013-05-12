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
  * Controls all the domain description information. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: DomDesc.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.Vector;

import tclass.util.Debug;
import tclass.util.StringMap;

public class DomDesc implements Cloneable, Serializable {
    
    public static final int NO_SUCH_LABEL = -1; 
    
    private ClassDescVecI classdesc; 
    private ChannelDesc[] chanbynum; 
    private StringMap chanmap = new StringMap(); 
    
    /**
     * Constructs a Domain Description object.
     *
     * @param f The file to construct the domain information from. 
     * See the file specification format for more info. The Domain
     * description contains all information related to: 
     * classes and the channel formats. 
     */

    public DomDesc(String f) throws IOException, FileNotFoundException, FileFormatException, InvalidParameterException {
	classdesc = (ClassDescVecI) new ClassDescVec(); 
	parseFile(f); 
    }

    
    public void parseFile(String f) throws IOException, FileNotFoundException, FileFormatException, InvalidParameterException {
	
	// Debug.dp(Debug.FN_CALL, "Creating Domain Description ... "): 
	BufferedReader b = new BufferedReader(new FileReader(f));
	StreamTokenizer st = new StreamTokenizer(b); 
	st.commentChar('#'); 
	//We want to treat numbers as parts of a word, as well as full
	//stops and - signs.
	st.wordChars('0','9'); 
	st.wordChars('.','.'); 
	st.wordChars('-','-'); 
	st.wordChars('_', '_'); 
	st.quoteChar('"'); 
	
	//First read the classes. 
	st.nextToken(); 
	
	//This is the typical format exception

	if(!(st.ttype == st.TT_WORD && st.sval.equals("classes"))){
	    throw new FileFormatException(f, st, "classes"); 
	}

	//Now read the list of classes

	st.nextToken(); 

	//Get the [

	if(st.ttype != '['){
	    throw new FileFormatException(f, st, "["); 
	}

	st.nextToken(); 

	while(st.ttype != ']'){
	    if(st.ttype != st.TT_WORD){
		throw new FileFormatException(f, st, "a class name"); 
	    }
	    classdesc.add(st.sval); 
	    st.nextToken(); 
	}
	//Now start reading the channels. 
       
	st.nextToken();
	
	DataTypeMgr dtm = DataTypeMgr.getInstance(); 
	Vector channeldescs = new Vector(); 

	while(st.ttype != st.TT_EOF){
	    if(!(st.ttype == st.TT_WORD && st.sval.equals("channel")))
		throw new FileFormatException(f, st, "channel"); 
	    
	    
	    st.nextToken(); 
	    
	    // The next token will be the name of this channel. 
	    
	    String chanName = st.sval; 

	    //The next token is the type of the channel, 
	    // so make a new DataType
	    
	    st.nextToken(); 
	    
	    DataTypeI dt = dtm.getClone(st.sval); 	    
	    if(dt == null)
		throw new FileFormatException(f, st, " a data type e.g. continuous"); 
	    
	    st.nextToken(); 
	    
	    //We are expecting a {
	    
	    if(st.ttype != '{')
		throw new FileFormatException(f, st, "{"); 
	    
	    // Now we start reading parameter-value pairs. 
	    	    
	    st.nextToken(); 
	    while(st.ttype != '}'){
		String param = st.sval; 
		st.nextToken(); 
		//Here are the possibilities: It's a number, but we
		//treat numbers like words anyway; it's a straight string
		//or it's quote-delimited string - " 
		if(!(st.ttype == st.TT_WORD || st.ttype == '"'))
		    throw new FileFormatException(f, st, "Parameter value"); 
		String value = st.sval; 
		dt.setParam(param, value); 
		// will spew an InvalidParameterException
		// if the parameter value thang didn't work. 

		st.nextToken(); 
	    }
	    ChannelDesc cd = new ChannelDesc(chanName, dt); 
	    chanmap.add(chanName); 
	    channeldescs.addElement(cd); 	    

	    st.nextToken(); 
	    // Now add this element to our collection of channels
	}
	convertToChanByNum(channeldescs); 
    }

    private void convertToChanByNum(Vector channelDescs){
	int numChans = channelDescs.size(); 
	chanbynum = new ChannelDesc[numChans]; 
	for(int i=0; i < numChans; i++){
	    chanbynum[i] = (ChannelDesc) channelDescs.elementAt(i); 
	}
    }
    
    /*
     * Get the class information. 
     * 
     * @return the name of the class. 
     */ 

    public ClassDescVecI getClassDescVec(){
	return classdesc; 
    }
    
    public void setClassDescVec(ClassDescVecI cd){
	classdesc = cd; 
    }
    
    /**
     * Get the number of channels
     *
     * @return number of channels
     *
     */
    
    public int numChans(){
	return chanmap.size();  
    }
   
    /** 
     * Gets a description of the channel 
     */ 

    public ChannelDesc getChannel(int index){
	try {
	    return chanbynum[index]; 
	}
	catch(ArrayIndexOutOfBoundsException ae){
	    return null; 
	}
    }

    public ChannelDesc getChannel(String name){
	try {
	    return chanbynum[chanmap.getInt(name)]; 
	}
	catch(ArrayIndexOutOfBoundsException ae){
	    return null; 
	}
    }

    
    public int getChanIndex(String name){
	return chanmap.getInt(name); 
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

    
    
    /**
     * A small test program 
     */ 
    public static void main(String[] args) throws Exception {
	Debug.setDebugLevel(Debug.EVERYTHING); 	
	DomDesc d = new DomDesc("tests/test.tdd"); 
	ClassDescVecI cdv = d.getClassDescVec(); 
	System.out.println("There are " + cdv.size() + " classes."); 
	System.out.print("They are: "); 
	
	for(int i=0;  i < cdv.size(); i++){
	    System.out.print(cdv.getClassLabel(i)+ " ");
	}

	System.out.println(); 
	
	//And now test some of the channels. 
	System.out.println("There are " + d.numChans() + " channels."); 
	System.out.println("These are: "); 
	for(int i=0; i < d.numChans(); i++){
	    System.out.println(d.getChannel(i)); 
	}
    }
       
}
