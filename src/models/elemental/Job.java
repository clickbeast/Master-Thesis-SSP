package models.elemental;

import core.ProblemManager;

import java.util.ArrayList;
import java.util.List;

public class Job {

    private ProblemManager problemManager;
    private int id;
    private int[] antiSet;
    private int[] set;



    public Job(int id,ProblemManager problemManager) {
        this.problemManager =  problemManager;
        List<Integer> collect = new ArrayList<>();
        List<Integer> collectAnti = new ArrayList<>();

        for (int i = 0; i < problemManager.getJobToolGrid()[id].length; i++) {
            int value = problemManager.getJobToolGrid()[id][i];
            if(value ==  1) {
                collect.add(i);
            }else{
                collectAnti.add(i);
            }
        }
        this.set = collect.stream().mapToInt(i->i).toArray();
        this.antiSet = collectAnti.stream().mapToInt(i->i).toArray();

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
}
