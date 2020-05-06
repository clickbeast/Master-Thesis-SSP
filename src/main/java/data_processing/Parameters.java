package data_processing;

public class Parameters {

    private String PROJECT_ROOT;
    private String ROOT_FOLDER;
    private String INSTANCE_FOLDER;
    private String INSTANCE;
    private String RUN_TYPE;
    private String INPUT_FILE_PATH;
    private String LOG_PATH;
    private String RESULTS_PATH;
    private String SOLUTION_PATH;
    private String LIVE_RESULT_PATH;

    private  long RUN_TIME;
    private  long START_TIME;

    private  int SEED;
    private  double START_TEMP;
    private  double END_TEMP;
    private  double DECAY_RATE;

    public Parameters(String PROJECT_ROOT,String ROOT_FOLDER, String INSTANCE, String RUN_TYPE, long RUN_TIME, long START_TIME, int SEED, double START_TEMP, double END_TEMP, double DECAY_RATE) {
        this.PROJECT_ROOT = PROJECT_ROOT;
        this.ROOT_FOLDER = ROOT_FOLDER;
        this.INSTANCE = INSTANCE;
        this.INSTANCE_FOLDER = ROOT_FOLDER + "/" + INSTANCE;
        this.INPUT_FILE_PATH = ROOT_FOLDER + "/" +  INSTANCE  + "/"  + INSTANCE + ".json";
        this.LIVE_RESULT_PATH = PROJECT_ROOT + "/data/instances/live.txt";
        this.RUN_TYPE = RUN_TYPE;
        this.RUN_TIME = RUN_TIME;
        this.START_TIME = START_TIME;
        this.SEED = SEED;
        this.START_TEMP = START_TEMP;
        this.END_TEMP = END_TEMP;
        this.DECAY_RATE = DECAY_RATE;

        this.createAdditionalFilePaths();
    }

    public void createAdditionalFilePaths() {
        String logPath = this.getINSTANCE_FOLDER() + "/" + "log_" + this.getRUN_TYPE() + ".csv";
        this.setLOG_PATH(logPath);
        String resultPath = this.getINSTANCE_FOLDER() + "/" + "result_" + this.getRUN_TYPE() + ".txt";
        this.setRESULTS_PATH(resultPath);
        String solutionPath = this.getINSTANCE_FOLDER() + "/" + "solution_" + this.getRUN_TYPE() + ".txt";
        this.setSOLUTION_PATH(solutionPath);
    }


    public void resetStartTime() {
        this.setSTART_TIME(System.currentTimeMillis());
    }

    public String getROOT_FOLDER() {
        return ROOT_FOLDER;
    }

    public void setROOT_FOLDER(String ROOT_FOLDER) {
        this.ROOT_FOLDER = ROOT_FOLDER;
    }

    public String getInstance() {
        return INSTANCE;
    }

    public void setInstance(String instance) {
        this.INSTANCE = instance;
    }

    public String getINPUT_FILE_PATH() {
        return INPUT_FILE_PATH;
    }

    public void setINPUT_FILE_PATH(String INPUT_FILE_PATH) {
        this.INPUT_FILE_PATH = INPUT_FILE_PATH;
    }

    public long getRUN_TIME() {
        return RUN_TIME;
    }

    public void setRUN_TIME(long RUN_TIME) {
        this.RUN_TIME = RUN_TIME;
    }

    public int getSEED() {
        return SEED;
    }

    public void setSEED(int SEED) {
        this.SEED = SEED;
    }

    public double getSTART_TEMP() {
        return START_TEMP;
    }

    public void setSTART_TEMP(double START_TEMP) {
        this.START_TEMP = START_TEMP;
    }

    public double getEND_TEMP() {
        return END_TEMP;
    }

    public void setEND_TEMP(double END_TEMP) {
        this.END_TEMP = END_TEMP;
    }

    public double getDECAY_RATE() {
        return DECAY_RATE;
    }

    public void setDECAY_RATE(double DECAY_RATE) {
        this.DECAY_RATE = DECAY_RATE;
    }

    public String getINSTANCE_FOLDER() {
        return INSTANCE_FOLDER;
    }

    public void setINSTANCE_FOLDER(String INSTANCE_FOLDER) {
        this.INSTANCE_FOLDER = INSTANCE_FOLDER;
    }


    public String getINSTANCE() {
        return INSTANCE;
    }

    public String getRUN_TYPE() {
        return RUN_TYPE;
    }


    public void setINSTANCE(String INSTANCE) {
        this.INSTANCE = INSTANCE;
    }

    public void setRUN_TYPE(String RUN_TYPE) {
        this.RUN_TYPE = RUN_TYPE;
    }

    public long getSTART_TIME() {
        return START_TIME;
    }

    public String getLIVE_RESULT_PATH() {
        return LIVE_RESULT_PATH;
    }

    public void setLIVE_RESULT_PATH(String LIVE_RESULT_PATH) {
        this.LIVE_RESULT_PATH = LIVE_RESULT_PATH;
    }

    public void setSTART_TIME(long START_TIME) {
        this.START_TIME = START_TIME;
    }

    public String getLOG_PATH() {
        return LOG_PATH;
    }

    public void setLOG_PATH(String LOG_PATH) {
        this.LOG_PATH = LOG_PATH;
    }

    public String getRESULTS_PATH() {
        return RESULTS_PATH;
    }

    public void setRESULTS_PATH(String RESULTS_PATH) {
        this.RESULTS_PATH = RESULTS_PATH;
    }

    public String getSOLUTION_PATH() {
        return SOLUTION_PATH;
    }

    public void setSOLUTION_PATH(String SOLUTION_PATH) {
        this.SOLUTION_PATH = SOLUTION_PATH;
    }
}
