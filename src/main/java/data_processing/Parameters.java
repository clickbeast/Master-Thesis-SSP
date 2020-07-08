package data_processing;
import picocli.CommandLine.Option;

import java.util.Map;


public class Parameters {

    @Option(names = {"--project_root"})
    private String PROJECT_ROOT             =   "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/" +
                                                    "MasterProef/Master-Thesis-SSP";

    @Option(names = {"--root_folder"})
    private String ROOT_FOLDER              =   "/Users/simonvermeir/Documents/industrial-engineering/" +
                                                "SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/" +
                                                "yanasse";
    @Option(names = {"--instance_folder"})
    private String INSTANCE_FOLDER          =   "yanasse";
    //cat_30_40_17_9
    //yan_8_15_10_29
    @Option(names = {"--instance"})
    //private String INSTANCE                 =   "cat_30_40_17_9";
    //private String INSTANCE                   =   "yan_8_15_10_29";
    private String INSTANCE                   =   "yan_5_6_3_1";
    //private String INSTANCE                 =   "yan_8_15_10_29";


    @Option(names = {"--run_type"})
    private String RUN_TYPE                 =   "TEST";


    private String INPUT_FILE_PATH          =   "/";
    private String LOG_PATH                 =   "/";
    private String RESULTS_PATH             =   "/";
    private String SOLUTION_PATH            =   "/";
    private String PARAMETER_PATH           =   "/";
    private String LIVE_RESULT_PATH         =   "/";



    @Option(names = {"--run_time"})
    private  long RUN_TIME                  =   60;
    private  long START_TIME                =   0;


    //CHOICES
    @Option(names = {"--constructive_heuristic"})
    private String constructiveHeuristic = "random";
    @Option(names = {"--local_search"})
    private String localSearch = "ruinAndRecreate";
    @Option(names = {"--meta_heuristic"})
    private String metaHeuristic = "steepestDescent";
    @Option(names = {"--objective"})
    private String objective = "switches";


    //OBJ
    @Option(names = {"--w_s"})
    private double W_S                      =   1;
    @Option(names = {"--w_hops"})
    private double W_HOPS                   =   1;
    @Option(names = {"--w_ktns_hops"})
    private double W_KTNS_HOPS              =   1;
    @Option(names = {"--w_dist"})
    private double W_DIST                   =   1;
    @Option(names = {"--w_dist_min"})
    private double W_DIST_MIN               =   1;
    @Option(names = {"--w_dist_max"})
    private double W_DIST_MAX               =   1;


    //RR
    @Option(names = {"--select"})
    private String select                   =   "";
    @Option(names = {"--match"})
    private String match               =   "";
    @Option(names = {"--insert"})
    private String insert               =   "";
    @Option(names = {"--decode"})
    private String decode               =   "";

    //STOCHASTIC
    @Option(names = {"--seed"})
    private  int SEED                       =   7;

    //SA
    @Option(names = {"--blink_rate"})
    private double BLINK_RATE               =   0.01;
        @Option(names = {"--avg_ruin"})
    private int AVG_RUIN                    =   10;

    //SA
    @Option(names = {"--sa_timed"})
    private  boolean SA_TIMED               =   false;

    @Option(names = {"--start_temp"})
    private  double  START_TEMP             =   100;
    @Option(names = {"--end_temp"})
    private  double  END_TEMP               =   0.00097;
    @Option(names = {"--decay_rate"})
    //private  double  DECAY_RATE             =   0.9990;
    private  double  DECAY_RATE             =   -1;
    @Option(names = {"--force_alpha"})
    private  boolean FORCE_ALPHA            =   true;
    @Option(names = {"--iterations"})
    private  long    ITERATIONS             =   -1;
    @Option(names = {"--alpha"})
    private  double  ALPHA                  =   -1;
    @Option(names = {"--beta"})
    private  double  BETA                   =   0;
    @Option(names = {"--w_f"})
    private  double  W_F                    =   1;
    @Option(names = {"--w_alpha"})
    private  double  W_ALPHA                =   0.737;
    @Option(names = {"--w_jobs"})
    private  double  W_JOBS                 =   0.60;
    @Option(names = {"--w_tools"})
    private  double  W_TOOLS                =   0.97;
    @Option(names = {"--w_mag"})
    private  double  W_MAG                  =   0.97;
    @Option(names = {"--w_tm"})
    private  double  W_TM                   =   0.2;


    //FORCE SEQUECE
    private int[] forceSequence = {1,2,3,4,5,6,7,8,9};

    //PROBLEM
    private int N_JOBS                      = -1;
    private int N_TOOLS                     = -1;
    private int MAGAZINE_SIZE               = -1;


    //CONFIG
    @Option(names = {"--log_info"})
    private boolean LOG_INFO                = true;
    @Option(names = {"--log_verbose"})
    private boolean LOG_VERBOSE             = true;
    @Option(names = {"--log"})
    private boolean LOG                     = true;
    @Option(names = {"--write_results"})
    private boolean WRITE_RESULTS           = true;
    @Option(names = {"--live_result"})
    private boolean LIVE_RESULT             = false;


    public Parameters(long START_TIME) {
        this.START_TIME = START_TIME;
    }



    public Parameters(String PROJECT_ROOT, String ROOT_FOLDER, String INSTANCE, String RUN_TYPE, long RUN_TIME,
                      long START_TIME, int SEED, double START_TEMP, double END_TEMP, double DECAY_RATE) {
        this.setPROJECT_ROOT(PROJECT_ROOT);
        this.ROOT_FOLDER = ROOT_FOLDER;
        this.INSTANCE = INSTANCE;
        
        
        this.RUN_TYPE = RUN_TYPE;
        this.RUN_TIME = RUN_TIME;
        this.START_TIME = START_TIME;
        this.SEED = SEED;
        this.START_TEMP = START_TEMP;
        this.END_TEMP = END_TEMP;
        this.DECAY_RATE = DECAY_RATE;
        this.createAdditionalFilePaths();
    }


    public Parameters(String PROJECT_ROOT, String ROOT_FOLDER, String INSTANCE, String RUN_TYPE, long RUN_TIME,
                      long START_TIME, int SEED, double START_TEMP, double END_TEMP, double DECAY_RATE,
                      boolean SA_TIMED, long ITERATIONS, double ALPHA,  double BETA, double w_F, double w_ALPHA,
                      double w_JOBS, double w_TOOLS, double w_MAG, double w_TM, boolean LOG_VERBOSE,
                      boolean LOG, boolean WRITE_RESULTS, boolean LIVE_RESULT) {


        this.setPROJECT_ROOT(PROJECT_ROOT);
        this.ROOT_FOLDER = ROOT_FOLDER;
        this.INSTANCE = INSTANCE;
        this.RUN_TYPE = RUN_TYPE;
        this.RUN_TIME = RUN_TIME;
        this.START_TIME = START_TIME;
        this.SEED = SEED;
        this.START_TEMP = START_TEMP;
        this.END_TEMP = END_TEMP;
        this.DECAY_RATE = DECAY_RATE;

        this.createAdditionalFilePaths();

        //SA PARAMS
        this.setSA_TIMED(SA_TIMED);
        this.ITERATIONS = ITERATIONS;
        this.setALPHA(ALPHA);
        this.setBETA(BETA);
        W_F = w_F;
        W_ALPHA = w_ALPHA;
        W_JOBS = w_JOBS;
        W_TOOLS = w_TOOLS;
        W_MAG = w_MAG;
        W_TM = w_TM;


        //LOG PARAMS
        this.LOG_VERBOSE = LOG_VERBOSE;
        this.LOG = LOG;
        this.WRITE_RESULTS = WRITE_RESULTS;
        this.LIVE_RESULT = LIVE_RESULT;


    }


    public void parametersRead() {
        this.createAdditionalFilePaths();
    }

    public void problemInstantiated(int n_JOBS, int n_TOOLS, int magazine_size) {
        this.setN_JOBS(n_JOBS);
        this.setN_TOOLS(n_TOOLS);
        this.setMAGAZINE_SIZE(magazine_size);
        generateSAParameters();
        System.out.println("Parameters set.");
        System.out.println(toString());
    }




    public void generateSAParameters() {

        if(getDECAY_RATE() != -1) {
            return;
        }

        if(getITERATIONS() != -1) {
            this.calculateDecayRate();
            return;
        }


        if(getALPHA() != - 1) {
            this.calculateIterations();
            this.calculateDecayRate();
            return;
        }

        this.calculateAlpha();
        this.calculateIterations();
        this.calculateDecayRate();
        System.out.println("bonjour");
    }

    public void calculateAlpha() {
        if(isFORCE_ALPHA()) {
            this.forceAlpha();
            return;
        }

        this.setALPHA(getW_ALPHA()
                * ( (getW_JOBS() * getN_JOBS())
                    + (getW_TM() *
                        ((getW_TOOLS() * getN_TOOLS()) - (getW_MAG() * getMAGAZINE_SIZE()))
                       )
                )
        );
    }


    public void forceAlpha() {
        //0.060  slower
        //0.0470  faster
        this.setALPHA(18 * getN_JOBS() * 0.014);
    }

    public void forceIterations() {
        double alpha = 18 * getN_JOBS() * 0.014;


        this.setITERATIONS(
                (long) ( 12000 + (getW_F() * Math.pow(10,alpha))));
    }


    public void calculateIterations() {
        this.setITERATIONS(
                (long) (getW_F() * Math.pow(10,(this.getALPHA() + this.getBETA()))));
    }


    public void calculateDecayRate() {
        this.setDECAY_RATE(Math.pow((this.getEND_TEMP()/this.getSTART_TEMP()), ( (float) 1/getITERATIONS())));
    }


    public void createAdditionalFilePaths() {

        this.INSTANCE_FOLDER = ROOT_FOLDER + "/" + INSTANCE;
        this.INPUT_FILE_PATH = ROOT_FOLDER + "/" + INSTANCE + "/" + INSTANCE + ".json";
        this.LIVE_RESULT_PATH = getPROJECT_ROOT() + "/data/instances/live.txt";
        
        String logPath = this.getINSTANCE_FOLDER() + "/" + "log_" + this.getRUN_TYPE() + ".csv";
        this.setLOG_PATH(logPath);
        String resultPath = this.getINSTANCE_FOLDER() + "/" + "result_" + this.getRUN_TYPE() + ".txt";
        this.setRESULTS_PATH(resultPath);
        String solutionPath = this.getINSTANCE_FOLDER() + "/" + "solution_" + this.getRUN_TYPE() + ".txt";
        this.setSOLUTION_PATH(solutionPath);
        String parameterPath = this.getINSTANCE_FOLDER() + "/" + "parameter_" + this.getRUN_TYPE() + ".txt";
        this.setPARAMETER_PATH(parameterPath);

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

    public long getITERATIONS() {
        return ITERATIONS;
    }

    public void setITERATIONS(long ITERATIONS) {
        this.ITERATIONS = ITERATIONS;
    }

    public double getW_F() {
        return W_F;
    }

    public void setW_F(double w_F) {
        W_F = w_F;
    }

    public double getW_ALPHA() {
        return W_ALPHA;
    }

    public void setW_ALPHA(double w_ALPHA) {
        W_ALPHA = w_ALPHA;
    }

    public double getW_JOBS() {
        return W_JOBS;
    }

    public void setW_JOBS(double w_JOBS) {
        W_JOBS = w_JOBS;
    }

    public double getW_TOOLS() {
        return W_TOOLS;
    }

    public void setW_TOOLS(double w_TOOLS) {
        W_TOOLS = w_TOOLS;
    }

    public double getW_MAG() {
        return W_MAG;
    }

    public void setW_MAG(double w_MAG) {
        W_MAG = w_MAG;
    }

    public double getW_TM() {
        return W_TM;
    }

    public void setW_TM(double w_TM) {
        W_TM = w_TM;
    }

    public boolean isLOG_VERBOSE() {
        return LOG_VERBOSE;
    }

    public void setLOG_VERBOSE(boolean LOG_VERBOSE) {
        this.LOG_VERBOSE = LOG_VERBOSE;
    }

    public boolean isLOG() {
        return LOG;
    }

    public void setLOG(boolean LOG) {
        this.LOG = LOG;
    }

    public boolean isWRITE_RESULTS() {
        return WRITE_RESULTS;
    }

    public void setWRITE_RESULTS(boolean WRITE_RESULTS) {
        this.WRITE_RESULTS = WRITE_RESULTS;
    }

    public boolean isLIVE_RESULT() {
        return LIVE_RESULT;
    }

    public void setLIVE_RESULT(boolean LIVE_RESULT) {
        this.LIVE_RESULT = LIVE_RESULT;
    }

    public double getALPHA() {
        return ALPHA;
    }

    public void setALPHA(double ALPHA) {
        this.ALPHA = ALPHA;
    }

    public int getN_JOBS() {
        return N_JOBS;
    }

    public void setN_JOBS(int n_JOBS) {
        N_JOBS = n_JOBS;
    }

    public int getN_TOOLS() {
        return N_TOOLS;
    }

    public void setN_TOOLS(int n_TOOLS) {
        N_TOOLS = n_TOOLS;
    }

    public int getMAGAZINE_SIZE() {
        return MAGAZINE_SIZE;
    }

    public void setMAGAZINE_SIZE(int MAGAZINE_SIZE) {
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
    }

    public boolean isSA_TIMED() {
        return SA_TIMED;
    }

    public void setSA_TIMED(boolean SA_TIMED) {
        this.SA_TIMED = SA_TIMED;
    }

    public double getBETA() {
        return BETA;
    }

    public void setBETA(double BETA) {
        this.BETA = BETA;
    }

    public String getPROJECT_ROOT() {
        return PROJECT_ROOT;
    }

    public void setPROJECT_ROOT(String PROJECT_ROOT) {
        this.PROJECT_ROOT = PROJECT_ROOT;
    }

    public double getW_S() {
        return W_S;
    }

    public void setW_S(double w_S) {
        W_S = w_S;
    }

    public double getW_HOPS() {
        return W_HOPS;
    }

    public void setW_HOPS(double w_HOPS) {
        W_HOPS = w_HOPS;
    }

    public double getW_KTNS_HOPS() {
        return W_KTNS_HOPS;
    }

    public void setW_KTNS_HOPS(double w_KTNS_HOPS) {
        W_KTNS_HOPS = w_KTNS_HOPS;
    }

    public double getW_DIST() {
        return W_DIST;
    }

    public void setW_DIST(double w_DIST) {
        W_DIST = w_DIST;
    }

    public double getW_DIST_MIN() {
        return W_DIST_MIN;
    }

    public void setW_DIST_MIN(double w_DIST_MIN) {
        W_DIST_MIN = w_DIST_MIN;
    }

    public double getW_DIST_MAX() {
        return W_DIST_MAX;
    }

    public void setW_DIST_MAX(double w_DIST_MAX) {
        W_DIST_MAX = w_DIST_MAX;
    }

    public double getBLINK_RATE() {
        return BLINK_RATE;
    }

    public void setBLINK_RATE(double BLINK_RATE) {
        this.BLINK_RATE = BLINK_RATE;
    }

    public int getAVG_RUIN() {
        return AVG_RUIN;
    }

    public void setAVG_RUIN(int AVG_RUIN) {
        this.AVG_RUIN = AVG_RUIN;
    }

    public String getPARAMETER_PATH() {
        return PARAMETER_PATH;
    }

    public void setPARAMETER_PATH(String PARAMETER_PATH) {
        this.PARAMETER_PATH = PARAMETER_PATH;
    }


    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public boolean isLOG_INFO() {
        return LOG_INFO;
    }

    public void setLOG_INFO(boolean LOG_INFO) {
        this.LOG_INFO = LOG_INFO;
    }

    public String getConstructiveHeuristic() {
        return constructiveHeuristic;
    }

    public void setConstructiveHeuristic(String constructiveHeuristic) {
        this.constructiveHeuristic = constructiveHeuristic;
    }

    public String getLocalSearch() {
        return localSearch;
    }

    public void setLocalSearch(String localSearch) {
        this.localSearch = localSearch;
    }

    public String getMetaHeuristic() {
        return metaHeuristic;
    }

    public void setMetaHeuristic(String metaHeuristic) {
        this.metaHeuristic = metaHeuristic;
    }

    public int[] getForceSequence() {
        return forceSequence;
    }

    public void setForceSequence(int[] forceSequence) {
        this.forceSequence = forceSequence;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "PROJECT_ROOT='" + PROJECT_ROOT + '\'' +
                ", ROOT_FOLDER='" + ROOT_FOLDER + '\'' +
                ", INSTANCE_FOLDER='" + INSTANCE_FOLDER + '\'' +
                ", INSTANCE='" + INSTANCE + '\'' +
                ", RUN_TYPE='" + RUN_TYPE + '\'' +
                ", INPUT_FILE_PATH='" + INPUT_FILE_PATH + '\'' +
                ", LOG_PATH='" + LOG_PATH + '\'' +
                ", RESULTS_PATH='" + RESULTS_PATH + '\'' +
                ", SOLUTION_PATH='" + SOLUTION_PATH + '\'' +
                ", LIVE_RESULT_PATH='" + LIVE_RESULT_PATH + '\'' +
                ", RUN_TIME=" + RUN_TIME +
                ", START_TIME=" + START_TIME +
                ", W_S=" + W_S +
                ", W_HOPS=" + W_HOPS +
                ", W_KTNS_HOPS=" + W_KTNS_HOPS +
                ", W_DIST=" + W_DIST +
                ", W_DIST_MIN=" + W_DIST_MIN +
                ", W_DIST_MAX=" + W_DIST_MAX +
                ", SEED=" + SEED +
                ", BLINK_RATE=" + BLINK_RATE +
                ", AVG_RUIN=" + AVG_RUIN +
                ", SA_TIMED=" + SA_TIMED +
                ", START_TEMP=" + START_TEMP +
                ", END_TEMP=" + END_TEMP +
                ", DECAY_RATE=" + DECAY_RATE +
                ", ITERATIONS=" + ITERATIONS +
                ", ALPHA=" + ALPHA +
                ", BETA=" + BETA +
                ", W_F=" + W_F +
                ", W_ALPHA=" + W_ALPHA +
                ", W_JOBS=" + W_JOBS +
                ", W_TOOLS=" + W_TOOLS +
                ", W_MAG=" + W_MAG +
                ", W_TM=" + W_TM +
                ", N_JOBS=" + N_JOBS +
                ", N_TOOLS=" + N_TOOLS +
                ", MAGAZINE_SIZE=" + MAGAZINE_SIZE +
                ", LOG_INFO=" + LOG_INFO +
                ", LOG_VERBOSE=" + LOG_VERBOSE +
                ", LOG=" + LOG +
                ", WRITE_RESULTS=" + WRITE_RESULTS +
                ", LIVE_RESULT=" + LIVE_RESULT +
                '}';
    }

    public boolean isFORCE_ALPHA() {
        return FORCE_ALPHA;
    }

    public void setFORCE_ALPHA(boolean FORCE_ALPHA) {
        this.FORCE_ALPHA = FORCE_ALPHA;
    }
}
