import core.ProblemManager;
import data_processing.DataProcessing;
import data_processing.ParameterProcessor;
import data_processing.Parameters;

import java.io.FileNotFoundException;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        ParameterProcessor params = new ParameterProcessor(args);

        //PARAMS
        String DEFAULT_ROOT =  "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/catanzaro";
        String ROOT_FOLDER = (params.getNamed().getOrDefault("root_folder", DEFAULT_ROOT));
        String INSTANCE = (params.getNamed().getOrDefault("instance", "Catan_A1_1"));
        System.out.println(params.getNamed().get("instance"));
        String RUN_TYPE = (params.getNamed().getOrDefault("run_type", "ran_swap-2job_full_sd_sw_v1_none"));
        long RUN_TIME =  params.getNamed().containsKey("run_time") ? Integer.parseInt(params.getNamed().get(
                "run_time")) : 7;
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
        Parameters parameters = new Parameters(ROOT_FOLDER,INSTANCE, RUN_TYPE,RUN_TIME,START_TIME,SEED, START_TEMP, END_TEMP, DECAY_RATE);
        ProblemManager problemManager = dataProcessing.instantiateProblem(parameters);

        if (problemManager == null) {
            System.out.println("Error while parsing");
            return;
        }

        problemManager.optimize();
    }
}
