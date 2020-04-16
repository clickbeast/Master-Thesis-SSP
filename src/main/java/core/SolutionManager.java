package core;

import models.Solution;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SolutionManager {

    private ProblemManager problemManager;
    private List<Solution>  solutions;
    private String fileName;

    public SolutionManager(ProblemManager problemManager) {
        this.problemManager = problemManager;


    }
}
