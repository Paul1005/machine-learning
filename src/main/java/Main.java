import revisionOperator.RevisionOperator;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        File input = new File("src/main/resources/Input.txt"); // grab the text file in the resources folder

        TextFileDecrypter textFileDecrypter = new TextFileDecrypter(input); // decrypt the file contents

        RevisionOperator revisionOperator = new RevisionOperator();
        revisionOperator.processData(textFileDecrypter.getBeliefSetK(),
                textFileDecrypter.getPhi(),
                textFileDecrypter.getOmega(),
                textFileDecrypter.getRevisions(),
                textFileDecrypter.getAttributeNames(),
                textFileDecrypter.getAttributes()); // grab all the data and see what we get.
        //revisionOperator.reviseData(beliefSetK, phi, omega, revisions);
    }
}
