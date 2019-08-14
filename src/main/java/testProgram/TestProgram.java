package testProgram;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.Id3;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.IOException;

public class TestProgram {

    public void transformData() throws IOException {
        /*CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File("leaf.csv"));
        Instances instances = csvLoader.getDataSet();

        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(instances);
        arffSaver.setFile(new File("src/main/resources/leaf.arff"));
        arffSaver.writeBatch();*/

        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File("tennis-train.csv"));
        Instances instances = csvLoader.getDataSet();

        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(instances);
        arffSaver.setFile(new File("src/main/resources/tennis-train.arff"));
        arffSaver.writeBatch();
    }

    private Instances getData(String file) throws Exception {
        DataSource trainingSource = new DataSource(file);
        Instances instances = trainingSource.getDataSet();
        instances.setClassIndex(instances.numAttributes()-1);

        return instances;
    }

    public void processData() throws Exception {
        Instances trainingInstances = getData("tennis-train.arff");

        Id3 id3 = new Id3();
        id3.buildClassifier(trainingInstances);
        System.out.println(id3);

        Evaluation evaluation = new Evaluation(trainingInstances);

        Instances testingInstances = getData("tennis-test.arff");

        evaluation.evaluateModel(id3, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());


        J48 j48 = new J48();
        String[] options = new String[3];
        options[0] = "-R";
        options[1] = "-N";
        options[2] = "10";
        j48.setOptions(options);
        j48.buildClassifier(trainingInstances);
        System.out.println(j48);
        
        evaluation = new Evaluation(trainingInstances);

        evaluation.evaluateModel(j48, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());

        AdaBoostM1 adaBoostM1 = new AdaBoostM1();
        adaBoostM1.setClassifier(new J48());
        adaBoostM1.setNumIterations(10);
        adaBoostM1.buildClassifier(trainingInstances);
        System.out.println(adaBoostM1);

        evaluation = new Evaluation(trainingInstances);

        evaluation.evaluateModel(adaBoostM1, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());

        Bagging bagging = new Bagging();
        bagging.setClassifier(j48);
        bagging.setNumIterations(10);
        bagging.buildClassifier(trainingInstances);
        System.out.println(bagging);

        evaluation = new Evaluation(trainingInstances);

        evaluation.evaluateModel(bagging, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());

        Stacking stacking = new Stacking();
        stacking.setMetaClassifier(new J48());
        Classifier[] classifiers = {new J48(), new NaiveBayes(), new RandomForest()};
        stacking.setClassifiers(classifiers);
        stacking.buildClassifier(trainingInstances);
        System.out.println(stacking);

        evaluation = new Evaluation(trainingInstances);

        evaluation.evaluateModel(stacking, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());

        Vote vote = new Vote();
        vote.setClassifiers(classifiers);
        vote.buildClassifier(trainingInstances);
        System.out.println(vote);

        evaluation = new Evaluation(trainingInstances);

        evaluation.evaluateModel(vote, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());
    }
}
