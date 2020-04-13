package data_processing;

import com.google.gson.Gson;
import core.ProblemManager;
import util.General;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Source: https://www.locked.de/how-to-get-list-of-objects-from-deeper-level-in-json-via-gson/
/*class Gsons{
    public static <T> List<T> asList(String json, String path, Class<T> clazz) {
        Gson gson = new Gson();
        String[] paths = path.split("\\.");
        JsonObject o = gson.fromJson(json, JsonObject.class);
        for (int i = 0; i < paths.length - 1; i++) {
            o = o.getAsJsonObject(paths[i]);
        }
        JsonArray jsonArray = o.getAsJsonArray(paths[paths.length - 1]);
        Class<T[]> clazzArray = (Class<T[]>) ((T[]) Array.newInstance(clazz, 0)).getClass();
        T[] objectArray = gson.fromJson(jsonArray, clazzArray);
        return Arrays.asList(objectArray);
    }
}*/

public class DataProcessing {

    public InputData instantiateProblem(Parameters parameters) throws FileNotFoundException {
        InputData inputData = new InputData();
        inputData.setParameters(parameters);
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(parameters.getINPUT_FILE_PATH()));

            // convert JSON file to map
            Map<?, ?> map = gson.fromJson(reader, Map.class);


            // print map entries
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }

            Double N_JOBS = (Double) map.get("N_JOBS");
            Double N_TOOLS = (Double) map.get("N_TOOLS");
            /*Map <?,?>  map2 = (Map<?, ?>) map.get("magazines");
            Double MAGAZINE_SIZE = (Double) map2.get("magazineSize");*/
            Double MAGAZINE_SIZE = (Double) map.get("MAGAZINE_SIZE");
            ArrayList<ArrayList<Double>> matrix = (ArrayList<ArrayList<Double>>) map.get("matrix");
            int[][] m = doubleToIntMatrix(matrix);

            inputData.setN_JOBS(N_JOBS.intValue());
            inputData.setN_TOOLS(N_TOOLS.intValue());
            inputData.setMAGAZINE_SIZE(MAGAZINE_SIZE.intValue());
            inputData.setJOB_TOOL_MATRIX(m);

            // close reader
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println(inputData);
        General.printGrid(inputData.getJOB_TOOL_MATRIX());

        //String problemDescription = file + File.separator + "problem_description.csv";
        //String jobToolMatrixFile =  file + File.separator + "job_tool_matrix.csv";


        return inputData;
    }



    public int[][] doubleToIntMatrix(ArrayList<ArrayList<Double>> list) {
        int[][] matrix = new int[list.size()][list.get(0).size()];

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(0).size(); j++) {
                matrix[i][j] = list.get(i).get(j).intValue();
            }
        }

        return matrix;
    }


    public int[][] parseMatrix(int n, int m, Scanner scanner) {
        //TODO:fix me
        int[][] matrix = new int[m][n];
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

}



