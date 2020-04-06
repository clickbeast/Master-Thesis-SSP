import core.ProblemManager;
import data_processing.DataProcessing;
import data_processing.Parameters;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        DataProcessing dataProcessing = new DataProcessing();
        String INSTANCE_FOLDER = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances";
        String INSTANCE  = "DAT_D4_9";
        //String INSTANCE = "DAT_A1_0";
        Parameters parameters = new Parameters(INSTANCE_FOLDER,INSTANCE);
        ProblemManager problemManager = dataProcessing.instantiateProblem(parameters);

        if (problemManager == null) {
            System.out.println("Error while parsing");
            return;
        }


        try {
            problemManager.optimize();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
