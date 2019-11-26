import java.io.IOException;
import weka.classifiers.trees.Id3;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * @author Gowtham Girithar Srirangasamy
 *
 */
public class Presentation {
    /**
     * This method is to load the data set.
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Instances getDataSet(String fileName) throws IOException {
        /** the arffloader to load the arff file */
        ArffLoader loader = new ArffLoader();
        /** load the traing data */
        loader.setSource(Presentation.class.getResourceAsStream("/" + fileName));
        Instances dataSet = loader.getDataSet();
        /** set the index based on the data given in the arff files */
        dataSet.setClassIndex(dataSet.numAttributes()-1);
        return dataSet;
    }

    /**
     * This method is used to process the input and return the statistics.
     *
     * @throws Exception
     */
    public static void processId3() throws Exception {

        Instances trainingDataSet = getDataSet("decision-train.arff");
        Instances testingDataSet = getDataSet("decision-test.arff");

        Classifier id3Classifier = new Id3();

        /** */
        id3Classifier.buildClassifier(trainingDataSet);
        System.out.println(id3Classifier);
        /**
         * train the alogorithm with the training data and evaluate the
         * algorithm with testing data
         */
        Evaluation evalId3 = new Evaluation(trainingDataSet);
        evalId3.evaluateModel(id3Classifier, testingDataSet);
        /** Print the algorithm summary */
        System.out.println(evalId3.toSummaryString());
        System.out.println(evalId3.toMatrixString());
        System.out.println(evalId3.toClassDetailsString());

    }

    public static void processLinearRegression() throws Exception {

        Instances trainingDataSet = getDataSet("linear-train.arff");
        Instances testingDataSet = getDataSet("linear-test.arff");
        /** Classifier here is Linear Regression */
        Classifier classifier = new weka.classifiers.functions.LinearRegression();
        /** */
        classifier.buildClassifier(trainingDataSet);
        System.out.println(classifier);
        /**
         * train the alogorithm with the training data and evaluate the
         * algorithm with testing data
         */
        Evaluation eval = new Evaluation(trainingDataSet);
        eval.evaluateModel(classifier, testingDataSet);
        /** Print the algorithm summary */
        System.out.println(eval.toSummaryString());

        Instance predicationDataSet = getDataSet("test-confused.arff").lastInstance();
        double value = classifier.classifyInstance(predicationDataSet);
        /** Prediction Output */
        System.out.println(value);
    }
}