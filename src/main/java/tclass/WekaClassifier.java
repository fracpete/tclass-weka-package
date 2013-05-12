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

package tclass;  

import weka.core.Instance;

public class WekaClassifier {
    weka.classifiers.Classifier classifier; 
    String name = "weka"; 
    String description = "Weka Classifier"; 
    public WekaClassifier(weka.classifiers.Classifier classifier){
        this.classifier = classifier; 
    } 
      public String getName(){
	return name; 
    }

   public String getDescription(){
	return description; 
    }

    public void classify(Instance inst, ClassificationI
			 classn) throws Exception {
        double bestClass = classifier.classifyInstance(inst); 
        classn.setPredictedClass((int) bestClass); 
        classn.setPredictedClassConfidence(1); 
    }
}

