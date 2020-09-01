import core.ProblemManager;
import data_processing.DataProcessing;
import data_processing.InputData;
import data_processing.ParameterProcessor;
import data_processing.Parameters;
import picocli.CommandLine;

import java.io.*;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        long START_TIME = System.currentTimeMillis();

        DataProcessing dataProcessing = new DataProcessing();
        Parameters parameters = new Parameters(START_TIME);
        new CommandLine(parameters).parseArgs(args);
        parameters.parametersRead();
        System.out.println(parameters.getBETA());
        System.out.println(parameters.getINSTANCE());
        System.out.println(parameters.getSOLUTION_PATH());

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

    public double parseDouble(ParameterProcessor params, String key, double std) {
        return params.getNamed().containsKey(key) ? Double.parseDouble(params.getNamed().get(
                key)) : std;
    }
    public double parseBoolean(ParameterProcessor params, String key, double std) {
        return params.getNamed().containsKey(key) ? Double.parseDouble(params.getNamed().get(
                key)) : std;
    }

    public String parseString(ParameterProcessor params, String key, double std) {

        return null;
    }
}



