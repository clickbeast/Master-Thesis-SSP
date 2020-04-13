package core;

import data_processing.DataProcessing;
import data_processing.InputData;
import data_processing.Logger;
import data_processing.Parameters;
import fastcsv.writer.CsvAppender;
import models.elemental.Job;
import models.elemental.Tool;
import util.General;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    String[] logTitles = {"Switches", "Best Switches", "Rem Dist" , "Best Rem Dist" , "Accepted", "Rejected" , "Improved", "Step" , "Time Remaining" , "Sequence"};

    //CONSTANTS
    private final int MAGAZINE_SIZE;
    private final int N_TOOLS;
    private final int N_JOBS;
    private int[][] JOB_TOOL_MATRIX;
    //Amount of different tool loading differences
    private int[][] DIFFERENCE_MATRIX;
    //Amount common tools
    private int[][] SHARED_TOOLS_MATRIX;
    private int[][] SWITCHES_LB_MATRIX;
    private int[][] TOOL_PAIR_COUNT_MATRIX;

    //MANAGERS
    private MoveManager moveManager;
    private final SolutionManager solutionManager;
    private DataProcessing dataProcessing;


    //VARIABLES
    private Job[] jobs;
    private Tool[] tools;


    private Result workingResult;
    private Result currentResult;
    private Result bestResult;


    //UTIL
    private Random random;
    private Logger logger;
    private long steps;


    public ProblemManager(InputData inputData) throws IOException {
        //Configure Constants
        this.MAGAZINE_SIZE = inputData.getMAGAZINE_SIZE();
        this.N_TOOLS = inputData.getN_TOOLS();
        this.N_JOBS = inputData.getN_JOBS();
        this.JOB_TOOL_MATRIX = inputData.getJOB_TOOL_MATRIX();

        this.parameters = inputData.getParameters();


        //Configure Params
        this.SEED = this.getParameters().getSEED();
        this.RUN_TIME = this.getParameters().getRUN_TIME();
        this.TIME_LIMIT = System.currentTimeMillis() + 1000 * this.getParameters().getRUN_TIME();

        this.random = new Random(this.getSEED());

        //SA PARAMSà
        this.START_TEMP = this.getParameters().getSTART_TEMP();
        this.END_TEMP = this.getParameters().getEND_TEMP();
        this.DECAY_RATE = this.getParameters().getDECAY_RATE();

        this.random = new Random(this.getSEED());

        this.moveManager = new MoveManager(this);
        this.solutionManager = new SolutionManager(this);

        this.steps = 0;
        this.logger = new Logger(this);

    }

    public void optimize() {
        try(CsvAppender csvAppender = this.logger.getCsvWriter().append(this.logger.getLogFile(), StandardCharsets.UTF_8)) {
            this.logger.setCsvAppender(csvAppender);
            this.logger.logLegend(logTitles);
            this.initialize();
            //this.initialSolution();
            this.initialOrderedSolution();
            General.printGrid(this.currentResult.getJobToolMatrix());
            //this.steepestDescent();

            //this.hillClimbing();
            this.simulatedAnnealing();
        }catch(IOException io) {
        }
    }

    public void initialize(){
        this.jobs = this.initializeJobs();
        this.initializeTools();
        this.DIFFERENCE_MATRIX = this.initializeDifferenceMatrix();
        this.SHARED_TOOLS_MATRIX = initializeSharedToolsMatrix();
        this.SWITCHES_LB_MATRIX = this.initializeSwitchesLowerBoundMatrix();
        this.TOOL_PAIR_COUNT_MATRIX = this.initializeToolPairCountMatrix();
    }

    /* DATA MODEL SETUP ------------------------------------------------------------------ */


    public Job[] initializeJobs() {
        Job[] jobs = new Job[N_JOBS];

        for (int i = 0; i < N_JOBS; i++) {
            Job job = new Job(i,this);
            jobs[i] = job;
        }
        return jobs;
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

    public void initialOrderedSolution() throws IOException {
        this.logger.logInfo("Creating initial solution");

        int[] sequence = this.orderedInitialSequence();
        //sequence = this.randomInitialSequence(sequence);
        int[][] jobToolMatrix = this.decode(sequence);
        //int[][] jobToolMatrix = this.copyGrid(this.getJOB_TOOL_MATRIX());

        //TODO: start using the job position...
        //int[] jobPosition = Arrays.copyOf(sequence,sequence.length);
        int[] switches = this.calculateSwitches(sequence,jobToolMatrix);
        int cost = this.evaluate(sequence, jobToolMatrix, switches);

        this.currentResult = new Result(sequence);
        this.currentResult.setCost(cost);
        this.currentResult.setJobToolMatrix(jobToolMatrix);
        this.currentResult.setSwitches(switches);


        this.workingResult = this.currentResult.getCopy();
        this.bestResult = this.currentResult.getCopy();

        this.logger.logInfo(String.valueOf(cost));
        this.logger.logInfo("Initial Solution Created");

        this.logger.log(cost, cost, sequence);
    }

    public void initialRandomSolution() throws IOException {
        this.logger.logInfo("Creating initial solution");

        int[] sequence = this.orderedInitialSequence();
        sequence = this.randomInitialSequence(sequence);
        //sequence = this.randomInitialSequence(sequence);
        int[][] jobToolMatrix = this.decode(sequence);
        //int[][] jobToolMatrix = this.copyGrid(this.getJOB_TOOL_MATRIX());

        //TODO: start using the job position...
        //int[] jobPosition = Arrays.copyOf(sequence,sequence.length);
        int[] switches = this.calculateSwitches(sequence,jobToolMatrix);
        int cost = this.evaluate(sequence, jobToolMatrix, switches);

        this.currentResult = new Result(sequence);
        this.currentResult.setCost(cost);
        this.currentResult.setJobToolMatrix(jobToolMatrix);
        this.currentResult.setSwitches(switches);


        this.workingResult = this.currentResult.getCopy();
        this.bestResult = this.currentResult.getCopy();

        this.logger.logInfo(String.valueOf(cost));
        this.logger.logInfo("Initial Solution Created");

        this.logger.log(cost, cost, sequence);
        //this.logger.writeSolution(this.bestResult);

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
    public void steepestDescent() throws IOException {
        boolean improved = false;
        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {

            for (int i = 0; i < this.workingResult.getSequence().length; i++) {
                for (int j = i + 1 ; j < this.workingResult.getSequence().length; j++) {
                    int[] seq = this.workingResult.getSequence();
                    int temp = seq[i];
                    seq[i] = seq[j];
                    seq[j] = temp;

                    //sequence = this.randomInitialSequence(sequence);
                    this.workingResult.setJobToolMatrix(this.decode(seq));
                    this.workingResult.setSwitches(this.calculateSwitches(seq,this.workingResult.getJobToolMatrix()));
                    this.workingResult.setCost(this.evaluate(seq, this.workingResult.getJobToolMatrix(), this.workingResult.getSwitches()));


                    if(this.workingResult.getCost() < this.bestResult.getCost()) {
                        improved = true;
                        this.bestResult = this.workingResult.getCopy();


                        this.logger.log(this.workingResult.getCost(), this.bestResult.getCost(), this.workingResult.getSequence());

                    }

                }
            }

            this.logger.logInfo("next neighbourhoud");

            if (!improved) {
                this.logger.logInfo("local min reached, no improvement");
                break;
            }
            improved = false;
        }

    }


    //Best Improvement
    public void steepestDescentTournament() {

    }


    //First improvement
    public void hillClimbing() throws IOException {
        boolean improved = false;
        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {

            climb: for (int i = 0; i < this.workingResult.getSequence().length; i++) {
                for (int j = i + 1 ; j < this.workingResult.getSequence().length; j++) {
                    int[] seq = this.workingResult.getSequence();

                    //Swap moves
                    int temp = seq[i];
                    seq[i] = seq[j];
                    seq[j] = temp;

                    //sequence = this.randomInitialSequence(sequence);
                    this.workingResult.setJobToolMatrix(this.decode(seq));
                    this.workingResult.setSwitches(this.calculateSwitches(seq,this.workingResult.getJobToolMatrix()));
                    this.workingResult.setCost(this.evaluate(seq, this.workingResult.getJobToolMatrix(), this.workingResult.getSwitches()));


                    if(this.workingResult.getCost() < this.bestResult.getCost()) {
                        improved = true;
                        this.bestResult = this.workingResult.getCopy();
                        this.logger.log(this.workingResult.getCost(), this.bestResult.getCost(), this.workingResult.getSequence());

                        //First improvement
                        break climb;
                    }

                }
            }

            this.logger.logInfo("next neighbourhoud");

            if (!improved) {
                this.logger.logInfo("local min reached, no improvement");
                break;
            }
            improved = false;
        }

        this.logger.writeSolution(this.bestResult);
    }




    //First improvement
    public void hillClimbingTournament() {


    }


    public void acceptImproved() {

    }


    //SA
    public void simulatedAnnealing() throws IOException {

        double temperature = this.START_TEMP;
        int j = 0;
        int steps = 0;
        long accepted = 0;
        long rejected = 0;
        long improved = 0;
        int steady = 0;


        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {

            int[] seq = this.workingResult.getSequence();
            this.getMoveManager().swap(this.workingResult);

            //sequence = this.randomInitialSequence(sequence);
            this.workingResult.setJobToolMatrix(this.decode(seq));
            this.workingResult.setSwitches(this.calculateSwitches(seq,this.workingResult.getJobToolMatrix()));
            this.workingResult.setCost(this.evaluate(seq, this.workingResult.getJobToolMatrix(), this.workingResult.getSwitches()));


            int deltaE = (int) (this.workingResult.getCost() - this.bestResult.getCost());

            if(deltaE > 0) {
                double acceptance = Math.exp(-deltaE/ temperature);
                double ran = random.nextDouble();

                if(acceptance > ran) {

                    //accept move -> not the best solution
                    this.currentResult = this.workingResult;
                    this.workingResult = this.currentResult.getCopy();

                    accepted+=1;

                }else{
                    rejected+=1;
                    //cancel move
                    this.workingResult = this.currentResult.getCopy();
                }
            }else{
                //accept & best solution now
                //this.logger.logInfo("New best solution found");

                this.currentResult = this.workingResult;
                this.workingResult = this.currentResult.getCopy();
                this.bestResult = this.currentResult.getCopy();

                improved+=1;
            }

            //Keep temperature steady for a few steps before dropping
            if(steady > 70) {
                temperature = temperature * DECAY_RATE;
                steady=0;
            }
            steady++;


            //Reheating
            if (temperature < 1.5) {
                //temperature = 10.0 + random.nextDouble() * 40;
            }

            //LOGGING
            if (steps % 1000 == 0) {
                this.logger.log(this.currentResult.getCost(), this.bestResult.getCost(), accepted, rejected, improved, steps, temperature, this.currentResult.getSequence());
            }

            //TODO: stop SA after no improvement is found anymore...

            steps++;
        }


    }


    /* EVALUATION ------------------------------------------------------------------ */


    public int[][] decode(int[] sequence) {
        ArrayList<LinkedList<Integer>> toolPrioritySequence = determineToolPriority(sequence);
        int[][] augmentedJobToolMatrix = new int[this.getN_JOBS()][this.getN_TOOLS()];

        //Set tools
        int[] prev = new int[this.getN_TOOLS()];

        for (int i = 0; i < sequence.length; i++) {

            //Set tools
            int numberOfToolsSet = 0;
            for (int j = 0; j < this.N_TOOLS; j++) {
                augmentedJobToolMatrix[i][j] = this.getJOB_TOOL_MATRIX()[i][j];
                if (prev[j] == 1 || this.getJOB_TOOL_MATRIX()[i][j] == 1) {
                    augmentedJobToolMatrix[i][j] = 1;
                    numberOfToolsSet += 1;
                }
            }
            int numberOfToolsToRemove = Math.max(0,numberOfToolsSet - getMAGAZINE_SIZE());
            //System.out.println(numberOfToolsToRemove);
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

            prev = augmentedJobToolMatrix[i];
        }

        //General.printGrid(augmentedJobToolMatrix);

        return augmentedJobToolMatrix;
    }


    //TODO:
    public ArrayList<LinkedList<Integer>> determineToolPriority(int[] sequence) {
        ArrayList<LinkedList<Integer>> toolPrioritySequence = new ArrayList<>(sequence.length);

        for(int i = 0; i < sequence.length; i++) {
            int[] visited = new int[this.getN_TOOLS()];
            int jobId = sequence[i];
            LinkedList<Integer> toolPriority = new LinkedList<>();
            for (int j = i + 1; j < sequence.length; j++) {
                for (int k = 0; k < visited.length; k++) {
                    // visiter, belongs to current job, is used here
                    if(visited[k] == 0 && this.getJOB_TOOL_MATRIX()[jobId][k] == 0 && getJOB_TOOL_MATRIX()[j][k] == 1){
                        toolPriority.add(k);
                        visited[k] = 1;
                    }
                }
            }

            //Add the remaining tools
            //TODO: optimize collect remaining tools
            for (int j = 0; j < visited.length; j++) {
                if(visited[j] == 0 && this.getJOB_TOOL_MATRIX()[jobId][j] == 0) {
                    toolPriority.add(j);
                }
            }


            toolPrioritySequence.add(toolPriority);
        }

        return toolPrioritySequence;
    }




    public int[] calculateSwitches(int[] sequence, int[][] jobToolMatrix) {
        return count_version_simon(sequence,jobToolMatrix);
    }

    public int[] count_version_simon(int[] sequence, int[][] jobToolMatrix) {

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
                //CHECK: current implementation: when a tool gets loaded a "switch" is performed
                if (previous[j] ==  0 &  current[j] ==  1) {
                    swapCount+=1;
                }
            }
            switches[i] = swapCount;
        }

        return switches;
    }

    public int[] count_version_vidal(int[] sequence, int[][] jobToolMatrix) {

        //COUNTING WHEN A 1 TURNS INTO A 0

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
                //CHECK: current implementation: when a tool gets loaded a "switch" is performed
                if (previous[j] ==  1 &  current[j] ==  0) {
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

    public int[][] getDIFFERENCE_MATRIX() {
        return DIFFERENCE_MATRIX;
    }

    public void setDIFFERENCE_MATRIX(int[][] DIFFERENCE_MATRIX) {
        this.DIFFERENCE_MATRIX = DIFFERENCE_MATRIX;
    }

    public int[][] getSHARED_TOOLS_MATRIX() {
        return SHARED_TOOLS_MATRIX;
    }

    public void setSHARED_TOOLS_MATRIX(int[][] SHARED_TOOLS_MATRIX) {
        this.SHARED_TOOLS_MATRIX = SHARED_TOOLS_MATRIX;
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

    public Parameters getParameters() {
        return parameters;
    }

    public String[] getLogTitles() {
        return logTitles;
    }

    public void setLogTitles(String[] logTitles) {
        this.logTitles = logTitles;
    }

    public int[][] getSWITCHES_LB_MATRIX() {
        return SWITCHES_LB_MATRIX;
    }

    public void setSWITCHES_LB_MATRIX(int[][] SWITCHES_LB_MATRIX) {
        this.SWITCHES_LB_MATRIX = SWITCHES_LB_MATRIX;
    }

    public int[][] getTOOL_PAIR_COUNT_MATRIX() {
        return TOOL_PAIR_COUNT_MATRIX;
    }

    public void setTOOL_PAIR_COUNT_MATRIX(int[][] TOOL_PAIR_COUNT_MATRIX) {
        this.TOOL_PAIR_COUNT_MATRIX = TOOL_PAIR_COUNT_MATRIX;
    }

    public Tool[] getTools() {
        return tools;
    }

    public void setTools(Tool[] tools) {
        this.tools = tools;
    }

    public Result getWorkingResult() {
        return workingResult;
    }

    public void setWorkingResult(Result workingResult) {
        this.workingResult = workingResult;
    }
}
