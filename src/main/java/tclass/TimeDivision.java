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
 * Another "hack class" that will be used to test how well a Naive Bayes 
 * does on straight time division. 
 * 
 * @author Waleed Kadous
 * @version $Id: TimeDivision.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
 */

package tclass;   

// Divides each channel into 10 equal values and then averages them. 
// A useful baseline for comparison. I'm hoping that this one does not work
// too well. But I bet you it will.

public class TimeDivision {
    DomDesc domDesc; 
    int numDivs; 
    TimeDivision(DomDesc d, int numDivs){
	domDesc = d; 
	this.numDivs = numDivs; 
    }
    AttDescVecI makeDescription(){
	AttDescVec adv = new AttDescVec(); 
	int numChans = domDesc.numChans(); 
	DataTypeMgr dtm = DataTypeMgr.getInstance(); 
	for(int i=0; i < numChans; i++){
	    String chanName = domDesc.getChannel(i).getName(); 
	    for(int j=0; j < numDivs; j++){		
		DataTypeI dt = dtm.getClone("continuous"); 
		adv.add(new AttDesc(chanName +"_"+j, dt)); 
	    }
	}
	// Debug.dp(Debug.PROGRESS, "Returning ADV as: " + adv); 
	return adv; 
    }
    
    StreamAttValI convert(StreamI stream){
	int numChans = domDesc.numChans(); 
	StreamAttVal retval = new StreamAttVal(numDivs*numChans); 
	for(int i=0; i < numChans; i++){
	    int baseIndex = i*numDivs; 
	    ChannelI thisChannel = stream.chanAt(i);
	    int numFrames = thisChannel.numFrames(); 
	    int currentPos = 0; 
	    for(int j=0; j < numDivs; j++){
		float sum=0; 
		int count=0; 
		int stop = numFrames*(j+1)/numDivs; 
		for(int k = currentPos; k < stop ; k++){
		    sum += thisChannel.valAt(k); 
		    count++; 
		    currentPos++; 
		}
		retval.setAtt(baseIndex+j,sum/count); 
	    }
	}
	return retval; 
    }

    ClassStreamAttValVecI timeDivide(ClassStreamVecI input){
	ClassStreamAttValVecI retval = new ClassStreamAttValVec(); 
	retval.setClassVec(input.getClassVec()); 
	StreamAttValVec outputData = new StreamAttValVec(); 
	StreamVecI inputData = input.getStreamVec(); 
	outputData.setDescription(makeDescription()); 
	int numStreams = input.size(); 
	for(int i=0; i < numStreams; i++){
	    StreamAttValI savi = convert(inputData.elAt(i));
	    // Debug.dp(Debug.PROGRESS, "Added: " + savi); 
	    outputData.add(savi); 
	    
	}
	retval.setStreamAttValVec(outputData); 
	return retval; 
    }
}
