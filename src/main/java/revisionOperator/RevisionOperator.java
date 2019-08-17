package revisionOperator;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.Id3;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;

public class RevisionOperator {
    private String trainingFile;
    private String testingFile;

    public void run(String csvTrainingFile, String csvTestingFile) throws Exception {
        trainingFile = csvTrainingFile + ".arff";
        testingFile = csvTestingFile + ".arff";
        //transformData(csvTrainingFile);
        //transformData(csvTestingFile);
        processData();
    }

    private void transformData(String csvFile) throws IOException {
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File(csvFile + ".csv"));
        Instances instances = csvLoader.getDataSet();

        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(instances);
        arffSaver.setFile(new File("src/main/resources/" + csvFile + ".arff"));
        arffSaver.writeBatch();
    }

    private Instances getData(String file) throws Exception {
        ConverterUtils.DataSource trainingSource = new ConverterUtils.DataSource(file);
        Instances instances = trainingSource.getDataSet();
        instances.setClassIndex(instances.numAttributes()-1);

        return instances;
    }

    private void processData() throws Exception {
        Instances trainingInstances = getData(trainingFile);

        Id3 id3 = new Id3();
        id3.buildClassifier(trainingInstances);
        System.out.println(id3);

        /*Evaluation evaluation = new Evaluation(trainingInstances);

        Instances testingInstances = getData(testingFile);

        evaluation.evaluateModel(id3, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());*/
    }

    public void reviseData(){

    }

    public void orderStates(){

    }

    public void revisionOperator(){
        
    }
}
