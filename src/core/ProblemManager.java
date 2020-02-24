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
    private int MAX_TOOLS_JOB_ID;


    //Build up
    public ProblemManager(int MAGAZINE_SIZE, int N_TOOLS, int N_JOBS, int[][] JOB_TOOL_MATRIX) {
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
        this.N_TOOLS = N_TOOLS;
        this.N_JOBS = N_JOBS;
        this.JOB_TOOL_MATRIX = JOB_TOOL_MATRIX;


        this.currentCost = Integer.MAX_VALUE;

        this.MAX_TOOLS_JOB = Integer.MIN_VALUE;

        this.SEED = 7;
        this.TIME_LIMIT = 600;

        this.START_TEMP = 330.0;
        this.END_TEMP = 0.0097;
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
        this.STD_SWITCHES = new int[N_JOBS][N_JOBS];
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

        this.currentCost = decodeToolsAndSwitches();
        printResult();
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

    /**
     * Merges the tools needed and the extra selected tools to minimize tool switches
     */
    public void finalizeResult() {
        for (int i = 0; i < N_JOBS; i++) {
            for (int j = 0; j < N_TOOLS; j++) {
                if(JOB_TOOL_MATRIX[i][j] == 1) {
                    this.result[i][j] = 1;
                }
            }
        }
    }

    public void printResult() {

        System.out.println("switches: " +  this.currentCost);
        this.finalizeResult();
        printSolution(this.JOB_TOOL_MATRIX);
        System.out.println();
        System.out.println();
        printSolution(this.result);
    }

    /**
     * Optimize the problem
     */
    public void optimize() {
        System.out.println("cost\tswitches\tmoves\t\ttemperature\t\t\ttimeRemaining\t");

        double temperature = this.START_TEMP;
        int j = 0;
        int steps = 0;
        long timeLimit = System.currentTimeMillis() + 1000 * TIME_LIMIT;

        while (true) {
            //Perform a move
            this.moveManager.doMove();
            //Clear switch count
            this.result = new int[N_JOBS][N_TOOLS];
            //decode to determine tools needed and switches needed
            int cost = this.decodeToolsAndSwitches();


            if(cost >= currentCost) {
                //ACCEPT to certain degree
                if (Math.exp((cost - currentCost) / temperature) < random.nextDouble()) {
                    moveManager.acceptMove();
                    this.currentCost = cost;

                }else{
                    moveManager.cancelMove();
                }
            }else{
                moveManager.acceptMove();
                this.currentCost = cost;
            }

            //Keep temperature for a few steps before dropping
            if(j > 1000) {
                temperature = temperature * DECAY_RATE;
                j=0;
            }
            j++;


            //Reheating
            if (temperature < 1.5) {
                temperature = 10.0 + random.nextDouble() * 40;
            }


            if (true) {
                System.out.println(this.currentCost + "\t" + steps + "\t\t\t" + steps
                        + "\t\t" + temperature + "\t" + (timeLimit - System.currentTimeMillis()) + "\t\t\t\t");
                //LOG SOLUTION
                //printResult();
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
            System.out.print("\t" + this.jobs[i].getSwitches());
            System.out.println("");
        }
        System.out.println("");
        System.out.println("");
    }



    public Job getJobSeqPos(int i) {
        return this.jobs[sequence[i]];
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
}
