import revisionOperator.RevisionOperator;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
  /*      File input = new File("src/main/resources/Input.txt"); // grab the text file in the resources folder

        TextFileDecrypter textFileDecrypter = new TextFileDecrypter(input); // decrypt the file contents

        RevisionOperator revisionOperator = new RevisionOperator();
        revisionOperator.processData(textFileDecrypter.getBeliefSetK(),
                textFileDecrypter.getPhi(),
                textFileDecrypter.getOmega(),
                textFileDecrypter.getRevisions(),
                textFileDecrypter.getAttributeNames(),
                textFileDecrypter.getAttributes()); // grab all the data and see what we get.
*/
        // The method below tries to do the above but using belief revision operations, it is not complete, but you can uncomment it if you wish to see its output

        /*input = new File("src/main/resources/InputOld.txt");

        textFileDecrypter = new TextFileDecrypter(input);

        revisionOperator.reviseData(textFileDecrypter.getBeliefSetK(),
                textFileDecrypter.getPhi(),
                textFileDecrypter.getOmega(),
                textFileDecrypter.getRevisions(),
                textFileDecrypter.getAttributeNames(),
                textFileDecrypter.getAttributes());*/
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File("test.csv"));
        Instances instances = csvLoader.getDataSet();

        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(instances);
        arffSaver.setFile(new File("src/main/resources/test.arff"));
        arffSaver.writeBatch();

        try {
            com.gg.ml.Presentation.processId3();
            com.gg.ml.Presentation.processLinearRegression();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
