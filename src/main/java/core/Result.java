package core;

import models.elemental.Job;

import java.util.LinkedList;


public class Result {

    //Reference to problemmanager
    private ProblemManager problemManager;

    //General
    private Double cost;
    private double tieBreakingCost;
    private double nSwitches;

    private int[][] jobToolMatrix;
    private int[] sequence;
    private int[] jobPositions;

    private int ktnsId;



    private String type;




    public Result(LinkedList<Integer> sequence, ProblemManager problemManager) {
        this.problemManager = problemManager;
    }


    public Result getCopy() {

        return null;
    }

    public Result getDeltaCopy() {

        return null;
    }


    /* DATA MODEL INTERACTION ------------------------------------------------------------------ */


    public boolean isToolUsedAtJobId(int toolId, int jobId) {
        if(this.getJobToolMatrix()[jobId][toolId] == (1 | this.getKtnsId())) {
            return true;
        }
        return false;
    }

    public boolean isToolUsedAtSeqPos(int toolId, int seqPos) {
        return isToolUsedAtJobId(toolId, this.getJobIdAtSeqPos(seqPos));
    }

    public boolean isToolRequiredAtJobId(int toolId, int jobId) {
        if(this.getJobToolMatrix()[jobId][toolId] == (1)) {
            return true;
        }
        return false;
    }

    public boolean isToolRequiredAtSeqPos(int toolId, int seqPos) {
        return isToolRequiredAtJobId(toolId, this.getJobIdAtSeqPos(seqPos));

    }

    public boolean isToolKTNSAtJobId(int toolId, int jobId) {
        if(this.getJobToolMatrix()[jobId][toolId] == this.getKtnsId()) {
            return true;
        }
        return false;
    }

    public boolean isToolKTNSAtSeqPos(int toolId, int seqPos) {
        return isToolKTNSAtJobId(toolId, this.getJobIdAtSeqPos(seqPos));
    }

    public Job getJobAtSeqPos(int seqPos) {
        return this.problemManager.getJob(getJobIdAtSeqPos(seqPos));
    }

    public int getJobIdAtSeqPos(int seqPos) {
        return this.getSequence()[seqPos];
    }


    public Job prevJob(Job job) {
        return null;
    }

    public Job nextJob(Job job) {
        return null;
    }




    /* ----------------------------------------------------------------------------------------- */


    
    /* GETTERS & SETTERS ------------------------------------------------------------------ */

    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public double getTieBreakingCost() {
        return tieBreakingCost;
    }

    public void setTieBreakingCost(double tieBreakingCost) {
        this.tieBreakingCost = tieBreakingCost;
    }

    public double getnSwitches() {
        return nSwitches;
    }

    public void setnSwitches(double nSwitches) {
        this.nSwitches = nSwitches;
    }

    public int[][] getJobToolMatrix() {
        return jobToolMatrix;
    }

    public void setJobToolMatrix(int[][] jobToolMatrix) {
        this.jobToolMatrix = jobToolMatrix;
    }

    public int[] getSequence() {
        return sequence;
    }

    public void setSequence(int[] sequence) {
        this.sequence = sequence;
    }

    public int[] getJobPositions() {
        return jobPositions;
    }

    public void setJobPositions(int[] jobPositions) {
        this.jobPositions = jobPositions;
    }

    public int getKtnsId() {
        return ktnsId;
    }

    public void setKtnsId(int ktnsId) {
        this.ktnsId = ktnsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
