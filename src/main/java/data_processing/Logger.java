package data_processing;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import com.sun.jna.platform.unix.solaris.LibKstat;
import core.ProblemManager;
import core.Result;
import fastcsv.writer.CsvAppender;
import fastcsv.writer.CsvWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Logger {

    //public CsvAppender csvAppender;
    ProblemManager problemManager;
    //CsvWriter csvWriter;
    ColoredPrinter cp;

    String spacing = "%-5s %-15s %-5s %-15s %-5s %-15s %-5s %-20s %-10s %-10s %-20s %-10s %-10s %-15s %-10s %-30s \n";
    //String[] logTitles2 = {"Switches", "Best Switches", "Rem Dist" , "Best Rem Dist" , "Accepted", "Rejected" , "Improved", "Step" , "Time Remaining" , "Sequence"};
    String[] logTitles = {"SW", "B_SW", "THOP" , "B_THOP" , "TAD", "B_TAD" , "TRD", "B_TRD" , "ACCEPT" , "REJECT", "IMPROVE", "STEP", "T_RUN","T_REM", "TEMP", "SEQ"};


    private PrintWriter logWriter;
    private PrintWriter resultsWriter;
    private PrintWriter solutionWriter;

    private long resultsCount = 0;


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

        String logSpacing = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s";
        out.printf(logSpacing, (Object[]) logTitles);
        out.println();

    }

    public void log( int switches, int bestSwitches, int[] sequence ) throws IOException {
        this.log(switches, bestSwitches,-1,-1,-1,-1, sequence);
    }

    public void log( int switches, int bestSwitches, long accepted, long rejected, long improved,int[] sequence ) throws IOException {
        this.log(switches, bestSwitches,accepted,rejected,improved,-1, sequence);
    }

    public void log( int switches, int bestSwitches, long accepted, long rejected, long improved, double temperature, int[] sequence ) throws IOException {
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
                Arrays.toString(sequence)
        );

        String logSpacing = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,\"%s\"";
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
                Arrays.toString(sequence)
        );

        out.println();


    }


    /* SOLUTIONS & RESULTS ------------------------------------------------------------------ */


    public void writeSolution(Result result) throws IOException {

        PrintWriter out = this.getSolutionWriter();

        long timeRunning = this.getTimeRunning();
        long timeRemaining = this.getTimeRemaining();

        //n_jobs
        out.println(this.problemManager.getN_JOBS());
        //n_tools
        out.println(this.problemManager.getN_TOOLS());
        //magazine_size
        out.println(this.problemManager.getMAGAZINE_SIZE());
        //switches
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
        //tool_hops_sequence
        out.println(-1);
        //tool_add_distance_sequence
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

        long timeRunning = this.getTimeRunning();
        long timeRemaining = this.getTimeRemaining();

         out.println("#" + this.getResultsCount());
         //n_jobs
         out.println(this.problemManager.getN_JOBS());
         //n_tools
         out.println(this.problemManager.getN_TOOLS());
         //magazine_size
         out.println(this.problemManager.getMAGAZINE_SIZE());
         //switches
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
         //tool_hops_sequence
         out.println(-1);
         //tool_add_distance_sequence
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

        resultsCount+=1;

    }

    public void printSolution(PrintStream printStream, Result result)  {
        PrintStream console = System.out;
        System.setOut(printStream);

        System.out.println(result.getCost());

        /*//TODO
        int[][] result = solution.getResult();
        int[][] ktns = solution.getKtns();
        int[] sequence = solution.getSequence();
        int[] switches = solution.getSwitches();

        for (int i = 0; i < sequence.length; i++) {
            int jobId = sequence[i];
            System.out.print(jobId + "\t");

            for (int j = 0; j < result[jobId].length; j++) {
                System.out.print(result[jobId][j] + " ");
            }

            System.out.print("\t" + switches[i]);
            System.out.println("");
        }

        System.out.print(" \t");
        for (int j = 0; j < result[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + "---");
        System.out.println("");

        System.out.print(" \t");

        for (int j = 0; j < result[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + solution.getCost());
        System.out.println("");
        System.out.println("");
        System.out.println("");
        //
        //


        for (int i = 0; i < ktns.length; i++) {
            int jobId = sequence[i];
            System.out.print(jobId + "\t");

            for (int j = 0; j < ktns[jobId].length; j++) {
                System.out.print(ktns[jobId][j] + " ");
            }

            System.out.print("\t" + "/");
            System.out.println("");
        }

        System.out.print(" \t");
        for (int j = 0; j < ktns[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + "---");
        System.out.println("");

        System.out.print(" \t");

        for (int j = 0; j < ktns[0].length; j++) {
            System.out.print("  ");
        }
        System.out.print("\t" + "/");
        System.out.println("");




        System.out.println("");

        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");

        System.out.println("");*/

        System.setOut(console);
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
}

