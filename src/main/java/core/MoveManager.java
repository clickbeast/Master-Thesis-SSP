package core;

import models.elemental.Job;


import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class MoveManager {

    private final Random random;
    ProblemManager problemManager;


    public MoveManager(ProblemManager problemManager) {
        this.problemManager = problemManager;

        this.random = this.problemManager.getRandom();

    }



    public Result swap(Result result) {

        //Selection policy
        int[] select = {0};
        int picked = select[this.random.nextInt(select.length)];

        if(picked == 0) {
            return  swapTwoJobs(result);
        }else{
            return  swapTwoBlocks(result);
        }

    }



    /* MOVES ------------------------------------------------------------------ */



    public Result rotateSequence(Result result) {

        return result;
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

    public Ruin ruin(Result result) {

        Ruin ruined = new Ruin();

        //Choose random tool
        int selectedToolId =  this.problemManager.getRandom().nextInt(this.problemManager.getN_TOOLS());


        //Remove associated tools -> currently: ONLY NON KTNS TOOLS
        for (int i = 0; i < this.problemManager.getN_JOBS(); i++) {
            Job job = this.problemManager.getJob(i);

            checkTool: for (int j = 0; j < job.getSet().length; j++) {

                int toolId = job.getSet()[j];

                if(selectedToolId == toolId) {
                    //CASE 1: job to be removed
                    ruined.getRemove().add(job.getId());
                    break;
                }
            }

            if(job.getTOOLS()[selectedToolId] == 1) {
                ruined.getRemove().add(job.getId());
            }else{
                ruined.getKeep().add(job.getId());
            }
        }

        return ruined;
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
    }

    public Result recreate(Result result, Ruin ruined) throws IOException {

        insertAtBestRandomPositionNoRelationOrdered(result, ruined);

        return result;
    }


    public void insertAtBestRandomPositionNoRelationOrdered(Result result, Ruin ruined) throws IOException {
        LinkedList<Integer> sequence = ruined.getKeep();
        int[] seq = sequence.stream().mapToInt(i->i).toArray();
        Result temp = new Result(seq, problemManager);


        for(Integer jobId: ruined.getRemove()) {

            int bestCost = 0;
            int bestPosition = 0;
            int nBestPositions = 0;

            for (int index = 0; index < sequence.size(); index++) {

                sequence.add(index, jobId);
                //To Array -> optimize to linked list strucuture
                temp.setSequence(sequence.stream().mapToInt(i->i).toArray());
                this.problemManager.getDecoder().decode(temp);

                if(temp.getCost() <= bestCost) {
                    if(nBestPositions == 0) {
                        bestPosition = index;
                        bestCost =  temp.getCost();
                        nBestPositions+=1;
                    }else{
                        float probability = 1/nBestPositions;
                        if(random.nextDouble() <= probability) {
                            bestPosition = index;
                            bestCost =  temp.getCost();
                        }
                        nBestPositions+=1;

                    }
                }
                sequence.remove(index);
            }
            sequence.add(bestPosition, jobId);
        }
  }






}
