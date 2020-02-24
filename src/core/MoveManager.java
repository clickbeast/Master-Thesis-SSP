package core;

import models.Feedback;

import java.util.Random;

public class MoveManager {

    private ProblemManager problemManager;
    private int[] sequence;
    private Random random;
    int A,B;

    public MoveManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
        this.random = this.problemManager.getRandom();
        this.sequence = this.problemManager.getSequence();

        //TMP
        int A = 0;
        int B = 0;
    }

    public void doMove() {
        swapTwoJobs();
    }

    public void cancelMove() {
        //TODO
        this.swapTwoJobs(A,B);
    }

    public void acceptMove() {
        //TODO
    }

    public void swapTwoJobs() {
        int jobA = this.random.nextInt(this.sequence.length);
        int jobB = this.random.nextInt(this.sequence.length);
        int tmp = 0;


        while (jobB == jobA) {
             jobB = this.random.nextInt(this.sequence.length);
        }

        //TODO: optimize swap
        tmp = sequence[jobA];
        sequence[jobA] = sequence[jobB];
        sequence[jobB] =  tmp;
    }

    public void swapTwoJobs(int A, int B) {
        int tmp = 0;
        //TODO: optimize swap
        tmp = sequence[A];
        sequence[A] = sequence[B];
        sequence[B] =  tmp;
    }


    public void swapBlocks() {


    }


    /* GETTERS & SETTERS ------------------------------------------------------------------ */

    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }


    public int[] getSequence() {
        return sequence;
    }

    public void setSequence(int[] sequence) {
        this.sequence = sequence;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}
