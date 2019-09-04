import revisionOperator.RevisionOperator;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File input = new File("src/main/resources/Input.txt"); // grab the text file in the resources folder

        TextFileDecrypter textFileDecrypter = new TextFileDecrypter(input); // decrypt the file contents

        RevisionOperator revisionOperator = new RevisionOperator();
        revisionOperator.processData(textFileDecrypter.getBeliefSetK(),
                textFileDecrypter.getPhi(),
                textFileDecrypter.getOmega(),
                textFileDecrypter.getRevisions(),
                textFileDecrypter.getAttributeNames(),
                textFileDecrypter.getAttributes()); // grab all the data and see what we get.

        // The method below tries to do the above but using belief revision operations, it is not complete, but you can uncomment it if you wish to see its output

        /*input = new File("src/main/resources/InputOld.txt");

        textFileDecrypter = new TextFileDecrypter(input);

        revisionOperator.reviseData(textFileDecrypter.getBeliefSetK(),
                textFileDecrypter.getPhi(),
                textFileDecrypter.getOmega(),
                textFileDecrypter.getRevisions(),
                textFileDecrypter.getAttributeNames(),
                textFileDecrypter.getAttributes());*/
    }
}
