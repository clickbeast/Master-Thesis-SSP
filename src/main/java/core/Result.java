package core;

import models.elemental.Job;
import util.General;

import java.util.ArrayList;
import java.util.Arrays;



public class Result {

    private ProblemManager problemManager;

    //General
    private Double cost;
    //-- full perspective --
    //The tool matrix
    private int[][] jobToolMatrix;
    private int[][] toolDistance;

    //-- jobs perspective --
    //The sequence of jobs
    private int[] sequence;
    private int[] jobPositions;
    private int[] switches;
    //private int[] toolDistance;
    private int[] nToolsDelete;
    private int[] nToolsKeep;
    private int[] nToolsAdd;


    //

    //tools perspective
    private int[] toolHops;
    private int[] ktnsToolHops;


    //Total amounts
    private int nSwitches;
    private int nToolHops;
    private int nKtnsToolHops;

    private String type;


    public double penaltyCost;
    public double toolDistanceCost;



    // NOT EXPORTED
    private int[][] zeroBlockLength;

    private double tieBreakingCost;

    //TODO: ADD PROPER DELTA EVAL: VERY VERY VERY IMPORTANT TOO...


    public Result(int[] sequence, ProblemManager problemManager) {
        this.problemManager = problemManager;
        this.sequence = sequence;
        if (problemManager != null) {
            this.jobToolMatrix = General.copyGrid(this.problemManager.getJOB_TOOL_MATRIX());
        }
        this.jobPositions = new int[this.problemManager.getN_JOBS()];
        this.reloadJobPositions();
    }

    public void reloadJobPositions() {
        for (int i = 0; i < sequence.length; i++) {
            int jobId = this.getSequence()[i];
            this.jobPositions[jobId] = i;
        }
    }

    public Result getCopy() {

        int[] sequence = Arrays.copyOf(this.getSequence(), this.getSequence().length);
        int[] switches = Arrays.copyOf(this.getSwitches(), this.getSwitches().length);
        //int[] jobPositions = Arrays.copyOf(this.getJobPositions(), this.getJobPositions().length);

        int[][] jobToolMatrix = General.copyGrid(this.getJobToolMatrix());


        Result result = new Result(sequence, this.getProblemManager());


        result.setCost(this.getCost());
        result.setJobToolMatrix(jobToolMatrix);
        result.setSwitches(switches);
        result.setnSwitches(this.getnSwitches());
        result.setType(this.getType());
        result.setTieBreakingCost(this.getTieBreakingCost());


        //result.setToolDistance(General.copyGrid(this.getToolDistance()));

        result.penaltyCost = this.penaltyCost;
        result.toolDistanceCost = this.toolDistanceCost;

        return result;
    }



    public Result getDeltaCopy() {
        //Jobtool matrix stays the same largley:
        return null;
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


    public int prevJobId(int jobId) {
        int prevJobPos = this.getJobPositions()[jobId] - 1;

        if(prevJobPos < 0) {
            return -1;
        }

        return this.getSequence()[prevJobPos];
    }

    public int nextJobId(int jobId) {
        int nextJobPos = this.getJobPositions()[jobId] + 1;

        if(nextJobPos > this.sequence.length - 1) {
            return -1;
        }

        return this.getSequence()[nextJobPos];
    }

    public Job nextJob(Job job) {
        int nextJobPos = this.getJobPositions()[job.getId()] + 1;
        if(nextJobPos > this.sequence.length - 1) {
            return null;
        }

        return this.problemManager.getJobs()[this.getSequence()[nextJobPos]];
    }



    public Double getCost() {
        return cost;
    }


    public void setCost(Double cost) {
        this.cost = cost;
    }


    public boolean toolUsed(int jobId, int toolId) {
        return this.getJobToolMatrix()[jobId][toolId] == 1;
    }


    public boolean toolUsedAtSeqPos(int i, int toolId) {
        return this.getJobToolMatrix()[this.getJobSeqPos(i).getId()][toolId] == 1;
    }

    public int[] getTools(Job job) {
        return this.getJobToolMatrix()[job.getId()];
    }

    public int[] getToolsAtSeqPos(int i) {
        return this.getTools(this.getJobSeqPos(i));
    }




    /* GETTERS & SETTERS ------------------------------------------------------------------ */


    public double getPenaltyCost() {
        return penaltyCost;
    }

    public void setPenaltyCost(double penaltyCost) {
        this.penaltyCost = penaltyCost;
    }

    public double getToolDistanceCost() {
        return toolDistanceCost;
    }

    public void setToolDistanceCost(double toolDistanceCost) {
        this.toolDistanceCost = toolDistanceCost;
    }

    public void setImproved() {
        this.setType("improved");

    }

    public void setAccepted() {
        this.setType("accepted");

    }

    public void setRejected() {
        this.setType("rejected");

    }

    public void setBackup() {
        this.setType("backup");
    }

    public void setTrial() {
        this.setType("trial");
    }

    public void setInitial() {
        this.setType("Initial");
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int[] getSequence() {
        return sequence;
    }

    public void setSequence(int[] sequence) {
        this.sequence = sequence;
        this.reloadJobPositions();
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


    public int[][] getToolDistance() {
        return toolDistance;
    }

    public void setToolDistance(int[][] toolDistance) {
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

    public int[][] getZeroBlockLength() {
        return zeroBlockLength;
    }

    public void setZeroBlockLength(int[][] zeroBlockLength) {
        this.zeroBlockLength = zeroBlockLength;
    }


    public double getTieBreakingCost() {
        return tieBreakingCost;
    }

    public void setTieBreakingCost(double tieBreakingCost) {
        this.tieBreakingCost = tieBreakingCost;
    }

    @Override
    public String toString() {
        return "Result{" +
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
