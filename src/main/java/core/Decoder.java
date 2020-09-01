package core;

import data_processing.Parameters;
import models.Feedback;
import models.elemental.Job;
import util.General;

import javax.xml.stream.FactoryConfigurationError;
import java.io.IOException;
import java.util.*;

/**
 *  Decoder is able to decode solutions by using the KTNS mechanism in a fast and efficient way
 */
public class Decoder {

    /* VARIABLES & CONSTANTS --------------------------------------------------------------------------------------- */

    ProblemManager problemManager;
    private Parameters parameters;
    //TODO
    int[] indexes;
    int[] sortedTools;

    //aux
    int[] visited;
    int trackKtns;

    /* SETUP ------------------------------------------------------------------------------------------------------- */


    public Decoder(ProblemManager problemManager) {
        this.problemManager = problemManager;
        this.parameters = problemManager.getParameters();
        this.trackKtns = 0;
    }


    /* PREPROCESS -------------------------------------------------------------------------------------------------- */




    /* EVALUATION -------------------------------------------------------------------------------------------------- */


    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* GENERAL
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


    public void decode(Result result) throws IOException{
        //this.decodeGroundTruth(result);
        //this.decodeV1(result);
        this.decodeV2(result);
        //this.decodeV3(result);
    }

    public void decodeRR(Result result) throws IOException{
        switch (this.problemManager.getParameters().getDecode()) {
            case "full": {
                //this.decode(result);
                this.decode(result);
                break;
            }
            case "hybrid": {
                this.hybridDecode(result);
                break;
            }

            case "shallow": {
                this.shallowDecode(result);
                break;
            }

            default: {
                this.problemManager.getLogger().logInfo("NO RR DECODE CHOSEN");
            }
        }
    }



    //<editor-fold desc="DECODE VERSION 2">


    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* DECODE VERSION 2
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    //OK
    public void decodeV2(Result result) throws IOException {

        //TODO: refine
        result.setJobToolMatrix(General.copyGrid(this.problemManager.getJOB_TOOL_MATRIX()));

        //for all jobs
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobSeqPos(seqPos);

            //base case
            if (seqPos == 0) {
                //resultJobToolMatrix[job.getId()] = Arrays.copyOf(job.getTOOLS(),job.getTOOLS().length);
            }else{

                Job prevJob = result.prevJob(job);

                LinkedList<Integer> diffPrevCurTools = this.getDifTools(result.getJobToolMatrix()[prevJob.getId()], job.getAntiSet());

                int unionPrevCurJobSize = diffPrevCurTools.size() + job.getSet().length;
                int nToolsDelete = Math.max(0, unionPrevCurJobSize - this.problemManager.getMAGAZINE_SIZE());
                int nToolsKeep = unionPrevCurJobSize - nToolsDelete;
                int nToolsAdd = nToolsKeep - job.getSet().length;


                Job nextJob = result.nextJob(job);

                while(nToolsAdd != 0 & nextJob != null & diffPrevCurTools.size() > 0) {

                    ListIterator<Integer> iter = diffPrevCurTools.listIterator();

                    while (iter.hasNext() && nToolsAdd != 0) {
                        int toolAddId = iter.next();


                        if(result.getJobToolMatrix()[nextJob.getId()][toolAddId] == 1) {
                            result.getJobToolMatrix()[job.getId()][toolAddId] = 1;
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

                    result.getJobToolMatrix()[job.getId()][toolAddId] = 1;
                    remainingIter.remove();
                    nToolsAdd-=1;
                }

            }

        }

        this.evaluate(result);
    }



    public void decodeV2RR(Result result) throws IOException {

        //TODO: refine
        result.setJobToolMatrix(General.copyGrid(this.problemManager.getJOB_TOOL_MATRIX()));

        //for all jobs
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobSeqPos(seqPos);

            //base case
            if (seqPos == 0) {
                //resultJobToolMatrix[job.getId()] = Arrays.copyOf(job.getTOOLS(),job.getTOOLS().length);
            }else{

                Job prevJob = result.prevJob(job);

                LinkedList<Integer> diffPrevCurTools = this.getDifTools(result.getJobToolMatrix()[prevJob.getId()], job.getAntiSet());

                int unionPrevCurJobSize = diffPrevCurTools.size() + job.getSet().length;
                int nToolsDelete = Math.max(0, unionPrevCurJobSize - this.problemManager.getMAGAZINE_SIZE());
                int nToolsKeep = unionPrevCurJobSize - nToolsDelete;
                int nToolsAdd = nToolsKeep - job.getSet().length;


                Job nextJob = result.nextJob(job);

                while(nToolsAdd != 0 & nextJob != null & diffPrevCurTools.size() > 0) {

                    ListIterator<Integer> iter = diffPrevCurTools.listIterator();

                    while (iter.hasNext() && nToolsAdd != 0) {
                        int toolAddId = iter.next();


                        if(result.getJobToolMatrix()[nextJob.getId()][toolAddId] == 1) {
                            result.getJobToolMatrix()[job.getId()][toolAddId] = 1;
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

                    result.getJobToolMatrix()[job.getId()][toolAddId] = 1;
                    remainingIter.remove();
                    nToolsAdd-=1;
                }

            }

        }

        this.evaluateRR(result);
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

        int switches = 0;
        for (int i = 0; i < result.getSequence().length - 1; i++) {
            Job job = result.getJobSeqPos(i);
            Job nextJob = result.nextJob(job);

            switches+= this.problemManager.getSWITCHES_LB_MATRIX()[job.getId()][nextJob.getId()];

        }
        result.setnSwitches(switches);
        result.setCost((double) switches);
        return result;
    }

    public void hybridDecode(Result result) {

    }




    //- - - - - - - -
    // Evaluation
    //- - - - - - - -


    public void evaluateRR(Result result) {
        int[] switches = this.count_switches(result);
        result.setSwitches(switches);
        result.setnSwitches(nSwitches(switches));

        //NEW TYPE OF COST
        result.setZeroBlockLength(this.zeroBlockLength(result));
        result.setTieBreakingCost(this.calculateTieBreakingCost(result));
        //result.setToolDistance(this.calculateToolDistance(result));

        //result.setToolDistance(new int[this.problemManager.getN_JOBS()][]);
        //result.toolDistanceCost = this.calculateToolDistanceCost(result);
        //result.penaltyCost = this.calculatePenaltyCost(result);

        result.setCost((double) result.getnSwitches());
        //result.setCost(result.getTieBreakingCost());


      /*  switch (this.problemManager.getParameters().getObjective()) {
            case "switches": {
                result.setCost((double) result.getnSwitches());
                break;
            }

            case "tieBreaking": {
                result.setCost(result.getTieBreakingCost());
                break;
            }

            case "toolDistance": {
                result.setCost(result.getToolDistanceCost());
                break;
            }

            case "penalty": {
                result.setCost(result.getPenaltyCost());
                break;
            }

            default: {
                this.problemManager.getLogger().logInfo("NO OBJECTIVE CHOSEN");
                return;
            }
        }*/


    }

    public void
    evaluate(Result result) {
        //int[] switches = this.count_switches(result);
        result.setSwitches(new int[result.getSequence().length]);

        //result.setnSwitches(nSwitches(switches));

        result.setnSwitches(nSwitchesSetupBased(result));

        //NEW TYPE OF COST
        //result.setZeroBlockLength(this.zeroBlockLength(result));
        //result.setTieBreakingCost(this.calculateTieBreakingCost(result));
        //result.setToolDistance(this.calculateToolDistance(result));

        //result.setToolDistance(new int[this.problemManager.getN_JOBS()][]);
        //result.toolDistanceCost = this.calculateToolDistanceCost(result);
        //result.penaltyCost = this.calculatePenaltyCos t(result);


        result.setCost((double) result.getnSwitches());

        //result.setCost(result.getTieBreakingCost());

        /*switch (this.problemManager.getParameters().getObjective()) {
            case "switches": {
                result.setCost((double) result.getnSwitches());
                break;
            }

            case "tieBreaking": {
                result.setCost(result.getTieBreakingCost());
                break;
            }

            case "toolDistance": {
                result.setCost(result.getToolDistanceCost());
                break;
            }

            case "penalty": {
                result.setCost(result.getPenaltyCost());
                break;
            }

            default: {
                this.problemManager.getLogger().logInfo("NO OBJECTIVE CHOSEN");
                return;
            }
        }*/

    }

    //OK
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



    public double calculatePenaltyCost(Result result) {
        //Calculate the number of failed KTNS Attempts
        return this.getParameters().getW_S()*result.getnSwitches() + this.getParameters().getwFailKTNS()*nFailedKTNS(result);
    }


    //OK
    public double calculateToolDistanceCost(Result result) {

        //Calculate max and min distance

        int minDist = 0;
        int maxDist = 0;

        for (int i = 0; i < result.getSequence().length; i++) {
            for (int toolId = 0; toolId < this.problemManager.getN_TOOLS(); toolId++) {
                if(result.toolUsedAtSeqPos(i,toolId)) {
                    minDist += result.getToolDistance()[result.getJobSeqPos(i).getId()][toolId];
                }else{
                    maxDist += result.getToolDistance()[result.getJobSeqPos(i).getId()][toolId];
                }
            }
        }

        return  (this.getParameters().getW_S()*result.getnSwitches()) +
                (this.getParameters().getW_DIST() * ((this.getParameters().getW_DIST_MIN() * minDist)
                + (this.getParameters().getW_DIST_MAX() * maxDist)));
    }


    //TODO: can be integrated into decoder
    //OK
    public int nFailedKTNS(Result result) {

        int ktnsFail = 0;

        for (int i = 0; i < result.getSequence().length - 1; i++) {
            for (int toolId = 0; toolId < this.problemManager.getN_TOOLS(); toolId++) {
                if (result.toolUsedAtSeqPos(i, toolId) && !result.getJobSeqPos(i).toolRequired(toolId)) {
                    if (!result.toolUsedAtSeqPos(i + 1, toolId)) {
                        ktnsFail += 1;
                    }
                }
            }
        }


        return ktnsFail;
    }

    //OK
    public int nSwitches(int[] switches) {
        int count = 0;
        for (int i = 0; i < switches.length; i++) {
            count+= switches[i];
        }
        return count;
    }


    public int nSwitchesSetupBased(Result result) {

        //Inserstions
        int setupCount = 0;
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if (result.getJobToolMatrix()[result.getSequence()[0]][i] == 1) {
                setupCount += 1;
            }
        }



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

            setupCount+=swapCount;
        }


        return  setupCount - this.problemManager.getMAGAZINE_SIZE();
    }



    //OK
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






    //TODO: FIX, counts first ones as zero block and does not count last one good -> retest
    public int[][] zeroBlockLength(Result result) {

        int[][] zeroBlocks = new int[this.problemManager.getN_TOOLS()][];

        for (int toolId = 0; toolId < this.problemManager.getN_TOOLS(); toolId++) {
            LinkedList<Integer> blocks = new LinkedList<>();

            int length = 0;
            boolean run = false;

            for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

                boolean used = result.toolUsedAtSeqPos(seqPos, toolId);

                if(!run) {
                    if(!used) {
                        if(seqPos > 0) {
                            if(result.toolUsedAtSeqPos(seqPos -1, toolId)) {
                                run = true;
                                length+=1;
                            }
                        }
                    }

                }else{
                    if(!used) {
                        //Run is in progress
                        length+=1;
                    }else{
                        //run is finished
                        blocks.add(length);
                        length = 0;
                        run = false;
                    }

                    //Run is finished
                   /* if(seqPos == result.getSequence().length - 1) {
                        run = false;
                        blocks.add(length);
                        length = 0;
                    }*/
                }
            }

            zeroBlocks[toolId] = blocks.stream().mapToInt(i->i).toArray();
        }

        return zeroBlocks;
    }





    //TODO: VERY INNEFICIENT -> INTEGRATE WITH DECODE
    //OK
    public int[][] calculateToolDistance(Result result) {

        //Create a new tool distance matrix
        int[][] toolDistance = new int[problemManager.getN_JOBS()][problemManager.getN_TOOLS()];


        for (int i = 0; i < result.getSequence().length - 1; i++) {
            Job job = result.getJobSeqPos(i);
            for (int toolId = 0; toolId < this.problemManager.getN_TOOLS(); toolId++) {

                //Lookup distance
                int distance = 0;

                measure: for (int j = i + 1; j < result.getSequence().length; j++) {
                    distance += 1;
                    if(result.toolUsedAtSeqPos(j,toolId)) {
                        break measure;
                    }else{

                        //Add 1 to distance when tool not needed anymore but requires a switch
                        if(j == result.getSequence().length - 1) {
                            distance+=1;
                        }


                    }
                }


                //Set distance
                toolDistance[job.getId()][toolId] = distance;

            }

        }

        return toolDistance;
    }


    //</editor-fold>





    //<editor-fold desc="DECODE Ground Truth 1 : ORDER: NM Tang and Denaro">

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* DECODE Ground Truth 1 : ORDER: NM Tang and Denaro
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

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

    //</editor-fold>


    //<editor-fold desc="DECODE VERSION 1">

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* DECODE VERSION 1
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */



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


    public ArrayList<LinkedList<Integer>> determineToolPriority ( int[] sequence){
        ArrayList<LinkedList<Integer>> toolPrioritySequence = new ArrayList<>(sequence.length);
        for (int i = 0; i < sequence.length; i++) {
            int[] visited = new int[this.problemManager.getN_TOOLS()];
            int jobId = sequence[i];
            LinkedList<Integer> toolPriority = new LinkedList<>();
            for (int j = i + 1; j < sequence.length; j++) {
                for (int k = 0; k < visited.length; k++) {
                    // visiter, belongs to current job, is used here
                    if (visited[k] == 0 && this.problemManager.getJOB_TOOL_MATRIX()[j][k] == 1) {
                        toolPriority.add(k);
                        visited[k] = 1;
                    }
                }
            }

            //Add the remaining tools
            //TODO: optimize collect remaining tools
            for (int j = 0; j < visited.length; j++) {
                if (visited[j] == 0) {
                    toolPriority.add(j);
                }
            }


            toolPrioritySequence.add(toolPriority);
        }

        return toolPrioritySequence;
    }


    //</editor-fold>


    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
