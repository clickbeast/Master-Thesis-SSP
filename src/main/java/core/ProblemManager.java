package core;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import data_processing.DataProcessing;
import data_processing.InputData;
import data_processing.Logger;
import data_processing.Parameters;
import exception.NoToolFoundException;
import models.elemental.Job;
import models.elemental.Tool;
import util.General;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


public class ProblemManager {

    //PARAMETERS
    private final Parameters parameters;

    private final long TIME_LIMIT;
    private final long RUN_TIME;

    //CONSTANTS
    private final int MAGAZINE_SIZE;
    private final int N_TOOLS;
    private final int N_JOBS;
    private int[][] JOB_TOOL_MATRIX;




    private int[][] DIFFERENCE_MATRIX;

    //The jobs A and B , difference int[A][B][Tool]
/*    private int[][][] DIFFERENCE_TOOLS_MATRIX;*/
    //common tools
    private int[][][] SHARED_TOOLS_MATRIX;

    private int[][] SWITCHES_LB_MATRIX;
    private int[][] TOOL_PAIR_MATRIX;
    private  MutableValueGraph<Integer, Integer> TOOL_PAIR_GRAPH;

    //MANAGERS
    private MoveManager moveManager;
    private final SolutionManager solutionManager;
    private DataProcessing dataProcessing;
    private Decoder decoder;


    //VARIABLES
    private Job[] jobs;
    private Tool[] tools;


    // RESULT
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
        this.RUN_TIME = this.getParameters().getRUN_TIME();
        this.TIME_LIMIT = System.currentTimeMillis() + 1000 * this.getParameters().getRUN_TIME();

        //
        this.random = new Random();

        this.moveManager = new MoveManager(this);
        this.solutionManager = new SolutionManager(this);

        this.decoder = new Decoder(this);

        this.steps = 0;
        this.initialize();

        this.logger = new Logger(this, inputData.getLogWriter(),inputData.getResultsWriter(), inputData.getSolutionWriter());

    }

    public void optimize() throws IOException {
        this.logger.logLegend();
        General.printGrid(this.getJOB_TOOL_MATRIX());
        General.printTransposeGrid(this.getJOB_TOOL_MATRIX());

        switch (this.getParameters().getConstructiveHeuristic()) {
            case "ordered": {
                this.initialOrderedSolution();
                break;
            }

            case "random": {
                this.initialRandomSolution();

                break;
            }

            case "tsp": {
                this.initialTSPSolution();
                break;
            }
            case "toolSequencing": {
                this.initialToolSequencingSolution();
                break;
            }


            case "skip": {
                break;
            }

            default: {
                this.logger.logInfo("NO CONSTRUCTIVE HEURISTIC CHOSEN");
                return;
            }
        }

        switch (this.getParameters().getMetaHeuristic()) {
            case "simulatedAnnealing": {
                this.simulatedAnnealing();
                break;
            }

            case "steepestDescentBestRandom": {
                this.steepestDescentRandomBest();
                break;
            }

            case "steepestDescentBestFirst": {
                this.steepestDescentFirstBest();
                break;
            }

            case "hillClimbing": {
                this.initialToolSequencingSolution();
                break;
            }

            case "forceSequence": {
                this.forceSequence(this.parameters.getForceSequence());
                break;
            }

            case "permutations": {
                this.permutations();
                break;
            }


            case "discover": {
                this.discover();
                break;
            }

            default: {
                this.logger.logInfo("NO META HEURISTIC CHOSEN");
                return;
            }
        }

        //this.forceSequence(this.parameters.getForceSequence());

        if(this.getParameters().isRunBackupSD()) {
            this.logger.logInfo("RUNNING BACKUP LS");
            this.steepestDescentRandomBest();
        }


        this.logger.logInfo("FINAL SOLUTION FOUND: " + String.valueOf(this.bestResult.getCost()));
        this.logger.log(this.bestResult);
        this.logger.writeResult(bestResult);
        this.logger.writeSolution(this.bestResult);
        this.logger.writeParameters();
    }






    public void initialize(){
        this.jobs = this.initializeJobs();
        this.initializeTools();
        //this.DIFFERENCE_TOOLS_MATRIX = this.initializeDifferenceMatrix();
        this.SHARED_TOOLS_MATRIX = initializeSharedToolsMatrix();
        this.SWITCHES_LB_MATRIX = this.initializeSwitchesLowerBoundMatrix();
        this.TOOL_PAIR_MATRIX = this.initializeToolPairMatrix();
        this.TOOL_PAIR_GRAPH = this.initializeToolPairGraph();
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
    public int[][][] initializeDifferenceMatrix() {


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



    //VERIFY
    public int[][] initializeSwitchesLowerBoundMatrix() {

        int[][] matrix = new int[this.getN_JOBS()][this.getN_JOBS()];

        for (int jobIdA = 0; jobIdA < this.getN_JOBS(); jobIdA++) {
            for (int jobIdB = jobIdA; jobIdB < this.getN_JOBS(); jobIdB++) {

                Job jobA = this.getJob(jobIdA);
                Job jobB = this.getJob(jobIdB);

                int intersection = this.getSHARED_TOOLS_MATRIX()[jobIdA][jobIdB].length;
                int LB = Math.max(0, (jobA.getSet().length + jobB.getSet().length - intersection - this.getMAGAZINE_SIZE()));
                matrix[jobIdA][jobIdB] = LB;
                matrix[jobIdB][jobIdA] = LB;
            }
        }


        return matrix;
    }

    //TODO: initializeToolPairMatrix
    //OK
    public int[][] initializeToolPairMatrix() {

        int[][] matrix = new int[this.getN_TOOLS()][this.getN_TOOLS()];

        for (int toolId1 = 0; toolId1 < this.getN_TOOLS() ; toolId1++) {
            for (int toolId2 = toolId1; toolId2 < this.getN_TOOLS(); toolId2++) {
                int value = this.toolPairOccurrences(toolId1, toolId2);
                matrix[toolId1][toolId2] = value;
                matrix[toolId2][toolId1] = value;
            }
        }

        return matrix;
    }


    public int toolPairOccurrences(int toolId1, int toolId2) {
        int count = 0;
        for (int jobId = 0; jobId < this.getN_JOBS(); jobId++) {
            if(this.getJOB_TOOL_MATRIX()[jobId][toolId1] == 1 && this.getJOB_TOOL_MATRIX()[jobId][toolId2] == 1) {
                count+=1;
            }
        }
        return count;
    }




    public MutableValueGraph<Integer, Integer> initializeToolPairGraph() {


        MutableValueGraph<Integer, Integer> weightedGraph = ValueGraphBuilder.undirected().build();

        for (int i = 0; i < this.getN_TOOLS(); i++) {
            weightedGraph.addNode(i);
        }

        for (int i = 0; i < this.getTOOL_PAIR_MATRIX().length ; i++) {
            for (int j = i+1; j < this.getTOOL_PAIR_MATRIX()[0].length ; j++) {
                if(i != j & this.getTOOL_PAIR_MATRIX()[i][j] != 0 ){
                    weightedGraph.putEdgeValue(i,j,this.getTOOL_PAIR_MATRIX()[i][j]);
                }
            }
        }


        return weightedGraph;
    }






    /* INITIAL SOLUTION ------------------------------------------------------------------ */

    public void initialOrderedSolution() throws IOException {
        this.logger.logInfo("Creating initial solution");

        int[] sequence = this.orderedInitialSequence();

        this.workingResult = new Result(sequence,this);
        this.decoder.decode(workingResult);

        //Set for all results
        this.currentResult = this.workingResult.getCopy();
        this.bestResult = this.currentResult.getCopy();


        this.logger.logInfo("Initial Solution Created : ordered");

        this.logger.log(this.getWorkingResult());

    }

    public void initialRandomSolution() throws IOException {
        this.logger.logInfo("Creating initial solution");

        int[] sequence = this.orderedInitialSequence();
        sequence = this.randomInitialSequence(sequence);

        this.currentResult = new Result(sequence, this);
        this.getDecoder().decode(this.currentResult);
        this.currentResult.setInitial();

        //Set for all results
        this.workingResult = this.currentResult.getCopy();
        this.bestResult = this.currentResult.getCopy();

        this.logger.logInfo(String.valueOf(this.workingResult.getCost()));
        this.logger.logInfo("Initial Solution Created");

        this.logger.log(this.workingResult);
        this.logger.writeResult(this.workingResult);

        //this.logger.writeSolution(this.bestResult);
    }


    public void initialTSPSolution() throws IOException {
        this.logger.logInfo("Creating initial solution");

        int[] sequence = this.orderedInitialSequence();
        sequence = this.randomInitialSequence(sequence);

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



    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* INITIAL SOLUTION: TOOL SEQUENCING
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


    /**
     * Gustavo Silva Paiva, et al.
     */
    public void initialToolSequencingSolution() throws IOException {
        this.logger.logInfo("Creating initial solution: Tool Sequencing");

        LinkedList<Integer> toolSequence = generateToolSequence();
        LinkedList<Integer> jobSequence = generateJobSequence(toolSequence);

        int[] sequence = jobSequence.stream().mapToInt(i -> i).toArray();

        this.currentResult = new Result(sequence, this);
        this.getDecoder().decode(this.currentResult);
        this.currentResult.setInitial();


        //Set for all solutions
        this.workingResult = this.currentResult.getCopy();
        this.bestResult = this.currentResult.getCopy();

        this.logger.logInfo(String.valueOf(this.workingResult.getCost()));
        this.logger.logInfo("Initial Solution Created");
        this.logger.log(this.workingResult);


    }


    public LinkedList<Integer> generateToolSequence() {

        LinkedList<Integer> toolSequence = new LinkedList<>();
        boolean[] visited = new boolean[this.getN_TOOLS()];
        boolean[] added = new boolean[this.getN_TOOLS()];
        int count_visited = 0;
        int count_added = 0;

        //Start at a random tool
        //int startToolId = this.random.nextInt(this.getN_TOOLS());

        int startToolId = 0;
        toolSequence.add(startToolId);
        added[startToolId] = true;
        count_added += 1;
        Queue<Integer> q = new LinkedList<>();
        q.add(startToolId);

        findToolSeq: while(count_visited < this.getN_TOOLS()) {

            while (!q.isEmpty()) {
                int toolId = q.remove();
                visited[toolId] = true;
                count_visited += 1;


                //TODO: optimize
                int[] sortedNeighbours = sortNeighboursDecreasing(toolId);

                for(Integer neighbourToolId : sortedNeighbours) {
                    if(!added[neighbourToolId]) {

                        added[neighbourToolId] = true;
                        count_added += 1;

                        toolSequence.add(neighbourToolId);
                        q.add(neighbourToolId);

                        if(count_added >= this.getN_TOOLS()) {
                            break findToolSeq;
                        }

                    }
                }
            }

            //if exists a node k ∈ V not visited then insert k in Q;
            if(count_visited < this.getN_TOOLS()) {
                for (int i = 0; i < visited.length; i++) {
                    if(!visited[i]) {
                        q.add(i);
                        break;
                    }
                }
            }
        }


        return toolSequence;

    }



    public LinkedList<Integer> generateJobSequenceSimplified(LinkedList<Integer> toolSequence) {

        LinkedList<Integer> jobSequence = new LinkedList<>();
        Set<Integer> parent = new HashSet<>();
        boolean[] addedJobs = new boolean[this.getN_JOBS()];


        //Naive
        for(Integer toolId: toolSequence) {
            parent.add(toolId);
            for (int i = 0; i < this.getN_JOBS(); i++) {
                if(!addedJobs[i]) {
                    if (isSubset(parent, this.jobs[i].getSet())) {
                        jobSequence.add(i);
                        addedJobs[i] = true;
                    }
                }
            }
        }



        return jobSequence;
    }


    public LinkedList<Integer> generateJobSequence(LinkedList<Integer> toolSequence) throws IOException {

        Comparator<Integer> byNumberOfTools = Comparator.comparingInt(
                id -> {
                    return this.jobs[id].getSet().length;
                });


        LinkedList<Integer> jobSequence = new LinkedList<>();
        boolean[] addedJobs = new boolean[this.getN_JOBS()];
        Set<Integer> parent = new HashSet<>();
        Set<Integer> candidates = new HashSet<>();

        for(Integer toolId: toolSequence) {
            parent.add(toolId);

            //Get canditate jobs
            for (int i = 0; i < this.getN_JOBS(); i++) {
                if(!addedJobs[i]) {
                    if(isSubset(parent,this.jobs[i].getSet())) {
                        candidates.add(i);
                    }
                }
            }


            while (!candidates.isEmpty()) {
                int picked = -1;

                if(jobSequence.isEmpty()) {
                    //Pick the one with the most amount of tools
                    picked = candidates.stream().max(byNumberOfTools).orElse(-1);

                }else{
                    //Pick the one that results in the least amount of switches
                    picked = pickMinKTNS(jobSequence, candidates);
                }

                candidates.remove(picked);
                jobSequence.add(picked);
                addedJobs[picked] = true;

            }

        }



        return jobSequence;
    }


    public int pickMinKTNS(LinkedList<Integer> jobSequence, Set<Integer> candidates) throws IOException {

        int min = -1;
        int minValue = Integer.MAX_VALUE;
        int nBestResults = 0;

        for(Integer c: candidates) {
            jobSequence.add(c);

            int[] sequence = jobSequence.stream().mapToInt(i -> i).toArray();
            Result partial = new Result(sequence,this);
            this.decoder.decode(partial);
            int value = partial.getnSwitches();

            if (value < minValue) {

                nBestResults = 1;
                minValue = value;
                min = c;

            }else if (value == minValue) {
                nBestResults+=1;
                float probability = 1/  (float)  nBestResults;
                if(random.nextDouble() <= probability) {
                    minValue = value;
                    min = c;
                }

            }

            jobSequence.remove(c);

        }

        return min;
    }


    public boolean isSubset(Set<Integer> parent, int[] child) {

        if(child.length > parent.size()) {
            return false;
        }


        for(int i = 0; i < child.length; i++) {
            if(!parent.contains(child[i])) {
                return false;
            }
        }

        return true;
    }


    public int[] sortNeighboursDecreasing(int node) {
        Set<EndpointPair<Integer>> endpointPairs = this.getTOOL_PAIR_GRAPH().incidentEdges(node);

        Comparator<EndpointPair<Integer>> byWeight = Comparator.comparingInt(
                pair -> {
                    return this.getTOOL_PAIR_GRAPH().edgeValue(pair).orElse(0);
                });

        //TODO: OPTIMIZE or leave out...
        Comparator<EndpointPair<Integer>> byNodeId = Comparator.comparingInt(
                pair -> {
                    if(pair.nodeU() != node) {
                        return pair.nodeU();
                    }else{
                        return pair.nodeV();
                    }
                });

        return  endpointPairs.stream().sorted(byWeight.reversed().thenComparing(byNodeId)).mapToInt(pair -> {
            if(pair.nodeU() != node) {
                return pair.nodeU();
            }else{
                return pair.nodeV();
            }
        }).toArray();

    }



    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


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
     * Tang and Denardo   -> skip
     */
    //TODO: TSPInitialSequence
    public void TSPInitialSequence() {
        for (int toolId1 = 0; toolId1 < this.N_TOOLS; toolId1++) {
            for (int toolId2 = toolId1; toolId2 < this.N_TOOLS; toolId2++) {

            }
        }
    }






    /* LS ------------------------------------------------------------------ */


    public void discover() throws IOException {
        System.out.println("bonjour");
        for (int i = 0; i < this.workingResult.getSequence().length; i++) {
            for (int j = i + 1 ; j < this.workingResult.getSequence().length; j++) {

                //PERFORM THE MOVE
                int[] seq = this.workingResult.getSequence();
                int temp = seq[i];
                seq[i] = seq[j];
                seq[j] = temp;

                //this.logger.writeResult(this.getWorkingResult());

                this.workingResult.reloadJobPositions();
                this.logger.writeResult(this.workingResult);
                this.decoder.decode(this.workingResult);
                this.logger.writeResult(this.workingResult);

                this.logger.writeResult(this.getWorkingResult());

            }

        }


    }



    //Best Improvement
    public void steepestDescentRandomBest() throws IOException {

        while (System.currentTimeMillis() < this.getTIME_LIMIT()) {
            //Visit the whole neighberhoud

            this.workingResult = this.bestResult.getCopy();

            int nBestResults = 1;

            for (int i = 0; i < this.workingResult.getSequence().length; i++) {
                for (int j = i + 1 ; j < this.workingResult.getSequence().length; j++) {


                    //PERFORM THE MOVE
                    int[] seq = this.workingResult.getSequence();
                    int temp = seq[i];
                    seq[i] = seq[j];
                    seq[j] = temp;


                    this.workingResult.reloadJobPositions();
                    this.decoder.decode(this.workingResult);


                    if(this.workingResult.getCost() <  this.currentResult.getCost()) {
                        this.currentResult = this.workingResult.getCopy();
                        nBestResults = 1;

                        this.logger.log(this.workingResult);

                    }else if(this.workingResult.getCost() == this.currentResult.getCost()) {
                        nBestResults+=1;
                        float probability = 1/  (float)  nBestResults;
                        if(random.nextDouble() <= probability) {
                            this.currentResult = this.workingResult.getCopy();
                            this.logger.log(this.workingResult);

                        }
                    }


                    this.logger.writeResult(this.workingResult);


                }

            }




            if (this.currentResult.getCost() <  this.bestResult.getCost()) {
                this.bestResult = this.currentResult.getCopy();
                this.logger.logInfo("next neighbourhood");
            }else{
                this.logger.logInfo("local min reached, no improvement");
                break;
            }
        }

    }

    public void steepestDescentFirstBest() throws IOException {
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


                    if(this.workingResult.getCost() < this.bestResult.getCost()) {
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


                    this.decoder.decode(this.workingResult);


                    if(this.workingResult.getnSwitches() < this.bestResult.getnSwitches()) {
                        improved = true;
                        this.bestResult = this.workingResult.getCopy();

                        //First improvement
                        break climb;
                    }

                }
            }

            this.logger.logInfo("next neighbourhood");

            if (!improved) {
                this.logger.logInfo("local min reached, no improvement");
                break;
            }
            improved = false;
        }
    }

    public void forceSequence(int[] sequence) throws IOException {
        this.logger.logInfo("RUNNING FORCE SEQUENCE");
        this.bestResult.reloadJobPositions();
        this.bestResult.setSequence(sequence);
        this.bestResult.reloadJobPositions();
        this.decoder.decode(this.bestResult);
        this.getLogger().writeLiveResult(this.bestResult);
        this.logger.writeResult(this.bestResult);
    }

    //SA


    public void permutations() {
        this.logger.logInfo("Finding all permutations");
    }

    public void simulatedAnnealing() throws IOException {

        if(this.getParameters().isSA_TIMED()) {
            this.logger.logInfo("Starting SA: timed");
            this.logger.logInfo("Running for max: " + String.valueOf(this.getParameters().getRUN_TIME()) + "seconds");
            this.logger.logInfo("START TEMP: " + this.parameters.getSTART_TEMP() + "; END TEMP:"
                    + this.parameters.getEND_TEMP() + "; DECAY RATE:" + this.parameters.getDECAY_RATE());
        }else{
            this.logger.logInfo("Starting SA: iterations");
            this.logger.logInfo("Running for: " + String.valueOf(this.getParameters().getITERATIONS()) + "steps");
            this.logger.logInfo("START TEMP: " + this.parameters.getSTART_TEMP() + "; END TEMP: "
                    + this.parameters.getEND_TEMP() + "; DECAY RATE: " + this.parameters.getDECAY_RATE());
        }

        double temperature = this.getParameters().getSTART_TEMP();
        this.setSteps(0);
        int steady = 0;
        int noChange = 0;
        int nBestResults = 0;

        while (System.currentTimeMillis() < this.getTIME_LIMIT() && this.steps <= this.getParameters().getITERATIONS()) {

            //Move
            this.getMoveManager().doMove(this.workingResult);
            //Evaluate
            this.getDecoder().decode(this.workingResult);

            //Acceptence criterium
            if((this.workingResult.getCost()) < (this.bestResult.getCost()) - temperature * Math.log(random.nextDouble())) {
                //Accept
                this.workingResult.setAccepted();
                this.currentResult = this.workingResult;
                accepted+=1;
            }else{
                //Reject
                this.workingResult.setRejected();
                rejected+=1;
            }

            //Improvement
            if(this.workingResult.getCost() < this.bestResult.getCost()) {
                this.workingResult.setImproved();
                this.bestResult = this.workingResult.getCopy();
                this.logger.log(this.workingResult, temperature);
                this.logger.writeResult(this.workingResult);
                improved+=1;
            }

            //Additional Stop criterium
            if(this.getWorkingResult().getnSwitches() == this.getBestResult().getnSwitches()) {
                noChange+=1;
            }

/*
            if(deltaE > 0) {

                deltaE = this.workingResult.getCost() - this.bestResult.getCost();
                double acceptance = Math.exp(-deltaE/ temperature);
                double ran = random.nextDouble();

                if(acceptance > ran) {
                    //accept move -> not the best solution
                    this.workingResult.setAccepted();
                    this.currentResult = this.workingResult;
                    accepted+=1;
                }else {
                    rejected += 1;
                    this.workingResult.setRejected();
                    //cancel move
                }
            }else{

                this.workingResult.setImproved();

                if(this.workingResult.getCost() <  this.bestResult.getCost()) {
                    this.currentResult = this.workingResult.getCopy();
                    nBestResults = 1;
                    this.logger.log(this.workingResult);
                }else if(this.workingResult.getCost() == this.bestResult.getCost()) {
                    nBestResults+=1;
                    float probability = 1/  (float)  nBestResults;
                    if(random.nextDouble() <= probability) {
                        this.currentResult = this.workingResult.getCopy();
                    }
                }


                this.currentResult = this.workingResult;
                this.bestResult = this.currentResult.getCopy();
                this.logger.log(this.getWorkingResult(), temperature);


                this.logger.writeResult(this.getWorkingResult());


                //TODO: cleanup
                if(this.getWorkingResult().getnSwitches() == this.getBestResult().getnSwitches()) {
                    noChange+=1;
                }


                improved+=1;
            }
*/

            //LOGGING
            if (steps % 1000 == 0) {
                this.logger.log(this.getWorkingResult(), temperature);
            }

            if(steps % 10000 == 0) {
                this.logger.writeResult(this.getWorkingResult());
                //this.getLogger().writeLiveResult(this.getWorkingResult());
            }


            // - PREPARE FOR NEW ITERATION - -

            //Copy for new iteration
            this.workingResult = this.currentResult.getCopy();

            /*//Keep temperature steady for a few steps before dropping
            if(steady > 70) {
                temperature = temperature * DECAY_RATE;
                steady=0;
            }
            steady++;*/


            if(noChange > 7000) {
                this.logger.logInfo("SA Stopped: result not changing");
                break;
            }

            //Reduce temperature
            temperature = temperature * this.getParameters().getDECAY_RATE();


            if(temperature < this.getParameters().getEND_TEMP()) {
                this.logger.logInfo("SA Stopped: min temp reached");

                break;
            }


            steps++;
        }


        if(!(System.currentTimeMillis() < this.getTIME_LIMIT())) {
            this.logger.logInfo("SA Stopped: time limit exceeded");
        }else{
            this.logger.logInfo("SA Stopped: max steps reached");

        }

        this.logger.logInfo("Number of steps used:" + String.valueOf(steps));

    }



    /* UTILITIES ------------------------------------------------------------------ */


    //TODO:
    public void runDecodeExperiment() {

    }




    public Job getJob(int id) {
        return this.getJobs()[id];
    }

    public boolean legalJob(int id) {
        return id >= 0 && id < this.getN_JOBS();
    }

    /* GETTERS & SETTERS ------------------------------------------------------------------ */

    public long getTIME_LIMIT() {
        return TIME_LIMIT;
    }

    public long getRUN_TIME() {
        return RUN_TIME;
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


    public int[][] getDIFFERENCE_MATRIX() {
        return DIFFERENCE_MATRIX;
    }

    public void setDIFFERENCE_MATRIX(int[][] DIFFERENCE_MATRIX) {
        this.DIFFERENCE_MATRIX = DIFFERENCE_MATRIX;
    }

    public void setImproved(long improved) {
        this.improved = improved;
    }


    public void setTOOL_PAIR_GRAPH(MutableValueGraph<Integer, Integer> TOOL_PAIR_GRAPH) {
        this.TOOL_PAIR_GRAPH = TOOL_PAIR_GRAPH;
    }

    public MutableValueGraph<Integer, Integer> getTOOL_PAIR_GRAPH() {
        return TOOL_PAIR_GRAPH;
    }
}
