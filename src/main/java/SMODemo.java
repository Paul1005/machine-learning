import java.io.File;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * @author Gowtham Girithar Srirangasamy
 */
public class SMODemo {

    /**
     * file names are defined
     */
    public static final String TRAINING_DATA_SET_FILENAME = "svm-train.arff";
    public static final String TESTING_DATA_SET_FILENAME = "svm-test.arff";
    public static final String PREDICTION_DATA_SET_FILENAME = "svm-confused.arff";

    /**
     * This method is to load the data set.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Instances getDataSet(String fileName) throws IOException {
        /**
         * we can set the file i.e., loader.setFile("finename") to load the data
         */
        int classIdx = 3;
        /** the arffloader to load the arff file */
        ArffLoader loader = new ArffLoader();
        /** load the traing data */
        loader.setSource(SMODemo.class.getResourceAsStream("/" +
                fileName));
        /**
         * we can also set the file like loader3.setFile(new
         * File("test-confused.arff"));
         */
        //loader.setFile(new File(fileName));
        Instances dataSet = loader.getDataSet();
        /** set the index based on the data given in the arff files */
        dataSet.setClassIndex(classIdx);
        return dataSet;
    }

    /**
     * This method is used to process the input and return the statistics.
     *
     * @throws Exception
     */
    public static void process() throws Exception {

        Instances trainingDataSet = getDataSet(TRAINING_DATA_SET_FILENAME);
        Instances testingDataSet = getDataSet(TESTING_DATA_SET_FILENAME);
        Instances confusedDataSet = getDataSet(PREDICTION_DATA_SET_FILENAME);
        /** Classifier here is Linear Regression */
        Classifier model = (Classifier) new SMO();
        model.buildClassifier(trainingDataSet);

        /**
         * train the alogorithm with the training data and evaluate the
         * algorithm with testing data
         */
        Evaluation eval = new Evaluation(trainingDataSet);
        eval.evaluateModel(model, testingDataSet);
        /** Print the algorithm summary */
        System.out.println("** SMO  Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(model);
        int ClassLabel = 100;
        ClassLabel = (int) model.classifyInstance(confusedDataSet.firstInstance());

        System.out.println("Classified: " + ClassLabel);
        System.out.println(trainingDataSet.attribute(3).value(ClassLabel));
    }

}