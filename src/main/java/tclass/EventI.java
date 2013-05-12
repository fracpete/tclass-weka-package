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
  * The description of a generic event. For now, event parameters are 
  * represented exclusively as doubles. 
  * 
  * @author Waleed Kadous
  * @version $Id: EventI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public interface EventI {
    
    /**
     * Gets the value of the <i>i</i>th parameter. These really only
     * make sense when combined with an EventDescI object. 
     */

    public float valOf(int i); 
 

    public float getMidtime(); //  Why not? return -1 if not interested. 
    public float getDuration(); // Why not? return -1 if not interested. 

}
