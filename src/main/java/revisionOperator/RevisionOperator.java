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
        outlook.add("Overcast");
        outlook.add("Sunny");
        ArrayList<String> temp = new ArrayList<>();
        temp.add("Cool");
        temp.add("Hot");
        ArrayList<String> humidity = new ArrayList<>();
        humidity.add("Normal");
        humidity.add("High");
        ArrayList<String> wind = new ArrayList<>();
        wind.add("Weak");
        wind.add("Strong");
        ArrayList<String> decision = new ArrayList<>();
        decision.add("No");
        decision.add("Yes");

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

        addInitialSetK("Outlook && Temp. && Humidity, 0", trainingInstances); // add initial dataSet K
        addInstance("Wind, 0", trainingInstances);
        addInstance("!Outlook, 1", trainingInstances);
        System.out.println(trainingInstances + "\n");

        Id3 id3 = new Id3();
        id3.buildClassifier(trainingInstances);
        System.out.println(id3 + "\n");

        addInstance("!Outlook && !Temp, 1", trainingInstances);

        System.out.println(trainingInstances + "\n");

        id3 = new Id3();
        id3.buildClassifier(trainingInstances);
        System.out.println(id3 + "\n");

    }

    private void addInitialSetK(String k, Instances instances) throws Exception {
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
    }

    private void addInstance(String newLine, Instances instances) throws Exception {
        String[] splitLine = newLine.split(", ");
        String[] terms = splitLine[0].split(" && ");

        double[] newInstance = new double[instances.numAttributes()];

        double[] k = instances.get(0).toDoubleArray(); // get first entry

        for(int i = 0; i < newInstance.length-1; i++){
            newInstance[i] = k[i];
            for(String term: terms){
                if(term.charAt(0) == '!'){
                    if(term.substring(1).equals(attributeNames.get(i))) {
                        newInstance[i] = 0;
                        break;
                    }
                } else{
                    if(term.equals(attributeNames.get(i))) {
                        newInstance[i] = 1;
                        break;
                    }
                }
            }
        }

        newInstance[newInstance.length-1] = Double.parseDouble(splitLine[1]);

        instances.add(new DenseInstance(1.0, newInstance));
    }

    public void reviseData() {

    }

    public void orderStates() {

    }

    public void revisionOperator() {

    }
}
