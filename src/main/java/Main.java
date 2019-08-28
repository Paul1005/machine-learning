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
        beliefSetK.add("!Outlook && !Temp. && !Humidity && !Wind && !Decision"); // make sure these are in the same order as the attributes

        String phi = "Outlook && !Temp. && !Humidity && Wind && Decision";
        String omega = "Outlook && Temp. && Humidity && Wind && Decision";

        ArrayList<String> revisions = new ArrayList<>();
        revisions.add("Outlook && !Temp. && !Humidity && !Wind && Decision");
        revisions.add("!Outlook && Temp. && !Humidity && !Wind && Decision");
        revisions.add("!Outlook && !Temp. && Humidity && !Wind && Decision");
        revisions.add("!Outlook && !Temp. && !Humidity && Wind && Decision");

        RevisionOperator revisionOperator = new RevisionOperator();
        //revisionOperator.processData(beliefSetK, phi, omega, revisions);
        revisionOperator.reviseData(beliefSetK, phi, omega, revisions);
    }
}
