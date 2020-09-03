package data_processing;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import com.google.gson.Gson;
import core.ProblemManager;
import core.Result;
import data_processing.serializable.OutputData;
import util.General;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Logger {

    //public CsvAppender csvAppender;
    ProblemManager problemManager;
    //CsvWriter csvWriter;
    ColoredPrinter cp;

    String spacing = "%-5s %-15s %-5s %-15s %-5s %-15s %-5s %-15s %-5s %-20s %-10s %-10s %-20s %-10s %-10s %-15s %-10s %-30s %-20s \n";
    //String[] logTitles2 = {"Switches", "Best Switches", "Rem Dist" , "Best Rem Dist" , "Accepted", "Rejected" , "Improved", "Step" , "Time Remaining" , "Sequence"};
    String[] logTitles = {"COST","B_COST","SW", "B_SW", "THOP" , "B_THOP" , "TAD", "B_TAD" , "TRD", "B_TRD" , "ACCEPT" , "REJECT", "IMPROVE", "STEP", "T_RUN","T_REM", "TEMP", "SEQ","TYPE","TYPEID"};


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
                this.getProblemManager().getParameters().getINSTANCE(),
                this.getProblemManager().getMAGAZINE_SIZE(),
                this.getProblemManager().getN_TOOLS(),
                this.getProblemManager().getN_JOBS(),
                this.getProblemManager().getJOB_TOOL_MATRIX(),
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

    public void logLegend()  {
        PrintWriter out = this.getLogWriter();

        System.out.println();
        System.out.printf(spacing, (Object[]) logTitles);
        System.out.println();

        String logSpacing = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s";
        out.printf(logSpacing, (Object[]) logTitles);
        out.println();

    }



    public void log(Result result) throws IOException {
        this.log(result, -1);
    }

    public void log(Result result, double temperature) throws IOException {
        this.log(result, result.getnSwitches(),this.problemManager.getBestResult().getnSwitches(), this.problemManager.getAccepted(),this.problemManager.getRejected(), this.problemManager.getImproved(), temperature, result.getSequence(), result.getType());
    }

    public void log(Result result , int switches, int bestSwitches, long accepted, long rejected, long improved, double temperature, int[] sequence, String type) throws IOException {

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        if(this.getProblemManager().getParameters().isLOG()) {
            PrintWriter out = this.getLogWriter();
            long timeRunning = this.getTimeRunning();
            long timeRemaining = this.getTimeRemaining();

            if (this.getProblemManager().getParameters().isLOG_VERBOSE()) {

                System.out.printf(spacing,
                        //COST
                        result.getCost(),
                        //B_COST
                        this.problemManager.getBestResult().getCost(),
                        //SW
                        switches,
                        // B_SW
                        bestSwitches,
                        // HOP
                        df.format(result.getTieBreakingCost()),
                        //result.getTieBreakingCost(),
                        // B_THOP
                        df.format(this.problemManager.getBestResult().getTieBreakingCost()),
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
                        temperature,
                        // SEQ
                        Arrays.toString(sequence),
                        //TYPE
                        type

                );


            }


            String logSpacing = "%s, %s, %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,\"%s\"j,%s";

            out.printf(logSpacing,
                    //COST
                    result.getCost(),
                    //B_COST
                    this.problemManager.getBestResult().getCost(),
                    //SW
                    switches,
                    // B_SW
                    bestSwitches,
                    // HOP
                    df.format(result.getTieBreakingCost()),
                    //result.getTieBreakingCost(),
                    // B_THOP
                    df.format(this.problemManager.getBestResult().getTieBreakingCost()),
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
                    temperature,
                    // SEQ
                    Arrays.toString(sequence),
                    //TYPE
                    type
            );

            out.println();

        }


    }


    /* SOLUTIONS & RESULTS ------------------------------------------------------------------ */


    public void writeParameters() {
        try (
                FileWriter lfw = new FileWriter(this.problemManager.getParameters().getPARAMETER_PATH(), false);
                BufferedWriter lbw = new BufferedWriter(lfw);
                PrintWriter liveWriter = new PrintWriter(lbw);
        ) {
            String a = gson.toJson(this.problemManager.getParameters());
            liveWriter.println(a);
        } catch (IOException io) {
            System.out.println("error writing update");
        }
    }


    public void writeSolution(Result result) throws IOException {
        this.logInfo("WRITE SOLUTION ");
        PrintWriter out = this.getSolutionWriter();

        int[][] jobToolMatrix = result.getJobToolMatrix();
        result.setJobToolMatrix(General.convertToBinaryGrid(result));

        result.setProblemManager(null);
        outputData.updateData(-1, this.getTimeRunning(), this.getTimeRemaining(),result);
        String a = gson.toJson(outputData);
        out.println(a);
        System.out.println(a);
        System.out.println(this.problemManager.getParameters().getSOLUTION_PATH());
        result.setProblemManager(this.getProblemManager());


        result.setJobToolMatrix(jobToolMatrix);
    }


    public void writeResult(Result result) throws IOException {
         if(this.getProblemManager().getParameters().isWRITE_RESULTS()) {
             PrintWriter out = this.getResultsWriter();

             int[][] jobToolMatrix = result.getJobToolMatrix();
             result.setJobToolMatrix(General.convertToBinaryGrid(result));

             result.setProblemManager(null);
             outputData.updateData(this.getResultsCount(), this.getTimeRunning(), this.getTimeRemaining(), result);
             String a = gson.toJson(outputData);
             out.println(a);
             result.setProblemManager(this.getProblemManager());


             //ONLY FOR USE WITH DELTA EVAL -> translation
             result.setJobToolMatrix(jobToolMatrix);


             ////out.println("#" + this.getResultsCount());
             //this.printResult(result, out);

             resultsCount += 1;
         }
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
        if(this.getProblemManager().getParameters().isLIVE_RESULT()) {

            try (
                    FileWriter lfw = new FileWriter(this.problemManager.getParameters().getLIVE_RESULT_PATH(), false);
                    BufferedWriter lbw = new BufferedWriter(lfw);
                    PrintWriter liveWriter = new PrintWriter(lbw);
            ) {
                result.setProblemManager(null);
                outputData.updateData(0, this.getTimeRunning(), this.getTimeRemaining(), result);
                String a = gson.toJson(outputData);
                liveWriter.println(a);
                result.setProblemManager(this.getProblemManager());
            } catch (IOException io) {
                System.out.println("error writing update");
            }
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

