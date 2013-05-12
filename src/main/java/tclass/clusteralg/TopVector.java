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
  * This is a class for storing the best "n" results. 
  * Note: best is "biggest". . 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: TopVector.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */
      
package tclass.clusteralg;   
import java.util.Vector;

public class TopVector {
    // Hmmm .... vectors or arrays? Go for arrays ...
    Vector data; 
    Vector fitnesses; // Ok, ok, yes it's a frigging nasty hack. 
                              // But this is a prototype. FIXME later. 
    float worstIncluded = Float.MAX_VALUE; 
    int length; 
    int maxSize; 
    int currentSize; 
    public TopVector(int maxElements){
        data = new Vector(maxElements); 
        fitnesses = new Vector(maxElements); 
        maxSize = maxElements; 
        currentSize = 0;
    }
    public void add(Object ob, float fitness){
        if(currentSize < maxSize){
            // Set the new lower bar to the last element of the list. 
            insertOrd(ob, fitness); 
            worstIncluded = ((Float) fitnesses.elementAt(currentSize)).floatValue(); 
            currentSize++; 
        }
        else { // i.e. we're at capacity. 
            if(fitness <= worstIncluded){
                insertOrd(ob, fitness); 
                data.setSize(maxSize); 
                worstIncluded = ((Float) fitnesses.elementAt(maxSize-1)).floatValue(); 
                currentSize=maxSize; 
            }
        }
    }
    
    boolean insertOrd(Object ob, float fitness){
        if(currentSize == 0){
            data.addElement(ob); 
            fitnesses.addElement(new Float(fitness)); 
            return true; 
        }
        for(int i=0; i < currentSize; i++){
            float currentFitness = ((Float) fitnesses.elementAt(i)).floatValue();
            if(fitness > currentFitness){
                data.insertElementAt(ob, i); 
                fitnesses.insertElementAt(new Float(fitness), i); 
                return true; 
            }
        }
        // if we're still here ...
        fitnesses.addElement(new Float(fitness)); 
        data.addElement(ob); 
        return true; 
    }
    
    public int size(){
        return currentSize; 
    }
    
    public int maxSize(){
        return maxSize(); 
    }
    
    public Object elAt(int n){
        return data.elementAt(n); 
    }
    
    public float fitnessAt(int n){
        return ((Float) fitnesses.elementAt(n)).floatValue(); 
    
   }
    
    @Override
    public String toString(){
        StringBuffer retval = new StringBuffer(); 
        for(int i = 0; i < currentSize; i++){
            retval.append("Rank " + i + ": " + data.elementAt(i) + " value " + fitnesses.elementAt(i) + "\n"); 
        }
        return retval.toString(); 
          
    }

    public static void main(String[] args){
        int maxSize = 10; 
        int numInserts = 100; 
        TopVector tv = new TopVector(10); 
        float randValue; 
        for(int i=0; i < numInserts; i++){
            randValue = (float) Math.random(); 
            tv.add(new String("a" + i), randValue); 
            System.out.println("Inserted a" + i + " with fitness " + randValue); 
        }
        System.out.println("Top results are: "); 
        System.out.println(tv.toString()); 
    }

}
