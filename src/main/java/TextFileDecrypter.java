import weka.core.Attribute;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class TextFileDecrypter {
    private ArrayList<String> attributeNames = new ArrayList<>();
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<String> beliefSetK = new ArrayList<>();
    private String phi = "";
    private String omega = "";
    private ArrayList<String> revisions = new ArrayList<>();

    TextFileDecrypter(File input) {
        try {
            Scanner scanner = new Scanner(input);
            ArrayList<String> inputArray = new ArrayList<>();
            while (scanner.hasNextLine()) {
                inputArray.add(scanner.nextLine());
            }
            scanner.close();

            ExtractInfo(inputArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void ExtractInfo(ArrayList<String> inputArray) {
        for (int i = 0; i < inputArray.size(); i++) {
            switch (inputArray.get(i)) {
                case "Attribute Names:":
                    i++;
                    while (!inputArray.get(i).equals("")) {
                        attributeNames.add(inputArray.get(i));
                        i++;
                    }
                    break;
                case "Attributes:":
                    i++;
                    for (String attributeName : attributeNames) {
                        i++;
                        ArrayList<String> values = new ArrayList<>();
                        while (!inputArray.get(i).equals("")) {
                            values.add(inputArray.get(i));
                            i++;
                        }
                        attributes.add(new Attribute(attributeName, values));
                        i++;
                    }
                    i--;
                    break;
                case "Belief Set K:":
                    i++;
                    while (!inputArray.get(i).equals("")) {
                        beliefSetK.add(inputArray.get(i));
                        i++;
                    }
                    break;
                case "Phi:":
                    i++;
                    phi = inputArray.get(i);
                    i++;
                    break;
                case "Omega:":
                    i++;
                    omega = inputArray.get(i);
                    i++;
                    break;
                case "Revisions:":
                    i++;
                    while (i < inputArray.size()) {
                        revisions.add(inputArray.get(i));
                        i++;
                    }
                    break;
            }
        }
    }

    ArrayList<String> getAttributeNames() {
        return attributeNames;
    }

    ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    ArrayList<String> getBeliefSetK() {
        return beliefSetK;
    }

    String getPhi() {
        return phi;
    }

    String getOmega() {
        return omega;
    }

    ArrayList<String> getRevisions() {
        return revisions;
    }
}
