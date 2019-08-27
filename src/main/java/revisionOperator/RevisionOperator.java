package revisionOperator;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.Id3;
import weka.core.*;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
NOTE: we want to find a new state whose distance is as small as possible from one of our existing solutions. The
existing solutions may have different possible values for certain states, we take the minimum distance.
Also the case where we have to discard one of our old beliefs, not sure what to do about that yet.
Add new belief, if logically consistent, keep old ones, if not discard something?
 */
public class RevisionOperator {
    //private String trainingFile;
    //private String testingFile;
    private String filePath = "src/main/resources/";

    public void run(String csvTrainingFile, String csvTestingFile) throws Exception {
        //trainingFile = csvTrainingFile + ".arff";
        //testingFile = csvTestingFile + ".arff";
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

    private ArrayList<String> setUpAttributeNames(){
        ArrayList<String> attributeNames = new ArrayList<>(5);
        attributeNames.add("Outlook");
        attributeNames.add("Temp.");
        attributeNames.add("Humidity");
        attributeNames.add("Wind");
        attributeNames.add("Decision");
        return attributeNames;
    }

    private ArrayList<Attribute> setUpAttributes(ArrayList<String> attributeNames){
        ArrayList<Attribute> attributes = new ArrayList<>(5);
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

        attributes.add(new Attribute(attributeNames.get(0), outlook));
        attributes.add(new Attribute(attributeNames.get(1), temp));
        attributes.add(new Attribute(attributeNames.get(2), humidity));
        attributes.add(new Attribute(attributeNames.get(3), wind));
        attributes.add(new Attribute(attributeNames.get(4), decision));

        return attributes;
    }

    private void processData() throws Exception {
        ArrayList<String> attributeNames = setUpAttributeNames();
        ArrayList<Attribute> attributes = setUpAttributes(attributeNames);
        Instances instanceK = new Instances("tennis-training", attributes, 0);
        Instances testingInstances = new Instances("tennis-testing", attributes, 0);
        testingInstances.setClassIndex(testingInstances.numAttributes() - 1);

        ArrayList<Attribute> classificationAttributes = new ArrayList<>(attributes);
        ArrayList<String> belief = new ArrayList<>();
        belief.add("False");
        belief.add("True");
        ArrayList<String> classificationAttributeNames = new ArrayList<>(attributeNames);
        classificationAttributeNames.add("Believes");
        classificationAttributes.add(new Attribute(classificationAttributeNames.get(5), belief));
        instanceK.setClassIndex(instanceK.numAttributes() - 1);

        Instances classificationInstances = new Instances("classification", classificationAttributes, 0);
        classificationInstances.setClassIndex(classificationInstances.numAttributes() - 1);

        String phi = "!Outlook && !Temp. && Humidity && Wind && Decision";
        addInstanceGeneric(phi, testingInstances, attributeNames);

        String k = "!Outlook && !Temp. && !Humidity && !Wind && !Decision";
        addInstanceGeneric(k, instanceK, attributeNames); // add initial dataSet K

        reviseAndTest(attributeNames, instanceK, testingInstances, classificationAttributeNames, classificationInstances, "Outlook && !Temp. && !Humidity && !Wind && Decision");
        reviseAndTest(attributeNames, instanceK, testingInstances, classificationAttributeNames, classificationInstances, "!Outlook && Temp. && !Humidity && !Wind && Decision");
        reviseAndTest(attributeNames, instanceK, testingInstances, classificationAttributeNames, classificationInstances, "!Outlook && !Temp. && Humidity && !Wind && Decision");
        reviseAndTest(attributeNames, instanceK, testingInstances, classificationAttributeNames, classificationInstances, "!Outlook && !Temp. && !Humidity && Wind && Decision");

        Id3 classifier = new Id3();
        classifier.buildClassifier(classificationInstances);
        System.out.println(classificationInstances + "\n");
        System.out.println(classifier + "\n");

        Evaluation classificationEvaluation = new Evaluation(classificationInstances);
        Instances classifierTester = new Instances("classifier-testing", classificationAttributes, 0);
        classifierTester.setClassIndex(classifierTester.numAttributes() - 1);

        String omega = "Outlook && Temp. && Humidity && Wind && Decision";
        double isPhiBelieved = reviseAndTest(attributeNames, instanceK, testingInstances, classificationAttributeNames, classificationInstances, omega);
        classificationInstances.remove(classificationInstances.size() - 1);

        addInstanceGeneric(omega +  " && Believes", classifierTester, classificationAttributeNames);

        classificationEvaluation.evaluateModel(classifier, classifierTester);

        if (isPhiBelieved == classificationEvaluation.pctCorrect()) {
            System.out.println("works");
            if(classificationEvaluation.pctCorrect() == 100){
                System.out.println("revising by omega will cause us to believe phi");
            } else {
                System.out.println("revising by omega will not cause us to believe phi");
            }
        } else {
            System.out.println("doesn't work");
        }
    }

    private double reviseAndTest(ArrayList<String> attributeNames, Instances instanceK, Instances testingInstances, ArrayList<String> classificationAttributeNames, Instances classificationInstances, String revision) throws Exception {
        Id3 id3 = new Id3();
        addInstanceGeneric(revision, instanceK, attributeNames);
        id3.buildClassifier(instanceK);
        System.out.println(instanceK + "\n");
        System.out.println(id3 + "\n");

        Evaluation evaluation = new Evaluation(instanceK);
        evaluation.evaluateModel(id3, testingInstances);

        if (evaluation.pctCorrect() == 100) {
            revision = revision + " && Believes";
        } else {
            revision = revision + " && !Believes";
        }
        addInstanceGeneric(revision, classificationInstances, classificationAttributeNames);

        instanceK.remove(instanceK.size() - 1);
        return evaluation.pctCorrect();
    }

    private ArrayList<String> getAttributeNames(ArrayList<Attribute> attributes) {
        ArrayList<String> attributeNames = new ArrayList<>();
        for (Attribute attribute : attributes) {
            attributeNames.add(attribute.name());
        }

        return attributeNames;
    }

    private ArrayList<Belief> determineSolutions(String tree, Instances instances, ArrayList<Attribute> attributes, ArrayList<String> attributeNames) {
        String[] lines = tree.split("\n");
        ArrayList<HashMap<String, String>> solutions = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("Yes")) {
                int j = i;
                HashMap<String, String> dictionary = new HashMap<>();
                while (lines[j].contains("|")) {
                    long level = lines[j].chars().filter(num -> num == '|').count();
                    String key = lines[j].split(" = ")[0].replace("|", "").replace(" ", "");
                    String value = lines[j].split(" = ")[1].split(": ")[0];
                    dictionary.put(key, value);
                    int k = 1;
                    while (lines[j - k].chars().filter(num -> num == '|').count() != level - 1) {
                        k++;
                    }
                    j = j - k;
                }
                String key = lines[j].split(" = ")[0];
                String value = lines[j].split(" = ")[1].split(": ")[0];
                dictionary.put(key, value);
                solutions.add(dictionary);
            }
        }

        ArrayList<Belief> beliefs = new ArrayList<>();

        for (HashMap<String, String> solution : solutions) {
            int unknowns = 0;
            for (int i = 0; i < attributeNames.size() - 1; i++) {
                if (!solution.containsKey(attributeNames.get(i))) {
                    unknowns++;
                    solution.put(attributeNames.get(i), "?");
                }
            }

            int numRows = (int) Math.pow(2, unknowns);
            for (int i = 0; i < numRows; i++) {
                Belief belief = new Belief();
                belief.setSolution(new HashMap<>());
                int k = 0;
                for (int j = 0; j < attributeNames.size() - 1; j++) {
                    if (solution.get(attributeNames.get(j)).equals("?")) {
                        int value = (i / (int) Math.pow(2, k)) % 2;
                        belief.put(attributeNames.get(j), attributes.get(j).value(value));
                        k++;
                    } else {
                        belief.put(attributeNames.get(j), solution.get(attributeNames.get(j)));
                    }
                }
                beliefs.add(belief);
            }
        }

        for (Instance instance : instances) {
            for (Belief belief : beliefs) {
                boolean isIdentical = true;
                for (int j = 0; j < attributeNames.size() - 1; j++) {
                    isIdentical = isIdentical && instance.stringValue(j).equals(belief.getSolution().get(attributeNames.get(j)));
                }
                if (isIdentical) {
                    belief.increaseRank();
                }
            }
        }
        return beliefs;
    }

    private double calculateInformationGain(double entropy, Instances instances, String attribute, ArrayList<Attribute> attributes, ArrayList<String> attributeNames) {
        double numInstances = instances.size();
        double numPositives = 0;
        double numNegatives = 0;
        double entropyPositive;
        double entropyNegative;

        int attributeIndex = 0;

        for (int i = 0; i < attributeNames.size(); i++) {
            if (attribute.equals(attributeNames.get(i))) {
                attributeIndex = i;
                break;
            }
        }

        Instances positiveInstances = new Instances("positive", attributes, 0);
        Instances negativeInstances = new Instances("negative", attributes, 0);
        for (Instance instance : instances) {
            if (instance.toDoubleArray()[attributeIndex] == 0) {
                negativeInstances.add(instance);
                numNegatives++;
            } else if (instance.toDoubleArray()[attributeIndex] == 1) {
                positiveInstances.add(instance);
                numPositives++;
            }
        }

        entropyPositive = calculateEntropy(positiveInstances);
        entropyNegative = calculateEntropy(negativeInstances);
        return entropy - numPositives / numInstances * entropyPositive - numNegatives / numInstances * entropyNegative;
    }

    private double calculateEntropy(Instances instances) {
        double numInstances = instances.size();
        if (numInstances == 0) {
            return 0;
        }
        double numPositives = 0;
        double numNegatives = 0;

        for (Instance instance : instances) {
            if (instance.toDoubleArray()[instance.numAttributes() - 1] == 0) {
                numNegatives++;
            } else if (instance.toDoubleArray()[instance.numAttributes() - 1] == 1) {
                numPositives++;
            }
        }

        return -numPositives / numInstances * logOfBase2(numPositives / numInstances) - numNegatives / numInstances * logOfBase2(numNegatives / numInstances);
    }

    private double logOfBase2(double num) {
        if (num == 0) {
            return Double.MAX_VALUE;
        }
        return Math.log(num) / Math.log(2);
    }

    private void addInstanceGeneric(String instance, Instances instances, ArrayList<String> attributeNames) {
        String[] terms = instance.split(" && ");

        double[] newInstance = new double[instances.numAttributes()];

        for (int i = 0; i < newInstance.length; i++) {
            for (String term : terms) {
                if (term.charAt(0) == '!') {
                    if (term.equals(attributeNames.get(i))) {
                        newInstance[i] = 0;
                        break;
                    }
                } else {
                    if (term.equals(attributeNames.get(i))) {
                        newInstance[i] = 1;
                        break;
                    }
                }
            }
        }

        instances.add(new DenseInstance(1.0, newInstance));
    }

    private void addInitialSetK(String k, Instances instances, ArrayList<String> attributeNames) {
        String[] splitLine = k.split(", ");
        String[] terms = splitLine[0].split(" && ");

        double[] newInstance = new double[instances.numAttributes()];

        for (int i = 0; i < newInstance.length - 1; i++) {
            newInstance[i] = 0;
            for (String term : terms) {
                if (term.equals(attributeNames.get(i))) {
                    newInstance[i] = 1;
                    break;
                }
            }
        }

        newInstance[newInstance.length - 1] = Double.parseDouble(splitLine[1]);

        instances.add(new DenseInstance(1.0, newInstance));
    }

    private void addInstance(String newLine, Instances instances, ArrayList<String> attributeNames) {
        String[] splitLine = newLine.split(", ");
        String[] terms = splitLine[0].split(" && ");

        double[] newInstance = new double[instances.numAttributes()];

        double[] k = instances.get(0).toDoubleArray(); // get first entry

        for (int i = 0; i < newInstance.length - 1; i++) {
            newInstance[i] = k[i];
            for (String term : terms) {
                if (term.charAt(0) == '!') {
                    term = term.substring(1);
                    if (term.equals(attributeNames.get(i))) {
                        newInstance[i] = 0;
                        break;
                    }
                } else {
                    if (term.equals(attributeNames.get(i))) {
                        newInstance[i] = 1;
                        break;
                    }
                }
            }
        }

        newInstance[newInstance.length - 1] = Double.parseDouble(splitLine[1]);

        instances.add(new DenseInstance(1.0, newInstance));
    }

    public void reviseData() {

    }

    public void orderStates() {

    }

    public void revisionOperator() {

    }
}
