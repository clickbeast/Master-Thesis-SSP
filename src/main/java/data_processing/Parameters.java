package data_processing;

public class Parameters {

    private String allInstancesFolder;
    private String instanceFolder;
    private String instance;
    private String jsonFile;

    private  long TIME_LIMIT;
    private  long RUN_TIME;

    private  int SEED;
    private  double START_TEMP;
    private  double END_TEMP;
    private  double DECAY_RATE;


    public Parameters(String allInstancesFolder, String instance) {
        this.allInstancesFolder = allInstancesFolder;
        this.instance = instance;
        this.instanceFolder = allInstancesFolder + "/" + instance;
        this.jsonFile = allInstancesFolder + "/" +  instance  + "/"  + instance + ".json";
    }


    public String getAllInstancesFolder() {
        return allInstancesFolder;
    }

    public void setAllInstancesFolder(String allInstancesFolder) {
        this.allInstancesFolder = allInstancesFolder;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public long getTIME_LIMIT() {
        return TIME_LIMIT;
    }

    public void setTIME_LIMIT(long TIME_LIMIT) {
        this.TIME_LIMIT = TIME_LIMIT;
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

    public String getInstanceFolder() {
        return instanceFolder;
    }

    public void setInstanceFolder(String instanceFolder) {
        this.instanceFolder = instanceFolder;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "allInstancesFolder='" + allInstancesFolder + '\'' +
                ", instanceFolder='" + instanceFolder + '\'' +
                ", instance='" + instance + '\'' +
                ", jsonFile='" + jsonFile + '\'' +
                ", TIME_LIMIT=" + TIME_LIMIT +
                ", RUN_TIME=" + RUN_TIME +
                ", SEED=" + SEED +
                ", START_TEMP=" + START_TEMP +
                ", END_TEMP=" + END_TEMP +
                ", DECAY_RATE=" + DECAY_RATE +
                '}';
    }
}
