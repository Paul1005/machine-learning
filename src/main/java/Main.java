import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File("test.csv"));
        Instances instances = csvLoader.getDataSet();

        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(instances);
        arffSaver.setFile(new File("src/main/resources/test.arff"));
        arffSaver.writeBatch();

        try {
            Presentation.processId3();
            Presentation.processLinearRegression();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
