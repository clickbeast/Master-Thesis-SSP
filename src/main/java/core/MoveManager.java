package core;

import core.moves.Move;
import models.elemental.Job;


import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MoveManager {

    private final Random random;
    ProblemManager problemManager;
    private Move move;

    public MoveManager(ProblemManager problemManager) {
        this.problemManager = problemManager;

        this.random = this.problemManager.getRandom();
        //this.setupMoves();
    }



    public Result doMove(Result result) throws IOException {
        if(problemManager.getParameters().getLocalSearch().equals("swaps")) {
            return this.swap(result);
        }else if(problemManager.getParameters().getLocalSearch().equals("ruinAndRecreate")){
            return this.ruinAndRecreate(result);
        }
        return null;
    }

    /* SWAP ------------------------------------------------------------------ */



    public Result swap(Result result) {
        return swapTwoJobs(result);
    }

    public Result swapTwoJobs(Result result) {
        int[] sequence = result.getSequence();

        //Here it is out of a random neighbourhoud...
        int jobA = this.random.nextInt(sequence.length);
        int jobB = this.random.nextInt(sequence.length);
        int tmp = 0;

        //Look until other job not the same
        while (jobB == jobA) {
            jobB = this.random.nextInt(sequence.length);
        }

        //TODO: optimize swap
        //Swap
        tmp = sequence[jobA];
        sequence[jobA] = sequence[jobB];
        sequence[jobB] =  tmp;

        //TODO: optimize
        result.reloadJobPositions();

        return result;
    }


    /* RUIN + RECREATE ------------------------------------------------------------------ */


    public Result ruinAndRecreate(Result result) throws IOException {
        Ruin ruin = null;

        int[] select = {0,0,0};
        int picked = select[this.random.nextInt(select.length)];

        if(picked == 0) {
            ruin = ruinCross(result);
        }else{
            ruin = ruinBlock(result);
        }



        recreate(result, ruin);

        return result;
    }


    class Ruin {
        LinkedList<Integer> remove;
        LinkedList<Integer> keep;

        public Ruin() {
            remove = new LinkedList<>();
            keep = new LinkedList<>();
        }

        public LinkedList<Integer> getRemove() {
            return remove;
        }

        public void setRemove(LinkedList<Integer> remove) {
            this.remove = remove;
        }

        public LinkedList<Integer> getKeep() {
            return keep;
        }

        public void setKeep(LinkedList<Integer> keep) {
            this.keep = keep;
        }

        @Override
        public String toString() {
            return "Ruin{" +
                    "remove=" + remove +
                    ", keep=" + keep +
                    '}';
        }
    }

    // STEP 1: Ruin
    //- - - - - - - - - - - -
    public Ruin ruinCross(Result result) {

        Ruin ruined = new Ruin();

        //Select
        int selectedToolId =  this.problemManager.getRandom().nextInt(this.problemManager.getN_TOOLS());



        switch (this.problemManager.getParameters().getMatch()) {
            case "requiredTool": {


                //Remove associated tools -> currently: ONLY NON KTNS TOOLS
                for (int i = 0; i < result.getSequence().length; i++) {
                    Job job = result.getJobSeqPos(i);
                    if(job.getTOOLS()[selectedToolId] == 1) {
                        //CASE 1: job to be removed
                        ruined.getRemove().add(job.getId());
                    }else{
                        ruined.getKeep().add(job.getId());
                    }
                }


                break;
            }

            case "ktnsTool": {

                //Remove associated tools, that qualify as KTNS tools:
                for(int i = 0; i < result.getSequence().length; i++) {
                    Job job = result.getJobSeqPos(i);
                    if (result.getJobToolMatrix()[job.getId()][selectedToolId] == 1) {
                        if (job.getTOOLS()[selectedToolId] == 0) {
                            //KTNS tool
                            ruined.getRemove().add(job.getId());
                        }else{
                            ruined.getKeep().add(job.getId());
                        }
                    } else {
                        ruined.getRemove().add(job.getId());

                    }
                }

                break;
            }

            case "usedTool": {
                //Remove associated tools, that qualify as KTNS tools:
                for(int i = 0; i < result.getSequence().length; i++) {
                    Job job = result.getJobSeqPos(i);
                    if(result.getJobToolMatrix()[job.getId()][selectedToolId] == 1) {
                        ruined.getRemove().add(job.getId());
                    }else{
                        ruined.getKeep().add(job.getId());
                    }
                }

                break;
            }


            case "notRequiredTool": {
                //Remove associated tools, that qualify as KTNS tools:
                for(int i = 0; i < result.getSequence().length; i++) {
                    Job job = result.getJobSeqPos(i);
                    if(result.getJobToolMatrix()[job.getId()][selectedToolId] == 0) {
                        ruined.getRemove().add(job.getId());
                    }else{
                        ruined.getKeep().add(job.getId());
                    }
                }

                break;
            }

            default: {
                this.problemManager.getLogger().logInfo("NO SELECT CHOSEN");
            }
        }


        //Sort procedures:
        Comparator<Integer> byNumberOfSwitchesCreated = Comparator.comparingInt(
                jobId -> {

                    int[] toolsJob = result.getJobToolMatrix()[jobId];
                    int prevJobId = result.prevJobId(jobId);
                    if(!problemManager.legalJob(prevJobId)) {
                        return 0;
                    }
                    int[] toolsPrevJob = result.getJobToolMatrix()[prevJobId];

                    int count = 0;
                    for (int toolId = 0; toolId < problemManager.getN_TOOLS(); toolId++) {
                        if(toolsPrevJob[toolId] == 1 && toolsJob[toolId] == 0) {
                            count+=1;
                        }
                    }

                    return count;
                });


        Comparator<Integer> byNumberOfKTNSFail = Comparator.comparingInt(
                jobId -> {

                    int[] toolsJob = result.getJobToolMatrix()[jobId];
                    int prevJobId = result.prevJobId(jobId);
                    if(!problemManager.legalJob(prevJobId)) {
                        return 0;
                    }
                    int[] toolsPrevJob = result.getJobToolMatrix()[prevJobId];

                    int count = 0;
                    for (int toolId = 0; toolId < problemManager.getN_TOOLS(); toolId++) {
                        if(toolsPrevJob[toolId] == 1 && toolsJob[toolId] == 0) {
                            if(problemManager.getJOB_TOOL_MATRIX()[prevJobId][toolId] == 0) {
                                count += 1;
                            }
                        }
                    }
                    return count;
                });

        //FILTER
        switch (this.problemManager.getParameters().getFilter()) {

            //OK
            case "random": {
                //Remove at random

                //Shuffle
                Collections.shuffle(ruined.getRemove(), random);
                /*System.out.println("remove size:" + ruined.getRemove().size());
                System.out.println("keep size:" + ruined.getKeep().size());
                System.out.println(ruined.getRemove());
                System.out.println(ruined.getKeep());*/

                int remove = ruined.getRemove().size() - this.problemManager.getParameters().getAVG_RUIN();
                //ystem.out.println("Have to remove:" + remove);



                //TODO: check for behavuoir of linked list during remove
                if(remove > 0) {
                    for (int i = 0; i < remove; i++) {
                        //int removed = ruined.getRemove().remove(i);
                        //System.out.println("removed: " + removed + " At: " + i);
                        //System.out.println(ruined.getRemove().removeFirst());
                        ruined.getKeep().add(ruined.getRemove().removeFirst());
                    }
                }

            }

            case "worst": {
                //TODO: Remove the worst performing ones
                /*

                    1) ktnsFailure -> failed ktns
                    2) hopCreator
                    3) Most switches
                 */
                int remove = ruined.getRemove().size() - this.problemManager.getParameters().getAVG_RUIN();
                this.problemManager.getLogger().writeLiveResult(result);
                //sorts ascending
                ruined.getRemove().sort(byNumberOfSwitchesCreated.thenComparing(byNumberOfKTNSFail));

                for (int i = 0; i < remove; i++) {
                    ruined.getKeep().add(ruined.getRemove().removeLast());
                }

                break;
            }


            default: {
                this.problemManager.getLogger().logInfo("NO FILTER CHOSEN");
            }
        }


        return ruined;
    }


    public Ruin ruinMultiCross(Result result) {
        Ruin ruined = new Ruin();

        int nTools =  this.random.nextInt(this.problemManager.getMAX_N_TOOLS()) + 1;
        int[] toolId = new int[nTools];

        //select n tools at random
        for (int i = 0; i < nTools; i++) {
            int selectedToolId =  this.problemManager.getRandom().nextInt(this.problemManager.getN_TOOLS());
            toolId[i] = selectedToolId;
        }

        boolean[] removed = new boolean[this.problemManager.getN_JOBS()];
        int[] nMatchesPerJob = new int[this.problemManager.getN_JOBS()];
        int max = 0;

        //match jobs, count how much matches per job
        jobLoop: for (int jobId = 0; jobId < this.problemManager.getN_JOBS(); jobId++) {
            if(this.problemManager.getJobs()[jobId].getSet().length >= nTools) {
                for (int j = 0; j < nTools; j++) {
                    if(this.problemManager.getJOB_TOOL_MATRIX()[jobId][toolId[j]] == 1) {
                        nMatchesPerJob[jobId]+= 1;
                    }
                }
            }

            if (nMatchesPerJob[jobId] > max) {
                max = nMatchesPerJob[jobId];
            }
        }


        //Only keep maximum matches
        for(int jobId = 0; jobId < nMatchesPerJob.length; jobId++) {
            if(nMatchesPerJob[jobId] == max) {
                ruined.getRemove().add(jobId);
                removed[jobId] =  true;
            }
        }


        //Add remaining tools to keep
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {
            int jobId = result.getSequence()[seqPos];
            if(!removed[jobId]) {
                ruined.getKeep().add(jobId);
            }
        }

        return ruined;
    }


    public Ruin ruinBlock(Result result) {
        Ruin ruined = new Ruin();

        //Select random tool
        int selectedToolId =  this.problemManager.getRandom().nextInt(this.problemManager.getN_TOOLS());

        //Select random job
        int selectedJobId =  this.problemManager.getRandom().nextInt(this.problemManager.getN_JOBS());


        //int[] position = new int[this.problemManager.getN_JOBS()];
        //int found



       /* //Find closest block
        if(result.getJobToolMatrix()[selectedJobId][selectedToolId] > 0) {
            if(selectedJobId > this.problemManager.getN_JOBS() / 2) {

                boolean run = false;
                ruined.getKeep().add(selectedJobId);


                //RIGHT TRAVERSE
                for (int i = selectedJobId; i < this.problemManager.getN_JOBS(); i++) {
                    if(result.getJobToolMatrix()[i][selectedJobId] == 0) {
                        run = true;
                        ruined.getRemove().add(result.getSequence()[i]);
                    }else{
                        ruined.getKeep().add(result.getSequence()[i]);
                    }
                }

                if(!run) {
                    //LEFT TRAVERSE
                    for (int i = selectedJobId; i > 0; i--) {
                        if (result.getJobToolMatrix()[i][selectedJobId] == 0) {
                            ruined.getRemove().add(result.getSequence()[i]);
                        }else{
                            ruined.getKeep().add(result.getSequence()[i]);
                        }
                    }
                }else{
                    //LEFT TRAVERSE
                    for (int i = selectedJobId; i > 0; i--) {
                        ruined.getKeep().add(result.getSequence()[i]);
                    }
                }

            }else{
                boolean run = false;
                ruined.getKeep().add(selectedJobId);

                //LEFT TRAVERSE
                for (int i = selectedJobId; i > 0; i--) {
                    if(result.getJobToolMatrix()[i][selectedJobId] == 0) {
                        run = true;
                        ruined.getRemove().add(result.getSequence()[i]);
                    }else{
                        ruined.getKeep().add(result.getSequence()[i]);
                    }
                }

                if(!run) {
                    //RIGHT TRAVERSE
                    for (int i = selectedJobId; i < this.problemManager.getN_JOBS(); i++) {
                        if (result.getJobToolMatrix()[i][selectedJobId] == 0) {
                            ruined.getRemove().add(result.getSequence()[i]);
                        }else{
                            ruined.getKeep().add(result.getSequence()[i]);
                        }
                    }
                }else{
                    //RIGHT TRAVERSE
                    for (int i = selectedJobId; i < this.problemManager.getN_JOBS(); i++) {
                        ruined.getKeep().add(result.getSequence()[i]);
                    }
                }
            }

        }else{

            //LEFT TRAVERSE
            for (int i = selectedJobId; i > 0; i--) {
                if (result.getJobToolMatrix()[i][selectedJobId] == 0) {
                    ruined.getRemove().add(result.getSequence()[i]);
                }else{
                    ruined.getKeep().add(result.getSequence()[i]);
                }
            }


            //RIGHT TRAVERSE
            for (int i = selectedJobId; i < this.problemManager.getN_JOBS(); i++) {
                if (result.getJobToolMatrix()[i][selectedJobId] == 0) {
                    ruined.getRemove().add(result.getSequence()[i]);
                }else{
                    ruined.getKeep().add(result.getSequence()[i]);
                }
            }

        }*/



        //System.out.println(ruined.getKeep().size() + ruined.getRemove().size());
        //System.out.println(ruined.getRemove());

        return ruined;
    }


    // STEP 2: Recreate
    //- - - - - - - - - - - -


    public Result recreate(Result result, Ruin ruined) throws IOException {

        insertJobsRandomBestPositionBlinks(result, ruined);

        /*//System.out.println("allo");
        if(this.problemManager.getParameters().getInsertPositions().equals("all")) {
            insertJobsRandomBestPositionBlinks(result, ruined);
        }else if(this.problemManager.getParameters().getInsertPositions().equals("removed")){
            //TODO: recreate only on removed positions
        }
*/
        return result;
    }


    // STEP 2A: Insert
    //- - - - - - - - - - - -



    public void insertJobsRandomBestPositionBlinks(Result result , Ruin ruined) throws IOException{
        LinkedList<Integer> sequence = ruined.getKeep();
        int[] seq = sequence.stream().mapToInt(i -> i).toArray();
        Result temp = new Result(seq, problemManager);

        //Shuffle Randomly
        Collections.shuffle(ruined.getRemove(), this.random);


        //System.out.println(ruined.toString());

         for (Integer jobId : ruined.getRemove()) {

            Double bestCost = Double.MAX_VALUE;
            int bestPosition = 0;
            int nBestPositions = 0;

            for (int index = 0; index < sequence.size(); index++) {

                //Blink
                if (random.nextDouble() <= (1 - this.problemManager.getParameters().getBLINK_RATE())) {

                    sequence.add(index, jobId);


                    //To Array -> optimize to linked list strucuture
                    temp.setSequence(sequence.stream().mapToInt(i -> i).toArray());
                    this.problemManager.getDecoder().decodeRR(temp);


                    //this.problemManager.getLogger().writeResult(temp);

                    if(temp.getCost() <  bestCost) {

                        nBestPositions = 1;
                        bestPosition = index;
                        bestCost = temp.getCost();


                    }else if(temp.getCost() == bestCost) {
                        nBestPositions += 1;
                        float probability = 1 / (float) nBestPositions;
                        if (random.nextDouble() <= probability) {
                            bestPosition = index;
                            bestCost = temp.getCost();
                        }
                    }

                    sequence.remove(index);
                }
            }

            sequence.add(bestPosition, jobId);

        }


        int[] seqOut = sequence.stream().mapToInt(i -> i).toArray();
         result.setSequence(seqOut);
        this.problemManager.getDecoder().decode(result);
    }



    /*//TODO
    public Result shake(Result result) {

    }*/

}
