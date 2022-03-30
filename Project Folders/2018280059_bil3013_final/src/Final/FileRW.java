package Final;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileRW {
    public static List<List<Double>> readFile(String path) throws IOException {
        String row;
        char counter = 0;
        List<List<Double>> finalData = new ArrayList<>();
        List<Double> bucket = new ArrayList<>();

        BufferedReader fileReader = new BufferedReader(new FileReader(path));
        row = fileReader.readLine(); // skip the title row
        while ((row = fileReader.readLine()) != null) {
            String[] splitRow = row.split(",");

            for (String values : splitRow)
                bucket.add(Double.parseDouble(values));
            bucket.add(0d); // add extra field to indicate cluster
            finalData.add(bucket);

            bucket = new ArrayList<>();
            counter++;
        }
        fileReader.close();

        return finalData;
    }

    public static void writeResults(List<List<Double>> finalData, List<Double> statistics, List<List<Double>> clusters, boolean debug, int runCnt) {
        // statistics.get(0) = wcss, 1 = bcss, 2 = dunn
        String desktop = System.getProperty("user.home") + "\\Desktop";
        String path;
        String fileName;
        if(!debug) {
            fileName = "\\sonuc.txt";
            path = desktop + fileName;
        }
        else {
            fileName = "\\2018280059_results" + "\\sonuc" + runCnt + ".txt";
            path = desktop + fileName;
        }

        List<Integer> counter = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            counter.add(0);
        }
        for (List<Double> data :
                finalData) {
            for (int i = 0; i < clusters.size(); i++) {
                if(data.get(6) == (double)i) {
                    counter.set(i, counter.get(i) + 1);
                }
            }
        }

        try {
            PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);
            for (int i = 0; i < finalData.size(); i++) {
                writer.println("Kayıt "+(i+1)+": " + "\t\t Küme " + Math.round(finalData.get(i).get(6)));
            }
            writer.println("");

            for (int i = 0; i < counter.size(); i++) {
                writer.println("Küme " + i + ": " + counter.get(i));
            }
            writer.println("");
            writer.println("WCSS: " + statistics.get(0));
            writer.println("BCSS: " + statistics.get(1));
            writer.println("Dunn Index: " + statistics.get(2));
            writer.close();

            System.out.println("Results saved to desktop as " + "'" + fileName+"'");
        } catch (IOException e) {
            System.out.printf("Error while writing results.");
            e.printStackTrace();
        }
    }
    
    private static void deleteFile(String path) {
        try {
            PrintWriter delete = new PrintWriter(path, StandardCharsets.UTF_8);
            delete.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
