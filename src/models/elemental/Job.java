package models.elemental;

import core.ProblemManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Job {

    private ProblemManager problemManager;

    //STATIC
    private int id;
    private int[] tools;
    private int[] antiSet;
    private int[] set;

    //HELP -> adjust to better structure than adjency

    //CALCULATION
    private int position;
    private int switches;
    private int nextFirstJobChosenAntiCount;
    private int[] result;



    /* INITIALIZATION ------------------------------------------------------------------ */

    public Job(int id,ProblemManager problemManager) {
        this.problemManager =  problemManager;
        List<Integer> collect = new ArrayList<>();
        List<Integer> collectAnti = new ArrayList<>();

        this.tools = problemManager.getJOB_TOOL_MATRIX()[id];
        this.id = id;

        for (int i = 0; i < problemManager.getJOB_TOOL_MATRIX()[id].length; i++) {
            int value = problemManager.getJOB_TOOL_MATRIX()[id][i];
            if(value ==  1) {
                collect.add(i);
            }else{
                collectAnti.add(i);
            }
        }
        this.set = collect.stream().mapToInt(i->i).toArray();
        this.antiSet = collectAnti.stream().mapToInt(i->i).toArray();

        this.position = id;
        this.result = this.problemManager.getResult()[id];
    }


    /* GRAPH ------------------------------------------------------------------ */

    public Job nextJob() {
        if(position + 1 >=  this.problemManager.getN_JOBS()) {
            return null;
        }

        Job nextJob = this.problemManager.getJobSeqPos(position + 1);
        return nextJob;
    }

    public Job prevJob() {
        //TODO: look to store this...
        if(position - 1 < 0) {
            return null;
        }

        Job prevJob = this.problemManager.getJobSeqPos(position - 1);
        return prevJob;

    }


    /* GETTERS & SETTERS ------------------------------------------------------------------ */


    public int getSwitches() {
        return switches;
    }

    public void setSwitches(int switches) {
        this.switches = switches;
    }

    public int getPickedToolsNextJobCount() {
        return nextFirstJobChosenAntiCount;
    }

    public void setNextFirstJobChosenAntiCount(int nextFirstJobChosenAntiCount) {
        this.nextFirstJobChosenAntiCount = nextFirstJobChosenAntiCount;
    }

    public int[] getTools() {
        return tools;
    }

    public void setTools(int[] tools) {
        this.tools = tools;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int[] getResult() {
        return result;
    }

    public void setResult(int[] result) {
        this.result = result;
    }

    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getAntiSet() {
        return antiSet;
    }

    public void setAntiSet(int[] antiSet) {
        this.antiSet = antiSet;
    }

    public int[] getSet() {
        return set;
    }

    public void setSet(int[] set) {
        this.set = set;
    }


    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", tools=" + Arrays.toString(tools) +
                ", antiSet=" + Arrays.toString(antiSet) +
                ", set=" + Arrays.toString(set) +
                '}';
    }
}
