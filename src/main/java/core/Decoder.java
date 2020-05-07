package core;

import models.elemental.Job;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

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

    public void decode(Result result) throws IOException {
        this.decodeV2(result);
    }

    public void decodeV2(Result result) throws IOException {

        int[][] resultJobToolMatrix = result.getJobToolMatrix();

        //for all jobs
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobSeqPos(seqPos);

            //base case
            if (seqPos == 0) {
                resultJobToolMatrix[job.getId()] = Arrays.copyOf(job.getTOOLS(),job.getTOOLS().length);
            }else{

                Job prevJob = result.prevJob(job);

                LinkedList<Integer> diffPrevCurTools = this.getDifTools(resultJobToolMatrix[prevJob.getId()], job.getAntiSet());

                //TODO: shortcut when there are no tools that need to be removed

                int unionPrevCurJobSize = diffPrevCurTools.size() + job.getSet().length;
                int nToolsDelete = Math.max(0, unionPrevCurJobSize - this.problemManager.getMAGAZINE_SIZE());
                int nToolsKeep = unionPrevCurJobSize - nToolsDelete;
                int nToolsAdd = nToolsKeep - job.getSet().length;


                //Add the required tools //OPTIMIZE
                resultJobToolMatrix[job.getId()] = Arrays.copyOf(job.getTOOLS(),job.getTOOLS().length);

                Job nextJob = result.nextJob(job);
                while(nToolsAdd != 0 & nextJob != null & diffPrevCurTools.size() > 0) {

                    ListIterator<Integer> iter = diffPrevCurTools.listIterator();

                    while (iter.hasNext() && nToolsAdd != 0) {
                        int toolAddId = iter.next();

                        if(resultJobToolMatrix[nextJob.getId()][toolAddId] == 1) {
                            resultJobToolMatrix[job.getId()][toolAddId] = 1;
                            nToolsAdd-=1;
                            iter.remove();
                        }
                    }


                    nextJob = result.nextJob(nextJob);
                }

                //Fill remaining nToolsAdd with any tool
                ListIterator<Integer> remainingIter = diffPrevCurTools.listIterator();
                while(nToolsAdd != 0) {
                    int toolAddId = remainingIter.next();
                    resultJobToolMatrix[job.getId()][toolAddId] = 1;
                    remainingIter.remove();
                    nToolsAdd-=1;
                }


            }

        }

        this.evaluate(result);
    }



    public LinkedList<Integer> getDifTools(int[] toolsA, int[] antiToolSetB) {

        LinkedList<Integer> list = new LinkedList<>();

        for(int i = 0; i < antiToolSetB.length; i++) {

            int toolId = antiToolSetB[i];

            if(toolsA[toolId] == 1) {
                list.add(toolId);
            }
        }

        return list;
    }






    public Result shallowDecode(Result result) {

        return null;
    }

    public void evaluate(Result result) {
        int[] switches = this.count_switches(result);
        result.setSwitches(switches);
        result.setnSwitches(nSwitches(switches));
    }

    public int nSwitches(int[] switches) {
        int count = 0;
        for (int i = 0; i < switches.length; i++) {
            count+= switches[i];
        }
        return count;
    }


    public int[] count_switches(Result result) {

        //Count first tool loadings

        int[] switches = new int[result.getSequence().length];

        Job jobPos1 = result.getJobSeqPos(0);

        //Inserstions
        int insertionCount = 0;
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if (result.getJobToolMatrix()[jobPos1.getId()][i] == 1) {
                insertionCount += 1;
            }
        }


        insertionCount = 0;
        switches[0] = insertionCount;



        for (int seqPos = 1; seqPos < result.getSequence().length; seqPos++) {
            int swapCount = 0;

            Job job = result.getJobSeqPos(seqPos);
            Job prevJob = result.prevJob(job);


            for (int j = 0; j < result.getTools(job).length; j++) {
                //CHECK: current implementation: when a tool gets loaded a "switch" is performed
                if (result.getTools(prevJob)[j] ==  0 &  result.getTools(job)[j] ==  1) {
                    swapCount+=1;
                }
            }

            switches[seqPos] = swapCount;
        }

        return switches;
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

        System.out.println(Arrays.toString(switches));
        return switches;
    }


}
