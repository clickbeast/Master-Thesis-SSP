package core;

import models.elemental.Job;
import util.General;

import java.util.Arrays;


public class Result {

    //Reference to problemmanager
    private ProblemManager problemManager;

    //General
    private Double cost;
    private double tieBreakingCost;
    private int nSwitches;

    private int[][] jobToolMatrix;
    private int[] sequence;

    private int ktnsId;

    private String type;

    public Result(int[] sequence, ProblemManager problemManager) {
        this.problemManager = problemManager;
        this.setSequence(sequence);

        int[][] jobToolMatrix = General.copyGrid(problemManager.getJOB_TOOL_MATRIX());

        this.setCost(this.getCost());
        this.setJobToolMatrix(jobToolMatrix);
        this.setnSwitches(-1);
        this.setType("Initial");
        this.setTieBreakingCost(-1);
        this.setKtnsId(2);
    }


    public Result getCopy() {
        int[] sequence = Arrays.copyOf(this.getSequence(), this.getSequence().length);
        int[][] jobToolMatrix = General.copyGrid(this.getJobToolMatrix());

        Result result = new Result(sequence, this.getProblemManager());

        result.setCost(this.getCost());
        result.setJobToolMatrix(jobToolMatrix);
        result.setnSwitches(this.getnSwitches());
        result.setType(this.getType());
        result.setTieBreakingCost(this.getTieBreakingCost());
        result.setKtnsId(ktnsId);

        return result;
    }


    /* DATA MODEL INTERACTION ------------------------------------------------------------------ */


    public boolean isToolUsedAtJobId(int toolId, int jobId) {
        return this.getJobToolMatrix()[jobId][toolId] == 1 || this.getJobToolMatrix()[jobId][toolId] == this.getKtnsId();
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
        if(seqPos >= this.problemManager.getN_JOBS()) {
            return -1;
        }

        return this.getSequence()[seqPos];
    }






    /* ----------------------------------------------------------------------------------------- */



    /* GETTERS & SETTERS ------------------------------------------------------------------ */

    public ProblemManager getProblemManager() {
        return problemManager;
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

    public int getnSwitches() {
        return nSwitches;
    }

    public void setnSwitches(int nSwitches) {
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
