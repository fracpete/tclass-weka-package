package tclass;   
import java.io.*; 

/**
  * A description of the channel. 
  * This includes the datatype at each timeframe and the channel's name
  *
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ChannelDesc.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public class ChannelDesc implements Serializable {
    private String name; 
    private DataTypeI datatype; 

    /**
     * Make a new Channel. 
     * @param name Name of the channel. 
     * @param datatype Data type of this channel. 
     */ 

    public ChannelDesc(String name, DataTypeI datatype){
	this.name = name; 
	this.datatype = datatype; 
    }
    
    public String getName(){
	return name; 
    }
    
    public void setName(String name){
	this.name = name; 
    }
    public DataTypeI getDataType(){
	return datatype; 
    }

    public void setDataType(DataTypeI dt){
	this.datatype = dt; 
    }
    
    public String toString(){
	String retval = "Channel " + name + "  " ; 
	retval += datatype.toString(); 
	return retval; 
    }
}
