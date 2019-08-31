package revisionOperator;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.Id3;
import weka.core.*;

import java.util.ArrayList;
import java.util.HashMap;

public class RevisionOperator {

    /*
    Primary method for the class. This takes all the various variables and determines weather revising Belief set K by Omega will cause us to believe Phi.
     */
    public void processData(ArrayList<String> beliefSetK, String phi, String omega, ArrayList<String> revisions, ArrayList<String> attributeNames, ArrayList<Attribute> attributes) throws Exception {
        Instances kInstances = new Instances("training", attributes, 0); // create out initial set of instances
        kInstances.setClassIndex(kInstances.numAttributes() - 1); // set the class to the final attribute
        for (String k : beliefSetK) { // add the instances specified in beliefSetK
            addInstance(k, kInstances, attributeNames);
        }
        System.out.println("Initial set K: \n" + kInstances + "\n"); // print out the initial belief set before any rivisions

        Instances phiInstance = new Instances("testing", attributes, 0); // create instances that will contain phi
        phiInstance.setClassIndex(phiInstance.numAttributes() - 1);
        addInstance(phi, phiInstance, attributeNames); // add phi to the instance

        ArrayList<String> belief = new ArrayList<>(); // create the belief attribute for our classification instances, which can be true or false
        belief.add("False");
        belief.add("True");

        ArrayList<String> classificationAttributeNames = new ArrayList<>(attributeNames); // create classification attribute name list based on the original attribute names
        classificationAttributeNames.add("Believes"); // add the final attribute name to our classification attribute names

        ArrayList<Attribute> classificationAttributes = new ArrayList<>(attributes); // create classification attributes based on the original attributes
        classificationAttributes.add(new Attribute(classificationAttributeNames.get(5), belief)); // add the aforementioned belief attribute to our classification attributes

        Instances classificationInstances = new Instances("classification", classificationAttributes, 0); // creates our classification instances using the classification attributes
        classificationInstances.setClassIndex(classificationInstances.numAttributes() - 1);

        for (String revision : revisions) { // revise belief set k by each revision and test it against phi
            System.out.println("Instances after revision by: " + revision);
            reviseAndTest(attributeNames, kInstances, phiInstance, classificationAttributeNames, classificationInstances, revision);
        }

        Id3 classifier = new Id3(); // Creates the id3 tree we will be using for classification
        classifier.buildClassifier(classificationInstances); // build the tree using the classification instances
        System.out.println("Classification Instances: \n" + classificationInstances); // print out our classification instances after adding revisions
        System.out.println(classifier + "\n"); // print out the id3 tree created by our classificationInstances

        Instances classifierTester = new Instances("classifier-testing", classificationAttributes, 0); // create a new instance for testing our classification tree
        classifierTester.setClassIndex(classifierTester.numAttributes() - 1);
        addInstance(omega + " && Believes", classifierTester, classificationAttributeNames); // Add the omega instance with belief attribute set to true

        Evaluation classificationEvaluation = new Evaluation(classificationInstances); // Create Evaluation object
        classificationEvaluation.evaluateModel(classifier, classifierTester); // See if the classifier Id3 tree correctly predicts our testing set

        // print out the results of our testing
        if(classificationEvaluation.pctCorrect() == 100){
            System.out.println("The ID3 tree produced by our classification thinks we will believe phi after revising by omega" + "\n");
        } else {
            System.out.println("The ID3 tree produced by our classification set does not think we will believe phi after revising by omega" + "\n");
        }

        System.out.println("Instances after revising by omega");
        double isPhiBelieved = reviseAndTest(attributeNames, kInstances, phiInstance, classificationAttributeNames, classificationInstances, omega); // revise K by omega and see if it believes Phi
        if(isPhiBelieved == 100){ // print the results of the test
            System.out.println("The ID3 tree produced by the initial set K after being revised by omega correctly predicts phi" + "\n");
        } else {
            System.out.println("The ID3 tree produced by the initial set K after being revised by omega does not predict phi" + "\n");
        }

        if (isPhiBelieved != classificationEvaluation.pctCorrect()) { // print out whether the results of our two tests match
            System.out.println("Our classification tree prediction does not match our instance tree prediction");
        } else {
            System.out.println("Our classification tree prediction matches our instance tree prediction");
        }
    }

    /*
    This method revises belief set K by the revision specified, tests it against instance Phi, adds the results to the classification instances, then removes the revision from K.
     */
    private double reviseAndTest(ArrayList<String> attributeNames, Instances instanceK, Instances testingInstances, ArrayList<String> classificationAttributeNames, Instances classificationInstances, String revision) throws Exception {
        Id3 id3 = new Id3(); // Id3 tree to be used in testing
        addInstance(revision, instanceK, attributeNames); // add revision to K
        id3.buildClassifier(instanceK); // build our tree using K
        System.out.println(instanceK + "\n"); // print K with revision
        System.out.println(id3 + "\n"); // print the tree generated

        Evaluation evaluation = new Evaluation(instanceK);
        evaluation.evaluateModel(id3, testingInstances); // evaluate to see if K, after being revised, correctly predicts Phi

        if (evaluation.pctCorrect() == 100) {
            revision = revision + " && Believes"; // if prediction was correct add the believes attribute as positive
        } else {
            revision = revision + " && !Believes"; // if prediction was incorrect add the believes attribute as negative
        }

        addInstance(revision, classificationInstances, classificationAttributeNames);  // add this revision, and its result, to our classification instances

        instanceK.remove(instanceK.size() - 1); // remove the revision from K

        return evaluation.pctCorrect();
    }

    private void addInstance(String instance, Instances instances, ArrayList<String> attributeNames) {
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

    /*
    All the following methods are not used in the program; they were used either for experimentation, or for a future version of this program that was not completed.
     */

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
}