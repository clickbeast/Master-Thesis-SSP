package core;

import data_processing.DataProcessing;
import data_processing.InputData;
import data_processing.Logger;
import data_processing.Parameters;
import models.elemental.Job;
import models.elemental.Tool;
import util.General;

import java.io.IOException;
import java.util.*;


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
    private int[][][] SHARED_TOOLS_MATRIX;

    private int[][] SWITCHES_LB_MATRIX;
    private int[][] TOOL_PAIR_MATRIX;

    //MANAGERS
    private MoveManager moveManager;
    private final SolutionManager solutionManager;
    private DataProcessing dataProcessing;
    private Decoder decoder;


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


    //TRACKING

    private long accepted = 0;
    private long rejected = 0;
    private long improved = 0;


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

        this.decoder = new Decoder(this);

        this.steps = 0;
        this.initialize();

        this.logger = new Logger(this, inputData.getLogWriter(),inputData.getResultsWriter(), inputData.getSolutionWriter());

    }

    public void optimize() throws IOException {
        this.logger.logLegend(logTitles);
        //this.initialize();

        General.printGrid(this.getJOB_TOOL_MATRIX());
        //General.printGrid(General.transposeMatrix(this.copyGrid(this.getJOB_TOOL_MATRIX())));
        //this.initialSolution();
        //this.initialOrderedSolution();
        this.initialRandomSolution();



        //String hello = gson.toJson(result);


        //this.steepestDescentRandomBest();

        //[2, 7, 4, 6, 5, 3, 1, 0] answer

        //Used for confirming algorithm

        //this.simulatedAnnealing();
        //this.steepestDescent();
        //this.bruteForce();

        //this.hillClimbing();
        ///


        this.simulatedAnnealing();
        //this.logger.logInfo("BONSOIR");
        //this.logger.log(this.bestResult);
        //int[] sequence = {2, 7, 4, 6, 5, 3, 1, 0};

        //2,7 ,4 ,6 ,5 ,3,1 ,0
        //this.forceSequence(sequence);


        this.logger.logInfo("BONSOIR");
        this.logger.log(this.bestResult);
        this.logger.writeResult(bestResult);
        this.logger.writeSolution(this.bestResult);



        //General.printGrid(General.transposeMatrix(this.copyGrid(this.getJOB_TOOL_MATRIX())));

        //General.printGrid(General.transposeMatrix(this.copyGrid(this.bestResult.getJobToolMatrix())));

    }



    public void initialize(){
        this.jobs = this.initializeJobs();
        this.initializeTools();
        this.DIFFERENCE_MATRIX = this.initializeDifferenceMatrix();
        this.SHARED_TOOLS_MATRIX = initializeSharedToolsMatrix();
        this.SWITCHES_LB_MATRIX = this.initializeSwitchesLowerBoundMatrix();
        this.TOOL_PAIR_MATRIX = this.initializeToolPairMatrix();
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




    public int[][][] initializeSharedToolsBinaryMatrix() {

        int[][][] sharedTools = new int[this.getN_JOBS()][this.getN_JOBS()][];

        for (int job1Id = 0; job1Id < this.getN_JOBS(); job1Id++) {

            for (int job2Id = job1Id; job2Id < this.getN_JOBS(); job2Id++) {

                int[] shared = new int[this.getN_TOOLS()];
                for (int toolId = 0; toolId < this.getN_TOOLS(); toolId++) {
                    if (this.getJOB_TOOL_MATRIX()[job1Id][toolId] == this.getJOB_TOOL_MATRIX()[job2Id][toolId]) {
                        shared[toolId] = this.getJOB_TOOL_MATRIX()[job1Id][toolId];
                    }
                }

                //Assign to both sides
                sharedTools[job1Id][job2Id] = shared;
                sharedTools[job2Id][job1Id]= shared;

            }
        }

        return sharedTools;
    }


    public int[][][] initializeSharedToolsMatrix() {

        int[][][] sharedTools = new int[this.getN_JOBS()][this.getN_JOBS()][];

        for (int job1Id = 0; job1Id < this.getN_JOBS(); job1Id++) {

            for (int job2Id = job1Id; job2Id < this.getN_JOBS(); job2Id++) {

                List<Integer> shared = new ArrayList<>();
                for (int toolId = 0; toolId < this.getN_TOOLS(); toolId++) {
                    if (this.getJOB_TOOL_MATRIX()[job1Id][toolId] == this.getJOB_TOOL_MATRIX()[job2Id][toolId]) {
                        if(this.getJOB_TOOL_MATRIX()[job1Id][toolId] == 1) {
                            shared.add(toolId);
                        }
                    }
                }

                //Assign to both sides
                sharedTools[job1Id][job2Id] = shared.stream().mapToInt(i->i).toArray();
                sharedTools[job2Id][job1Id]= shared.stream().mapToInt(i->i).toArray();

            }
        }

        return sharedTools;
    }




    //TODO: initializeSwitchesLowerBoundMatrix
    public int[][] initializeSwitchesLowerBoundMatrix() {

        return null;
    }

    //TODO: initializeToolPairMatrix
    public int[][] initializeToolPairMatrix() {

        int[][] matrix = new int[this.getN_TOOLS()][this.getN_TOOLS()];

        for (int toolId1 = 0; toolId1 < this.getN_TOOLS() ; toolId1++) {
            for (int toolId2 = toolId1; toolId2 < this.getN_TOOLS(); toolId2++) {
                int value = this.toolPairOccurences(toolId1, toolId2);
                matrix[toolId1][toolId2] = value;
                matrix[toolId2][toolId1] = value;
            }
        }

        return matrix;
    }

    //Make A graph

    public void initalizeToolPairGraph() {


    }



    public int toolPairOccurences(int toolId1, int toolId2) {
        int count = 0;
        for (int jobId = 0; jobId < this.getN_JOBS(); jobId++) {
            if(this.getJOB_TOOL_MATRIX()[jobId][toolId1] == 1 && this.getJOB_TOOL_MATRIX()[jobId][toolId2] == 1) {
                count+=1;
            }
        }

        return count;
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

        this.currentResult = new Result(sequence,this);
        this.currentResult.setnSwitches(cost);
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

        this.currentResult = new Result(sequence, this);
        this.getDecoder().decode(this.currentResult);
        this.currentResult.setInitial();


        this.workingResult = this.currentResult.getCopy();
        this.bestResult = this.currentResult.getCopy();

        this.logger.logInfo(String.valueOf(this.workingResult.getCost()));
        this.logger.logInfo("Initial Solution Created");

        this.logger.log(this.workingResult);
        //this.logger.writeSolution(this.bestResult);
    }


    public int[] orderedInitialSequence() {
        int[] sequence = new int[this.N_JOBS];
        //Setup sequence
        for (int i = 0; i < N_JOBS; i++) {
            Job job = this.getJobs()[i];
            sequence[i] = job.getId();
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
    public void toolSequencingInitialSequence() {
        int[] toolSequence = new int[this.getN_TOOLS()];


    }

    /**
     * Tang and Denardo
     */
    //TODO: TSPInitialSequence
    public void TSPInitialSequence() {
        for (int toolId1 = 0; toolId1 < this.N_TOOLS; toolId1++) {
            for (int toolId2 = toolId1; toolId2 < this.N_TOOLS; toolId2++) {

            }
        }
    }





    /* LS ------------------------------------------------------------------ */

    //Best Improvement
    public void steepestDescentRandomBest() throws IOException {
        boolean improved = false;



        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {
            //Visit the whole neighberhoud

            this.workingResult = this.bestResult.getCopy();
            int nBestResults = 0;

            for (int i = 0; i < this.workingResult.getSequence().length; i++) {
                for (int j = i + 1 ; j < this.workingResult.getSequence().length; j++) {


                    //PERFORM THE MOVE
                    int[] seq = this.workingResult.getSequence();
                    int temp = seq[i];
                    seq[i] = seq[j];
                    seq[j] = temp;


                    this.workingResult.reloadJobPositions();
                    this.decoder.decode(this.workingResult);


                    if(this.workingResult.getnSwitches() <=  this.bestResult.getnSwitches()) {
                        improved = true;

                        if(nBestResults==0) {
                            this.bestResult = this.workingResult.getCopy();
                            nBestResults+=1;
                        }else{
                            float probability = 1/nBestResults;
                            if(random.nextDouble() <= probability) {
                                this.bestResult = this.workingResult.getCopy();
                            }
                            nBestResults+=1;
                        }

                        this.bestResult = this.workingResult.getCopy();
                        this.logger.log(this.workingResult);

                    }

                    this.logger.writeLiveResult(this.workingResult);
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


    public void steepestDescentBestFirst() throws IOException {
        boolean improved = false;



        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {
            //Visit the whole neighberhoud

            this.workingResult = this.bestResult.getCopy();

            for (int i = 0; i < this.workingResult.getSequence().length; i++) {
                for (int j = i + 1 ; j < this.workingResult.getSequence().length; j++) {

                    int[] seq = this.workingResult.getSequence();
                    int temp = seq[i];
                    seq[i] = seq[j];
                    seq[j] = temp;

                    this.workingResult.reloadJobPositions();
                    this.decoder.decode(this.workingResult);


                    if(this.workingResult.getnSwitches() < this.bestResult.getnSwitches()) {
                        improved = true;
                        this.bestResult = this.workingResult.getCopy();
                        this.logger.log(this.workingResult);

                    }

                    this.logger.writeLiveResult(this.workingResult);
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
                    this.workingResult.setnSwitches(this.evaluate(seq, this.workingResult.getJobToolMatrix(), this.workingResult.getSwitches()));


                    if(this.workingResult.getnSwitches() < this.bestResult.getnSwitches()) {
                        improved = true;
                        this.bestResult = this.workingResult.getCopy();
                        this.logger.log(this.workingResult.getnSwitches(), this.bestResult.getnSwitches(), this.workingResult.getSequence());

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
    }


    //First improvement
    public void hillClimbingTournament() {


    }


    public void acceptImproved() {

    }

    //BRUTE FORCE
    //TODO: THIS PERMUTATION IS NOT THE CORRECT ONE
    public void bruteForce() throws IOException {
        //Try all permutations of the string
        //int[] sequence = Arrays.copyOf(this.getCurrentResult().getSequence(), this.getCurrentResult().getSequence().length);
        //permutation(sequence, 0);
        //this.orderedInitialSequence();
        int[] seq = this.orderedInitialSequence();
        this.workingResult = new Result(seq,this);

        //brute(seq);
        this.permutation(seq, 0);
    }


    private void permutation(int[] sequence, int offset) throws IOException {
        if(offset == sequence.length - 1) {
            brute(Arrays.copyOf(sequence, sequence.length));
        }else {

            for (int i = offset; i < sequence.length; i++) {
                swap(sequence, i, offset);
                permutation(sequence, offset + 1);
                swap(sequence, offset ,i);
            }
        }
    }



    //TODO: check for a swap in place
    private int[] swap(int[] list, int a, int b) {
        int temp = list[a];
        list[a] = list[b];
        list[b] = temp;
        return list;
    }



    int bruteCount = 0;

    private void brute(int[] sequence) throws IOException {

        int[] refer = {2, 7, 4, 6, 5, 3, 1, 0};
        if(sequence.equals(refer)) {
            this.logger.logInfo("GEVONDEN GEVONDEN GEVONDEN");
        }

        this.workingResult.setSequence(sequence);
        this.decoder.decode(this.workingResult);


        if(this.bestResult == null) {
            this.bestResult = this.workingResult.getCopy();
        }


        if(this.workingResult.getCost() < this.bestResult.getCost()) {
            this.bestResult = this.workingResult.getCopy();
            //this.logger.log(bestResult);
        }


        if(bruteCount == 1000) {
            this.logger.log(this.workingResult);

            bruteCount = 0;
            this.logger.writeResult(this.workingResult);
            this.logger.writeLiveResult(this.workingResult);
        }

        bruteCount+=1;


        //this.logger.writeLiveResult(this.workingResult);
    }



    //FORCE SEQUENCE

    public void forceSequence(int[] sequence) throws IOException {
        //this.bestResult.setSequence(sequence);
        this.bestResult.reloadJobPositions();
        this.bestResult = this.bestResult.getCopy();
        this.bestResult.setSequence(sequence);
        this.bestResult.reloadJobPositions();
        this.decoder.decode(this.bestResult);
    }


    //SA
    public void simulatedAnnealing() throws IOException {

        double temperature = this.START_TEMP;
        int j = 0;
        this.setSteps(0);
        int steady = 0;


        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {

            this.getMoveManager().swap(this.workingResult);

            this.getDecoder().decode(this.workingResult);


            int deltaE = this.workingResult.getnSwitches() - this.bestResult.getnSwitches();

            if(deltaE > 0) {
                double acceptance = Math.exp(-deltaE/ temperature);
                double ran = random.nextDouble();

                if(acceptance > ran) {

                    //accept move -> not the best solution
                    this.workingResult.setAccepted();
                    this.currentResult = this.workingResult;

                    accepted+=1;

                }else{
                    rejected+=1;
                    this.workingResult.setRejected();
                    //cancel move
                }
            }else{
                //accept & best solution now
                //this.logger.logInfo("New best solution found");

                this.workingResult.setImproved();

                this.currentResult = this.workingResult;
                this.bestResult = this.currentResult.getCopy();

                this.logger.log(this.getWorkingResult(), temperature);

                improved+=1;
            }



            //LOGGING
            if (steps % 330 == 0) {
                this.logger.log(this.getWorkingResult(), temperature);
            }

            if(steps % 10000 == 0) {
                this.logger.writeResult(this.getWorkingResult());
            }

            // - PREPARE FOR NEW ITERATION - -

            //Copy for new iteration
            this.workingResult = this.currentResult.getCopy();



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

            if(temperature < 0.007) {
                break;
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

            System.out.println("Decode");
            General.printGrid(augmentedJobToolMatrix);

            int numberOfToolsToRemove = Math.max(0,numberOfToolsSet - getMAGAZINE_SIZE());
            //System.out.println(numberOfToolsToRemove);
            LinkedList<Integer> toolPriority = toolPrioritySequence.get(i);
            //remove unwanted tools
            for (int j = 0; j < numberOfToolsToRemove; j++) {
                while(true) {
                    int toolId = toolPriority.removeLast();
                    if(augmentedJobToolMatrix[i][toolId] == 1 && this.getJOB_TOOL_MATRIX()[i][toolId] != 1) {
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
                    if(visited[k] == 0  && getJOB_TOOL_MATRIX()[j][k] == 1){
                        toolPriority.add(k);
                        visited[k] = 1;
                    }
                }
            }

            //Add the remaining tools
            //TODO: optimize collect remaining tools
            for (int j = 0; j < visited.length; j++) {
                if(visited[j] == 0) {
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
                if (previous[j] ==  0 &  current[j] ==  1) {
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



    public Job getJob(int id) {
        return this.getJobs()[id];
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

    public int[][][] getSHARED_TOOLS_MATRIX() {
        return SHARED_TOOLS_MATRIX;
    }

    public void setSHARED_TOOLS_MATRIX(int[][][] SHARED_TOOLS_MATRIX) {
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

    public int[][] getTOOL_PAIR_MATRIX() {
        return TOOL_PAIR_MATRIX;
    }

    public void setTOOL_PAIR_MATRIX(int[][] TOOL_PAIR_MATRIX) {
        this.TOOL_PAIR_MATRIX = TOOL_PAIR_MATRIX;
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

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }


    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getImproved() {
        return improved;
    }

    public void setImproved(long improved) {
        this.improved = improved;
    }
}
