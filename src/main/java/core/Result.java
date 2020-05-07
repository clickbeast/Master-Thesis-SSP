package core;

import models.elemental.Job;
import util.General;

import java.util.ArrayList;
import java.util.Arrays;

public class Result {

    private ProblemManager problemManager;

    //General
    private int cost;

    //-- full perspective --
    //The tool matrix
    private int[][] jobToolMatrix;

    //-- jobs perspective --
    //The sequence of jobs
    private int[] sequence;
    private int[] jobPositions;
    private int[] switches;
    private int[] toolDistance;
    private int[] nToolsDelete;
    private int[] nToolsKeep;
    private int[] nToolsAdd;


    //tools perspective
    private int[] toolHops;
    private int[] ktnsToolHops;


    //Total amounts
    private int nSwitches;
    private int nToolHops;
    private int nKtnsToolHops;


    //private int[][] magazineState;

    //TODO: ADD PROPER DELTA EVAL: VERY VERY VERY IMPORTANT TOO...


    public Result(ProblemManager problemManager, int[] sequence, int[] jobPosition, int[][] jobToolMatrix) {
        this.problemManager = problemManager;
        this.sequence = sequence;

        this.jobToolMatrix = jobToolMatrix;
    }

    public Result(int[] sequence, ProblemManager problemManager) {
        this.problemManager = problemManager;
        this.sequence = sequence;

        this.jobToolMatrix = General.copyGrid(this.problemManager.getJOB_TOOL_MATRIX());

        this.jobPositions = new int[this.sequence.length];
        this.reloadJobPositions();
    }


    public void reloadJobPositions() {
        for (int i = 0; i < jobPositions.length; i++) {
            this.jobPositions[this.getSequence()[i]] = i;
        }
    }

    public Result getCopy() {

        int[] sequence = Arrays.copyOf(this.getSequence(), this.getSequence().length);
        int[] switches = Arrays.copyOf(this.getSwitches(), this.getSwitches().length);
        //int[] jobPositions = Arrays.copyOf(this.getJobPositions(), this.getJobPositions().length);

        int[][] jobToolMatrix = General.copyGrid(this.getJobToolMatrix());


        Result result = new Result(sequence, this.getProblemManager());
        result.setJobToolMatrix(jobToolMatrix);
        result.setSwitches(switches);
        result.setnSwitches(this.getnSwitches());


        return result;
    }


    /* DATA MODEL SETUP ------------------------------------------------------------------ */


    public Job getJobSeqPos(int i) {
        return this.problemManager.getJobs()[this.getSequence()[i]];
    }

    public Job prevJob(Job job) {
        int prevJobPos = this.getJobPositions()[job.getId()] - 1;

        if(prevJobPos < 0) {
            return null;
        }

        return this.problemManager.getJobs()[this.getSequence()[prevJobPos]];
    }


    public Job nextJob(Job job) {
        int nextJobPos = this.getJobPositions()[job.getId()] + 1;
        if(nextJobPos > this.sequence.length - 1) {
            return null;
        }

        return this.problemManager.getJobs()[this.getSequence()[nextJobPos]];
    }


    public int getCost() {
        return this.getnSwitches();
    }

    public void setCost(int cost) {
        this.setnSwitches(cost);
        this.cost = cost;
    }

    public int[] getTools(Job job) {
        return this.getJobToolMatrix()[job.getId()];
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

    public int[] getJobPositions() {

        return jobPositions;
    }

    public void setJobPositions(int[] jobPositions) {
        this.jobPositions = jobPositions;
    }

    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }



    public int[] getToolDistance() {
        return toolDistance;
    }

    public void setToolDistance(int[] toolDistance) {
        this.toolDistance = toolDistance;
    }

    public int[] getnToolsDelete() {
        return nToolsDelete;
    }

    public void setnToolsDelete(int[] nToolsDelete) {
        this.nToolsDelete = nToolsDelete;
    }

    public int[] getnToolsKeep() {
        return nToolsKeep;
    }

    public void setnToolsKeep(int[] nToolsKeep) {
        this.nToolsKeep = nToolsKeep;
    }

    public int[] getnToolsAdd() {
        return nToolsAdd;
    }

    public void setnToolsAdd(int[] nToolsAdd) {
        this.nToolsAdd = nToolsAdd;
    }

    public int[] getToolHops() {
        return toolHops;
    }

    public void setToolHops(int[] toolHops) {
        this.toolHops = toolHops;
    }

    public int[] getKtnsToolHops() {
        return ktnsToolHops;
    }

    public void setKtnsToolHops(int[] ktnsToolHops) {
        this.ktnsToolHops = ktnsToolHops;
    }

    public int getnSwitches() {
        return nSwitches;
    }

    public void setnSwitches(int nSwitches) {
        this.nSwitches = nSwitches;
    }

    public int getnToolHops() {
        return nToolHops;
    }

    public void setnToolHops(int nToolHops) {
        this.nToolHops = nToolHops;
    }

    public int getnKtnsToolHops() {
        return nKtnsToolHops;
    }

    public void setnKtnsToolHops(int nKtnsToolHops) {
        this.nKtnsToolHops = nKtnsToolHops;
    }

    @Override
    public String toString() {
        return "Result{" +
                "problemManager=" + problemManager +
                ", jobToolMatrix=" + Arrays.toString(jobToolMatrix) +
                ", sequence=" + Arrays.toString(sequence) +
                ", jobPositions=" + Arrays.toString(jobPositions) +
                ", switches=" + Arrays.toString(switches) +
                ", toolDistance=" + Arrays.toString(toolDistance) +
                ", nToolsDelete=" + Arrays.toString(nToolsDelete) +
                ", nToolsKeep=" + Arrays.toString(nToolsKeep) +
                ", nToolsAdd=" + Arrays.toString(nToolsAdd) +
                ", toolHops=" + Arrays.toString(toolHops) +
                ", ktnsToolHops=" + Arrays.toString(ktnsToolHops) +
                ", nSwitches=" + nSwitches +
                ", nToolHops=" + nToolHops +
                ", nKtnsToolHops=" + nKtnsToolHops +
                '}';
    }
}
