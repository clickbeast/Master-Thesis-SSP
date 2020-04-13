package data_processing;

import java.io.PrintWriter;
import java.util.Arrays;

public class InputData {

    Parameters parameters;

    int N_JOBS;
    int N_TOOLS;
    int MAGAZINE_SIZE;
    int[][] JOB_TOOL_MATRIX;

    private PrintWriter logWriter;
    private PrintWriter resultsWriter;
    private PrintWriter solutionWriter;

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
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

    public int[][] getJOB_TOOL_MATRIX() {
        return JOB_TOOL_MATRIX;
    }

    public void setJOB_TOOL_MATRIX(int[][] JOB_TOOL_MATRIX) {
        this.JOB_TOOL_MATRIX = JOB_TOOL_MATRIX;
    }


    @Override
    public String toString() {
        return "InputData{" +
                "N_JOBS=" + N_JOBS +
                ", N_TOOLS=" + N_TOOLS +
                ", MAGAZINE_SIZE=" + MAGAZINE_SIZE +
                ", JOB_TOOL_MATRIX=" + Arrays.toString(JOB_TOOL_MATRIX) +
                '}';
    }

    public void setWriters(PrintWriter logWriter, PrintWriter resultsWriter, PrintWriter solutionWriter) {
        this.logWriter = logWriter;
        this.resultsWriter = resultsWriter;
        this.solutionWriter = solutionWriter;
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
}
