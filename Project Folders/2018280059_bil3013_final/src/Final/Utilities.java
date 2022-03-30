package Final;

import java.util.*;
import java.util.List;

public class Utilities {
    public static List<Double> analyzePerformance(List<List<Double>> clusters, List<List<Double>> finalData, int clusterSize) {
        int columnSize = finalData.get(0).size();
        int finalDataSize = finalData.size();

        //calculate max of IntraCluster
        double distance, maxIntraDistance = 0;
        double cluster1Sum=0, cluster2Sum=0, cluster3Sum=0;

        List<Double> clusterSums = new ArrayList<>(clusters.size());
        for (int i = 0; i < clusters.size(); i++) {
            clusterSums.add(0.0);
        }

        for (List<Double> data :
                finalData) {
            double itsCluster = data.get(columnSize-1);

            for (int i = 0; i < clusters.size(); i++) {
                if(itsCluster == clusters.get(i).get(columnSize-1)) {
                    distance = euclideanD(data, clusters.get(i));
                    clusterSums.set(i, clusterSums.get(i) + Math.pow(distance, 2));
                    if(distance > maxIntraDistance) {
                        maxIntraDistance = distance;
                    }
                }
            }
        }

        List<Integer> clusterCounter = new ArrayList<>(clusterSize);
        for (int i = 0; i < clusterSize; i++) {
            clusterCounter.add(0);
        }

        List<Double> clusterWcssCounter = new ArrayList<>(clusterSize);
        for (int i = 0; i < clusterSize; i++) {
            clusterWcssCounter.add(0d);
        }

        for (int i = 0; i < clusterSize; i++) {
            for (int j = 0; j < finalDataSize; j++) {
                if(finalData.get(j).get(6) == (double)i) {
                    clusterCounter.set(i, clusterCounter.get(i)+1);
                }
            }
        }

        // calculate min of InterCluster
        //create CLUSTER_SIZExxCLUSTER_SIZE matrix
        List<List<Double>> distancesMatrix = new ArrayList<>(clusterSize);
        for (int i = 0; i < clusterSize; i++) {
            List<Double> bucket = new ArrayList<>();
            for (int j = 0; j < clusterSize; j++) {
                bucket.add(0d);
            }
            distancesMatrix.add(bucket);
            bucket = new ArrayList<>();
        }

        double minInterCluster = 999d;
        for (int i = 0; i < clusterSize; i++) {
            for (int j = 0; j < clusterSize; j++) {
                if(i != j) {
                    distance = euclideanD(clusters.get(i), clusters.get(j));
                    clusterWcssCounter.set(i, clusterWcssCounter.get(i) + Math.pow(distance, 2));
                    if(distance < minInterCluster) {
                        minInterCluster = distance;
                    }
                    distancesMatrix.get(i).set(j, distance);
                }
            }
        }

        List<Double> subBcss = new ArrayList<>(clusterSize);
        System.out.println("");
        for (int i = 0; i < clusterSize; i++) {
            double sum = 0;
            for (int j = 0; j < clusterSize; j++) {
                if(i != j) {
                    sum += distancesMatrix.get(i).get(j);
                }
            }
            subBcss.add(clusterCounter.get(i) * sum);
        }

        double bcss = 0;
        for (double sub :
                subBcss) {
            bcss += sub;
        }

        double wcss = 0;
        for (double subWcss :
                clusterWcssCounter) {
            wcss += subWcss;
        }

        double dunn =  minInterCluster / maxIntraDistance;
        System.out.println("WCSS: " + wcss);
        System.out.println("BCSS: " + bcss);
        System.out.println("Dunn Index: " + dunn);
        return new ArrayList<>(Arrays.asList(wcss, bcss, dunn));
    }

    public static double euclideanD(List<Double> entryOne, List<Double> entryTwo) {
        int len = entryOne.size();
        int lenCheck = entryTwo.size();
        if(len != lenCheck) {
            System.out.println("Something wrong with list sizes");
            System.exit(-1);
        }
        double sum = 0;
        for (int i = 0; i < len-1; i++) {
            sum += Math.pow(entryOne.get(i) - entryTwo.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    public static void printData(List<List<Double>> finalData) {
        for (var x :
                finalData) {
            for (var y :
                    x) {
                System.out.print(y + " ");
            }
            System.out.println("");
        }
    }

    public static List<String> getCategoryInput() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "Sports");
        map.put(2, "Religious");
        map.put(3, "Nature");
        map.put(4, "Theatre");
        map.put(5, "Shopping");
        map.put(6, "Picnic");

        Scanner sc= new Scanner(System.in);

        System.out.println("Select the 1. category (by its number): ");
        for (int i = 1; i < 7; i++)
            System.out.println((i) + " - " + map.get(i));
        String firstCategory = map.get(sc.nextInt());

        System.out.println("Select the 2. category (by its number): ");
        for (int i = 1; i < 7; i++)
            System.out.println((i) + " - " + map.get(i));
        String secondCategory = map.get(sc.nextInt());

        return new ArrayList<>(Arrays.asList(firstCategory, secondCategory));
    }
}
