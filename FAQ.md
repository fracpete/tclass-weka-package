# What is TClass? #

TClass is a supervised learner for multivariate time series.

A supervised learner is a computer program that learns by presentation of classified examples. For example, a supervised learner could learn to tell different fruit, if given lots of examples of different fruit and told what kind of fruits there are (like: "Here's a banana: it's boomerang-shaped, smooth and yellow", and "Here's an orange: it's round, dimpled and orange" and so on). Eventually the computer "learns" and you can ask questions like: "There's a fruit here, that's green and round with smooth skin -- what is it?"

Just to clarify, this is called supervised learning because the learner is told what the types of fruit are. An unsupervised learner would be where you point the learner at a fruit bowl and say "There's a fruit bowl: you figure out what the different types of fruit are."

Supervised learning is hardly new. In fact a whole area, called [machine learning](http://directory.google.com/Top/Computers/Artificial_Intelligence/Machine_Learning/), has developed that studies supervised learners. Even before machine learning, statisticians were interested in classification.

What's different about TClass is that learns not from an attribute-value representation (like: colour is red, shape is round, etc) but from time series data. A time series is a value that varies over time. For example, if you log the temperature in your office every half an hour, then this is a time series. A multivariate time series is a time series that has more than one measurement -- e.g. if you logged temperature, humidity and pressure.

That's fine -- but what does this have to do with classification problems -- like the fruit one above?

Humans frequently classify time series. For example, speech can be characterised as a multivariate time series. TClass can be set up to recognise individual words. There are many other domains with time series -- a few of which are below.

# What domains has TClass been used on? #

TClass has been used on several real-world and artificial domains. These include:

  * **ECG Classification:** Electrocardiographs are measurements of the heart's electrical signals. Cardiologists can often diagnose patients' heart problems by examining these recordings. The diagnosis can be used to label examples (e.g. healthy or sick), which then provide the training examples for TClass. When used with TClass, a single heartbeat is analysed, and diagnosed, based on training examples provided by the experts.
  * **Sign recognition:** Signs from Auslan were recorded using high-tech gloves that measured finger bends, each hand's position and its orientation. Each glove records the measurements about 100 times a second. These signs are labelled by their type and provided as examples. The computer can then produce a classifier to label unseen signs.

# What platforms does TClass run on? #

TClass runs on any platform that supports Java 2.


# What license is it released under? #

TClass is released under the [GNU General Public License](http://www.gnu.org/licenses/gpl.html).