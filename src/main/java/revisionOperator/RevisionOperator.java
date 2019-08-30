package revisionOperator;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.Id3;
import weka.core.*;

import java.util.ArrayList;
import java.util.HashMap;

public class RevisionOperator {

    public void processData(ArrayList<String> beliefSetK, String phi, String omega, ArrayList<String> revisions, ArrayList<String> attributeNames, ArrayList<Attribute> attributes) throws Exception {
        Instances kInstances = new Instances("tennis-training", attributes, 0);
        kInstances.setClassIndex(kInstances.numAttributes() - 1);
        for (String k : beliefSetK) {
            addInstanceGeneric(k, kInstances, attributeNames);
        }
        System.out.println("Initial set K: \n" + kInstances + "\n");

        Instances phiInstance = new Instances("tennis-testing", attributes, 0);
        phiInstance.setClassIndex(phiInstance.numAttributes() - 1);
        addInstanceGeneric(phi, phiInstance, attributeNames);

        ArrayList<String> belief = new ArrayList<>();
        belief.add("False");
        belief.add("True");

        ArrayList<String> classificationAttributeNames = new ArrayList<>(attributeNames);
        classificationAttributeNames.add("Believes");

        ArrayList<Attribute> classificationAttributes = new ArrayList<>(attributes);
        classificationAttributes.add(new Attribute(classificationAttributeNames.get(5), belief));

        Instances classificationInstances = new Instances("classification", classificationAttributes, 0);
        classificationInstances.setClassIndex(classificationInstances.numAttributes() - 1);

        for (String revision : revisions) {
            System.out.println("Instances after revision by: " + revision);
            reviseAndTest(attributeNames, kInstances, phiInstance, classificationAttributeNames, classificationInstances, revision);
        }

        Id3 classifier = new Id3();
        classifier.buildClassifier(classificationInstances);
        System.out.println("Classification Instances: \n" + classificationInstances);
        System.out.println(classifier + "\n");

        Instances classifierTester = new Instances("classifier-testing", classificationAttributes, 0);
        classifierTester.setClassIndex(classifierTester.numAttributes() - 1);
        addInstanceGeneric(omega + " && Believes", classifierTester, classificationAttributeNames);

        Evaluation classificationEvaluation = new Evaluation(classificationInstances);
        classificationEvaluation.evaluateModel(classifier, classifierTester);

        if(classificationEvaluation.pctCorrect() == 100){
            System.out.println("The ID3 tree produced by our classification thinks we will believe phi after revising by omega" + "\n");
        } else {
            System.out.println("The ID3 tree produced by our classification set does not think we will believe phi after revising by omega" + "\n");
        }

        System.out.println("Instances after revising by omega");
        double isPhiBelieved = reviseAndTest(attributeNames, kInstances, phiInstance, classificationAttributeNames, classificationInstances, omega);
        if(isPhiBelieved == 100){
            System.out.println("The ID3 tree produced by the initial set K after being revised by omega correctly predicts phi" + "\n");
        } else {
            System.out.println("The ID3 tree produced by the initial set K after being revised by omega does not predict phi" + "\n");
        }

        if (isPhiBelieved != classificationEvaluation.pctCorrect()) {
            System.out.println("Our classification tree does not match our instance tree");
        } else {
            System.out.println("Our classification tree matches our instance tree");
        }
    }

    public void reviseData(ArrayList<String> beliefSetK, String phi, String omega, ArrayList<String> revisions,  ArrayList<String> attributeNames, ArrayList<Attribute> attributes) {
        ArrayList<Belief> beliefs = determineAllPossibleBeliefs(attributes, attributeNames);

        for (String beliefK : beliefSetK) {
            beliefs = reviseBeliefs(beliefs, beliefK, attributes);
        }

        for (Belief belief : beliefs) {
            System.out.println(belief.toString());
        }

        System.out.println();
        for (String revision : revisions) {
            ArrayList<Belief> revisedBeliefs = reviseBeliefs(beliefs, revision, attributes);
            for (Belief revisedBelief : revisedBeliefs) {
                System.out.println(revisedBelief.toString());
            }
            System.out.println();
        }

        ArrayList<Belief> omegaBeliefs = reviseBeliefs(beliefs, omega, attributes);
        for (Belief omegaBelief : omegaBeliefs) {
            System.out.println(omegaBelief.toString());
        }

        int omegaRank = findRank(omegaBeliefs, phi, attributes);
        System.out.println(omegaRank);
    }

    private int findRank(ArrayList<Belief> omegaBeliefs, String phi, ArrayList<Attribute> attributes){
        String[] splitPhi = phi.split(" && ");
        for (Belief omegaBelief : omegaBeliefs) {
            boolean matches = true;
            for (int i = 0; i < splitPhi.length; i++) {
                if (splitPhi[i].charAt(0) == '!') {
                    matches = matches && omegaBelief.getSolution().get(splitPhi[i].substring(1)).equals(attributes.get(i).value(0));
                } else {
                    matches = matches && omegaBelief.getSolution().get(splitPhi[i]).equals(attributes.get(i).value(1));
                }
            }
            if(matches){
                return omegaBelief.getRank();
            }
        }
        return -1;
    }

    private ArrayList<Belief> reviseBeliefs(ArrayList<Belief> beliefs, String newBelief, ArrayList<Attribute> attributes) {
        String[] newBeliefSplit = newBelief.split(" && ");
        ArrayList<Belief> revisedBeliefs = new ArrayList<>();
        for (Belief belief : beliefs) {
            Belief revisedBelief = new Belief();
            revisedBelief.setSolution(belief.getSolution());
            revisedBelief.setRank(belief.getRank());
            for (int i = 0; i < newBeliefSplit.length; i++) {
                if (newBeliefSplit[i].charAt(0) == '!') {
                    if (belief.getSolution().get(newBeliefSplit[i].substring(1)).equals(attributes.get(i).value(0))) {
                        revisedBelief.increaseRank();
                    }
                } else {
                    if (belief.getSolution().get(newBeliefSplit[i]).equals(attributes.get(i).value(1))) {
                        revisedBelief.increaseRank();
                    }
                }
            }
            revisedBeliefs.add(revisedBelief);
        }
        return revisedBeliefs;
    }

    private ArrayList<Belief> determineAllPossibleBeliefs(ArrayList<Attribute> attributes, ArrayList<String> attributeNames) {
        ArrayList<Belief> beliefs = new ArrayList<>();

        int numRows = (int) Math.pow(2, attributes.size());
        for (int i = 0; i < numRows; i++) {
            Belief belief = new Belief();
            for (int j = 0; j < attributes.size(); j++) {
                int value = (i / (int) Math.pow(2, j)) % 2;
                belief.put(attributeNames.get(j), attributes.get(j).value(value));
            }
            beliefs.add(belief);
        }
        return beliefs;
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
}