package tclass.clusteralg; 
import tclass.*; 

public class GClustVec implements ClusterVecI {
    EventDescI eventDesc; 
    GClust[] clusters; 
    float[] sds; 
    int pepIndex; 
    int numParams; 
    float[][] clustSDs; 
    int distMetric;
    EventI[] origEvents; 
    
    public GClustVec(EventDescI eventDesc, int numClusters, float[] sds, int pepIndex, float[][] clustSDs, int distMetric, EventI[] origEvents){
        this.eventDesc = eventDesc; 
        numParams = eventDesc.numParams(); 
        clusters = new GClust[numClusters]; 
        this.sds = sds; 
        this.pepIndex = pepIndex; 
        this.clustSDs = clustSDs; 
        this.distMetric = distMetric; 
        this.origEvents = origEvents; 
    }
    
    public void insert(GClust clus, int position){
        clusters[position] = clus; 
    }
    
    public int size(){
        return clusters.length; 
    }
    
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
        return clusters[i]; 
    }

    // This is just bullshit. Ignore this. 

    public ClusterMem findBestLabel(EventI event){
        float[] distances = getDistances(event); 
        int[] best = smallestTwo(distances); 
        float confidence; 
        if(distMetric == GainCluster.DISTRATIO){
            confidence = (float) Math.log(distances[best[1]]/distances[best[0]]); 
           
        }
        else {
            confidence = (float) 1/distances[best[0]]; // 1/ to cause it to be the same way.  
        }
        ClusterMem retval = new ClusterMem(clusters[best[0]], confidence); 
        retval.setOrigEvent(event); 
        return retval;
        
    }
    
    public float findMatch(int cluster, StreamEventsI sei){
        // First pull out the events of interest: 
        EventVecI events = sei.getEvents(pepIndex); 
        int numEvents = events.size(); 
        float currMatch; 
        // To get the confidence we need the most popular and second most popular. 
        float bestMatch = -Float.MAX_VALUE; 
        for(int i=0; i < numEvents; i++){
            float[] distances = getDistances(events.elAt(i)); 
            int[] best = smallestTwo(distances); 
            if(best[0] == cluster){
                // In other words, it's a match. 
                if(distMetric == GainCluster.DISTRATIO){
                    currMatch = distances[best[1]]/distances[best[0]]; 
                }
                else {
                    currMatch = 1/distances[best[0]]; 
                }
                if(currMatch > bestMatch){
                    bestMatch = currMatch; 
                }
            }
        }
        if(bestMatch == -Float.MAX_VALUE){
            return 0; 
        }
        else {
            return (float) Math.log(bestMatch); 
        }
    }
        
    public float[] getDistances(EventI ev){
        float[] retval = new float[clusters.length]; 
        for(int i=0; i < clusters.length; i++){
            retval[i] = distance(clusters[i].cent(), ev); 
        }
        return retval; 
    }
    float distance(EventI ev1, EventI ev2){
        float retval; 
        float sumDist2=0; 
        for(int i=0; i < numParams; i++){
            float rawDist = (ev1.valOf(i)-ev2.valOf(i))/sds[i]; 
            sumDist2 += rawDist*rawDist; 
        }
        retval = (float) Math.sqrt(sumDist2);
        return retval; 
    }

    // Finds the index of the smallest and second smallest instances in the given array. 
    int[] smallestTwo(float[] data){
        // Assumes we have at least two elements 
        int smallest, secondSmallest; 
        if(data[0] > data[1]){ 
            smallest = 1; 
            secondSmallest = 0; 
        }
        else {
            smallest = 0; 
            secondSmallest = 1; 
        }
        for(int i=2; i < data.length; i++){
            if(data[i] < data[secondSmallest]){
                if(data[i] < data[smallest]){
                    secondSmallest = smallest; 
                    smallest = i; 
                }
                else {
                    secondSmallest = i; 
                }
            }
        }
        int[] retval = new int[2]; 
        retval[0] = smallest; 
        retval[1] = secondSmallest; 
        return retval; 
    }
}
