package models.elemental;

import core.ProblemManager;

import java.util.ArrayList;
import java.util.List;

public class Tool {
    private ProblemManager problemManager;

    //STATIC
    private int id;
    private int[] JOBS;
    private int[] antiSet;
    private int[] set;


    /* INITIALIZATION ------------------------------------------------------------------ */

    public Tool(int id, ProblemManager problemManager) {

        this.problemManager =  problemManager;
        List<Integer> collect = new ArrayList<>();
        List<Integer> collectAnti = new ArrayList<>();

        this.id = id;
        this.JOBS = new int[problemManager.getN_JOBS()];

        for (int jobId = 0; jobId < problemManager.getN_JOBS(); jobId++) {
            int value = problemManager.getJOB_TOOL_MATRIX()[jobId][this.id];
            if(value ==  1) {
                collect.add(jobId);
            }else{
                collectAnti.add(jobId);
            }
        }

        this.set = collect.stream().mapToInt(i->i).toArray();
        this.antiSet = collectAnti.stream().mapToInt(i->i).toArray();

        //Configure Jobs
        for(int jobId : set) {
            JOBS[jobId] = 1;
        }

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

    public int[] getJOBS() {
        return JOBS;
    }

    public void setJOBS(int[] JOBS) {
        this.JOBS = JOBS;
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
}
