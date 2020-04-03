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
        this.solutions = new LinkedList<>();
        this.fileName = "data/result.txt";
        File file = new File(this.fileName);
        try {
            FileWriter writer = new FileWriter(file,false);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*

    public Solution saveSolution() {
        int[] sequence = this.problemManager.getSequence().clone();
        int cost = this.problemManager.getCurrentCost();
        int[][] result = new int[this.problemManager.getN_JOBS()][this.problemManager.getN_TOOLS()];
        int[][] ktns = new int[this.problemManager.getN_JOBS()][this.problemManager.getN_TOOLS()];
        int[] switches = this.problemManager.getSwitches().clone();

        this.problemManager.copyGrid(this.problemManager.getResult(),result);
        this.problemManager.copyGrid(this.problemManager.getKtns(),ktns);

        //LEGACY
        */
/*//*
/Determine number of switches between...
        for (int i = 0; i < sequence.length; i++) {
            switches[i] = this.problemManager.getSwitchesAtSeqPos(i);
        }*//*


        Solution solution = new Solution(cost, sequence, result, ktns, switches);

        this.solutions.add(solution);
        return solution;
    }
*/

    public void writeSolution(Solution solution) throws IOException {
        // Creating a File object that represents the disk file.
        PrintStream o = new PrintStream(new PrintStream(new FileOutputStream(this.fileName, true)));
        printSolution(o, solution);
        o.close();
    }

    public void printSolution(PrintStream printStream, Solution solution)  {
        PrintStream console = System.out;
        System.setOut(printStream);

        //TODO
        int[][] result = solution.getResult();
        int[][] ktns = solution.getKtns();
        int[] sequence = solution.getSequence();
        int[] switches = solution.getSwitches();

        for (int i = 0; i < sequence.length; i++) {
            int jobId = sequence[i];
            System.out.print(jobId + "\t");

            for (int j = 0; j < result[jobId].length; j++) {
                System.out.print(result[jobId][j] + " ");
            }

            System.out.print("\t" + switches[i]);
            System.out.println("");
        }

        System.out.print(" \t");
        for (int j = 0; j < result[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + "---");
        System.out.println("");

        System.out.print(" \t");

        for (int j = 0; j < result[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + solution.getCost());
        System.out.println("");
        System.out.println("");
        System.out.println("");
        //
        //
        

        for (int i = 0; i < ktns.length; i++) {
            int jobId = sequence[i];
            System.out.print(jobId + "\t");

            for (int j = 0; j < ktns[jobId].length; j++) {
                System.out.print(ktns[jobId][j] + " ");
            }

            System.out.print("\t" + "/");
            System.out.println("");
        }

        System.out.print(" \t");
        for (int j = 0; j < ktns[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + "---");
        System.out.println("");

        System.out.print(" \t");

        for (int j = 0; j < ktns[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + "/");
        System.out.println("");




        System.out.println("");

        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");

        System.out.println("");

        System.setOut(console);
    }

}
