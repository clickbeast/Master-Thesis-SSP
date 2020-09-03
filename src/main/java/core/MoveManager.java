package core;

import core.moves.Move;
import models.elemental.Job;
import util.General;


import java.io.IOException;
import java.util.*;

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
        //TODO: for debug
        return this.swap(result);

       /* if(problemManager.getParameters().getLocalSearch().equals("swaps")) {
            return this.swap(result);
        }else if(problemManager.getParameters().getLocalSearch().equals("ruinAndRecreate")){
            return this.ruinAndRecreate(result);
        }
        return null;*/
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
        //result.reloadJobPositions();

        return result;
    }


    /* RUIN + RECREATE ------------------------------------------------------------------ */


    public Result ruinAndRecreate(Result result) throws IOException {
        Ruin ruin = null;

        int[] select = {0,0,1};
        int picked = select[this.random.nextInt(select.length)];


        if(picked == 0) {
            ruin = ruinMultiCross(result);
        }else if(picked == 1){
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

        //Match
        //Remove associated tools -> currently: ONLY NON KTNS TOOLS
        for (int i = 0; i < result.getSequence().length; i++) {
            Job job = result.getJobAtSeqPos(i);
            if(job.getTOOLS()[selectedToolId] == 1) {
                //CASE 1: job to be removed
                ruined.getRemove().add(job.getId());
            }else{
                ruined.getKeep().add(job.getId());
            }
        }

        //Filter at random

        //Shuffle
        Collections.shuffle(ruined.getRemove(), random);


        int remove = ruined.getRemove().size() - this.problemManager.getParameters().getAVG_RUIN();



        //TODO: check for behavuoir of linked list during remove
        if(remove > 0) {
            for (int i = 0; i < remove; i++) {
                ruined.getKeep().add(ruined.getRemove().removeFirst());
            }
        }

        return ruined;
    }


    class Match {
        int jobId;
        int nMatches;

        public Match(int jobId, int nMatches) {
            this.jobId = jobId;
            this.nMatches = nMatches;
        }
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


    public Ruin ruinMultiCrossXBestMatch(Result result) {
        Ruin ruined = new Ruin();

        int nTools =  this.random.nextInt(this.problemManager.getMAX_N_TOOLS()) + 1;
        int[] toolId = new int[nTools];

        //select n tools at random
        for (int i = 0; i < nTools; i++) {
            int selectedToolId =  this.problemManager.getRandom().nextInt(this.problemManager.getN_TOOLS());
            toolId[i] = selectedToolId;
        }

        boolean[] removed = new boolean[this.problemManager.getN_JOBS()];
        //int[] nMatchesPerJob = new int[this.problemManager.getN_JOBS()];

        ArrayList<Match> matches = new ArrayList<>(this.problemManager.getN_JOBS());

        int max = 0;

        //match jobs, count how much matches per job
        jobLoop: for (int jobId = 0; jobId < this.problemManager.getN_JOBS(); jobId++) {
            matches.add(new Match(jobId,0));
            if(this.problemManager.getJobs()[jobId].getSet().length >= nTools) {
                for (int j = 0; j < nTools; j++) {
                    if(this.problemManager.getJOB_TOOL_MATRIX()[jobId][toolId[j]] == 1) {
                        matches.get(jobId).nMatches += 1;
                    }
                }
            }


            //Try random select
            if (matches.get(jobId).nMatches > max) {
                max = matches.get(jobId).nMatches;
            }
        }

        //Only keep maximum matches
        /*for(int jobId = 0; jobId < nMatchesPerJob.length; jobId++) {
            if(nMatchesPerJob[jobId] == max) {
                ruined.getRemove().add(jobId);
                removed[jobId] =  true;
            }
        }*/

        Comparator<Match> byNumberOfMatches = Comparator.comparingInt(
                match -> {
                    return match.nMatches;
                });

        matches.sort(byNumberOfMatches.reversed());

        int nRemove = this.random.nextInt(this.problemManager.getParameters().getAVG_RUIN()) + 1;
        //Keep the x best matches
        for (Match match: matches) {
            ruined.getRemove().add(match.jobId);
            removed[match.jobId] = true;
            nRemove --;

            if(nRemove < 1) {
                break;
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

        return this.ruinBlockAtTool(result, ruined, selectedToolId);
    }


    public Ruin ruinBlockAtTool(Result result, Ruin ruined, int selectedToolId) {

        //TODO: can be combined with tie breaking cost
        boolean[] zeroBlock = new boolean[result.getSequence().length];
        int nZeroBlocks = 0;
        boolean run = false;
        //Locate 0 blocks
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            boolean used = result.isToolUsedAtSeqPos(selectedToolId,seqPos);

            if(!run) {
                if(!used) {
                    //Left tool has to be 1
                    if (seqPos > 0) {
                        if (result.isToolUsedAtSeqPos(selectedToolId, seqPos -1)) {
                            run = true;
                            zeroBlock[seqPos] = true;
                            nZeroBlocks += 1;
                        }
                    }
                }
            }else{
                if(used) {
                    //run is finished
                    run = false;
                }

                /*if(seqPos == result.getSequence().length - 1) {
                    //run is finsished
                    run = false;
                }*/
            }
        }

        //Fallback
        if(nZeroBlocks < 1) {
            //System.out.println("Zero block fallback");
            return this.ruinCross(result);
        }

        //Select n'th zero block randomly
        int selectZeroBlock = this.random.nextInt(nZeroBlocks);


        //Collect selected Zero block as removed, others as keep
        run = false;
        boolean selectedRun = false;
        int zeroBlockId = -1;
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            if(!run) {
                if (zeroBlock[seqPos]) {
                    //Zero block is found
                    run = true;
                    zeroBlockId+=1;

                    //Check if zero block is desired zero block
                    if(zeroBlockId == selectZeroBlock) {
                        selectedRun = true;
                        ruined.getRemove().add(result.getJobIdAtSeqPos(seqPos));
                    }else{
                        ruined.getKeep().add(result.getJobIdAtSeqPos(seqPos));
                    }
                }else{
                    ruined.getKeep().add(result.getJobIdAtSeqPos(seqPos));
                }
            }else{
                boolean used = result.isToolUsedAtSeqPos(seqPos, selectedToolId);

                if(!used) {
                    //run is still in progress
                    if(selectedRun) {
                        //Selected run is in progress
                        ruined.getRemove().add(result.getJobIdAtSeqPos(seqPos));
                    }else{
                        //Not selected run is in progress
                        ruined.getKeep().add(result.getJobIdAtSeqPos(seqPos));
                    }

                }else{
                    //run is finished
                    run = false;
                    selectedRun = false;
                    ruined.getKeep().add(result.getJobIdAtSeqPos(seqPos));
                }
            }
        }



        return ruined;
    }


    public Ruin ruinMultiBlock(Result result) {

        Ruin ruined = new Ruin();
        int nTools =  this.random.nextInt(this.problemManager.getN_TOOLS()) + 1;

        //First find block
        int selectedToolId = this.random.nextInt(this.problemManager.getN_TOOLS());
        ruined = this.ruinBlockAtTool(result, ruined,selectedToolId);

        General.printGridP(result);
        General.printArrayP(this.problemManager.getTools()[selectedToolId].getJOBS(), result.getSequence());
        System.out.println(Arrays.toString(result.getSequence()));
        int[] nMatchesPerJob = new int[this.problemManager.getN_JOBS()];
        int max = 0;


        for (int nTool = 1; nTool < nTools; nTool++) {
            selectedToolId = this.random.nextInt(this.problemManager.getN_TOOLS());
            //Check which jobs still qualify
            //Keep MAX QUALIFIERS
            for(Integer jobId: ruined.getRemove()) {

                //TODO: better check-> full block check
                if(result.getJobToolMatrix()[jobId][selectedToolId] == 0) {
                    nMatchesPerJob[jobId] += 1;
                }

                if (nMatchesPerJob[jobId] > max) {
                    max = nMatchesPerJob[jobId];
                }
            }
        }


        Ruin ruinedOut = new Ruin();
        boolean[] removed = new boolean[this.problemManager.getN_JOBS()];

        //Only keep maximum matches
        for(Integer jobId: ruined.getRemove()) {
            if(nMatchesPerJob[jobId] == max) {
                ruinedOut.getRemove().add(jobId);
                removed[jobId] = true;
            }
        }

        //Add remaining tools to keep
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {
            int jobId = result.getSequence()[seqPos];
            if(!removed[jobId]) {
                ruinedOut.getKeep().add(jobId);
            }
        }


        return ruinedOut;
    }

    class Block {
        //Interval [a,b[
        //Included
        int start;
        //Not included
        int end;
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

                    // TODO: Optimize -> handle linked list directly
                    temp.setSequence(sequence.stream().mapToInt(i -> i).toArray());
                    this.problemManager.getDecoder().decode(temp);

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





    public void insertJobsRandomBestPositionBlinksParallel(Result result , Ruin ruined) throws IOException{
        LinkedList<Integer> sequence = ruined.getKeep();
        int[] seq = sequence.stream().mapToInt(i -> i).toArray();

        //Shuffle Randomly
        Collections.shuffle(ruined.getRemove(), this.random);


        //System.out.println(ruined.toString());

        for (Integer jobId : ruined.getRemove()) {
            Double bestCost = Double.MAX_VALUE;
            int bestPosition = 0;
            int nBestPositions = 0;

            double[] scores = this.getScoresBestInsert(result, ruined, jobId, sequence);

            for (int index = 0; index < sequence.size(); index++) {

                //Blink
                if (random.nextDouble() <= (1 - this.problemManager.getParameters().getBLINK_RATE())) {
                    if(scores[index] <  bestCost) {

                        nBestPositions = 1;
                        bestPosition = index;
                        bestCost = scores[index];

                    }else if(scores[index] == bestCost) {
                        nBestPositions += 1;
                        float probability = 1 / (float) nBestPositions;
                        if (random.nextDouble() <= probability) {
                            bestPosition = index;
                            bestCost = scores[index];
                        }
                    }
                }
            }

            sequence.add(bestPosition, jobId);

        }


        int[] seqOut = sequence.stream().mapToInt(i -> i).toArray();
        result.setSequence(seqOut);
        this.problemManager.getDecoder().decode(result);
    }


    class BestPlace extends Thread {

        int score;
        ProblemManager problemManager;
        LinkedList<Integer> sequence;
        int insertPosition;
        int jobId;
        double value;

        public BestPlace(ProblemManager problemManager, int jobId, LinkedList<Integer> copy, int insertPosition) {
            this.problemManager = problemManager;
            this.jobId = jobId;
            this.insertPosition = insertPosition;
            this.sequence = copy;
        }

        @Override
        public void run() {
            super.run();

            int[] seq = new int[0];
            Result temp = new Result(seq, problemManager);

            sequence.add(insertPosition, jobId);
            //To Array -> optimize to linked list strucuture
            // TODO: Optimize -> handle linked list directly
            temp.setSequence(sequence.stream().mapToInt(i -> i).toArray());

            try {
                this.problemManager.getDecoder().decode(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.value = temp.getCost();

        }

        public double getScore() {
            return value;
        }
    }








    public double[] getScoresBestInsert(Result result, Ruin ruined, int jobId, LinkedList<Integer> sequence) {
        double[] scores = new double[sequence.size()];
        BestPlace[] bestPlaces = new BestPlace[sequence.size()];

        for (int insertPosition = 0; insertPosition < sequence.size(); insertPosition++) {
            LinkedList<Integer> copy = new LinkedList<>(sequence);
            bestPlaces[insertPosition] = new BestPlace(this.problemManager,jobId,copy,insertPosition);
            bestPlaces[insertPosition].start();
        }


        try {
            for (BestPlace bestPlace : bestPlaces) {
                bestPlace.join();
            }

        } catch (InterruptedException e) {
            System.out.println("An error occured");
        }


        for (BestPlace bestPlace: bestPlaces) {
            scores[bestPlace.insertPosition] = bestPlace.getScore();
        }

        return scores;
    }






}
