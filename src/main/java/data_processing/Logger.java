package data_processing;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import core.ProblemManager;
import fastcsv.writer.CsvAppender;
import fastcsv.writer.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Logger {

    ProblemManager problemManager;
    File file;
    CsvWriter csvWriter;
    ColoredPrinter cp;
    CsvAppender csvAppender;

    public Logger(ProblemManager problemManager, File file) throws IOException {
        this.problemManager = problemManager;
        cp = new ColoredPrinter.Builder(1, true)
                .foreground(Ansi.FColor.BLUE)
                .build();

        this.file = file;
        this.csvWriter = new CsvWriter();
        this.csvAppender = csvWriter.append(file, StandardCharsets.UTF_8);
    }

    public void logLegend(String[] titles) throws IOException {
        System.out.println();
        System.out.printf("%-10s %-10s %-10s %-20s %-10s %-10s %-10s \n", (Object[]) titles);
        System.out.println();
        csvAppender.appendField("BONSOIR");
        csvAppender.endLine();

    }

    public void log(String[] items) throws IOException {
        System.out.printf("%-10s %-10s %-10s %-20s %-10s %-10s %-10s \n", (Object[]) items);
        csvAppender.appendLine(items);
        csvAppender.endLine();
    }

    public void logInfo(String bonjour) {
        cp.println(">> " + bonjour);
    }

    public void logWarningMessage(String bonjour) {
        cp.errorPrint(">!> " +  bonjour);
    }


}
