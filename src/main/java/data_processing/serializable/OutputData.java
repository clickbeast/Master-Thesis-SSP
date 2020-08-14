package data_processing.serializable;

import core.Result;

public class OutputData {
    //CHANGEABLE
    private Result result;
    private long id;
    private long timeRunning;
    private long timeRemaining;

    //CONSTANTS
    private  int magazineSize;
    private  int nTools;
    private  int nJobs;
    private int[][] jobToolMatrix;
    //Amount of different tool loading differences
    private int[][] differenceMatrix;
    //Amount common tools
    private int[][][] sharedToolsMatrix;

    private int[][] switchesLbMatrix;
    private int[][] toolPairMatrix;

    private String instance = "bonjour";


    public OutputData(String instance, int MAGAZINE_SIZE, int n_TOOLS, int n_JOBS, int[][] JOB_TOOL_MATRIX,
                      int[][][] SHARED_TOOLS_MATRIX, int[][] SWITCHES_LB_MATRIX, int[][] TOOL_PAIR_MATRIX) {
        this.instance = instance;
        this.magazineSize = MAGAZINE_SIZE;
        nTools = n_TOOLS;
        nJobs = n_JOBS;
        this.jobToolMatrix = JOB_TOOL_MATRIX;
        this.differenceMatrix = null;
        this.sharedToolsMatrix = SHARED_TOOLS_MATRIX;
        this.switchesLbMatrix = SWITCHES_LB_MATRIX;
        this.toolPairMatrix = TOOL_PAIR_MATRIX;
    }


    public void updateData(long id,  long  timeRunning, long timeRemaining, Result result) {
        this.setResult(result);
        this.setId(id);
        this.setTimeRunning(timeRunning);
        this.setTimeRemaining(timeRemaining);
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeRunning() {
        return timeRunning;
    }

    public void setTimeRunning(long timeRunning) {
        this.timeRunning = timeRunning;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public void setMagazineSize(int magazineSize) {
        this.magazineSize = magazineSize;
    }

    public int getnTools() {
        return nTools;
    }

    public void setnTools(int nTools) {
        this.nTools = nTools;
    }

    public int getnJobs() {
        return nJobs;
    }

    public void setnJobs(int nJobs) {
        this.nJobs = nJobs;
    }

    public int[][] getJobToolMatrix() {
        return jobToolMatrix;
    }

    public void setJobToolMatrix(int[][] jobToolMatrix) {
        this.jobToolMatrix = jobToolMatrix;
    }

    public int[][] getDifferenceMatrix() {
        return differenceMatrix;
    }

    public void setDifferenceMatrix(int[][] differenceMatrix) {
        this.differenceMatrix = differenceMatrix;
    }

    public int[][][] getSharedToolsMatrix() {
        return sharedToolsMatrix;
    }

    public void setSharedToolsMatrix(int[][][] sharedToolsMatrix) {
        this.sharedToolsMatrix = sharedToolsMatrix;
    }

    public int[][] getSwitchesLbMatrix() {
        return switchesLbMatrix;
    }

    public void setSwitchesLbMatrix(int[][] switchesLbMatrix) {
        this.switchesLbMatrix = switchesLbMatrix;
    }

    public int[][] getToolPairMatrix() {
        return toolPairMatrix;
    }

    public void setToolPairMatrix(int[][] toolPairMatrix) {
        this.toolPairMatrix = toolPairMatrix;
    }
}
