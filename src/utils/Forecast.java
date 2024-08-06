package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Forecast {
    public static String generateForecast(List<Double> salesData) {
        try {
            return runPythonScript(salesData);
        } catch (Exception e) {
            throw new RuntimeException("Error forecasting sales: " + e.getMessage());
        }
    }


    private static String runPythonScript(List<Double> salesData) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("src/python/forecast.py");
        for (Double data : salesData) {
            command.add(data.toString());
        }
        System.out.println(command);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Script execution failed with exit code " + exitCode);
        }

        return result.toString();
    }
}
