package data_processing;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import core.ProblemManager;
import core.Result;
import fastcsv.writer.CsvAppender;
import fastcsv.writer.CsvWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Logger {

    public CsvAppender csvAppender;
    ProblemManager problemManager;
    CsvWriter csvWriter;
    ColoredPrinter cp;

    File logFile;

    String spacing = "%-5s %-15s %-5s %-15s %-5s %-15s %-5s %-20s %-10s %-10s %-20s %-10s %-10s %-15s %-10s %-30s \n";
    //String[] logTitles2 = {"Switches", "Best Switches", "Rem Dist" , "Best Rem Dist" , "Accepted", "Rejected" , "Improved", "Step" , "Time Remaining" , "Sequence"};
    String[] logTitles = {"SW", "B_SW", "THOP" , "B_THOP" , "TAD", "B_TAD" , "TRD", "B_TRD" , "ACCEPT" , "REJECT", "IMPROVE", "STEP", "T_RUN","T_REM", "TEMP", "SEQ"};


    public Logger(ProblemManager problemManager) throws IOException {
        this.problemManager = problemManager;

        //Setup color printer
        cp = new ColoredPrinter.Builder(1, true)
                .foreground(Ansi.FColor.BLUE)
                .build();
        this.logFile = new File(this.problemManager.getParameters().getLOG_PATH());
        this.csvWriter = new CsvWriter();


        //Create log file

        //Create results file


    }

    public void logLegend(String[] titles)  {
        System.out.println();
        System.out.printf(spacing, (Object[]) logTitles);
        System.out.println();
    }


    public void csvAppend(CsvAppender csvAppender, int currentCost, int minCost, int steps, int temperature, int accepted, int[] sequence ) throws IOException {

        long remaining = (this.problemManager.getTIME_LIMIT() - System.currentTimeMillis());

        csvAppender.appendLine(
                String.valueOf(currentCost),
                String.valueOf(minCost),
                String.valueOf(steps),
                String.valueOf(temperature),
                String.valueOf(remaining),
                String.valueOf(accepted),
                "Arrays.toString(sequence)");
    }

    public void log( int currentCost, int minCost, int steps, int temperature, int accepted, int[] sequence ) throws IOException {

        long remaining = (this.problemManager.getTIME_LIMIT() - System.currentTimeMillis());
        //"Switches", "Best Switches", "Rem Dist" , "Best Rem Dist" , "Accepted", "Rejected" , "Improved", "Step" , "Time Remaining" , "Sequence"

        System.out.printf(spacing, currentCost, minCost, steps, temperature, remaining, accepted,  Arrays.toString(sequence));

        csvAppender.appendLine(
                String.valueOf(currentCost),
                String.valueOf(minCost),
                String.valueOf(steps),
                String.valueOf(temperature),
                String.valueOf(remaining),
                String.valueOf(accepted),
                "");

    }



    public void log( int switches, int bestSwitches, int[] sequence ) throws IOException {

    }


    public void log( int switches, int bestSwitches, long accepted, long rejected, long improved, int step, double temperature, int[] sequence ) throws IOException {
        //TODO: add deletion cost , accepted , etc..

        long timeRemaining = (this.problemManager.getTIME_LIMIT() - System.currentTimeMillis());
        long timeRunning = (System.currentTimeMillis() - this.problemManager.getParameters().getSTART_TIME());

        //"Switches", "Best Switches", "Rem Dist" , "Best Rem Dist" , "Accepted", "Rejected" , "Improved", "Step" , "Time Remaining" , "Sequence", "Temperature"

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
                step,
                // T_RUN
                timeRunning,
                // T_REM
                timeRemaining,
                // TEMP
                temperature ,
                // SEQ
                sequence
           );

        csvAppender.appendLine(

                String.valueOf(switches),
                String.valueOf(bestSwitches),
                "",
                "",

                "",
                "",
                "",

                String.valueOf(step),
                String.valueOf(timeRemaining),

                ""
        );

        step+=1;

    }

    public void writeSolution(Result result) throws IOException {
        try(FileWriter fw = new FileWriter("/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/catanzaro/results.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(result.getCost());

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

    /*    // Creating a File object that represents the disk file.
        PrintStream o = new PrintStream(new PrintStream(new FileOutputStream(this.fileName, true)));
        printSolution(o, result);
        o.close();*/
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




    public void log(String[] items) throws IOException {
        System.out.printf(spacing, (Object[]) items);
        /*try(CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
            // header
            csvAppender.appendLine(items);
            csvAppender.endLine();
        }


        //csvAppender.appendLine(items);
        //csvAppender.endLine();*/
    }

    public void logInfo(String bonjour) {
        cp.println(">> " + bonjour);
    }

    public void logWarningMessage(String bonjour) {
        cp.errorPrint(">!> " +  bonjour);
    }


    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }

    public CsvAppender getCsvAppender() {
        return csvAppender;
    }

    public void setCsvAppender(CsvAppender csvAppender) {
        this.csvAppender = csvAppender;
    }

    public CsvWriter getCsvWriter() {
        return csvWriter;
    }

    public void setCsvWriter(CsvWriter csvWriter) {
        this.csvWriter = csvWriter;
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

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }
}

