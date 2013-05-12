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
  * This is an example of a clustering algorithm, in other words, 
  * what we expect our clustering algorithms to be able to do. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: ClassHistogram.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */
      
package tclass.clusteralg;   

public class ClassHistogram {
    int[] classCounts;
    int totalInstances; 
    int numClasses; 
    static final float LN2 = (float) Math.log(2); 
    public ClassHistogram(int numClasses){
        this.numClasses = numClasses; 
        classCounts = new int[numClasses]; 
        // Assume they'll all be initialised to zero. 
    }
    
    public void inc(int classNum){
        classCounts[classNum]++; 
        totalInstances++; 
    }

    // Gets the count for an individual class.
    public int getCount(int classNum){
        return classCounts[classNum]; 
    }

    static float log2(float x){
        return (float) Math.log(x)/LN2; 
    }

    // This function based on the definition of information given in 
    // Quinlan's C4.5 book (page 21)
    public float info(){
        float totalInformation = 0;
        for(int i=0; i < numClasses; i++){
            if(classCounts[i] != 0){
                float frac= ((float) classCounts[i])/totalInstances; 
                totalInformation += -frac*log2(frac); 
            }
        }
        return totalInformation; 
    }
    
    public int getCount(){
        return totalInstances; 
    }

    @Override
    public String toString(){
        StringBuffer retval = new StringBuffer("Cl\tFreq\n"); 
        for(int i=0; i < numClasses; i++){
            retval.append(i + "\t" + classCounts[i] + "\n"); 
        }
        retval.append("Total instances: " + totalInstances + "\n"); 
        retval.append("Information: " + info()+"\n"); 
        return retval.toString(); 
    }

    public static void main(String[] args){
        // Make a histogram with 2 classes
        int numDivisions = Integer.parseInt(args[0]); 
        // Now check information gain for different examples
        for(int i=0; i <= numDivisions; i++){
            ClassHistogram ch = new ClassHistogram(2);
            System.out.println(i + " positive " + (numDivisions-i) + " negative\n"); 
            for(int j=0; j < i; j++){
                ch.inc(0); // Add i positive examples
            }
            for(int j=0; j < (numDivisions-i); j++){
                ch.inc(1); 
            }
            // Now print out the histogram. 
            System.out.println(ch); 
        }
    }
}
