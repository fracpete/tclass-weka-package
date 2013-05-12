/**
  * Parameter description. Simple enough not to require an interface. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: Param.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass;   

public class Param {
    private String name;
    private String description; 
    private String defaultValue; 
    
    public Param(String name, String desc, String def){
	this.name = name; 
	description = desc; 
	defaultValue = def; 
    }
    
    public String getName(){
	return name; 
    }

    public String getDesc(){
	return description; 
    }
    
    public String getDefault(){
	return defaultValue; 
	
    }
    public String toString(){
	return name + ": " + description + ". Default value: " + defaultValue;
    }

}
