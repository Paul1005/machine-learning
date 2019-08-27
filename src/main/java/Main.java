import revisionOperator.RevisionOperator;
import testProgram.TestProgram;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {

        //DecisionTreeDemo.process();
        //KNNDemo.process();
        //LogisticRegressionDemo.process();
        //NaiveBayesDemo.process();
        //RandomForestDemo.process();
        //SMODemo.process();
        //TestProgram testProgram = new TestProgram();
        //testProgram.transformData();
        //testProgram.processData();

        ArrayList<String> beliefSetK = new ArrayList<>();
        beliefSetK.add("!Outlook && !Temp. && !Humidity && !Wind && !Decision");
        String phi = "Outlook && !Temp. && !Humidity && Wind && Decision";
        String omega = "Outlook && Temp. && Humidity && Wind && Decision";

        RevisionOperator revisionOperator = new RevisionOperator();
        revisionOperator.processData(beliefSetK, phi, omega);
    }
}
