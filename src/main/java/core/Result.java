package core;

import models.elemental.Job;
import util.General;

import java.util.Arrays;

public class Result {
    //The sequence of jobs
    private int[] sequence;
    //The tool matrix
    private int[][] jobToolMatrix;
    //The amount of switches to reach a given position
    private int[] switches;
    //The position of jobs within the sequence
    //FIXME: check if combination with jobs is better
    private int[] jobPosition;
    //the cost of this result
    private int cost;

    private ProblemManager problemManager;

    //Possibility-> use magazine representation
    private int[][] magazineState;

    //TODO: ADD PROPER DELTA EVAL: VERY VERY VERY IMPORTANT TOO...


    public Result(ProblemManager problemManager, int[] sequence, int[] jobPosition, int[][] jobToolMatrix) {
        this.problemManager = problemManager;
        this.sequence = sequence;

        this.jobToolMatrix = jobToolMatrix;
    }

    public Result(int[] sequence) {
        this.sequence = sequence;
    }

    public Result getCopy() {

        int[] sequence = Arrays.copyOf(this.getSequence(), this.getSequence().length);
        int[] switches = Arrays.copyOf(this.getSwitches(), this.getSwitches().length);
        int[][] jobToolMatrix = General.copyGrid(this.getJobToolMatrix());
        int cost = this.getCost();

        Result result = new Result(sequence);
        result.setJobToolMatrix(jobToolMatrix);
        result.setSwitches(switches);
        result.setCost(cost);

        return result;
    }


    /* DATA MODEL SETUP ------------------------------------------------------------------ */


    public Job getJobSeqPos(int i) {
        return this.problemManager.getJobs()[this.getSequence()[i]];
    }

    public int getSwitchesAtSeqPos(int i) {
        return this.getSwitches()[i];
    }



    /* GETTERS & SETTERS ------------------------------------------------------------------ */


    public int[] getSequence() {
        return sequence;
    }

    public void setSequence(int[] sequence) {
        this.sequence = sequence;
    }

    public int[][] getJobToolMatrix() {
        return jobToolMatrix;
    }

    public void setJobToolMatrix(int[][] jobToolMatrix) {
        this.jobToolMatrix = jobToolMatrix;
    }

    public int[] getSwitches() {
        return switches;
    }

    public void setSwitches(int[] switches) {
        this.switches = switches;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }



    @Override
    public String toString() {
        return "Result{" +
                "sequence=" + Arrays.toString(sequence) +
                ", jobToolMatrix=" + Arrays.deepToString(jobToolMatrix) +
                ", switches=" + Arrays.toString(switches) +
                ", cost=" + cost +
                ", magazineState=" + Arrays.deepToString(magazineState) +
                '}';
    }
}
