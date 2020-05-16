import core.ProblemManager;
import data_processing.DataProcessing;
import data_processing.InputData;
import data_processing.ParameterProcessor;
import data_processing.Parameters;

import java.io.*;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        ParameterProcessor params = new ParameterProcessor(args);


        //cat_10_10_4_1
        //cat_30_40_15_1

        //PARAMS
        String DEFAULT_ROOT =  "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/yanasse";
        String PROJECT_ROOT =  "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP";
        String ROOT_FOLDER = (params.getNamed().getOrDefault("root_folder", DEFAULT_ROOT));
        String PROJECT_FOLDER = (params.getNamed().getOrDefault("project_folder", PROJECT_ROOT));
        String INSTANCE = (params.getNamed().getOrDefault("instance", "yan_8_15_10_29"));
        System.out.println(params.getNamed().get("instance"));
        String RUN_TYPE = (params.getNamed().getOrDefault("run_type", "TEST-1"));
        long RUN_TIME =  params.getNamed().containsKey("run_time") ? Integer.parseInt(params.getNamed().get(
                "run_time")) : 70;
        int SEED = params.getNamed().containsKey("seed") ? Integer.parseInt(params.getNamed().get(
                "seed")) : 7;
        double START_TEMP = params.getNamed().containsKey("start_temp") ? Double.parseDouble(params.getNamed().get(
                "start_temp")) : 100;
        double END_TEMP = params.getNamed().containsKey("end_temp") ? Double.parseDouble(params.getNamed().get(
                "end_temp")) : 0.000097;
        double DECAY_RATE = params.getNamed().containsKey("decay_temp") ? Double.parseDouble(params.getNamed().get(
                "decay_rate")) : 0.99900;

        long START_TIME = System.currentTimeMillis();

        DataProcessing dataProcessing = new DataProcessing();
        Parameters parameters = new Parameters(PROJECT_ROOT,ROOT_FOLDER,INSTANCE, RUN_TYPE,RUN_TIME,START_TIME,SEED, START_TEMP, END_TEMP, DECAY_RATE);
        InputData inputData = dataProcessing.instantiateProblem(parameters);

        ProblemManager problemManager;

        try(
                FileWriter lfw = new FileWriter(parameters.getLOG_PATH(), false);
                BufferedWriter lbw = new BufferedWriter(lfw);
                PrintWriter logWriter = new PrintWriter(lbw);


                FileWriter rfw = new FileWriter(parameters.getRESULTS_PATH(), false);
                BufferedWriter rbw = new BufferedWriter(rfw);
                PrintWriter resultsWriter = new PrintWriter(rbw);

                FileWriter sfw = new FileWriter(parameters.getSOLUTION_PATH(), false);
                BufferedWriter sbw = new BufferedWriter(sfw);
                PrintWriter solutionWriter = new PrintWriter(sbw)

        ) {


            inputData.setWriters(logWriter, resultsWriter, solutionWriter);
            problemManager = new ProblemManager(inputData);
            problemManager.optimize();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
