package core;

import models.elemental.Job;
import util.General;

import java.io.IOException;
import java.util.ArrayList;
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



    //- - - - - - - - - - - - -
    // DECODE Ground Truth 1
    //- - - - - - - - - - - - -



    //GROUND-TRUTH
    public void decodeGroundTruth(Result result) throws IOException {

        int[][] resultJobToolMatrix = result.getJobToolMatrix();
        resultJobToolMatrix  = new int[this.problemManager.getN_JOBS()][this.problemManager.getN_TOOLS()];

        int n = 0;

        int[] toolRowJob = new int[this.problemManager.getN_TOOLS()];

        int C = 0;
        //Step 1
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if(L(i,n, result) == 0) {
                toolRowJob[i] = 1;
                C+=1;
            }
            if(C == this.problemManager.getMAGAZINE_SIZE()) {
                break;
            }
        }

        //Step1.2

        n = 1;
        step2(n,toolRowJob,result);

    }



    public void step2(int n, int[] toolRowJob, Result result) {
        result.getJobToolMatrix()[n] = toolRowJob;
        if(n != this.problemManager.getN_JOBS()) {
            step3(n, toolRowJob,result);
        }else{
            stop(n, toolRowJob,result);
        }
    }

    //STEP 3 : If each i having L(i, n) = n also has Ji = 1, set n = n + 1 and go to Step 2.
    public void step3(int n,int[] toolRowJob , Result result) {

        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if(L(i,n,result) ==  n) {
                if(toolRowJob[i] != 1) {

                }
            }
        }

        step4(n, toolRowJob, result);


        n = n + 1;
        step2(n, toolRowJob, result);

    }

    public void step4(int n, int[] toolRowJob, Result result) {

        step5(n, toolRowJob, result);
    }


    //Set Jk = 0 for a k that maximizes L(p, n) over {p: Jp = 1}. Go to Step 3.
    public void step5(int n , int[] toolRowJob, Result result) {



        step3(n, toolRowJob, result);
    }



    public void stop(int n, int[] toolRowJob, Result result) {


        //Evaluate
        this.evaluate(result);

    }



    public int L(int toolId, int instant, Result result) {
        for (int i = 0; i < result.getSequence().length; i++) {
            Job job = result.getJobSeqPos(i);
            if(this.problemManager.getJOB_TOOL_MATRIX()[job.getId()][toolId] == 1) {
                return i;
            }
        }

        return this.problemManager.getN_JOBS() - 1;
    }



    public void decodeV3(Result result) throws IOException{

    }

    //- - - - - - - - - - - - -
    // DECODE Version 1
    //- - - - - - - - - - - - -


    public void decodeV1(Result result) throws IOException {

        this.evaluate(result);
    }




    public int[][] decode(int[] sequence) {
        ArrayList<LinkedList<Integer>> toolPrioritySequence = determineToolPriority(sequence);
        int[][] augmentedJobToolMatrix = new int[this.problemManager.getN_JOBS()][this.problemManager.getN_TOOLS()];

        //Set tools
        int[] prev = new int[this.problemManager.getN_TOOLS()];

        for (int i = 0; i < sequence.length; i++) {

            //Set tools
            int numberOfToolsSet = 0;
            for (int j = 0; j < this.problemManager.getN_TOOLS(); j++) {
                augmentedJobToolMatrix[i][j] = this.problemManager.getJOB_TOOL_MATRIX()[i][j];
                if (prev[j] == 1 || this.problemManager.getJOB_TOOL_MATRIX()[i][j] == 1) {
                    augmentedJobToolMatrix[i][j] = 1;
                    numberOfToolsSet += 1;
                }
            }

            System.out.println("Decode");
            General.printGrid(augmentedJobToolMatrix);

            int numberOfToolsToRemove = Math.max(0,numberOfToolsSet - this.problemManager.getMAGAZINE_SIZE());
            //System.out.println(numberOfToolsToRemove);
            LinkedList<Integer> toolPriority = toolPrioritySequence.get(i);
            //remove unwanted tools
            for (int j = 0; j < numberOfToolsToRemove; j++) {
                while(true) {
                    int toolId = toolPriority.removeLast();
                    if(augmentedJobToolMatrix[i][toolId] == 1 && this.problemManager.getJOB_TOOL_MATRIX()[i][toolId] != 1) {
                        augmentedJobToolMatrix[i][toolId] = 0;
                        break;
                    }
                }
            }

            prev = augmentedJobToolMatrix[i];
        }

        //General.printGrid(augmentedJobToolMatrix);

        return augmentedJobToolMatrix;
    }


    //TODO:
    public ArrayList<LinkedList<Integer>> determineToolPriority(int[] sequence) {
        ArrayList<LinkedList<Integer>> toolPrioritySequence = new ArrayList<>(sequence.length);
        for(int i = 0; i < sequence.length; i++) {
            int[] visited = new int[this.problemManager.getN_TOOLS()];
            int jobId = sequence[i];
            LinkedList<Integer> toolPriority = new LinkedList<>();
            for (int j = i + 1; j < sequence.length; j++) {
                for (int k = 0; k < visited.length; k++) {
                    // visiter, belongs to current job, is used here
                    if(visited[k] == 0  && this.problemManager.getJOB_TOOL_MATRIX()[j][k] == 1){
                        toolPriority.add(k);
                        visited[k] = 1;
                    }
                }
            }

            //Add the remaining tools
            //TODO: optimize collect remaining tools
            for (int j = 0; j < visited.length; j++) {
                if(visited[j] == 0) {
                    toolPriority.add(j);
                }
            }


            toolPrioritySequence.add(toolPriority);
        }

        return toolPrioritySequence;
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
                if (result.getTools(prevJob)[j] ==  1 &  result.getTools(job)[j] ==  0) {
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
