# Basics #
This page assumes that you have already installed TClass successfully and have some background in machine learning and have possibly even read the paper and/or PhD on TClass. Once you have ensured that both TClass and Weka are in your CLASSPATH environment variable, all you need to do to run TClass is:
```
% java tclass.TClass
```
Of course, it generally helps if you do this in a directory that is set up for TClass (we will call these TClass experiment directories), for example if $TCLASS\_HOME is where you installed TClass, try for example, `$TCLASS_HOME/data/techsupport/`.

TClass requires several files. These are:

  * **Domain description file:** (Default: tclass.tdd) This file describes the domain of interest: the number of channels, their types etc. etc.
  * **Training set:** (Default: tclass.tsl) This file lists the name of each datafile that contains a training time series and each time series' class.
  * **Test set:** (Default: tclass.ttl) This file lists the name of each data filethat contains a testing time series and its class.
  * **Run settings:** (Default: tclass.tal) This file describes which meta-features to extract from which channels and how to cluster them.

In addition, in most TClass experiment directories, you will find a "data" sub-directory which contains the actual time series data. Time series data are indicated by a ".tsd" suffix. These and more are described in [Waleed's PhD](http://www.cse.unsw.edu.au/~waleed/phd/), in particular this section on the practical implementation of TClass.

# TClass command line options #
TClass currently supports the following command line options:
  * `-tr filename`: Use filename for the training set.
  * `-te filename`: Use filename for the test set.
  * `-dd filename`: Use filename for the domain description.
  * `-s filename`: Use filename for the settings for the learning task.
  * `-l learner`: Use learner (where learner is a weka class) instead of the default `weka.classifiers.trees.J48`. Note that you can use ":" as a separator. So for example, if you wanted to use part, but with a minimum of 5 examples per leaf node (usually expressed at the command line as -M 5), it could be given as -l weka.classifiers.trees.PART:-M:5. Likewise, to use `AdaBoost` with J48 as the base learner, this could be given as -l `weka.classifiers.meta.AdaBoostM1:-W:weka.classifiers.trees.J48`
  * `-md`: Make a description of the extracted rule, using the method described in [Waleed's PhD Thesis](http://www.cse.unsw.edu.au/~waleed/phd/). This takes a synthetic feature, and translates it to a more human-readable form. Note: Currently,this only works with directed clustering and decision rule or decision tree based learners that produce attribute-value comparisons (such as x <= 1).

# Hints #
TClass has been developed and tested using JDK 1.2, 1.3 and 1.4.

Some java implementations have a -server option, designed for code continually running on servers. It seems to do more code optimisation in this case. Empirical experience suggests that if your TClass process is likely to run for more than 10 minutes, the -server improves performance. For processes running for more than a few hours, we have found up to a 25 per cent reduction in running time.

Also note that because of the size of the datasets, you may have to increase the amount of RAM available to java processes. This is typically done through the -Xmx command -- e.g. -Xmx200m to give a java process 200 megabytes of RAM