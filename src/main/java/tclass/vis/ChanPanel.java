package tclass.vis;

import tclass.*; 
import tclass.util.*; 
import java.util.*; 
import java.awt.*; 

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
