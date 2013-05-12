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
  * This is a sorted set of ints. 
  *
  * 
  * @author Waleed Kadous
  * @version $Id: SortedSet.java,v 1.1.1.1 2002/06/28 07:36:16 waleed Exp $
  */
      
package tclass.clusteralg;   
import java.util.Vector;

public class SortedSet {
    // Hmmm .... vectors or arrays? Go for arrays ...
    Vector data; 
    public SortedSet(){
        data = new Vector(); 
    }

    public void add(int s){
        insertOrd(s); 
    }
    public void add(int[] ss){
        for(int i =0 ; i < ss.length; i++){
            insertOrd(ss[i]); 
        }
    }
    boolean insertOrd(int s){
        int currentSize = size(); 
        if(currentSize == 0){
            data.addElement(new Integer(s)); 
            return true; 
        }
        for(int i=0; i < currentSize; i++){
            int currentInt= ((Integer) data.elementAt(i)).intValue(); 
            if(currentInt == s){
                return true; 
            }
            if(s < currentInt){
                data.insertElementAt(new Integer(s),i); 
                return true; 
            }
        }
        // if we're still here ...
        data.addElement(new Integer(s)); 
        return true; 
    }
    
    public int size(){
        return data.size(); 
    }
    
    int[] randomSubset(int numEls){
        // If numEls > size, return whole thing. 
        int currentSize = size(); 
        if(numEls >= currentSize){
            int[] retval = new int[currentSize]; 
            for(int i=0; i < retval.length; i++){
                retval[i] = elAt(i); 
            }
            return retval; 
        }
        else {
            int[] retval = new int[numEls]; 
            int currentChoiceIndex;
            for(int i=0; i < retval.length; i++){
                currentChoiceIndex = (int) (Math.random()*currentSize);
                while(foundIn(elAt(currentChoiceIndex), retval, i)){
                    currentChoiceIndex = (int) (Math.random()*currentSize);
                }
                retval[i] = elAt(currentChoiceIndex); 
            }
            return retval; 
        }
    }

    public int elAt(int n){
        return ((Integer) data.elementAt(n)).intValue(); 
    }
     boolean foundIn(int point, int[] points, int maxIndex){
        for(int i=0; i < maxIndex; i++){
            if(points[i] == point){
                return true; 
            }
        }
        return false; 
    }
    @Override
    public String toString(){
        StringBuffer retval = new StringBuffer(); 
        int currentSize = size(); 
        for(int i = 0; i < currentSize; i++){
            retval.append("Rank " + i + ": " + data.elementAt(i) + "\n"); 
        }
        return retval.toString(); 
          
    }
    
    String printArr(int[] array){
        StringBuffer retval = new StringBuffer("[ "); 
        for(int i=0; i < array.length; i++){
            retval.append(array[i] + " "); 
        }
        retval.append("]"); 
        return retval.toString(); 
    }
    
    public static void main(String[] args){
        int maxSize = 10; 
        int numInserts = 100; 
        SortedSet ss = new SortedSet(); 
        int randValue; 
        for(int i=0; i < numInserts; i++){
            randValue = (int) (Math.random()*20); 
            ss.add(randValue); 
            System.out.println("Inserted " + i + " with value " + randValue); 
        }
        System.out.println("Top results are: "); 
        System.out.println(ss.toString()); 
        for(int i=0; i < 10;i ++){
            System.out.println("Random subset is: " + ss.printArr(ss.randomSubset(i))); 
        }
    }

}
