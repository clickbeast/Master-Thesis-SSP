import core.ProblemManager;
import data_processing.DataProcessing;
import data_processing.Parameters;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {

        String[] catan = {
                "Catan_A1_1",
                "Catan_A1_2",
                "Catan_A1_3",
                "Catan_A1_4",
                "Catan_A1_5",
                "Catan_A1_6",
                "Catan_A1_7",
                "Catan_A1_8",
                "Catan_A1_9",
                "Catan_A1_10",
        };


        String[] mecler = {
                "Mecler_C4_1",
                "Mecler_A4_2",
                "Mecler_A4_3",
                "Mecler_A4_4",
                "Mecler_A4_5",
        };

        String[] dat = mecler;

   /*     String initial  = "";
        String meta = "";
        String   = "";
        String lsm = "";
        String ktns = "";


        String log = ini*/

        for (int i = 0; i < dat.length; i++) {

            DataProcessing dataProcessing = new DataProcessing();
            String INSTANCE_FOLDER = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/mecler";
            String INSTANCE  = dat[i];
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



}
