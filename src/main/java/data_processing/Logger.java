package data_processing;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import com.google.gson.Gson;
import core.ProblemManager;
import core.Result;
import java.io.*;
import java.util.Arrays;

public class Logger {

    //public CsvAppender csvAppender;
    ProblemManager problemManager;
    //CsvWriter csvWriter;
    ColoredPrinter cp;

    String spacing = "%-5s %-15s %-5s %-15s %-5s %-15s %-5s %-20s %-10s %-10s %-20s %-10s %-10s %-15s %-10s %-30s %-20s \n";
    //String[] logTitles2 = {"Switches", "Best Switches", "Rem Dist" , "Best Rem Dist" , "Accepted", "Rejected" , "Improved", "Step" , "Time Remaining" , "Sequence"};
    String[] logTitles = {"SW", "B_SW", "THOP" , "B_THOP" , "TAD", "B_TAD" , "TRD", "B_TRD" , "ACCEPT" , "REJECT", "IMPROVE", "STEP", "T_RUN","T_REM", "TEMP", "SEQ","TYPE","TYPEID"};


    private PrintWriter logWriter;
    private PrintWriter resultsWriter;
    private PrintWriter solutionWriter;

    private long resultsCount = 0;
    private OutputData outputData;
    private Gson gson;


    /* SETUP ------------------------------------------------------------------ */



    public Logger(ProblemManager problemManager,
                  PrintWriter logWriter,
                  PrintWriter resultsWriter,
                  PrintWriter solutionWriter) throws IOException {

        this.problemManager = problemManager;

        //Setup color printer
        cp = new ColoredPrinter.Builder(1, true)
                .foreground(Ansi.FColor.BLUE)
                .build();

        this.createLogFile();
        this.createResultsFile();
        this.createSolutionFile();

        this.logWriter = logWriter;
        this.resultsWriter = resultsWriter;
        this.solutionWriter = solutionWriter;
        this.gson = new Gson();

        this.outputData = new OutputData(
                this.getProblemManager().getMAGAZINE_SIZE(),
                this.getProblemManager().getN_TOOLS(),
                this.getProblemManager().getN_JOBS(),
                this.getProblemManager().getJOB_TOOL_MATRIX(),
                this.getProblemManager().getDIFFERENCE_MATRIX(),
                this.getProblemManager().getSHARED_TOOLS_MATRIX(),
                this.getProblemManager().getSWITCHES_LB_MATRIX(),
                this.getProblemManager().getTOOL_PAIR_MATRIX()
        );

    }



    public void createLogFile() throws IOException {
        Writer fileWriter = new FileWriter(this.problemManager.getParameters().getLOG_PATH(), false);
    }

    public void createResultsFile() throws IOException {
        Writer fileWriter = new FileWriter(this.problemManager.getParameters().getRESULTS_PATH(), false);

    }

    public void createSolutionFile() throws IOException {
        Writer fileWriter = new FileWriter(this.problemManager.getParameters().getSOLUTION_PATH(), false);
    }



    /* SETUP ------------------------------------------------------------------ */




    /* PROGRESS ------------------------------------------------------------------ */

    public void logLegend(String[] titles)  {
        PrintWriter out = this.getLogWriter();

        System.out.println();
        System.out.printf(spacing, (Object[]) logTitles);
        System.out.println();

        String logSpacing = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s";
        out.printf(logSpacing, (Object[]) logTitles);
        out.println();

    }

    public void log( int switches, int bestSwitches, int[] sequence ) throws IOException {
        this.log(switches, bestSwitches,-1,-1,-1,-1, sequence,"");
    }

    public void log( int switches, int bestSwitches, long accepted, long rejected, long improved,int[] sequence ) throws IOException {
        this.log(switches, bestSwitches,accepted,rejected,improved,-1, sequence,"");
    }

    public void log(Result result) throws IOException {
        this.log(result, -1);
    }

    public void log(Result result, double temperature) throws IOException {
        this.log(result.getnSwitches(),this.problemManager.getBestResult().getnSwitches(), this.problemManager.getAccepted(),this.problemManager.getRejected(), this.problemManager.getImproved(), temperature, result.getSequence(), result.getType());
    }

    public void log( int switches, int bestSwitches, long accepted, long rejected, long improved, double temperature, int[] sequence, String type) throws IOException {
        PrintWriter out = this.getLogWriter();
        long timeRunning = this.getTimeRunning();
        long timeRemaining = this.getTimeRemaining();
        System.out.printf(spacing,
                //SW
                switches,
                // B_SW
                bestSwitches,
                // HOP
                "",
                // B_THOP
                "",
                // TAD
                "",
                // B_TAD
                "",
                // TRD
                "",
                // B_TRD
                "",
                // ACCEPT
                accepted,
                // REJECT
                rejected,
                // IMPROVE
                improved,
                // STEP
                this.getProblemManager().getSteps(),
                // T_RUN
                timeRunning,
                // T_REM
                timeRemaining,
                // TEMP
                temperature ,
                // SEQ
                Arrays.toString(sequence),
                //TYPE
                type

        );

        String logSpacing = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,\"%s\"j,%s";
        out.printf(logSpacing,
                //SW
                switches,
                // B_SW
                bestSwitches,
                // HOP
                "",
                // B_THOP
                "",
                // TAD
                "",
                // B_TAD
                "",
                // TRD
                "",
                // B_TRD
                "",
                // ACCEPT
                accepted,
                // REJECT
                rejected,
                // IMPROVE
                improved,
                // STEP
                this.getProblemManager().getSteps(),
                // T_RUN
                this.getTimeRunning(),
                // T_REM
                this.getTimeRemaining(),
                // TEMP
                temperature ,
                // SEQ
                Arrays.toString(sequence),
                //TYPE
                type
        );

        out.println();


    }


    /* SOLUTIONS & RESULTS ------------------------------------------------------------------ */


    public void writeSolution(Result result) throws IOException {

        PrintWriter out = this.getSolutionWriter();

        result.setProblemManager(null);
        outputData.updateData(-1, this.getTimeRunning(), this.getTimeRemaining(),result);
        String a = gson.toJson(outputData);

        out.println(a);
        //this.printResult(result, out);
    }


    public void writeResult(Result result) throws IOException {
         /*result = {
                "n_jobs": 0,
                "n_tools": 0,
                "magazine_size": 0,
                "switches": 0,
                "tool_hops": 0,
                "tool_add_distance": 0,
                "tool_remove_distance": 0,
                "run_time": 0,
                "sequence": [],
                "tool_hops_sequence": [],
                "tool_add_distance_sequence": [],
                "tool_remove_distance_sequence": [],
                "matrix": [[]],
        }*/

         PrintWriter out = this.getResultsWriter();


         result.setProblemManager(null);
         outputData.updateData(this.getResultsCount(), this.getTimeRunning(), this.getTimeRemaining(),result);
         String a = gson.toJson(outputData);
         out.println(a);
         result.setProblemManager(this.getProblemManager());
         ////out.println("#" + this.getResultsCount());
         //this.printResult(result, out);

        resultsCount+=1;

    }

    public void printResult(Result result, PrintWriter out) {
        long timeRunning = this.getTimeRunning();
        long timeRemaining = this.getTimeRemaining();

        //n_jobs
        out.println(this.problemManager.getN_JOBS());
        //n_tools
        out.println(this.problemManager.getN_TOOLS());
        //magazine_size
        out.println(this.problemManager.getMAGAZINE_SIZE());
        //n_switches
        out.println(result.getCost());
        //tool_hops
        out.println(-1);
        //tool_add_distance
        out.println(-1);
        //tool_remove_distance
        out.println(-1);
        //run_time
        out.println(timeRunning);
        //sequence
        out.println(Arrays.toString(result.getSequence()));
        //printArray(result.getSequence(),out);
        //tool_hops_sequence
        out.println(-1);
        //tool_distance
        out.println(-1);
        //tool_remove_distance_sequence
        out.println(-1);
        //matrix
        for (int i = 0; i < this.problemManager.getN_JOBS(); i++) {
            for (int j = 0; j < this.problemManager.getN_TOOLS(); j++) {
                out.print(result.getJobToolMatrix()[i][j]);
                out.print(" ");
            }
            out.println();
        }
    }

    //Costly operation use only f
    public void writeLiveResult(Result result) {
        try(
                FileWriter lfw = new FileWriter(this.problemManager.getParameters().getLIVE_RESULT_PATH(), false);
                BufferedWriter lbw = new BufferedWriter(lfw);
                PrintWriter liveWriter = new PrintWriter(lbw);
        ){
            liveWriter.println(this.problemManager.getParameters().getINSTANCE());
            this.printResult(result, liveWriter);
        }catch (IOException io) {
            System.out.println("error writing update");
        }
    }



    public void printArray(int[] ar , PrintWriter out) {
        for (int i = 0; i < ar.length; i++) {
            out.print(ar[i]);
            out.print(" ");
        }
        out.println("");
    }




    /* MESSAGES ------------------------------------------------------------------ */


    public void logInfo(String bonjour) {
        cp.println(">> " + bonjour);
    }

    public void logWarningMessage(String bonjour) {
        cp.errorPrint(">!> " +  bonjour);
    }


    /* UTILITY ------------------------------------------------------------------ */

    public long getTimeRemaining() {
        return (this.problemManager.getTIME_LIMIT() - System.currentTimeMillis());
    }

    public long getTimeRunning() {
        return (System.currentTimeMillis() - this.problemManager.getParameters().getSTART_TIME());
    }


    /* GETTERS & SETTERS ------------------------------------------------------------------ */

    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }


    public ColoredPrinter getCp() {
        return cp;
    }

    public void setCp(ColoredPrinter cp) {
        this.cp = cp;
    }

    public String getSpacing() {
        return spacing;
    }

    public void setSpacing(String spacing) {
        this.spacing = spacing;
    }

    public String[] getLogTitles() {
        return logTitles;
    }

    public void setLogTitles(String[] logTitles) {
        this.logTitles = logTitles;
    }

    public PrintWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public PrintWriter getResultsWriter() {
        return resultsWriter;
    }

    public void setResultsWriter(PrintWriter resultsWriter) {
        this.resultsWriter = resultsWriter;
    }

    public PrintWriter getSolutionWriter() {
        return solutionWriter;
    }

    public void setSolutionWriter(PrintWriter solutionWriter) {
        this.solutionWriter = solutionWriter;
    }


    public long getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(long resultsCount) {
        this.resultsCount = resultsCount;
    }


    public OutputData getOutputData() {
        return outputData;
    }

    public void setOutputData(OutputData outputData) {
        this.outputData = outputData;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
}

