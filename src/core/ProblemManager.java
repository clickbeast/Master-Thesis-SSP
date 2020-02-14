package core;

import models.elemental.Job;
import org.w3c.dom.ls.LSOutput;

public class ProblemManager {

    private final int MAGAZINE_SIZE;
    private final int N_TOOLS;
    private final int N_JOBS;
    private final int SEED;


    private int[] sequence;
    private Job[] jobs;
    private int[][] jobToolGrid;
    private int[][] result;

    private int currentCost;


    //Build up
    public ProblemManager(int MAGAZINE_SIZE, int N_TOOLS, int N_JOBS, int[][] jobToolGrid) {
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
        this.N_TOOLS = N_TOOLS;
        this.N_JOBS = N_JOBS;
        this.jobToolGrid = jobToolGrid;

        this.SEED = 7;

        this.result  = new int[N_JOBS][N_TOOLS];

        this.initializeJobs();

    }


    public void initializeJobs() {
        Job[] jobs = new Job[N_JOBS];

        for (int i = 0; i < N_JOBS; i++) {
            jobs[i] = new Job(0,this);
        }
        this.jobs = jobs;
    }


    public void initialSolution() {
        this.printGrid(result);

        int cost = 0;

        //Random sequence
        for (int i = 0; i < N_JOBS; i++) {
            Job job = this.getJobs()[i];
        }

    }




    /**
     * Optimize the problem
     */
    public void optimize() {
        System.out.println("cost\tswitches\tmoves\t\ttemperature\t\t\ttimeRemaining\t");
        /*"Steepest descent" +
                "Vervang de huidige oplossing door de beste oplossing uit de omgeving STOP wanneer er in de omgeving " +
                "geen oplossing is die beter is dan de huidige";*/

        boolean RUN = true;
        while(RUN) {
            this.doMove();
            int x = this.evaluate();
        }
    }



    public void doMove() {

    }

    public void decode() {
        //KTNS
    }

    public int evaluate() {
        return 0;
    }


    /* UTILITIES ------------------------------------------------------------------ */

    public void printGrid(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("");
    }

    /* GETTERS & SETTERS ------------------------------------------------------------------ */



    public int getMAGAZINE_SIZE() {
        return MAGAZINE_SIZE;
    }

    public int getN_TOOLS() {
        return N_TOOLS;
    }

    public int getN_JOBS() {
        return N_JOBS;
    }

    public int[] getSequence() {
        return sequence;
    }

    public void setSequence(int[] sequence) {
        this.sequence = sequence;
    }

    public Job[] getJobs() {
        return jobs;
    }

    public void setJobs(Job[] jobs) {
        this.jobs = jobs;
    }

    public int[][] getJobToolGrid() {
        return jobToolGrid;
    }

    public void setJobToolGrid(int[][] jobToolGrid) {
        this.jobToolGrid = jobToolGrid;
    }

    public int[][] getResult() {
        return result;
    }

    public void setResult(int[][] result) {
        this.result = result;
    }
}
