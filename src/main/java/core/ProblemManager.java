package core;

import data_processing.DataProcessing;
import data_processing.InputData;
import data_processing.Logger;
import data_processing.Parameters;
import models.elemental.Job;

import java.io.File;
import java.io.IOException;
import java.util.*;

class Result {
    //The sequence of jobs
    private int[] sequence;
    //The tool matrix
    private int[][] jobToolMatrix;
    //The amount of switches to reach a given position
    private int[] switches;
    //The position of jobs within the sequence
    //FIXME: check if combination with jobs is better
    private int[] jobPosition;
    //the cost of this result
    private double cost;

    private ProblemManager problemManager;

    //Possibility-> use magazine representation
    private int[][] magazineState;

    public Result(ProblemManager problemManager, int[] sequence, int[] jobPosition, int[][] jobToolMatrix) {
        this.problemManager = problemManager;
        this.sequence = sequence;

        this.jobToolMatrix = jobToolMatrix;



    }



    /* DATA MODEL SETUP ------------------------------------------------------------------ */


    public Job getJobSeqPos(int i) {
        return this.problemManager.getJobs()[this.getSequence()[i]];
    }

    public int getSwitchesAtSeqPos(int i) {
        return this.getSwitches()[i];
    }



    /* GETTERS & SETTERS ------------------------------------------------------------------ */


    public int[] getSequence() {
        return sequence;
    }

    public void setSequence(int[] sequence) {
        this.sequence = sequence;
    }

    public int[][] getJobToolMatrix() {
        return jobToolMatrix;
    }

    public void setJobToolMatrix(int[][] jobToolMatrix) {
        this.jobToolMatrix = jobToolMatrix;
    }

    public int[] getSwitches() {
        return switches;
    }

    public void setSwitches(int[] switches) {
        this.switches = switches;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


    @Override
    public String toString() {
        return "Result{" +
                "sequence=" + Arrays.toString(sequence) +
                ", jobToolMatrix=" + Arrays.deepToString(jobToolMatrix) +
                ", switches=" + Arrays.toString(switches) +
                ", cost=" + cost +
                ", magazineState=" + Arrays.deepToString(magazineState) +
                '}';
    }
}

//TODO: CHANGE TO BITMAP VERSIONIONING

public class ProblemManager {

    //PARAMETERS
    private final Parameters parameters;

    private final long TIME_LIMIT;
    private final long RUN_TIME;

    private final int SEED;
    private final double START_TEMP;
    private final double END_TEMP;
    private final double DECAY_RATE;

    String[] logTitles = {"Cost", "Min Cost" , "Moves" , "Temperature", "Time" , "Accepted" , "Sequence"};

    //CONSTANTS
    private final int MAGAZINE_SIZE;
    private final int N_TOOLS;
    private final int N_JOBS;
    private int[][] JOB_TOOL_MATRIX;
    //Amount of different tool loading differences
    private int[][] JOB_JOB_DIFFERENCES;
    //Amount common tools
    private int[][] JOB_JOB_INTERSECTION;

    //MANAGERS
    private MoveManager moveManager;
    private final SolutionManager solutionManager;
    private DataProcessing dataProcessing;


    //VARIABLES
    private Job[] jobs;
    private int currentCost;
    private int minCost;

    private Result currentResult;
    private Result bestResult;


    //UTIL
    private Random random;
    private Logger logger;


    public ProblemManager(InputData inputData) throws IOException {

        //Configure Constants
        this.MAGAZINE_SIZE = inputData.getMAGAZINE_SIZE();
        this.N_TOOLS = inputData.getN_TOOLS();
        this.N_JOBS = inputData.getN_JOBS();
        this.JOB_TOOL_MATRIX = inputData.getJOB_TOOL_MATRIX();

        //Configure Params
        this.SEED = 7;

        this.RUN_TIME = 900;
        this.TIME_LIMIT = System.currentTimeMillis() + 1000 * this.getRUN_TIME();;

        //SA PARAMS
        this.START_TEMP = 270.0;
        this.END_TEMP = 0.000097;
        this.DECAY_RATE = 0.99900;

        this.parameters = inputData.getParameters();

        //Configure managers
        this.moveManager = new MoveManager(this);
        this.solutionManager = new SolutionManager(this);
        this.random = new Random(this.getSEED());
        File logFile = new File(this.parameters.getInstanceFolder() + "/" + "log.csv");
        this.logger = new Logger(this,logFile);
    }

    public void optimize() throws IOException {
        this.logger.logLegend(logTitles);
        this.initialSolution();
        this.steepestDescent();
    }

    /* DATA MODEL SETUP ------------------------------------------------------------------ */


    public void initializeJobs() {
        Job[] jobs = new Job[N_JOBS];

        for (int i = 0; i < N_JOBS; i++) {
            Job job = new Job(i,this);
            jobs[i] = job;
        }
        this.jobs = jobs;
    }

    public void initializeTools() {


    }


    //TODO: initializeDifferenceMatrix
    public int[][] initializeDifferenceMatrix() {

        return null;
    }


    //TODO: initializeSharedToolsMatrix
    public int[][] initializeSharedToolsMatrix() {

        return null;
    }


    //TODO: initializeSwitchesLowerBoundMatrix
    public int[][] initializeSwitchesLowerBoundMatrix() {

        return null;
    }

    //TODO: initializeToolPairMatrix
    public int[][] initializeToolPairCountMatrix() {


        return null;

    }


    /* INITIAL SOLUTION ------------------------------------------------------------------ */

    public void initialSolution() {
        this.logger.logInfo("Creating initial solution");


        int[] sequence = this.orderedInitialSequence();
        int[][] jobToolMatrix = this.copyGrid(this.JOB_TOOL_MATRIX);

        //TODO: start using the job position...
        //int[] jobPosition = Arrays.copyOf(sequence,sequence.length);
        int[] switches = this.calculateSwitches(sequence,jobToolMatrix);
        int cost = this.evaluate(sequence, jobToolMatrix, switches);

        this.logger.logInfo("Initial Solution Created");
    }

    public int[] orderedInitialSequence() {
        int[] sequence = new int[this.N_JOBS];
        //Setup sequence
        for (int i = 0; i < N_JOBS; i++) {
            Job job = this.getJobs()[i];
            sequence[i] = job.getId();
            job.setPosition(i);
        }
        return sequence;
    }

    //https://www.journaldev.com/32661/shuffle-array-java
    public int[] randomInitialSequence(int[] sequence) {
        Random rand = this.getRandom();

        for (int i = 0; i < sequence.length; i++) {
            int randomIndexToSwap = rand.nextInt(sequence.length);
            int temp = sequence[randomIndexToSwap];
            sequence[randomIndexToSwap] = sequence[i];
            sequence[i] = temp;
        }

        return sequence;
    }

    /**
     * Gustavo Silva Paiva, et al.
     */
    //TODO : toolJobRelationBFSInitialSequence
    public void toolJobRelationBFSInitialSequence() {

    }

    /**
     * Tang and Denardo
     */
    //TODO: TSPInitialSequence
    public void TSPInitialSequence() {

    }



    /* LS ------------------------------------------------------------------ */

    //Best Improvement
    public void steepestDescent() {
        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {
            if(this.currentResult.getCost() >= this.bestResult.getCost()) {
                this.bestResult = this.currentResult;
                Result steepestBest = this.currentResult;

                for (int i = 0; i < this.currentResult.getSequence().length; i++) {
                    for (int j = i + 1 ; j < this.currentResult.getSequence().length; j++) {

                    }
                }

            }else{
                logger.logInfo("local min reached");
                break;
            }
        }

    }


    //Best Improvement
    public void steepestDescentTournament() {

    }


    //First improvement
    public void hillClimbing() {
        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {
            if(this.currentResult.getCost() >= this.bestResult.getCost()) {
                this.bestResult = this.currentResult;

                for (int i = 0; i < this.currentResult.getSequence().length; i++) {
                    for (int j = i + 1 ; j < this.currentResult.getSequence().length; j++) {
                        continue;
                    }
                }

            }else{
                logger.logInfo("local min reached");
                break;
            }
        }
    }

    //First improvement
    public void hillClimbingTournament() {


    }

    //SA
    public void simulatedAnnealing() {

        double temperature = this.START_TEMP;
        int j = 0;
        int steps = 0;
        long accepted = 0;
        int steady = 0;



        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {
            //
            //this.moveManager.doMove();

            int deltaE = (int) (this.bestResult.getCost() - this.currentResult.getCost());

            if(deltaE > 0) {
                double acceptance = Math.exp(-deltaE/ temperature);
                double ran = random.nextDouble();
                if (acceptance > ran) {
                    //accept move
                }else{
                    //cancel move
                }
            }else{


            }

            //Keep temperature steady for a few steps before dropping
            if(steady > 50000) {
                temperature = temperature * DECAY_RATE;
                steady=0;
            }
            steady++;


            //Reheating
            if (temperature < 1.5) {
                //temperature = 10.0 + random.nextDouble() * 40;
            }

            //LOGGING
            if (steps % 100000 == 0) {
                long remaining = (this.getTIME_LIMIT() - System.currentTimeMillis());
            }


            steps++;
        }


    }


    /* EVALUATION ------------------------------------------------------------------ */


    public int[][] decode(int[] sequence) {
        ArrayList<LinkedList<Integer>> toolPrioritySequence = determineToolPriority(sequence);
        int[][] augmentedJobToolMatrix = new int[this.getN_JOBS()][this.getN_TOOLS()];

        int M = this.MAGAZINE_SIZE;


        //Set tools
        int[] prev = new int[this.getN_TOOLS()];

        //Remove unwanted tools tools
        for (int i = 0; i < sequence.length; i++) {


            //set tools
            int numberOfToolsSet = 0;
            for (int j = 0; j < this.N_TOOLS; j++) {
                augmentedJobToolMatrix[i][j] = this.getJOB_TOOL_MATRIX()[i][j];
                if (prev[j] == 1 || this.getJOB_TOOL_MATRIX()[i][j] == 1) {
                    augmentedJobToolMatrix[i][j] = 1;
                    numberOfToolsSet += 1;
                }
            }



            int numberOfToolsToRemove = Math.abs(numberOfToolsSet - getMAGAZINE_SIZE());

            LinkedList<Integer> toolPriority = toolPrioritySequence.get(i);
            //remove unwanted tools
            for (int j = 0; j < numberOfToolsToRemove; j++) {
                while(true) {
                    int toolId = toolPriority.removeLast();

                    if(augmentedJobToolMatrix[i][toolId] == 1) {
                        augmentedJobToolMatrix[i][toolId] = 0;
                        break;
                    }
                }
            }

        }


        return augmentedJobToolMatrix;
    }


    //TODO:
    public ArrayList<LinkedList<Integer>> determineToolPriority(int[] sequence) {
        ArrayList<LinkedList<Integer>> toolPrioritySequence = new ArrayList<>(sequence.length);
        int[] visited = new int[this.getN_TOOLS()];

        for(int i = 0; i < sequence.length; i++) {
            int jobId = sequence[i];
            LinkedList<Integer> toolPriority = new LinkedList<>();
            for (int j = 0; j < sequence.length; j++) {
                for (int k = 0; k < this.getN_TOOLS(); k++) {
                    if(visited[k] == 0 && this.getJOB_TOOL_MATRIX()[jobId][k] == 0 && getJOB_TOOL_MATRIX()[j][k] == 1){
                        toolPriority.add(k);
                        visited[k] = 1;
                    }
                }
            }
            toolPrioritySequence.add(toolPriority);
        }

        return toolPrioritySequence;
    }




    public int[] calculateSwitches(int[] sequence, int[][] jobToolMatrix) {

        //TODO: countSwitches : see if combination with KTNS is possible
        int[] switches = new int[sequence.length];
        //Inserstions
        int insertionCount = 0;
        for (int i = 0; i < jobToolMatrix[0].length; i++) {
            if (jobToolMatrix[0][i] == 1) {
                insertionCount += 1;
            }
        }

        switches[0] = insertionCount;

        //CHECK: a job switch is counted when one is SETUP, not when it is removed
        //Or differently removing a tool is considered part of the setup process.

        for (int i = 1; i < sequence.length; i++) {
            int swapCount = 0;
            int[] previous = jobToolMatrix[i-1];
            int[] current = jobToolMatrix[i];
            for (int j = 0; j < current.length; j++) {
                if (previous[j] != current[j]) {
                    swapCount+=1;
                }
            }
            switches[i] = swapCount;
        }

        return switches;
    }

    //TODO: can be made much better
    public int evaluate(int[] sequence, int[][] jobToolMatrix, int[] switches) {
        int count = 0;
        for (int i = 0; i < switches.length; i++) {
            count+= switches[i];
        }
        return count;
    }


    //TODO: countSwitchesBitVector
    public int[] countSwitchesBitVector(int[] sequence, int[][] jobToolMatrix) {
        return null;
    }



    /* UTILITIES ------------------------------------------------------------------ */

    public int[][] copyGrid(int[][] grid) {
        int[][] gridCopy = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, gridCopy[i], 0, grid[i].length);
        }
        return gridCopy;
    }


    /* GETTERS & SETTERS ------------------------------------------------------------------ */

    public long getTIME_LIMIT() {
        return TIME_LIMIT;
    }

    public long getRUN_TIME() {
        return RUN_TIME;
    }

    public int getSEED() {
        return SEED;
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

    public int getMAGAZINE_SIZE() {
        return MAGAZINE_SIZE;
    }

    public int getN_TOOLS() {
        return N_TOOLS;
    }

    public int getN_JOBS() {
        return N_JOBS;
    }

    public int[][] getJOB_TOOL_MATRIX() {
        return JOB_TOOL_MATRIX;
    }

    public void setJOB_TOOL_MATRIX(int[][] JOB_TOOL_MATRIX) {
        this.JOB_TOOL_MATRIX = JOB_TOOL_MATRIX;
    }

    public int[][] getJOB_JOB_DIFFERENCES() {
        return JOB_JOB_DIFFERENCES;
    }

    public void setJOB_JOB_DIFFERENCES(int[][] JOB_JOB_DIFFERENCES) {
        this.JOB_JOB_DIFFERENCES = JOB_JOB_DIFFERENCES;
    }

    public int[][] getJOB_JOB_INTERSECTION() {
        return JOB_JOB_INTERSECTION;
    }

    public void setJOB_JOB_INTERSECTION(int[][] JOB_JOB_INTERSECTION) {
        this.JOB_JOB_INTERSECTION = JOB_JOB_INTERSECTION;
    }

    public MoveManager getMoveManager() {
        return moveManager;
    }

    public void setMoveManager(MoveManager moveManager) {
        this.moveManager = moveManager;
    }

    public SolutionManager getSolutionManager() {
        return solutionManager;
    }

    public DataProcessing getDataProcessing() {
        return dataProcessing;
    }

    public void setDataProcessing(DataProcessing dataProcessing) {
        this.dataProcessing = dataProcessing;
    }


    public Job[] getJobs() {
        return jobs;
    }

    public void setJobs(Job[] jobs) {
        this.jobs = jobs;
    }

    public int getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(int currentCost) {
        this.currentCost = currentCost;
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        this.minCost = minCost;
    }

    public Result getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(Result currentResult) {
        this.currentResult = currentResult;
    }

    public Result getBestResult() {
        return bestResult;
    }

    public void setBestResult(Result bestResult) {
        this.bestResult = bestResult;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
