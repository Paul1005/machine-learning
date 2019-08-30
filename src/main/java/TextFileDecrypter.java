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

    TextFileDecrypter(File input) throws FileNotFoundException {
        Scanner scanner = new Scanner(input);

        ArrayList<String> inputArray = new ArrayList<>();
        while (scanner.hasNextLine()) {
            inputArray.add(scanner.nextLine());
        }
        scanner.close();

        ExtractInfo(inputArray);
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
                case "BeliefSetK:":
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

    public ArrayList<String> getAttributeNames() {
        return attributeNames;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public ArrayList<String> getBeliefSetK() {
        return beliefSetK;
    }

    public String getPhi() {
        return phi;
    }

    public String getOmega() {
        return omega;
    }

    public ArrayList<String> getRevisions() {
        return revisions;
    }
}
