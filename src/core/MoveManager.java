package core;

import models.Feedback;

import java.util.Random;

public class MoveManager {

    private ProblemManager problemManager;
    private int[] sequence;
    private Random random;
    private int A,B;

    public MoveManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
        this.random = this.problemManager.getRandom();
        this.sequence = this.problemManager.getSequence();

        //TMP
        this.A = 0;
        this.B = 0;
    }

    public void doMove() {
        swapTwoJobs();
    }

    public void cancelMove() {
        //TODO
        this.swapTwoJobs(this.A,this.B);
    }

    public void acceptMove() {
        //TODO
    }

    public void swapTwoJobs() {
        int jobA = this.random.nextInt(this.sequence.length);
        int jobB = this.random.nextInt(this.sequence.length);
        int tmp = 0;

        //Look until other job not the same
        while (jobB == jobA) {
             jobB = this.random.nextInt(this.sequence.length);
        }

        //TODO: optimize swap
        //Swap
        tmp = sequence[jobA];
        sequence[jobA] = sequence[jobB];
        sequence[jobB] =  tmp;

        //Set to swapped
        this.setA(jobA);
        this.setB(jobB);

        //Set the position inside the job
        this.problemManager.getJobSeqPos(jobA).setPosition(jobA);
        this.problemManager.getJobSeqPos(jobB).setPosition(jobB);

    }

    public void swapTwoJobs(int A, int B) {
        int tmp = 0;

        //TODO: optimize swap
        tmp = sequence[A];
        sequence[A] = sequence[B];
        sequence[B] =  tmp;

        //Set the position inside the job
        this.problemManager.getJobSeqPos(A).setPosition(A);
        this.problemManager.getJobSeqPos(B).setPosition(B);
    }


    public void swapBlocks() {

    }


    /* GETTERS & SETTERS ------------------------------------------------------------------ */


    public int getA() {
        return A;
    }

    public void setA(int a) {
        A = a;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }

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
