package revisionOperator;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.Id3;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RevisionOperator {
    private String trainingFile;
    private String testingFile;
    private String filePath = "src/main/resources/";
    private ArrayList<Attribute> attributes;
    private ArrayList<String> attributeNames;

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
        arffSaver.setFile(new File(filePath + csvFile + ".arff"));
        arffSaver.writeBatch();
    }

    private Instances getData(String file) throws Exception {
        ConverterUtils.DataSource trainingSource = new ConverterUtils.DataSource(file);
        Instances instances = trainingSource.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        return instances;
    }

    private Instances setUpData() throws Exception {
        attributes = new ArrayList<>(5);
        ArrayList<String> outlook = new ArrayList<>();
        outlook.add("Sunny");
        outlook.add("Overcast");
        ArrayList<String> temp = new ArrayList<>();
        temp.add("Hot");
        temp.add("Cool");
        ArrayList<String> humidity = new ArrayList<>();
        humidity.add("High");
        humidity.add("Normal");
        ArrayList<String> wind = new ArrayList<>();
        wind.add("Strong");
        wind.add("Weak");
        ArrayList<String> decision = new ArrayList<>();
        decision.add("Yes");
        decision.add("No");

        attributeNames = new ArrayList<>(5);
        attributeNames.add("Outlook");
        attributeNames.add("Temp.");
        attributeNames.add("Humidity");
        attributeNames.add("Wind");
        attributeNames.add("Decision");

        attributes.add(new Attribute(attributeNames.get(0), outlook));
        attributes.add(new Attribute(attributeNames.get(1), temp));
        attributes.add(new Attribute(attributeNames.get(2), humidity));
        attributes.add(new Attribute(attributeNames.get(3), wind));
        attributes.add(new Attribute(attributeNames.get(4), decision));

        Instances instances = new Instances("tennis-test", attributes, 0);
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    private void processData() throws Exception {
        Instances trainingInstances = setUpData();

        trainingInstances = addInitialSetK("Outlook && Temp. && Humidity, 0", trainingInstances); // add initial dataSet K

        System.out.println(trainingInstances);

        trainingInstances = addInstance("Strong, 0", trainingInstances);
        Id3 id3 = new Id3();
        id3.buildClassifier(trainingInstances);

        trainingInstances.numAttributes();

        /*Evaluation evaluation = new Evaluation(trainingInstances);

        Instances testingInstances = getData(testingFile);

        evaluation.evaluateModel(id3, testingInstances);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());*/
    }

    private Instances addInstance(String newLine, Instances instances) throws Exception {
        String[] splitLine = newLine.split(", ");
        String[] terms = splitLine[0].split(" && ");

        double[] newInstance = new double[instances.numAttributes()];

        Instance k = instances.get(0);

        for(int i = 0; i < newInstance.length-1; i++){
            newInstance[i] = k.index(i);
            for(String term: terms){
                if(term.equals(attributeNames.get(i))){
                    if(term.charAt(0) == '!'){
                        newInstance[i] = 0;
                    } else {
                        newInstance[i] = 1;
                    }
                    break;
                }
            }

        }

        newInstance[newInstance.length-1] = Double.parseDouble(splitLine[1]);

        instances.add(new DenseInstance(1.0, newInstance));

        return instances;
    }

    private Instances addInitialSetK(String k, Instances instances) throws Exception {
        String[] splitLine = k.split(", ");
        String[] terms = splitLine[0].split(" && ");

        double[] newInstance = new double[instances.numAttributes()];

        for(int i = 0; i < newInstance.length-1; i++){
            newInstance[i] = 0;
            for(String term: terms){
                if(term.equals(attributeNames.get(i))){
                    newInstance[i] = 1;
                    break;
                }
            }
        }

        newInstance[newInstance.length-1] = Double.parseDouble(splitLine[1]);

        instances.add(new DenseInstance(1.0, newInstance));

        return instances;
    }

    public void reviseData() {

    }

    public void orderStates() {

    }

    public void revisionOperator() {

    }
}
