package data_processing;

public class Parameters {

    private String ROOT_FOLDER;
    private String INSTANCE_FOLDER;
    private String INSTANCE;
    private String RUN_ID;
    private String INSTANCE_JSON_PATH;

    private  long RUN_TIME;
    private  long START_TIME;

    private  int SEED;
    private  double START_TEMP;
    private  double END_TEMP;
    private  double DECAY_RATE;


    public Parameters(String ROOT_FOLDER, String INSTANCE, String RUN_TYPE) {
        this.ROOT_FOLDER = ROOT_FOLDER;
        this.INSTANCE = INSTANCE;
        this.INSTANCE_FOLDER = ROOT_FOLDER + "/" + INSTANCE;
        this.INSTANCE_JSON_PATH = ROOT_FOLDER + "/" +  INSTANCE  + "/"  + INSTANCE + ".json";
        this.RUN_ID = RUN_TYPE;
    }

    public Parameters(String ROOT_FOLDER, String INSTANCE, String RUN_TYPE, long RUN_TIME, long START_TIME, int SEED, double START_TEMP, double END_TEMP, double DECAY_RATE) {
        this.ROOT_FOLDER = ROOT_FOLDER;
        this.INSTANCE = INSTANCE;
        this.INSTANCE_FOLDER = ROOT_FOLDER + "/" + INSTANCE;
        this.INSTANCE_JSON_PATH = ROOT_FOLDER + "/" +  INSTANCE  + "/"  + INSTANCE + ".json";
        this.RUN_ID = RUN_TYPE;

        this.RUN_TIME = RUN_TIME;
        this.START_TIME = START_TIME;
        this.SEED = SEED;
        this.START_TEMP = START_TEMP;
        this.END_TEMP = END_TEMP;
        this.DECAY_RATE = DECAY_RATE;
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

    public String getINSTANCE_JSON_PATH() {
        return INSTANCE_JSON_PATH;
    }

    public void setINSTANCE_JSON_PATH(String INSTANCE_JSON_PATH) {
        this.INSTANCE_JSON_PATH = INSTANCE_JSON_PATH;
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

    public String getRUN_ID() {
        return RUN_ID;
    }
}
