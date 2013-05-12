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
