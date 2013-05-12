
package tclass;   

/**
  * The interface for the clustering algorithms, in other words, 
  * what we expect our clustering algorithms to be able to do. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClusterAlgI.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

public interface ClusterAlgI extends Cloneable {


    /**
     * Name of this clustering algorithm. 
     */ 
    
    public String name(); 

    /**
     * Copy this object. 
     */ 

    public Object clone();

    /**
     * Set the description of the incoming Class Stream Events Vector
     * Note that we need this first ... for the parsing of values,
     * before we do any actual processing. I expect that the
     * ClusterAlgMgr should have a copy of it and passes it through in
     * the constructor, pretty much like the Domain description for the 
     * GlobalExtrMgr
     */

    public void setEventDescVec(EventDescVecI events); 

    /**
     * Set the domain description for this clustering algorithm. Used 
     * as part of the setting up of the Cluster Algorithm (e.g. for 
     * seeing the datatypes of certain things
     */ 

    public void setDomDesc(DomDesc dd); 

     /** 
     * Provides a description of the clustering algorithm.

     * This description explains what the basic idea of the clustering
     * algorihtm is (i.e. the sort of shapes it tried to find). It
     * should also explain any potential configuration options that
     * may be used to configure the object, using the configure
     * option.
     * 
     * @return The description of this class.  
     */
    
    public String description(); 

    /**
     * Configures this instance so that parameter <i>p</i> has
     * value <i>v</i>. 
     *
     * @param p the parameter to set. 
     * @param v the value of the parameter. 
     * @exception InvalidParameterException Occurs when the parameter can not
     * be set to the given value, or there is no such parameter. 
     *
     */

    public void setParam(String p, String v) throws InvalidParameterException; 
    /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */    
    public ParamVec getParamList(); 

    /**
     * Probably the most important method for a clustering algorithm. 
     * Taking a set of classified streams labelled by event, a classification
     * is performed. Depending on the clustering algorithm, it may use the class
     * information, but may not. 
     *
     * @param csvi The stuff that needs to be clustered
     *
     */ 

    public ClusterVecI cluster(ClassStreamEventsVecI csvi); 

    
}
