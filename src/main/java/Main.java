import revisionOperator.RevisionOperator;
import testProgram.TestProgram;

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

        RevisionOperator revisionOperator = new RevisionOperator();
        revisionOperator.run("tennis-train", "tennis-test");
    }
}
