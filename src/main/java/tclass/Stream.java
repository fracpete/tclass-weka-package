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
 * A standard implementation of stream functionality. 
 *
 * This is a convenient class for representing a stream. You don't have
 * to use it, of course. You can provide the same interface using your
 * own classes; of course. 
 * 
 * @author Waleed Kadous
 * @version $Id: Stream.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 *
 *
 */

package tclass; 
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Vector;

import tclass.util.Debug;

public class Stream implements StreamI {
    
  /** for serialization. */
  private static final long serialVersionUID = 6744267364187512594L;
    String comment; 
    private float data[][]; 
    
    /*
     * Internally, for efficiency, we represent each stream as a
     * two-dimensional array. The first array dimension represents the
     * frame, the second the channel.
     * 
     * This particular implementation is designed for reading streams
     * from a file. Note that the class is not encoded
     * explicitly in the data file itself. This enables users to test
     * various classification schemes. Typically, this is likely to
     * happen more in temporal classification than static
     * classification. 
     * 
     * 
     * Note that the comment by default is set to the file name we are 
     * asked to open. 
     */

    /**
     * Build a stream from a file. 
     *
     */
    
    public Stream(String filename, DomDesc dd) 
	throws FileNotFoundException, IOException, FileFormatException 
    {
	Debug.dp(Debug.FN_CALLS, "Stream constructor called"); 
	comment = filename; 
	BufferedReader br = new BufferedReader(new FileReader(filename)); 
	StreamTokenizer st = new StreamTokenizer(br); 
	
	st.resetSyntax(); 
	//The file format is really simple. 
	//But we do have to do some setting up of the tokenizer

	//Also tell it that new lines are important: 
	st.eolIsSignificant(true); 
	
	// Also tell it that commas are whitespace chars. 
	// This is good, since it means we can ignore commas, 
	// which should make importing files easy. 
	st.whitespaceChars(',',','); 
	st.whitespaceChars(' ',' '); 
	st.whitespaceChars('\t','\t'); 
	st.commentChar('#'); 
	st.wordChars('0', '9'); 
	st.wordChars('.','.'); 
	st.wordChars('-','-');
	st.wordChars('_','_');  
	st.wordChars('A','Z'); 
	st.wordChars('a','z'); 
	//Now figure out the size of each array. 
	int numChans = dd.numChans(); 
	
	//Get an array of data types for efficiency. 
	DataTypeI[] datatype = new DataTypeI[numChans]; 
	for(int i=0; i < numChans; i++){
	    datatype[i] = dd.getChannel(i).getDataType(); 
	}

	//We're going to stick them into a Vector and then move them
	//into an array. Some overhead, but should be fine. 

	Vector frames = new Vector(); 

	//And now ... the adventure begins
	st.nextToken(); 
	
	while(st.ttype != st.TT_EOF){

	    float[] thisframe = new float[numChans]; 

	    //Line could be blank. If so ignore it. 
	    if(st.ttype == st.TT_EOL){
		st.nextToken(); 
		continue; //Go to the next line
	    }
	    for(int i=0; i< numChans; i++){
		Debug.dp(6, "Token is " + st.sval + " i = " + i);
		// BUGPATCH!! You can't tell StreamTokenizer
		// to not treat numbers specially without some real
		// headaches. 
		// This fixes the problem.
		if(st.ttype == st.TT_WORD){
		    thisframe[i] = datatype[i].read(st.sval);
		    st.nextToken(); 
		}
		else if(st.ttype == st.TT_NUMBER){
		    thisframe[i] = datatype[i].read(String.valueOf(st.nval));
		    st.nextToken(); 
		}
		else {
		    throw new FileFormatException(filename, st, "a data symbol"); 
		}
	    }
	    //The current token should be an end of line. 
	    if(st.ttype != st.TT_EOL){
		throw new FileFormatException(filename, st, "A newline"); 
	    }
	    st.nextToken(); 
	    frames.addElement(thisframe); 
	}
	//And now we create the two-dimensional array with the data in it. 
	
	int numFrames = frames.size(); 
	data = new float[numFrames][numChans]; 
	for(int i=0; i<numFrames; i++){
	    data[i] = (float []) frames.elementAt(i); 
	}
	br.close(); 
    }
    
        

    /**
     * Gets the comment associated with this object 
     * 
     * @return the comment of this label 
     */
    
    public String getComment(){
	return comment; 
    }

    /**
     * Set the label of this stream. 
     *
     * @param comment the comment associated with this stream
     */
    public void setComment(String comment){
	this.comment = comment; 
    }
    

    /** 
     * Gets the number of frames
     *
     * @return Number of frames in this stream. 
     */

    public int numFrames(){
	return data.length; 
    }

    /**
     * Gets the value for the frame f and the channel c
     *
     * @param f Frame of interest
     * @param c Channel of interest
     * @return A float representing the information 
     *          stored for this channel
     */
         
    public float valAt(int f, int c){
	return data[f][c]; 
    }

    /**
     * Gets a requested channel. 
     * Note: The number of channels can be retrieved from the DomDescI object. 
     *
     * @param c Channel of interest
     * @return A channel
     *         
     */

    public ChannelI chanAt(int c){
	return new ChanWrapper(c, this); 
    }

    @Override
    public String toString(){
	String retval = "Comment: " + comment + "\n"; 
	for(int i=0; i < data.length; i++){
	    retval += "Fr " + i + ": "; 
	    for(int j=0; j < data[i].length; j++)
		retval += data[i][j] + " ";
	    retval += "\n"; 
	}
	return retval; 
    }
    
    public static void main(String[] args) throws Exception {
	Debug.setDebugLevel(0);
	DomDesc d = new DomDesc("tests/test.tdd"); 
	Stream s = new Stream("tests/test.tsd", d); 
	System.out.println("Stream follows");
	System.out.println(s); 
	//Now test channels. 
	for(int i=0; i < d.numChans(); i++){
	    ChannelI chan = s.chanAt(i); 
	    System.out.println("Chan " + i + ": " + chan); 
	}
    }
}

// We create a class that implements ChannelI so that we can pass it back. 
// This is an efficient way of providing a channel interface. 

class ChanWrapper implements ChannelI {

    int channel; 
    Stream strm; 

    ChanWrapper(int channel, Stream strm){
	this.channel = channel; 
	this.strm = strm; 
    }
    
    public int numFrames(){
	return strm.numFrames(); 
    }
    
    public float valAt(int frame){
	return strm.valAt(frame, channel); 
    }
    
    @Override
    public String toString(){
	String retval = " [ "; 
	for(int i=0; i < numFrames(); i++){
	    retval += valAt(i) + " ";
	}
	retval += "]";
	return retval; 
    }
}
