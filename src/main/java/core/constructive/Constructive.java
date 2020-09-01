package core.constructive;

import core.ProblemManager;

public abstract class Constructive {

    private ProblemManager problemManager;

    public Constructive(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }
    public void constructSolution() {
        System.out.println("nothing");
    }

}
