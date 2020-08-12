package core;

import core.moves.Move;
import core.moves.RR;
import core.moves.Swap;
import models.elemental.Job;


import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

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


    public Result rotateSequence(Result result) {

        return result;
    }

    //TODO:
    public Result jobPairSwap(Result result) {

        //int jobA = this.random.nextInt(sequence.length);
        return null;

    }


    //TODO:
    public Result inversionBetweenTwoJobs(Result result) {
        return null;
    }

    public Result swapTwoBlocks(Result result) {

        int[] sequence = result.getSequence();

        int maxLength = sequence.length/2;
        int length = this.random.nextInt(maxLength);

        int b1 = this.random.nextInt(sequence.length-length);

        //TODO: makes sure they are spread out by a certain amount

        int b2 = this.random.nextInt(sequence.length - length);
        for (int i = 0; i < length; i++) {

            //Swap
            int tmp = sequence[b1];
            sequence[b1] = sequence[b2];
            sequence[b2] = tmp;

            b1 += 1;
            b2 += 1;

        }
        return result;
    }


    public Result swapTwoBlocksSeparated(Result result) {

        return null;
    }



    /* RUIN + RECREATE ------------------------------------------------------------------ */


    public Result ruinAndRecreate(Result result) throws IOException {
        Ruin ruined = ruin(result);
        recreate(result, ruined);
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
    public Ruin ruin(Result result) {

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




    public Ruin selectZeroBlock(Result result) {

        return null;
    }




    //<editor-fold desc="RR sub routines">


    // STEP 1A: SELECT
    //- - - - - - - - - - - -


    public  Ruin selectRandomTool(Result result) {

        return null;

    }


    public Ruin selectRandomJob(Result result) {


        return null;
    }


    public Ruin selectMostHopsRoulette(Result result) {


        return null;
    }


    // STEP 1B: MATCH
    //- - - - - - - - - - - -


    public Ruin matchRequiredTool(Result result, int id) {

        return null;
    }


    public Ruin matchNotRequiredTool(Result result, int id) {

        return null;
    }

    public Ruin matchKtnsTool(Result result, int id) {

        return null;
    }


    public Ruin matchKtnsFail(Result result, int id) {

        return null;
    }



    // STEP 1B': REMOVE JOBS
    //- - - - - - - - - - - -


    public void remove() {

    }

    public void removeRandom() {



    }

    public void filterWorst() {



    }



    //</editor-fold>


    // STEP 2: Recreate
    //- - - - - - - - - - - -


    public Result recreate(Result result, Ruin ruined) throws IOException {
        //System.out.println("allo");
        if(this.problemManager.getParameters().getInsertPositions().equals("all")) {
            insertJobsRandomBestPositionBlinks(result, ruined);
        }else if(this.problemManager.getParameters().getInsertPositions().equals("removed")){
            //TODO: recreate only on removed positions
        }

        return result;
    }


    // STEP 2A: Insert
    //- - - - - - - - - - - -


    //TODO
    public void insertJobsRemovedRandomBestPositionsBlinks(Result result, Ruin ruined) {


    }



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
