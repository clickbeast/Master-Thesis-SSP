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



    public Result decodeV2(Result result) {


        return result;
    }


    public Result shallowDecode(Result result) {

        return null;
    }

    public Result evaluate(Result result) {

        return result;
    }



    public Solution decodeToSolution(Result result) {

        return null;
    }


    public int[] count_switches(Result result) {
        return null;
    }




    public int[] count_version_simon(int[] sequence, int[][] jobToolMatrix) {

        //TODO: countSwitches : see if combination with KTNS is possible
        int[] switches = new int[sequence.length];
        //Inserstions
        int insertionCount = 0;
        for (int i = 0; i < jobToolMatrix[0].length; i++) {
            if (jobToolMatrix[0][i] == 1) {
                insertionCount += 1;
            }
        }

        switches[0] = insertionCount;

        //CHECK: a job switch is counted when one is SETUP, not when it is removed
        //Or differently removing a tool is considered part of the setup process.

        for (int i = 1; i < sequence.length; i++) {
            int swapCount = 0;
            int[] previous = jobToolMatrix[i-1];
            int[] current = jobToolMatrix[i];
            for (int j = 0; j < current.length; j++) {
                //CHECK: current implementation: when a tool gets loaded a "switch" is performed
                if (previous[j] ==  0 &  current[j] ==  1) {
                    swapCount+=1;
                }
            }
            switches[i] = swapCount;
        }

        return switches;
    }


}
