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

package tclass.vis;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;

import tclass.DomDesc;

/**
  * Panel listing the channels. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ChanPanel.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */


class ChanList extends Panel {
    
    public ChanList(DomDesc dd){
	this.setLayout(new GridLayout(dd.numChans()+1,1)); 
	this.add(new Label("Channel Name")); 
	int numChans = dd.numChans(); 
	for(int i=0; i < numChans; i++){
	    this.add(new Label(dd.getChannel(i).getName())); 
	}
    }

    // public Dimension getPreferredSize(){
// 	return new Dimension(300,300); 
//     }
}

public class ChanPanel extends ScrollPane {

    private Panel chanList;

    /**
     * Construct a channel panel from the domain description. 
     */ 
    public ChanPanel(DomDesc dd){
	super(ScrollPane.SCROLLBARS_AS_NEEDED); 
	chanList = new ChanList(dd); 
	this.add(chanList);
    }

    @Override
    public Dimension getPreferredSize(){
	return new Dimension(200,400); 
    }

    public static void main(String[] argv) throws Exception {
	DomDesc d = new DomDesc("sl.tdd"); 
	Frame f = new Frame(); 
	f.add(new ChanPanel(d)); 
	f.pack(); 
	f.show(); 
    }

}
