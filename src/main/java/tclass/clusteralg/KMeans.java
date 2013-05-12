/**
  * This is a simple k means clustering algorithm.
  * Auto numclasses works out the average number of events per event vec. 
  * 
  *
  *
  * 
  * @author Waleed Kadous
  * @version $Id: KMeans.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */

package tclass.clusteralg;   
import tclass.*; 
import tclass.util.*; 
import java.util.*; 


class KMClusterVec implements ClusterVecI {
    KMCluster[] clusters; 
    

    KMClusterVec(KMCluster[] clusters){
	this.clusters = clusters; 
    }

    
    public ClusterMem findBestLabel(EventI ev){
        return null; 
    }

    /**
     * Clone the current object. 
     *
     */ 

    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }

    public ClusterI elAt(int i){
	return (ClusterI) clusters[i]; 
    }
    
    public int size(){
	return clusters.length; 
    }
    
    public void add(ClusterI e){
	// Do nothing. 
    }

    public StreamAttValI matchAll(StreamI si, StreamEventsI sei){
        int numAtts = clusters.length; 
        // System.out.println("matchAll called"); 
        StreamAttVal sav = new StreamAttVal(numAtts); 
        for(int j=0; j < numAtts; j++){
            sav.setAtt(j, clusters[j].findMatch(si, sei)); 
        }
        // System.out.println("SAV = " + sav); 
        return sav; 
    }

}

class KMCluster implements ClusterI {
    // Ok. Each cluster consists of an eventVec 
    // which are all the objects that belong to this cluster.
    
    String myName = "unnamed-kmeans"; 
    EventVec myEvents = new EventVec(); 
    EventDescI edi; 
    Event centroid; 
    int pepIndex; 
    boolean useCentroid; 
    float[] sds; 

 

    KMCluster(EventDescI edi, int pepIndex, boolean useCentroid, float[] sds){
	this.edi = edi; 
	this.useCentroid = useCentroid; 
	this.pepIndex = pepIndex; 
	this.sds = sds; 
    }

    /**
     * Clone the current object. 
     *
     */ 
    
    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }


    public String getName(){
        return myName;
    }

    public void setName(String name){
        myName = name; 
    }
    
    public String getDescription(){
	return "Cluster centroid is: " + centroid.toString() +"\n"; 
    }
    
    public String toString(){
	return getDescription(); 
    }

    int size(){
	return myEvents.size(); 
    }

    void addEvent(EventI e){
	myEvents.add(e); 
    }
    
    void removeEvent(EventI e){
	myEvents.remove(e); 
    }
    
    EventI elAt(int pos){
	return myEvents.elAt(pos); 
    }

    void computeMean(){
	//Well, the mean is an array of floats equal in size to the 
	//eventDesc. 
	int numParams = edi.numParams();
	int numEvents = myEvents.size(); 
        // System.out.println("NumEvents = " + numEvents); 
	float[] mean = new float[edi.numParams()]; 
	for(int i=0; i < numParams; i++){
	    mean[i] = 0; 
	}
	for(int i=0; i < numEvents; i++){
	    for(int j=0; j < numParams; j++){
                // System.out.println("MyEv = " + myEvents.elAt(i) + " i=" + i + " j=" + j);
                // System.out.println("WTF?" + myEvents.elAt(i).valOf(j));
                float temp = myEvents.elAt(i).valOf(j); 
		mean[j] += temp; 
	    }
	}
	for(int i=0; i < numParams; i++){
	    mean[i] = mean[i]/numEvents; 
	}
	// And now make an event from it. 
	centroid = new Event(mean); 
    }

    // Compute the distance between two events. 

    float eventDistance(EventI a, EventI b){
    // Ok. 
    // We can get the description of these objects. 	
	int numParams = edi.numParams(); 
	float distanceSum = 0; 
	for(int i=0; i < numParams;i++){
	    distanceSum += ((a.valOf(i)-b.valOf(i))/sds[i])*((a.valOf(i)-b.valOf(i))/sds[i]); 
	}
	return (float) Math.sqrt(distanceSum); 
    }
    
    float findClosest(EventI e){
	int numEvents = myEvents.size(); 
	float minDistance = Float.MAX_VALUE; 
	for(int i=0; i < numEvents; i++){
	    float dist = eventDistance(myEvents.elAt(i), e); 
	    if(dist < minDistance){
		minDistance = dist; 
	    }
	}
	return minDistance; 	
    }
    
    float distFromCentroid(EventI e){
	return eventDistance(centroid, e); 
    }

    public float findMatch(StreamI stream, StreamEventsI streamEvents){
	// Ok ... messy! We need to know about the pep index.  
	// Get the events of relevance to us. 
	
	// First grab our data. 
	
	EventVecI events = streamEvents.getEvents(pepIndex); 
	int numEvents = events.size(); 
	float bestEventDistance = Float.MAX_VALUE; 

	if(useCentroid){		    
	    for(int i=0; i < numEvents; i++){
		float dist = distFromCentroid(events.elAt(i)); 
		if(dist < bestEventDistance){
		    bestEventDistance = dist; 
		}
	    }
	}
	else {
	   for(int i=0; i < numEvents; i++){
		float dist = findClosest(events.elAt(i)); 
		if(dist < bestEventDistance){
		    bestEventDistance = dist; 
		}
	    } 
	}
	return bestEventDistance; 
    }

}

public class KMeans implements ClusterAlgI { 

    
    private String baseName = "kmeans";
    private String description = "Implements a k-means clustering algorithm";
    private EventDescVecI edvi = null; 
    private DomDesc domDesc = null;
    private int pepIndex = 0; 
    private boolean ignoreClasses = false; 
    private boolean autoNumClusters = true; 
    private int numClusters = 0; 
    private int classToCluster = 0; 
    private boolean orderedAllocate = true; 
    private boolean useCentroid = true; 
    private float[] sds;
    //If true, use the centroid of the cluster for deciding reclustering.
    //If false, use the closest instance, not distance to centroid. 
    
    
    /**
     * Name of this clustering algorithm. 
     */ 
    
    public String name(){
	return baseName;
    }
    
    /**
     * Clone the current object. 
     *
     */ 
    
    public Object clone()
    {
	try {
	    return super.clone(); 
	}
	catch (CloneNotSupportedException e){
	    // Can't happen, or so the java programming book says
	    throw new InternalError(e.toString()); 
	}
    }
    
    public void setDomDesc(DomDesc dd){
	domDesc = dd; 
    }
    
    /**
     * Set the description of the incoming Class Stream Events Vector
     * Note that we need this first ... for the parsing of values,
     * before we do any actual processing. I expect that the
     * ClusterAlgMgr should have a copy of it and passes it through in
     * the constructor, pretty much like the Domain description for the 
     * GlobalExtrMgr
     */

    public void setEventDescVec(EventDescVecI events){
	edvi = events; 
    }
    
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
    
    public String description(){
	return description; 
    }

    /**
     * Configures this instance so that parameter <i>p</i> has
     * value <i>v</i>. 
     *
     * @param p the parameter to set. 
     * @param v the value of the parameter. 
     * @return true if the operation succeeded. 
     *
     */

    public void setParam(String p, String v) throws InvalidParameterException {
	// Let's just use a simple parameter as an example. 
	if(p.equals("metafeature")){
	    // So they want to use a particular metafeature. 
	    //Try to find the metafeature. 
	    pepIndex = edvi.elIndex(v); 
	    if(pepIndex == -1){
		throw new InvalidParameterException(p, v, "Unknown metafeature " + v); 
	    }
	}
	else if(p.equals("numclusters")){
	    if(v.equals("auto")){
		autoNumClusters = true; 
	    }
	    else {
		try {
		    numClusters = Integer.parseInt(v); 
		}
		catch(NumberFormatException nfe){
		    throw new InvalidParameterException(p, v, "Could not understand number of exceptions."); 
		    
		}
	    }
	}
	else if(p.equals("ignoreclass")){
	    if(v.equals("true")){
		ignoreClasses = true;
	    }
	    else if(v.equals("false")){
		ignoreClasses = false; 
	    }
	    else {
		throw new InvalidParameterException(p, v, "Must be either true or false"); 
	    }
	
	}
	else if(p.equals("useonly")){
	    classToCluster = domDesc.getClassDescVec().getId(v); 
	    if(classToCluster == -1){
		throw new InvalidParameterException(p, v, "Unknown class " + v); 
	    }
	}
	else if(p.equals("initialdist")){
	    if(v.equals("random")){
		orderedAllocate = false; 
	    }
	    else if(v.equals("ordered")){
		orderedAllocate = true; 
	    }
	    else {
		throw new InvalidParameterException(p, v, "Must be either random or ordered"); 
	    }
	}
	else if(p.equals("closeness")){
	    if(v.equals("centroid")){
		useCentroid = true; 
	    }
	    else if(v.equals("closest")){
		useCentroid = false; 
	    }
	    else {
		throw new InvalidParameterException(p, v, "Must be either centroid or closest"); 
	    }
	}
	else {
	    throw new InvalidParameterException(p, v, "Unknown parameter "+p); 
	}
    }
    
    /** 
     *
     * Describes any parameters used by this global extractor,
     * to suit a particular domain. 
     *
     * @return A vector of parameters. 
     */    
    public ParamVec getParamList(){
	ParamVec pv = new ParamVec(); 
	pv.add(new Param("metafeature", "The name of the metafeature to apply this to", "First")); 
	pv.add(new Param("numclusters", "Number of clusters. Possible values: auto or a number", "auto")); 
	pv.add(new Param("ignoreclass", "Completely ignore class information. Can be true or false", "false")); 
	pv.add(new Param("useonly", "Only cluster instances of one class. Parameter is the class to cluster.", "First")); 
	pv.add(new Param("initialdist", "Can be either: random, (random cluster distribution), ordered (distribute by order in sequence", "ordered")); 
	pv.add(new Param("closeness", "Either centroid or closest", "centroid")); 
	
	return pv; 
    }
    
    public ClusterVecI cluster(ClassStreamEventsVecI csevi){
	// Ok. First let's pull out our data. 
	EventVecI[] data = pullData(csevi); 	
	//Now we need to perform an initial clustering. 
	//Create the clusters
	KMCluster[] clusters = new KMCluster[numClusters]; 
	EventDescI edi = edvi.elAt(pepIndex); 
	float[] sds = findSD(data, edi); 
	for(int i=0; i < clusters.length; i++){
	    clusters[i] = new KMCluster(edi, pepIndex, useCentroid, sds); 
	}
	// Now do an initial distribution. 
	distribute(data, clusters); 
	//Now the main loop
	int numLoops = 0 ; 
	while(redistribute(clusters) && numLoops < 40){
	    Debug.dp(Debug.PROGRESS, "Redistribution " + numLoops); 
	    numLoops++; 

	}
       
	//Redistribute returns true while ever there is a change in
	//cluster membership.
	return(new KMClusterVec(clusters)); 
    }


    float[] findSD(EventVecI[] evi, EventDescI edi){
	int numParams = edi.numParams();
	float[] retval = new float[numParams]; 
	float[] avg = new float[numParams]; 
	
	// Go through all the data. 
	for(int i=0; i < numParams; i++){
	    float sumx=0; 
	    float sumx2=0; 
	    int count = 0; 
	    for(int j=0; j < evi.length; j++){
		for(int k=0; k < evi[j].size(); k++){
		    float currentVal = evi[j].elAt(k).valOf(i); 
		    sumx += currentVal; 
		    sumx2 += currentVal*currentVal; 
		    count++; 
		}
	    }
	    // Var(X) = E(X^2)-E(X)^2
	    float e_x2 = sumx2/count;
	    avg[i] = sumx/count; 
	    float ex_2 = avg[i]*avg[i]; 
	    float varx = e_x2-ex_2; 
	    retval[i] = (float) Math.sqrt(varx); 
	    if(retval[i] == 0){
		retval[i] = (float) 0.0001; 
	    }
	}
	
	Debug.dp(Debug.EVERYTHING, "SD's are: ");
	for(int i=0; i < retval.length; i++){
	    Debug.dp(Debug.EVERYTHING, edi.paramName(i) + " avg = " + avg[i] + " sd = " + retval[i]);
	}
	return retval; 
    }

// From here on in, internal methods. 

    boolean redistribute(KMCluster[] clusters){
	// int oldDL = Debug.getDebugLevel(); 
	// Debug.setDebugLevel(Debug.EVERYTHING); 
	Debug.dp(Debug.FN_CALLS, "Redistribute called ..."); 
	boolean hasChanged = false; 
	int numClusters = clusters.length; 
	if(useCentroid){
	    for(int i=0; i < numClusters; i++){
		clusters[i].computeMean(); 
	    }
	}

	for(int i=0; i < numClusters; i++){
	    int numEvents = clusters[i].size(); 
	    for(int j=0; j < numEvents; j++){
		int oldCluster = i; 
		int newCluster = -1; 
		EventI currentEvent = clusters[i].elAt(j); 
		if(useCentroid){
		    newCluster = closestCentroid(clusters, currentEvent);
		}
		else {
		    // So we use closestPoint instead. 
		    newCluster = closestPoint(clusters, currentEvent); 
		}
		
		if(newCluster != oldCluster){
		    Debug.dp(Debug.FN_PARAMS, "old = " + oldCluster + " new = " + newCluster); 
		    hasChanged = true; 
		    clusters[oldCluster].removeEvent(currentEvent); 
		    clusters[newCluster].addEvent(currentEvent); 
		    numEvents--; 
		    j--; 
		}
	    }
	}
	// Debug.setDebugLevel(oldDL); 
	return hasChanged; 
    }

    int closestCentroid(KMCluster[] clusters, EventI ev){
	float minDistance = Float.MAX_VALUE; 
	int minCluster = 0; 
	for(int i=0; i < clusters.length; i++){
	    float dist = clusters[i].distFromCentroid(ev); 
	    if(dist <= minDistance){
		minDistance = dist; 
		minCluster = i; 
	    }
	}
	return minCluster; 
    }

    int closestPoint(KMCluster[] clusters, EventI ev){
	float minDistance = Float.MAX_VALUE; 
	int minCluster = 0; 
	for(int i=0; i < clusters.length; i++){
	    float dist = clusters[i].findClosest(ev); 
	    if(dist <= minDistance){
		minDistance = dist; 
		minCluster = i; 
	    }
	}
	return minCluster; 
    }

    void distribute(EventVecI[] data, KMCluster[] clusters){
	// Now let's check. 
	if(orderedAllocate){
	    // So now we have to allocate on the assumption of locality. 
	    int numClusters = clusters.length; 
	    for(int i=0; i < data.length; i++){
		EventVecI currentEvents = data[i]; 
		// Now we have to allocate these instances to the clusters. 
		// Algorithm is as follows: 
	        // Say we have 3 events and 5 clusters. 
		// Then we want to put 0->0 1->2 and 2->4
		int numEvents = currentEvents.size();
		for(int j=0; j < currentEvents.size(); j++){
		    int clusterPos = j*numClusters/numEvents; 
		    clusters[clusterPos].addEvent(currentEvents.elAt(j)); 
		}
	    }
	}
	else { //randomAllocate
	    int numClusters = clusters.length; 
	    for(int i=0; i < data.length; i++){
		EventVecI currentEvents = data[i]; 
		for(int j=0; j < currentEvents.size(); j++){
		    int randomCluster = (int) (Math.random()*numClusters); 
		    clusters[randomCluster].addEvent(currentEvents.elAt(j)); 
		}
	    }
	}
	
    }

    
    // Pulls the data based on the variables for this object being set. 
    EventVecI[] pullData(ClassStreamEventsVecI csevi){
	if(ignoreClasses){
	    int numStreams = csevi.size(); 
	    EventVecI[] retval = new EventVecI[numStreams]; 
	    StreamEventsVecI sevi = csevi.getStreamEventsVec(); 
	    ClassificationVecI cvi = csevi.getClassVec(); 
	    int totalEvents = 0; 
	    for(int i=0; i <  numStreams; i++){
		EventVecI events = sevi.elAt(i).getEvents(pepIndex); 
		totalEvents += events.size(); 
		retval[i] = events; 
	    }
	    if(autoNumClusters){
		numClusters = (int) Math.ceil(((float) totalEvents)/numStreams); 
	    }
	    return retval; 
	}
	else {
	    // The class we want to cluster on is 
	    StreamEventsVecI sevi = csevi.getStreamEventsVec(); 
	    ClassificationVecI cvi = csevi.getClassVec(); 
	    int numStreams = csevi.size(); 
	    Vector evs = new Vector(); 
	    int totalEvents = 0; 
	    int streamCount = 0; 
	    for(int i=0; i <  numStreams; i++){
		if(cvi.elAt(i).getRealClass() == classToCluster){
		    EventVecI events = sevi.elAt(i).getEvents(pepIndex); 
		    totalEvents += events.size(); 
		    evs.addElement(events); 
		    streamCount++; 
		}
	    }
	    if(autoNumClusters){
		numClusters = (int) Math.ceil(((float) totalEvents)/streamCount); 
	    }

	    EventVecI[] retval = new EventVecI[evs.size()]; 
	    
	    for(int i=0; i < retval.length; i++){
		retval[i] = (EventVecI) evs.elementAt(i); 
	    }
		
	    return retval; 
	}
    }
    
}
