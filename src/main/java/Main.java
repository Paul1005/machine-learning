import revisionOperator.RevisionOperator;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        ArrayList<String> beliefSetK = new ArrayList<>();
        beliefSetK.add("Outlook && Temp. && Humidity && !Wind && !Decision");
        beliefSetK.add("Outlook && Temp. && Humidity && Wind && !Decision");
        beliefSetK.add("!Outlook && Temp. && Humidity && !Wind && Decision");
        beliefSetK.add("!Outlook && !Temp. && !Humidity && !Wind && Decision");
        beliefSetK.add("!Outlook && !Temp. && !Humidity && !Wind && !Decision");
        beliefSetK.add("Outlook && !Temp. && Humidity && !Wind && !Decision");

        String phi = "!Outlook && !Temp. && Humidity && Wind && !Decision";
        String omega = "!Outlook && Temp. && !Humidity && !Wind && Decision";

        ArrayList<String> revisions = new ArrayList<>();

        revisions.add("Outlook && !Temp. && !Humidity && Wind && Decision");
        revisions.add("!Outlook && !Temp. && Humidity && Wind && Decision");
        revisions.add("!Outlook && !Temp. && !Humidity && Wind && !Decision");

        RevisionOperator revisionOperator = new RevisionOperator();
        revisionOperator.processData(beliefSetK, phi, omega, revisions);
        //revisionOperator.reviseData(beliefSetK, phi, omega, revisions);
    }
}
