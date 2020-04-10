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

        String[] cat1 = {
                "Catan_A1_1"
        };

        String[] mecler = {
                "Mecler_C4_1",
                "Mecler_A4_2",
                "Mecler_A4_3",
                "Mecler_A4_4",
                "Mecler_A4_5",
        };

        String[] dat = cat1;
        String ROOT_FOLDER = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/catanzaro";

        //PARAMS
        String RUN_TYPE = "ran_swap-2job_full_sd_sw_v1_none";

        long RUN_TIME = 900;

        int SEED = 7;

        double START_TEMP = 100;
        double END_TEMP = 0.000097;
        double DECAY_RATE = 0.99900;


        for (int i = 0; i < dat.length; i++) {

            DataProcessing dataProcessing = new DataProcessing();
            String INSTANCE  = dat[i];
            //init_move_neighbourhoud_metaheurisitic_objective_ktns_delta_SEED

            //String INSTANCE = "DAT_A1_0";
            Parameters parameters = new Parameters(ROOT_FOLDER,INSTANCE, RUN_TYPE,RUN_TIME,SEED, START_TEMP, END_TEMP, DECAY_RATE);
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
