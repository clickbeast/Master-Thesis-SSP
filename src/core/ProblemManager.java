package core;

import models.Jobs;
import models.elemental.Job;

public class ProblemManager {

    private final int MAGAZINE_SIZE;
    private final int NUMBER_TOOLS;
    private final int NUMBER_JOBS;

    private int[] sequence;
    private Job[] jobs;
    private int[][] jobToolGrid;
    private int[][] result;

    //Build up
    public ProblemManager( int MAGAZINE_SIZE, int NUMBER_TOOLS, int NUMBER_JOBS, int[][] jobToolGrid) {
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
        this.NUMBER_TOOLS = NUMBER_TOOLS;
        this.NUMBER_JOBS = NUMBER_JOBS;
        this.jobToolGrid = jobToolGrid;


        //Solustion
        this.result  = new int[NUMBER_JOBS][NUMBER_TOOLS];
        this.initializeJobs();

    }


    public void initializeJobs() {
        Job[] jobs = new Job[NUMBER_JOBS];

        for (int i = 0; i < NUMBER_JOBS; i++) {
            jobs[i] = new Job(0,this);
        }

        this.jobs = jobs;
    }


    public void initialSolution() {

        for (int i = 0; i < NUMBER_JOBS; i++) {
            for (int j = 0; j < NUMBER_TOOLS; j++) {
                if(jobToolGrid[i][j] == 1 ) {

                }
            }

        }

        return;
    }

    /**
     * Optimize the problem
     */
    public void optimize() {
        System.out.println("cost\tswitches\tmoves\t\ttemperature\t\t\ttimeRemaining\t");
        /*"Steepest descent" +
                "Vervang de huidige oplossing door de beste oplossing uit de omgeving STOP wanneer er in de omgeving " +
                "geen oplossing is die beter is dan de huidige";*/


    }


    public void decode() {
        //KTNS
    }

    public int evaluate() {
        return 0;
    }


    public int getMAGAZINE_SIZE() {
        return MAGAZINE_SIZE;
    }

    public int getNUMBER_TOOLS() {
        return NUMBER_TOOLS;
    }

    public int getNUMBER_JOBS() {
        return NUMBER_JOBS;
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
