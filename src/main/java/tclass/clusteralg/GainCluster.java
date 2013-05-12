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
 * An algorithm that does clustering based on the gain that is to be
 * received on using these particular centroids. It does a random
 * search.
 * 
 *
 * 
 * @author Waleed Kadous
 * @version $Id: GainCluster.java,v 1.3 2002/08/02 05:05:34 waleed Exp $
 * $Log: GainCluster.java,v $
 * Revision 1.3  2002/08/02 05:05:34  waleed
 * Hmmm ... fixed the sd's, but then forgot that distance measure
 * itself has to change.
 *
 * Revision 1.2  2002/08/02 05:02:21  waleed
 * Added better handling of discrete parameters of metafeatures.
 * Standard deviation is no longer applied.
 *
 *
 * Yuuuuk. This thing is getting really messy and ugly. But too much effort into it. 
 * Needs to be rewritten. 
 */
      
package tclass.clusteralg;   
import java.util.Random;

import tclass.ClassStreamEventsVecI;
import tclass.ClassificationVecI;
import tclass.ClusterAlgI;
import tclass.ClusterVec;
import tclass.ClusterVecI;
import tclass.DomDesc;
import tclass.EventDescI;
import tclass.EventDescVecI;
import tclass.EventI;
import tclass.EventVecI;
import tclass.InvalidParameterException;
import tclass.Param;
import tclass.ParamVec;
import tclass.StreamEventsVecI;
import tclass.util.Debug;
import tclass.util.FastMath;

// import weka.core.Statistics; 

public class GainCluster implements ClusterAlgI { 
    static final float MIN_SD = 1e-9f; 
    static final int RANDOM = 1;
    static final int GENETIC = 2; 
    static final int GAINRATIO = 1; 
    static final int GAIN = 2; 
    static final int CHISQUARE = 3; 
    static final int DISTRATIO = 1;
    static final int DISTANCE = 2; 
    String baseName = "directed";
    String description = "Implements clustering using a random search for high-gain centroids";
    EventDescVecI edvi = null; 
    DomDesc domDesc = null; 
    int numTrials  = 1000; 
    int clustBias = 1; 
    int minCent = 2; 
    int maxCent = 8; 
    int pepIndex = 0; 
    int searchAlg = RANDOM; 
    
    int evalMetric = GAINRATIO; 

    int distMetric = DISTRATIO; 

    // These are required by the genetic algorithm. 
    int numRounds = 10; 
    float survivalRate = 0.05f; // This is the ratio of cases which will survive the next round. 
    float crossoverRate = 0.4f; // Percentage of instances that will be "crossed over" 
    float mutationRate = 0.05f;  // Random rate of mutation. 
    
    // These are used for calculations. 
    float[][] points; 
    int[] classes;  // An array containing the class of each instance as an int. 
    int[] clusters; // An array containing the cluster of each instance as an int. 
    int numInstances; 
    int numParams; 
    int numClasses; 
    boolean isDiscrete[]; 
    float[] sds; 
    float[][] clustSDs; // Store the individual standard deviation of each cluster. 
    float[] avgs; 
    EventI[] origEvents; 
    ClassHistogram allHist; // The histogram of all classes. 
    ClassHistogram[] chs; // Made global to simplify debugging 
    int[] clusterMem; 
    float allInfo; 
    

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

    @Override
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
        else if(p.equals("numTrials")){
            // Cool bananas. Do nothing. 
            try {
                numTrials = Integer.parseInt(v); 
            }
	    catch(NumberFormatException nfe){
		throw new InvalidParameterException(p, v, "Could not understand number of trials."); 
	    }
        }
        else if(p.equals("clustBias")){
            // Cool bananas. Do nothing. 
            try {
                clustBias = Integer.parseInt(v); 
            }
	    catch(NumberFormatException nfe){
		throw new InvalidParameterException(p, v, "Could not understand bias exponent."); 
	    }
        }
        else if(p.equals("minCent")){
            // Cool bananas. Do nothing. 
            try {
                minCent = Integer.parseInt(v); 
            }
	    catch(NumberFormatException nfe){
		throw new InvalidParameterException(p, v, "Could not understand number of trials."); 
	    }
        }
        else if(p.equals("maxCent")){
            // Cool bananas. Do nothing. 
            try {
                maxCent = Integer.parseInt(v); 
            }
            catch(NumberFormatException nfe){
                throw new InvalidParameterException(p, v, "Could not understand number of trials."); 
            }
	}
        else if(p.equals("searchAlg")){
            if(v.equals("random")){
                searchAlg = RANDOM; 
            }
            else if(v.equals("genetic")){
                searchAlg = GENETIC; 
            }
            else {
                throw new InvalidParameterException(p, v, "Algorithm is random or genetic"); 
            }
        }
        else if(p.equals("dispMeasure")){
            if(v.equals("gainratio")){
                evalMetric = GAINRATIO; 
            }
            else if(v.equals("chisquare")){
                evalMetric = CHISQUARE; 
            }
            else {
                throw new InvalidParameterException(p, v, "Algorithm is random or genetic"); 
            }
        }
        else if(p.equals("distMetric")){
            if(v.equals("distratio")){
                evalMetric = DISTRATIO; 
            }
            else if(v.equals("distance")){
                evalMetric = DISTANCE; 
            }
            else {
                throw new InvalidParameterException(p, v, "Distance is distratio or distance"); 
            }
        }
        else {
            throw new InvalidParameterException(p, v, "I was expecting exp"); 
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
        pv.add(new Param("metafeature", "Name of metafeature to operate on", "First metafeature")); 
        pv.add(new Param("numTrials", "Number of Trials to Run", "1000")); 
        pv.add(new Param("minCent", "Minimum number of centroids", "2")); 
        pv.add(new Param("maxCent", "Maximum number of centroids", "8")); 
        pv.add(new Param("clustBias", "Bias exponent towards more clusters", "1")); 
        pv.add(new Param("searchAlg", "Search algorithm: random or genetic", "random")); 
        pv.add(new Param("evalMetric", "Evaluation metric: gainratio, gain or chisquare", "random"));         
        return pv; 
    }
    
    public ClusterVecI cluster(ClassStreamEventsVecI csvi){
        // int oldDebug = Debug.getDebugLevel(); 
        // Debug.setDebugLevel(Debug.EVERYTHING); 

        // First, flatten the data and get it into a nice form. 
        Debug.dp(Debug.EVERYTHING, ""); 
        if(!createData(csvi)){
            // Uh oh ... no instances ;-(. 
            // Return an empty clusterVecI. 
            return new ClusterVec(); 
        }
        findSDs(); 
        Debug.dp(Debug.EVERYTHING, "Preprocessing complete"); 
        if(allInfo == 0){
	    return new ClusterVec(); 
        }
        // Then, normalise the data. 
        // Then pick some random points. 
        int[] bestCentroids = new int[0]; 
        float bestGR = -Float.MAX_VALUE; 
        int[] bestClusterMem = new int[0]; 
        int[] currentCentroids;
        float currentGR; 
        ClassHistogram[] bestchs = new ClassHistogram[0]; 
        // System.out.println("Search Alg = " + searchAlg); 
        if(searchAlg == RANDOM){ 
            for(int i=0; i < numTrials; i++){
                currentCentroids = randomCentroids(); 
                currentGR = gainRatio(currentCentroids); 
                Debug.dp(Debug.EVERYTHING, "Test: " + i  + " Current: " + currentGR + " Best: " + bestGR); 
                if(currentGR > bestGR){
                    bestCentroids = currentCentroids; 
                    bestGR = currentGR; 
                    bestchs = chs; 

		    bestClusterMem = clusterMem; 
                }
            }       
        }
        else{
            // Ok, this is the new thing. 
            TopVector parents = new TopVector((int) (survivalRate*numTrials));  
            TopVector children = new TopVector((int) (survivalRate*numTrials));  
            int countSoFar = 0; 
            // Start out with a normal run; pretty much the same as 
            // the random search, except we insert results into the TopVector. 
            for(int i=0; i < numTrials; i++){
                
                currentCentroids = randomCentroids(); 
                currentGR = gainRatio(currentCentroids); 
                parents.add(currentCentroids, currentGR); 
		if(currentGR > bestGR){
                    bestCentroids = currentCentroids; 
                    bestGR = currentGR; 
                    bestchs = chs; 
                    bestClusterMem = clusterMem; 
                }
                Debug.dp(Debug.EVERYTHING, "GTest: " + i  + " Current: " + currentGR + " Best: " + bestGR); 
                countSoFar++; 
            }
            for(int i=0; i < (numRounds-1); i++){
                children = new TopVector((int) (survivalRate*numTrials));  
                for(int j=0; j < numTrials; j++){
                    currentCentroids = breed(parents); 
                    // Breeds a bunch of centroids. 
                    currentGR = gainRatio(currentCentroids); 
                    children.add(currentCentroids, currentGR); 
		    if(currentGR > bestGR){
			bestCentroids = currentCentroids; 
			bestGR = currentGR; 
			bestchs = chs; 
                         
			bestClusterMem = clusterMem; 

		    }
                    Debug.dp(Debug.EVERYTHING, "GTest: " + countSoFar + " Current: " + currentGR + " Best: " + bestGR); 

                    countSoFar++; 
                }
                parents = children; 
            }
        }
        if(Debug.getDebugLevel() >= Debug.EVERYTHING){
            System.err.println("The BEST division (gr = " + bestGR + ", n = " + bestCentroids.length + ") is: "); 
            for(int i=0; i < bestCentroids.length; i++){
		System.err.println(bestchs[i]); 
            }
        }
        findClustSDs(bestCentroids, bestClusterMem);         
        ClusterVecI cvi = makeClusters(bestCentroids);
        // Debug.setDebugLevel(oldDebug); 
        return cvi; 
    }
    
    public int[] breed(TopVector genes){
        Debug.dp(Debug.EVERYTHING, "Breeding ..."); 
        int numGenes = genes.size(); 
        int[] retval; 
        Random R = new Random(); // Why use random here? Because it didn't look 
        // Random. 
        // Crossover stage 
        if(R.nextFloat() < crossoverRate){ // Check if there is a crossover; 
            // Make a set out of the instances. 
            int mumIndex = (int) Math.abs(R.nextInt() % numGenes); 
            int dadIndex = (int) Math.abs(R.nextInt() % numGenes); 
            while(dadIndex == mumIndex){
                dadIndex = (int) Math.abs(R.nextInt() % numGenes); 
            }
            Debug.dp(Debug.EVERYTHING, "Mother is: " + printArr((int[]) genes.elAt(mumIndex))); 
            Debug.dp(Debug.EVERYTHING, "Father is: " + printArr((int []) genes.elAt(dadIndex))); 

            SortedSet s = new SortedSet();  
            s.add((int [] ) genes.elAt(mumIndex)); 
            s.add((int [] ) genes.elAt(dadIndex)); 
            int numCentroids = minCent + (int) Math.floor(Math.pow(Math.random(), 1.0/clustBias)*(maxCent-minCent+1)); 
            retval =  s.randomSubset(numCentroids); 
        }
        else {
            retval = (int[]) genes.elAt((int) Math.abs(R.nextInt() % numGenes)); 
        }
        // Mutation stage
        int currentCent; 
        for(int i=0; i < retval.length; i++){
            if(R.nextFloat() < mutationRate){
                currentCent = (int) (R.nextFloat()*numInstances);
                while(foundIn(currentCent, retval, retval.length)){
                    currentCent = (int) (R.nextFloat()*numInstances);
                }
                retval[i] = currentCent; 
            }
        }
        Debug.dp(Debug.EVERYTHING, "Child is: " + printArr(retval)); 
        return retval; 
    }
    
    // Note: Sets the following class variables: 
    // 1. Number of instances
    // 2. Number of streams
    // 3. Classes
    // 4. allHist
    // 5. allInfo. 
    // 6. numClasses

    String printArr(int[] array){
        StringBuffer retval = new StringBuffer("[ "); 
        for(int i=0; i < array.length; i++){
            retval.append(array[i] + " "); 
        }
        retval.append("]"); 
        return retval.toString(); 
    }

    boolean createData(ClassStreamEventsVecI csevi){
        StreamEventsVecI sevi = csevi.getStreamEventsVec(); 
        ClassificationVecI cvi = csevi.getClassVec();
        numClasses = cvi.getClassDescVec().size(); 
        allHist = new ClassHistogram(numClasses); 
        int numStreams = sevi.size(); 
        numParams = edvi.elAt(pepIndex).numParams(); 
        isDiscrete = new boolean[numParams]; 
	EventDescI edi = edvi.elAt(pepIndex); 
	for(int i=0; i < numParams; i++){
		if(edi.getDataType(i).getName().equals("discrete")){
			isDiscrete[i] = true; 
		        Debug.dp(Debug.EVERYTHING, "Param " + i + " is discrete."); 
		}
		else {
			isDiscrete[i] = false; 
		}
		
	}
        Debug.dp(Debug.EVERYTHING, "NumParams = " + numParams); 
        // Assume there is at least one StreamEventI
        numInstances = 0; 
        for(int i=0; i < numStreams; i++){
            numInstances += sevi.elAt(i).getEvents(pepIndex).size(); 
        }
        if(numInstances == 0){
            return false; 
        }
        classes = new int[numInstances]; 
	
        points = new float[numInstances][numParams]; 
        origEvents = new EventI[numInstances]; 
        int currentPosition = 0; 
        EventVecI events; 
        int currSize; 
        EventI currEvent; 
        for(int i=0; i < numStreams; i++){
            events = sevi.elAt(i).getEvents(pepIndex); 
            currSize = events.size(); 
            for(int j=0; j < currSize; j++){
                currEvent = events.elAt(j); 
                for(int k=0; k < numParams; k++){ // Loop to copy the values
                    points[currentPosition][k] = currEvent.valOf(k); 
                }
                // Now set up the class vector
                classes[currentPosition] = cvi.elAt(i).getRealClass(); 
                allHist.inc(cvi.elAt(i).getRealClass()); 
                origEvents[currentPosition] = currEvent; 
                currentPosition++; 
            }
        }
        Debug.dp(Debug.EVERYTHING, "Total instances: " + numInstances); 
        Debug.dp(Debug.EVERYTHING, allHist.toString()); 
        allInfo = allHist.info(); 
        return true; 
    }

    EventI[] getOrigEvents(){
        return origEvents; 
    }

    void findSDs(){
        float[] sumx= new float[numParams]; 
        float[] sumx2= new float[numParams];
        avgs = new float[numParams]; 
        sds = new float[numParams]; 
        
        float currentVal; 
        // First compute the sums ...
        for(int i=0; i < points.length; i++){
						for(int j=0; j < numParams; j++){
								currentVal = points[i][j];
								sumx[j] += currentVal; 
								sumx2[j] += currentVal*currentVal; 
						}
        }
        // And now compute the averages and standard deviations. 
        for(int i=0; i < numParams; i++){
            // Debug.dp(Debug.EVERYTHING, "i = " + i); 
            // Debug.dp(Debug.EVERYTHING, "sum = " + sumx[i]); 
            // Debug.dp(Debug.EVERYTHING, "sum2 = " + sumx2[i]); 
						if(!isDiscrete[i]){     
								avgs[i] = sumx[i]/numInstances; 
								// Var(X) = E(X*X) - E(X)*E(X)
								// sd = sqrt(var)
								sds[i] = (float) Math.sqrt(sumx2[i]/numInstances-avgs[i]*avgs[i]); 
								if(sds[i] == 0){
										sds[i] = MIN_SD; 
								}
						}
						else {
								sds[i] = 1; 
						}
        }
    }

    void findClustSDs(int[] bestCentroids, int[] bestClustMem){
         
	float[][] sumx= new float[bestCentroids.length][numParams]; 
	float[][] sumx2= new float[bestCentroids.length][numParams];
	// avgs = new float[bestCentroids.length][numParams]; 
	float avg; 
	clustSDs = new float[bestCentroids.length][numParams]; 
	int[] numPerCluster = new int[bestCentroids.length]; 
	float currentVal; 
	// First compute the sums ...
	for(int i=0; i < points.length; i++){
	    for(int j=0; j < numParams; j++){
                currentVal = points[i][j];
                sumx[bestClustMem[i]][j] += currentVal; 
                sumx2[bestClustMem[i]][j] += currentVal*currentVal; 
                numPerCluster[bestClustMem[i]]++; 
            }
        }
        // And now compute the averages and standard deviations. 
	for(int j=0; j < bestCentroids.length; j++){
	    for(int i=0; i < numParams; i++){
		// Debug.dp(Debug.EVERYTHING, "i = " + i); 
		// Debug.dp(Debug.EVERYTHING, "sum = " + sumx[i]); 
		// Debug.dp(Debug.EVERYTHING, "sum2 = " + sumx2[i]); 
					if(!isDiscrete[i]){
							avg = sumx[j][i]/numPerCluster[j]; 
							// Var(X) = E(X*X) - E(X)*E(X)
							// sd = sqrt(var)
							clustSDs[j][i] = (float) Math.sqrt(sumx2[j][i]/numPerCluster[j]-avg*avg); 
							if(clustSDs[j][i] == 0){
									clustSDs[j][i] = MIN_SD; 
							}
					}
					else {
							clustSDs[j][i] = 1; 
					}
	    }
	}
    }

    // This function generates a random selection of indexes
    // for centroids. Note: no point can be repeated. 

    int[] randomCentroids(){
        int numCentroids = minCent + (int) Math.floor(Math.pow(Math.random(), 1.0/clustBias)*(maxCent-minCent+1)); 

        // Perhaps choice of centroids based on number of
        // possibilities? There seems to be a bias to smaller cluster
        // sizes.

        // Let's see if we can come up with another way ... what if we take the 3rd
        // root? 
        
        // Debug.dp(Debug.EVERYTHING, "number of centroids = " + numCentroids); 
        if(numCentroids >= numInstances){
            numCentroids = numInstances; 
            Debug.dp(Debug.EMERGENCY, "WARNING: numCentroids was > numInstances "); 

        }
        // Selects a random number between minCent and maxCent inclusive. 
        int[] retval = new int[numCentroids]; 
        int currentCent; 
        for(int i=0; i < numCentroids; i++){
            currentCent = (int) Math.floor(Math.random()*numInstances) ;
            while(foundIn(currentCent, retval, i)){
                currentCent = (int) Math.floor(Math.random()*numInstances); 
            }
            retval[i] = currentCent; 
        }
        return retval; 
    }

    // Linear search function. Sees if point is found in points, up to
    // (but not including) maximum index maxIndex. 
    boolean foundIn(int point, int[] points, int maxIndex){
        for(int i=0; i < maxIndex; i++){
            if(points[i] == point){
                return true; 
            }
        }
        return false; 
    }
    
    // Computes the gain ratio of a particular group of centroids. 
    // This is now in the absurd, as it will also, depending on the setting of the evalMetric
    // calculate the chi'squared or the gain. 
    float gainRatio(int[] centroids){
        chs = new ClassHistogram[centroids.length]; 
        // For each centroid, there is a class histogram. 

        int nearest; 
        float newInfo = 0; 
        float splitInfo = 0; 
        float gain = 0; 
        float frac = 0; 
        int[] thisClusterMem  = new int[numInstances]; 
        

        for(int i=0; i < centroids.length; i++){
            chs[i] = new ClassHistogram(numClasses); 
        }
        for(int i=0; i < numInstances; i++){
            nearest = nearestCentroid(i, centroids); 
            thisClusterMem[i] = nearest; 
            chs[nearest].inc(classes[i]); 
            // Increment the entry in the histogram of the centroid closest 
            // to the current instance
        }
        clusterMem = thisClusterMem; 
	// The part above is the same for all evaluation metrics. The next 
        // part is specific. 
        if(evalMetric == GAINRATIO){
            // First compute the gain. Quinlan P. 22
            // newinfo = sum{i=1..n} T_i/T*info(T_i)
            // splitinfo -sum{i=1..n} T_i/T*log2(T_i/T)
            for(int i=0; i < centroids.length; i++){
                frac = ((float) chs[i].getCount())/numInstances; 
                newInfo += frac*chs[i].info(); 
                if(frac!= 0){
                    splitInfo -= frac*ClassHistogram.log2(frac); 
                }
            }
            gain = allInfo - newInfo; 
            // Gain ratio = gain/splitInfo
	    return(gain/splitInfo); 
        }
        else if(evalMetric == CHISQUARE){
            // Alright ... here we go. 
            // Notes: 
            // - There is one row for each class. 
            // - Thus we can retrieve row totals from allHist. 
            // - There is one column for each class. 
            // - Thus we can retrieve the column totals using getCount
            // - The total count of instances is numInstances. . 
            // The outer loop is by classes. The inner is by cluster. 
            float chisquaretot=0;
            int df; 
            df = (numClasses-1)*(centroids.length-1); 
            for(int i=0; i < numClasses; i++){
                for(int j=0; j < centroids.length; j++){
                    float expected = ((float) allHist.getCount(i)*chs[j].getCount())/numInstances; 
                    //if((expected) < 3.0 && (numInstances > 2*numClasses)){
                    //    Debug.dp(Debug.EMERGENCY, "WARNING: Not enough examples in test! Abandoning this test. Expected = " + expected + " for " + allHist.getCount(i) + " and " +  chs[j].getCount() ); 
                    //   return(0); 
                    //}
                    float observed = chs[j].getCount(i); 
                    float tmp; 
                    //Below needed to handle case where no instances belonging to a
                    // class have events. 
                    if(expected != 0){
                        tmp = (observed-expected)*(observed-expected)/expected; 
                    }
                    else {
                        tmp = 0; 
                    }
                    chisquaretot += tmp; 
                }
            }
            double probability = FastMath.lnChiSqProb(chisquaretot, df); 
            Debug.dp(Debug.EVERYTHING, "Chisquare tot = " + chisquaretot + " df = " + df + " logprob = " + (-probability)); 
            // To avoid precision issues. The smaller the number, the higher the negative
            // log. 
            return((float) -probability); // This should stop things from being broken outside. 
        }
        return 0; 
    }

    int nearestCentroid(int point, int[] centroids){
        float smallestDist = Float.MAX_VALUE; 
        int bestIndex = -1; 
        float currentDistance; 
        for(int i=0; i < centroids.length; i++){
            currentDistance= distance(point, centroids[i]); 
            if(currentDistance < smallestDist){
                smallestDist = currentDistance; 
                bestIndex = i; 
            }
        }
        return bestIndex; 
    }
    
    // Computes the Euclidean distance, normalised by SD. 
    float distance(int point1, int point2){
        float sumDist2=0; 
        //         Debug.dp(Debug.EMERGENCY, "p1 = " + point1 + " p2 = " + point2); 
        for(int i=0; i < numParams; i++){
	    float rawDist; 
	    if(!isDiscrete[i]){
		rawDist = (points[point1][i]-points[point2][i])/sds[i]; 
	    }
	    else {
		// Use a distance measure of "0" if equal, 
		// 1 if different. 
		rawDist = points[point1][i] == points[point2][i] ? 0: 1; 
	    }
            sumDist2 += rawDist*rawDist; 
        }
        float retval = (float) Math.sqrt(sumDist2);
        return retval; 
    }

    ClusterVecI makeClusters(int[] centroids){
        GClustVec gcv = new GClustVec(edvi.elAt(pepIndex), centroids.length, sds, pepIndex, clustSDs, distMetric, origEvents);
        for(int i=0; i < centroids.length; i++){
            GClust gc = new GClust(gcv, origEvents[centroids[i]], i); 
            gcv.insert(gc,i); 
        }
        return gcv; 
    }
   
}

