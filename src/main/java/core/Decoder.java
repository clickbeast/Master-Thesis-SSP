package core;

import models.Solution;

/**
 *  Decoder is able to decode solutions by using the KTNS mechanism in a fast and efficient way
 */
public class Decoder {

    /* VARIABLES & CONSTANTS ------------------------------------------------------------------ */

    ProblemManager problemManager;

    //TODO
    int[] indexes;
    int[] sortedTools;

    //aux
    int[] visited;

    /* SETUP ------------------------------------------------------------------ */


    public Decoder(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }


    /* PREPROCESS ------------------------------------------------------------------ */

    public void determineToolOrder() {

    }


    /* EVALUATION ------------------------------------------------------------------ */


    public Result evaluate(Result result) {

        return result;
    }



    public Solution decodeToSolution(Result result) {

        return null;
    }
}
