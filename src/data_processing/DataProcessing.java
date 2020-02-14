package data_processing;

import core.ProblemManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataProcessing {

    public ProblemManager instantiateProblem() throws FileNotFoundException {
        String file = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/instances/datA1";
        String problemDescription = file + File.separator + "problem_description.csv";
        String jobToolMatrixFile =  file + File.separator + "job_tool_matrix.csv";

        Scanner scanner = new Scanner(new File(file));

        int N_TOOLS = 0;
        int N_JOBS = 0;
        int MAGAZINE_SIZE = 0;

        loop: while(scanner.hasNext()) {
            String input = scanner.nextLine();

            if(input.matches("n=.*")) {
                N_JOBS = extractValue(input);
            }else if (input.matches("m=.*")) {
                N_TOOLS = extractValue(input);
            }else if (input.matches("c=.*")) {
                MAGAZINE_SIZE = extractValue(input);
            }else if (input.matches("problem.*")) {
                break loop;
            }

        }

        int[][] matrix = parseMatrix(N_TOOLS, N_JOBS, scanner);
        int[] magazine  = new int[MAGAZINE_SIZE];

        ProblemManager problemManager = new ProblemManager(
                MAGAZINE_SIZE, N_TOOLS, N_JOBS, matrix
        );

        return problemManager;
    }

    public int[][] parseMatrix(int n, int m, Scanner scanner) {
        int[][] matrix = new int[n][m];
        int i = 0;
        String input = "";

        while (scanner.hasNext()) {
            input = scanner.nextLine();
            if(input.matches("[0,1].*")) {
                break;
            }
        }

        while(input.matches("[0,1].*")) {
            String[] s = input.split(" ");
            for (int k = 0; k < n; k++) {
                matrix[i][k] =  Integer.parseInt(s[k]);
            }
            i++;
            input = scanner.nextLine();
        }

        return matrix;
    }

    public int extractValue(String input) {
        String regex = ".*=(\\d+)";
        final Pattern pattern_number = Pattern.compile(regex);
        final Matcher matcher = pattern_number.matcher(input);
        if(matcher.matches()) {
            return Integer.parseInt(matcher.toMatchResult().group(1));
        }
        return  0;
    }

    public void writeSolution() {

    }





}



