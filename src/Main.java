import core.ProblemManager;
import data_processing.DataProcessing;

import java.io.FileNotFoundException;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {

        DataProcessing dataProcessing = new DataProcessing();
        ProblemManager problemManager = dataProcessing.instantiateProblem();
        problemManager.initialSolution();
        problemManager.optimize();

    }
}
