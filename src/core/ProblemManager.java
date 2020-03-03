package core;

import data_processing.DataProcessing;
import models.elemental.Job;

import java.util.Arrays;
import java.util.Random;

public class ProblemManager {

    //SA PARAMATERS
    private final long TIME_LIMIT;
    private final double START_TEMP;
    private final double END_TEMP;
    private final double DECAY_RATE;
    private Random random;

    //MANAGERS
    private MoveManager moveManager;
    private DataProcessing dataProcessing;

    //PARAMETERS
    private final int MAGAZINE_SIZE;
    private final int N_TOOLS;
    private final int N_JOBS;
    private final int SEED;

    //CONSTANTS
    private int[][] STD_SWITCHES;
    private int[][] JOB_TOOL_MATRIX;
    private int MAX_TOOLS_JOB;
    private int[] EQUALIZE;


    //VARIABLES
    private int[] sequence;
    private Job[] jobs;
    private int[][] result;

    private int currentCost;
    private int minCost;
    private int MAX_TOOLS_JOB_ID;


    //Build up
    public ProblemManager(int MAGAZINE_SIZE, int N_TOOLS, int N_JOBS, int[][] JOB_TOOL_MATRIX) {
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
        this.N_TOOLS = N_TOOLS;
        this.N_JOBS = N_JOBS;
        this.JOB_TOOL_MATRIX = JOB_TOOL_MATRIX;


        this.currentCost = Integer.MAX_VALUE;
        this.minCost = Integer.MAX_VALUE;

        this.MAX_TOOLS_JOB = Integer.MIN_VALUE;

        this.SEED = 7;
        this.TIME_LIMIT = 600;

        this.START_TEMP = 200.0;
        this.END_TEMP = 0.000097;
        this.DECAY_RATE = 0.9997;

        this.random = new Random(this.SEED);

        this.result  = new int[N_JOBS][N_TOOLS];
        this.sequence = new int[N_JOBS];


        this.initializeJobs();
        this.initializeDifferenceMatrix();
        System.out.println("difference");
        printGrid(this.getSTD_SWITCHES());
        this.moveManager = new MoveManager(this);

        System.out.println(Arrays.toString(jobs));
    }

    public void initializeJobs() {
        Job[] jobs = new Job[N_JOBS];

        for (int i = 0; i < N_JOBS; i++) {
            Job job = new Job(i,this);
            jobs[i] = job;
            this.MAX_TOOLS_JOB = Math.max(job.getSet().length, this.MAX_TOOLS_JOB);
        }
        this.jobs = jobs;
    }


    public void initializeDifferenceMatrix() {

       //TODO: make more efficient

        /*this.STD_SWITCHES = new int[N_JOBS][N_JOBS];
        int f = 1;
        for (int i = 0; i < N_JOBS; i++) {
            for (int j = f; j < N_JOBS; j++) {

                int k = 0;

                for (int l = 0; l < N_TOOLS; l++) {
                    if(this.JOB_TOOL_MATRIX[i][l] != this.JOB_TOOL_MATRIX[j][l]) {
                        k++;
                    }
                }

                this.STD_SWITCHES[i][j] = k;
                this.STD_SWITCHES[j][i] = k;
            }
            f++;
        }*/

        this.STD_SWITCHES = new int[N_JOBS][N_JOBS];
        for (int i = 0; i < N_JOBS; i++) {
            for (int j = 0; j < N_JOBS; j++) {

                int switches = 0;
                for (int t = 0; t < N_TOOLS; t++) {
                    if(this.JOB_TOOL_MATRIX[i][t] != this.JOB_TOOL_MATRIX[j][t]) {
                        switches += 1;
                    }
                }

                this.STD_SWITCHES[i][j] = switches;
            }
        }
    }

    public void initialSolution() {
        System.out.println("Initial solution:");
        this.printGrid(result);

        //Setup sequence
        for (int i = 0; i < N_JOBS; i++) {
            Job job = this.getJobs()[i];
            this.sequence[i] = job.getId();
            job.setPosition(i);
        }

        System.out.println(Arrays.toString(sequence));

        this.currentCost = calculateSwitches();
        this.copyGrid(this.JOB_TOOL_MATRIX,this.result);
        this.printGrid(result);
    }

    public int calculateSwitches() {
        int switches = 0;
        for (int i = 0; i < sequence.length; i++) {
            switches = switches + this.getSwitchesAtSeqPos(i);
        }
        return switches;
    }

    public int decodeToolsAndSwitches() {

        //TODO: check me

        //OPT: do not recalculate all when unneeded
        //OPT: perform KTNS with flow network
        //OPT: collect more side info
        int switches = 0;

        //PERFORM KTNS + DETERMINE SWITCHES
        for (int i = 0; i < N_JOBS; i++) {
            Job job = this.getJobSeqPos(i);
            //calculate amount of switches
            if(i != 0) {
                //NOTE : wat als je kiest om er toch te houden die wel in de set zitten van deze ma in de antiset van den andere
                job.setSwitches(this.STD_SWITCHES[job.getId()][job.prevJob().getId()]
                        - job.prevJob().getPickedToolsNextJobCount());
                switches += job.getSwitches();
            }

            int m = MAGAZINE_SIZE - job.getSet().length;
            int c = 1;
            int nextFirstJobChosenAntiCount  = 0;
            //fill the remainder of the places in the magazine (KTNS)
            Job nextJob =  job.nextJob();
            m_fill : while(m > 0 && nextJob != null) {
                for (int k = 0; k < job.getAntiSet().length; k++) {
                    if(this.JOB_TOOL_MATRIX[nextJob.getId()][job.getAntiSet()[k]] == 1) {
                        //enable the tool for this job
                        result[job.getId()][job.getAntiSet()[k]] = 1;
                        //calculate how many stay present
                        nextFirstJobChosenAntiCount = nextFirstJobChosenAntiCount +  c;
                        m--;
                        //continue m_fill;
                    }
                }
                job.setNextFirstJobChosenAntiCount(nextFirstJobChosenAntiCount);
                c=0;
                nextFirstJobChosenAntiCount = 0;
                nextJob =  nextJob.nextJob();
            }
        }

        //CALCULATE SWITCHES SEPPERATETLY
        return switches;
    }



/*
    */
/**
     * Merges the tools needed and the extra selected tools to minimize tool switches
     *//*

    public void finalizeResult() {
        for (int i = 0; i < N_JOBS; i++) {
            for (int j = 0; j < N_TOOLS; j++) {
                if(JOB_TOOL_MATRIX[i][j] == 1) {
                    this.result[i][j] = 1;
                }
            }
        }
    }
*/
/*
    public void printResult() {
        this.finalizeResult();
        this.printFinalSolution();
    }*/



    /**
     * Optimize the problem
     */
    public void optimize() {
    System.out.printf("%-10s %-10s %-10s %-20s %-10s %-10s %-10s \n", "Cost", "Min Cost" , "Moves" , "Temperature", "Time" , "Accepted" , "Sequence");

        //SIMILARITY SCORING FUNCTION
        double temperature = this.START_TEMP;
        int j = 0;
        int steps = 0;
        long accepted = 0;
        long timeLimit = System.currentTimeMillis() + 1000 * TIME_LIMIT;
        while (true) {
            this.moveManager.doMove();
            int cost = this.calculateSwitches();

            int deltaE = cost - this.currentCost;

            if(deltaE > 0) {
                double acceptance = Math.exp(-deltaE/ temperature);
                double ran = random.nextDouble();
                if (acceptance < ran) {
                    moveManager.acceptMove();
                    accepted+=1;
                    this.currentCost = cost;
                }else{
                    moveManager.cancelMove();
                }
            }else{
                this.currentCost = cost;
                this.minCost = Math.min(this.minCost,this.currentCost);
                moveManager.acceptMove();
            }

            //Keep temperature steady for a few steps before dropping
            if(j > 1000) {
                temperature = temperature * DECAY_RATE;
                j=0;
            }
            j++;


            //Reheating
            if (temperature < 1.5) {
                temperature = 10.0 + random.nextDouble() * 40;
            }

            //PROBLEM: it prints out the current one but it prints a worked on solution
            //REWIND NOT WORKING CORRECTLY
            if (steps % 100000 == 0) {
                long remaining = (timeLimit - System.currentTimeMillis());
                System.out.printf("%-10s %-10s %-10s %-20s %-10s %-10s %-10s \n", this.currentCost,this.minCost, steps, temperature, remaining, accepted,  Arrays.toString(sequence));
                accepted = 0;
                //this.printFinalSolution();

            }

            if(steps % 100000 == 0) {
                //this.printFinalSolution();
            }
            steps++;
        }

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

    public void printSolution(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }

            System.out.print("\t" + this.getSwitchesAtSeqPos(i));

            System.out.println("");
        }

        for (int j = 0; j < grid[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + "---");
        System.out.println("");


        for (int j = 0; j < grid[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + this.currentCost);
        System.out.println("");



        System.out.println("");
        System.out.println("");
    }


    public void printFinalSolution() {

        for (int i = 0; i < sequence.length; i++) {
            int jobId = sequence[i];
            System.out.print(jobId + "\t");

            for (int j = 0; j < result[jobId].length; j++) {
                System.out.print(result[jobId][j] + " ");
            }

            System.out.print("\t" + this.getSwitchesAtSeqPos(i));
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
        System.out.print("\t" + this.currentCost);
        System.out.println("");

        System.out.println("");
        System.out.println("");
    }



    public int getSwitchesAtSeqPos(int i) {
        if(i == 0) {
            return 0;
        }
        Job job = this.getJobSeqPos(i);
        return this.STD_SWITCHES[job.prevJob().getId()][job.getId()];
    }

    public Job getJobSeqPos(int i) {
        return this.jobs[sequence[i]];
    }


    public void copyGrid(int[][] from, int[][] to) {
        for (int i = 0; i < from.length; i++) {
            for (int j = 0; j < from[i].length; j++) {
                to[i][j] = from[i][j];
            }
        }
    }

    /* GETTERS & SETTERS ------------------------------------------------------------------ */


    public long getTIME_LIMIT() {
        return TIME_LIMIT;
    }

    public double getSTART_TEMP() {
        return START_TEMP;
    }

    public double getEND_TEMP() {
        return END_TEMP;
    }

    public double getDECAY_RATE() {
        return DECAY_RATE;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public MoveManager getMoveManager() {
        return moveManager;
    }

    public void setMoveManager(MoveManager moveManager) {
        this.moveManager = moveManager;
    }

    public DataProcessing getDataProcessing() {
        return dataProcessing;
    }

    public void setDataProcessing(DataProcessing dataProcessing) {
        this.dataProcessing = dataProcessing;
    }

    public int getSEED() {
        return SEED;
    }

    public int[][] getSTD_SWITCHES() {
        return STD_SWITCHES;
    }

    public void setSTD_SWITCHES(int[][] STD_SWITCHES) {
        this.STD_SWITCHES = STD_SWITCHES;
    }

    public int getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(int currentCost) {
        this.currentCost = currentCost;
    }

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

    public int[][] getJOB_TOOL_MATRIX() {
        return JOB_TOOL_MATRIX;
    }

    public void setJOB_TOOL_MATRIX(int[][] JOB_TOOL_MATRIX) {
        this.JOB_TOOL_MATRIX = JOB_TOOL_MATRIX;
    }

    public int[][] getResult() {
        return result;
    }

    public void setResult(int[][] result) {
        this.result = result;
    }

    public int getMAX_TOOLS_JOB() {
        return MAX_TOOLS_JOB;
    }

    public void setMAX_TOOLS_JOB(int MAX_TOOLS_JOB) {
        this.MAX_TOOLS_JOB = MAX_TOOLS_JOB;
    }

    public int[] getEQUALIZE() {
        return EQUALIZE;
    }

    public void setEQUALIZE(int[] EQUALIZE) {
        this.EQUALIZE = EQUALIZE;
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        this.minCost = minCost;
    }

    public int getMAX_TOOLS_JOB_ID() {
        return MAX_TOOLS_JOB_ID;
    }

    public void setMAX_TOOLS_JOB_ID(int MAX_TOOLS_JOB_ID) {
        this.MAX_TOOLS_JOB_ID = MAX_TOOLS_JOB_ID;
    }
}
