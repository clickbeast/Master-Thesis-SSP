package core;

import models.elemental.Job;
import util.General;

import javax.xml.stream.FactoryConfigurationError;
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

    public void decode(Result result) throws IOException{
        //this.decodeGroundTruth(result);
        //this.decodeV1(result);
        this.decodeV2(result);
        //this.decodeV3(result);
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
    // DECODE Ground Truth 1 : ORDER: NM Tang and Denaro
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



    public void step2(int n, int[] toolRowJob, Result result) throws IOException {
        if(n != this.problemManager.getN_JOBS()) {
            result.getJobToolMatrix()[n-1] = toolRowJob.clone();
            step3(n, toolRowJob,result);
        }else{
            stop(n, toolRowJob,result);
        }
    }

    //STEP 3 : If each i having L(i, n) = n also has Ji = 1, set n = n + 1 and go to Step 2.
    public void step3(int n, int[] toolRowJob , Result result) throws IOException {

        boolean goToStep2 = true;
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if(L(i,n,result) ==  n) {
                if(toolRowJob[i] != 1) {
                     goToStep2 = false;
                     break;
                }
            }
        }

        if(goToStep2) {
            n = n + 1;
            step2(n, toolRowJob, result);
        }else{
            step4(n, toolRowJob, result);
        }
    }



    //TODO: convertable to induvidual steps possible


    //Pick i having L(i, n) = n and Ji = 0. Set Ji = 1. IE INSERT THE REQUIRED TOOLS
    public void step4(int n, int[] toolRowJob, Result result) throws IOException {

        //Maybe for only 1 tool
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if(L(i,n, result) == n & toolRowJob[i] == 0) {
                toolRowJob[i] = 1;
            }
        }


        step5(n, toolRowJob, result);
    }


    //Set Jk = 0 for a k that maximizes L(p, n) over {p: Jp = 1}. Go to Step 3. IE DELETE THE LEAST IMPORTANT TOOL

    public void step5(int n , int[] toolRowJob, Result result) throws IOException {

        //Step How many tools to remove?
            //Not explicitlely mentioned??
        int count = 0;
        for (int i = 0; i < toolRowJob.length; i++) {
            if(toolRowJob[i] ==  1) {
                count+=1;
            }
        }

        int nDelete = Math.max(0,count - this.problemManager.getMAGAZINE_SIZE());

        //TODO: possible to optimize

        while(nDelete != 0) {

            int maxL = -1;
            int id = -1;

            for (int i = 0; i < toolRowJob.length; i++) {
                if (toolRowJob[i] == 1) {
                    int value = L(i, n, result);
                    if (value > maxL) {
                        id = i;
                        maxL = value;
                    }
                }
            }

            if(id != -1) {
                //Set to Jk = O
                toolRowJob[id] = 0;
                nDelete--;
            }
        }



        step3(n, toolRowJob, result);
    }



    public void stop(int n, int[] toolRowJob, Result result) throws IOException {
        //Evaluate
        this.evaluate(result);
        //this.problemManager.getLogger().writeResult(result);
    }



    public int L(int toolId, int n, Result result) {

        if(result.getSequence()[0] == 0) {
            //System.out.println("helloo");
        }

        for (int i = n; i < result.getSequence().length; i++) {
            Job job = result.getJobSeqPos(i);
            if(this.problemManager.getJOB_TOOL_MATRIX()[job.getId()][toolId] == 1) {
                return i;
            }
        }

        return this.problemManager.getN_JOBS();
    }



    public void decodeV3(Result result) throws IOException{

    }

    //- - - - - - - - - - - - -
    // DECODE Version 1
    //- - - - - - - - - - - - -


    public void decodeV1(Result result) throws IOException {
        result.setJobToolMatrix(decodeV1GetAugmentedJobToolMatrix(result.getSequence()));
        this.evaluate(result);
    }


    public int[][] decodeV1GetAugmentedJobToolMatrix(int[] sequence) {
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

            //System.out.println("Decode");
            //General.printGrid(augmentedJobToolMatrix);

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

        //NEW TYPE OF COST
        result.setZeroBlockLength(this.zeroBlockLength(result));
        result.setTieBreakingCost(this.calculateTieBreakingCost(result));
        //System.out.println(result.getTieBreakingCost());

    }
    public double calculateTieBreakingCost(Result result) {
        double total = 0;

        for (int toolId = 0; toolId < result.getZeroBlockLength().length; toolId++) {
            for (int zeroBlockId = 0; zeroBlockId < result.getZeroBlockLength()[toolId].length; zeroBlockId++) {
                int zeroBlockLength = result.getZeroBlockLength()[toolId][zeroBlockId];
                total+= Math.sqrt(zeroBlockLength);
            }
        }

        return total;
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





    public int[][] zeroBlockLength(Result result) {

        int[][] zeroBlocks = new int[this.problemManager.getN_TOOLS()][];


        for (int toolId = 0; toolId < this.problemManager.getN_TOOLS(); toolId++) {
            LinkedList<Integer> blocks = new LinkedList<>();
            int length = 0;
            boolean run = false;

            for (int jobId = 0; jobId < this.problemManager.getN_JOBS(); jobId++) {
                int value = result.getJobToolMatrix()[jobId][toolId];

                if(!run) {
                    if (value == 0) {
                        run = true;
                        length += 1;
                    }
                }else{
                    if(value == 0) {
                        length+=1;
                    }else{
                        blocks.add(length);
                        length = 0;
                        run = false;
                    }
                }
            }

            if(run) {
                blocks.add(length);
            }

            zeroBlocks[toolId] = blocks.stream().mapToInt(i->i).toArray();

        }

        return zeroBlocks;
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
