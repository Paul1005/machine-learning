import revisionOperator.RevisionOperator;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        File input = new File("src/main/resources/Input.txt");

        TextFileDecrypter textFileDecrypter = new TextFileDecrypter(input);

        RevisionOperator revisionOperator = new RevisionOperator();
        revisionOperator.processData(textFileDecrypter.getBeliefSetK(),
                textFileDecrypter.getPhi(),
                textFileDecrypter.getOmega(),
                textFileDecrypter.getRevisions(),
                textFileDecrypter.getAttributeNames(),
                textFileDecrypter.getAttributes());
        //revisionOperator.reviseData(beliefSetK, phi, omega, revisions);
    }
}
