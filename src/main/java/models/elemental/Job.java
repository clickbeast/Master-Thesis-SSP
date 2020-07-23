package models.elemental;

import core.ProblemManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Job {

    private ProblemManager problemManager;

    //STATIC
    private int id;
    private int[] TOOLS;
    private int[] antiSet;
    private int[] set;

    //HELP -> adjust to better structure than adjency

    //CALCULATION
    private int position;
    private int switches;
    private int nextFirstJobChosenAntiCount;
    private int[] result;



    /* INITIALIZATION ------------------------------------------------------------------ */

    public Job(int id, ProblemManager problemManager) {
        this.problemManager =  problemManager;
        List<Integer> collect = new ArrayList<>();
        List<Integer> collectAnti = new ArrayList<>();

        this.TOOLS = problemManager.getJOB_TOOL_MATRIX()[id];
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
    }



    /* GRAPH ------------------------------------------------------------------ */


    /* GETTERS & SETTERS ------------------------------------------------------------------ */

    public int[] getTOOLS() {
        return TOOLS;
    }

    public void setTOOLS(int[] TOOLS) {
        this.TOOLS = TOOLS;
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
                ", tools=" + Arrays.toString(TOOLS) +
                ", antiSet=" + Arrays.toString(antiSet) +
                ", set=" + Arrays.toString(set) +
                '}';
    }
}
