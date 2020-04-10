package core;

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







}
