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

package tclass;   

/**
  * A abstraction of single channel. Note that this is an interface,
  * so just about anything can provide it. Only two methods: 
  * One for working out the number of frames and the other for working 
  * out the value. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ChannelI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $ */

public interface ChannelI {
    
    /**
     * Get the number of frames in this channel. 
     *
     * @return the number of frames. 
     */ 
    
    public int numFrames(); 

    /**
     * Returns the value of this channel at a particular frame. This
     * is designed to be used together with a <a href="tclass.ChannelDescI.html">ChannelDescI</a> object,
     * which gives the returned value its meaning. 
     *
     * @return the value at a particular frame.
     *  */

    public float valAt(int frame); 
   
}
