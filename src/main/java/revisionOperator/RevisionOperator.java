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
        // Set up K
        Instances kInstances = new Instances("training", attributes, 0); // create out initial set of instances
        kInstances.setClassIndex(kInstances.numAttributes() - 1); // set the final attribute as classification
        for (String k : beliefSetK) { // add the instances specified in beliefSetK
            addInstance(k, kInstances, attributeNames);
        }
        System.out.println("Initial set K: \n" + kInstances + "\n"); // print out the initial belief set before any revisions

        // Set up Phi
        Instances phiInstance = new Instances("testing", attributes, 0); // create instance that will contain phi, we will use this for testing
        phiInstance.setClassIndex(phiInstance.numAttributes() - 1);
        addInstance(phi, phiInstance, attributeNames); // add phi to the instance

        // Set up classification instance
        ArrayList<String> belief = new ArrayList<>(); // create the belief attribute for our classification instances, which can be true or false
        belief.add("False");
        belief.add("True");

        ArrayList<String> classificationAttributeNames = new ArrayList<>(attributeNames); // create classification attribute name list based on the original attribute names
        classificationAttributeNames.add("Believes"); // add the final attribute name to our classification attribute names

        ArrayList<Attribute> classificationAttributes = new ArrayList<>(attributes); // create classification attributes based on the original attributes
        classificationAttributes.add(new Attribute(classificationAttributeNames.get(5), belief)); // add the aforementioned belief attribute to our classification attributes

        Instances classificationInstances = new Instances("classification", classificationAttributes, 0); // creates our classification instances using the classification attributes
        classificationInstances.setClassIndex(classificationInstances.numAttributes() - 1);

        // Do our revisions
        for (String revision : revisions) { // revise belief set k by each revision and test it against phi
            System.out.println("Instances after revision by: " + revision);
            reviseAndTest(attributeNames, kInstances, phiInstance, classificationAttributeNames, classificationInstances, revision);
        }

        // Id3 tree for classification
        Id3 classifier = new Id3(); // Creates the id3 tree we will be using for classification
        classifier.buildClassifier(classificationInstances); // build the tree using the classification instances
        System.out.println("Classification Instances: \n" + classificationInstances); // print out our classification instances after adding revisions
        System.out.println(classifier + "\n"); // print out the id3 tree created by our classificationInstances

        // Set up testing instance for classification
        Instances classifierTester = new Instances("classifier-testing", classificationAttributes, 0); // create a new instance for testing our classification tree
        classifierTester.setClassIndex(classifierTester.numAttributes() - 1);
        addInstance(omega + " && Believes", classifierTester, classificationAttributeNames); // Add an instance that says we will believe omega

        // Evaluate the classification instance
        Evaluation classificationEvaluation = new Evaluation(classificationInstances); // Create Evaluation object
        classificationEvaluation.evaluateModel(classifier, classifierTester); // See if the classifier Id3 tree correctly predicts our testing set

        // Print out the results of our classification testing
        if(classificationEvaluation.pctCorrect() == 100){
            System.out.println("The ID3 tree produced by our classification thinks we will believe phi after revising by omega" + "\n");
        } else {
            System.out.println("The ID3 tree produced by our classification set does not think we will believe phi after revising by omega" + "\n");
        }

        // Print out the results of testing K
        System.out.println("Instances after revising by omega");
        double isPhiBelieved = reviseAndTest(attributeNames, kInstances, phiInstance, classificationAttributeNames, classificationInstances, omega); // revise K by omega and see if it believes Phi
        if(isPhiBelieved == 100){
            System.out.println("The ID3 tree produced by the initial set K after being revised by omega correctly predicts phi" + "\n");
        } else {
            System.out.println("The ID3 tree produced by the initial set K after being revised by omega does not predict phi" + "\n");
        }

        // Print whether or not our classification testing matches our belief set K testing
        if (isPhiBelieved != classificationEvaluation.pctCorrect()) {
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

    /*
    Adds the specified string instance to the set of instances
     */
    private void addInstance(String instance, Instances instances, ArrayList<String> attributeNames) {
        String[] terms = instance.split(" && "); // split up the various terms by &&

        double[] newInstance = new double[instances.numAttributes()]; // instance values are stored as doubles

        for (int i = 0; i < newInstance.length; i++) {
            for (String term : terms) {
                if (term.charAt(0) == '!') { // value is negative
                    if (term.equals(attributeNames.get(i))) {
                        newInstance[i] = 0;
                        break;
                    }
                } else { // value is positive
                    if (term.equals(attributeNames.get(i))) {
                        newInstance[i] = 1;
                        break;
                    }
                }
            }
        }

        instances.add(new DenseInstance(1.0, newInstance)); // add the newly created instance
    }

    /*
    None of the following methods are used in this program; they were used either for experimentation, or for a future version of this program that was never completed.
     */

    /*
    this takes all possible beliefs for a given attribute set, then revises by by our initial belief set k, revises again by each revision, then revises by omega, and prints the rank of phi
     */
    public void reviseData(ArrayList<String> beliefSetK, String phi, String omega, ArrayList<String> revisions, ArrayList<String> attributeNames, ArrayList<Attribute> attributes) {
        ArrayList<Belief> beliefs = determineAllPossibleBeliefs(attributes, attributeNames);

        for (String beliefK : beliefSetK) {
            beliefs = reviseBeliefs(beliefs, beliefK, attributes); //revise beliefs by initial belief set K
        }

        for (Belief belief : beliefs) {
            System.out.println(belief.toString());
        }

        System.out.println();
        for (String revision : revisions) {
            ArrayList<Belief> revisedBeliefs = reviseBeliefs(beliefs, revision, attributes); // revise beliefs again by revision instance
            for (Belief revisedBelief : revisedBeliefs) {
                System.out.println(revisedBelief.toString());
            }
            System.out.println();
        }

        ArrayList<Belief> omegaBeliefs = reviseBeliefs(beliefs, omega, attributes); // revise beliefs by omega
        for (Belief omegaBelief : omegaBeliefs) {
            System.out.println(omegaBelief.toString());
        }

        int phiRankInOmega = findRank(omegaBeliefs, phi, attributes); // find rank of phi in instances revised by omega
        int minRank = findMinRank(omegaBeliefs, attributes);
        int maxRank = findMaxRank(omegaBeliefs, attributes);
        int rankRange = maxRank - minRank;
        float averageRank = minRank + (float)rankRange/2;
        System.out.println("Min Rank: " + minRank);
        System.out.println("Max Rank: " + maxRank);
        System.out.println("Rank Range: " + rankRange);
        System.out.println("Phi Rank: " + phiRankInOmega);
        System.out.println("Average Rank: " + averageRank);
    }

    private int findMaxRank(ArrayList<Belief> beliefs, ArrayList<Attribute> attributes) {
        int maxRank = beliefs.get(0).getRank();
        for (Belief belief : beliefs) {
            if(belief.getRank() > maxRank) {
                maxRank = belief.getRank();
            }
        }
        return maxRank;
    }

    private int findMinRank(ArrayList<Belief> beliefs, ArrayList<Attribute> attributes) {
        int minRank = beliefs.get(0).getRank();
        for (Belief belief : beliefs) {
            if(belief.getRank() < minRank) {
                minRank = belief.getRank();
            }
        }
        return minRank;
    }

    /*
     Finds the rank (likelihood) of a given instance in a a set of beliefs (higher rank = more likely)
     */
    private int findRank(ArrayList<Belief> beliefs, String instance, ArrayList<Attribute> attributes){
        String[] splitPhi = instance.split(" && ");
        for (Belief belief : beliefs) {
            boolean matches = true;
            for (int i = 0; i < splitPhi.length; i++) {
                if (splitPhi[i].charAt(0) == '!') {
                    matches = matches && belief.getSolution().get(splitPhi[i].substring(1)).equals(attributes.get(i).value(0));
                } else {
                    matches = matches && belief.getSolution().get(splitPhi[i]).equals(attributes.get(i).value(1));
                }
            }
            if(matches){
                return belief.getRank();
            }
        }
        return -1;
    }

    /*
    Increases the ranks of the beliefs based on how closely they match the new belief. Each attribute whose values match increases the rank by one.
     */
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

    /*
    Finds all possible combinations of values for a given number of attributes
     */
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

    /*
    Determines the solutions for attributes based on the ID3 tree
     */
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

    /*
    Calculates the information gain (entropy reduction) that an attribute will give us.
     */
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

    /*
     Calculates the overall entropy of a set of instances
     */
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