package Final;

import java.util.ArrayList;
import java.util.List;

public class Normalize {
    // normalize the value for each individual column
    public static List<List<Double>> zScore(List<List<Double>> finalData) {
        final double len = finalData.size();

        List<Double> numbers = new ArrayList<>();

        for(int i = 0; i<6; i++) {
            double sum = 0, mean = 0, sdPow = 0;
            for (var row : finalData) {
                sum += row.get(i);
                numbers.add(row.get(i));
            }
            mean = sum / len;
            for (double number : numbers)
                sdPow += Math.pow(number - mean, 2);
            sdPow = sdPow / len;

            double sqrtSdPow = Math.sqrt(sdPow);
            double sd = sqrtSdPow;

            for (int j = 0; j < numbers.size(); j++) {
                finalData.get(j).set(i, (numbers.get(j) - mean) / sd);
                //numbers.set(j, (numbers.get(j) - mean) / sd);
            }

            numbers = new ArrayList<>();
        }

        return finalData;
    }
}
