package Final;

import java.io.*;
import java.util.*;
import java.util.List;

public class Final {
    public static void main(String[] args) {
        boolean whileFlag = true, drawFlag;
        //Final-data.txt must in be in C: directory

        // make debug -> true, if you want to run the algorithm more than once
        // !!! this will also change cluster selection from "first N entry" to "random"
        // (the reason why I don't use "random" method at all times is to show you,
        // that algorithm is consistent and outputs same results when it's given same input)
        //
        // as I've already discussed with you via e-mail, algorithm doesn't give the same results on every run when it selects the clusters randomly
        // I may have deceived myself as this being a problem, I start to think that it is only natural to get different cluster values and sizes
        // since they start from different points.
        // I also used "float" variables at the beginning of the project and I converted them to "double" when necessary,
        // now I've changed all to "double", it should benefit to the reliability.
        // after visualizing the data, I've seen that it works 'not perfect' but 'pretty accurate'.
        //
        // program will save a PNG for each run to a folder called "2018280059_charts" on your desktop, if "debug == true"
        // otherwise it is going to save a single PNG to desktop. same logic applies to "sonuc.txt" files as well.
        //
        boolean debug = false;
        int times_run = 20; // how many times do you want the program to run, if debug = true

        // get k-value from user
        Scanner sc= new Scanner(System.in);
        System.out.print("Enter cluster count(K value): ");
        int clsCount = sc.nextInt();
        System.out.println("");

        // get categories to draw
        List<String> categories = Utilities.getCategoryInput();

        List<List<Double>> clusters = new ArrayList<>(); // cluster list
        List<List<Double>> distances = new ArrayList<>(); // distances list
        List<Double> bucket = new ArrayList<>(); // temporary

        List<List<Double>> finalData = null;
        List<List<Double>> nonNormalizedFinalData = null;
        try {
            finalData = FileRW.readFile("C:\\Final-data.txt");
            nonNormalizedFinalData = FileRW.readFile("C:\\Final-data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // constants
        final int CLUSTER_SIZE = clsCount;
        final int COLUMN_SIZE = finalData.get(0).size();
        final int DATA_SIZE = finalData.size();

        // fill the distances list for later use
        for (int i = 0; i < DATA_SIZE; i++) {
            for (int j = 0; j < CLUSTER_SIZE; j++) {
                bucket.add(0d);
            }
            distances.add(bucket);
            bucket = new ArrayList<>();
        }

        finalData = Normalize.zScore(finalData);

        // temporary fix to be able to copy lists by value, and not by reference
        for(int l = 0; l<CLUSTER_SIZE; l++) {
            clusters.add(new ArrayList<>(Arrays.asList(0d,0d,0d,0d,0d,0d,(double)l)));
        }

        // run once if debug is false, otherwise run N times
        int boss = 1;
        if(debug) boss = times_run;
        for (int p = 0; p < boss; p++) {
            distances.clear();
            for (int i = 0; i < DATA_SIZE; i++) {
                for (int j = 0; j < CLUSTER_SIZE; j++) {
                    bucket.add(0d);
                }
                distances.add(bucket);
                bucket = new ArrayList<>();
            }

            clusters.clear();
            if(debug){
                // randomly assign N clusters
                Random rand = new Random();
                List<Integer> newList = new ArrayList<>();
                for(int i = 0; i < CLUSTER_SIZE; i++) {
                    clusters.add(new ArrayList<>(new ArrayList<>(Arrays.asList(0d,0d,0d,0d,0d,0d,(double)i))));

                    int nextRand = rand.nextInt(finalData.size());
                    while(newList.contains(nextRand)) {
                        nextRand = rand.nextInt(finalData.size());
                    }
                    newList.add(nextRand);
                    for (int j = 0; j < 6; j++) {
                        double test = finalData.get(nextRand).get(j);
                        clusters.get(i).set(j, test);
                    }
                }
            } else {
                //instead of randomly assigning the clusters, assign first N values as clusters
                for(int i = 0; i < CLUSTER_SIZE; i++) {
                    clusters.add(new ArrayList<>(new ArrayList<>(Arrays.asList(0d,0d,0d,0d,0d,0d,(double)i))));
                    for (int j = 0; j < COLUMN_SIZE-1; j++) {
                        double test = finalData.get(i).get(j);
                        clusters.get(i).set(j, test);
                    }
                }
            }

            int iterationCounter = 0;

            // do-while, while(clusters don't change)
            do {
                iterationCounter++;

                //calculate the distances of each entry and save them to distances:List
                for (int i = 0; i < DATA_SIZE; i++) {
                    for (int j = 0; j < CLUSTER_SIZE; j++) {
                        distances.get(i).set(j, Utilities.euclideanD(finalData.get(i), clusters.get(j)));
                    }
                }

                // find the minimum values in the distances list, and assign the clusters accordingly
                double minVal;
                int minIndex;
                for (int i = 0; i < DATA_SIZE; i++) {
                    minVal = 999f;
                    minIndex=9;
                    for (int j = 0; j < distances.get(i).size(); j++) {
                        if (distances.get(i).get(j) < minVal) {
                            minVal = distances.get(i).get(j);
                            minIndex = j;
                        }
                    }
                    //System.out.println("MinVal at distances [" + i + "]: " + minVal + " " + minIndex);

                    finalData.get(i).set(COLUMN_SIZE-1, (double)minIndex);
                }

                // lists to put average values of clusters' elements
                List<List<Double>> clusterAverages = new ArrayList<>();
                for(int m = 0; m<CLUSTER_SIZE; m++) {
                    clusterAverages.add(new ArrayList<>(Arrays.asList(0d,0d,0d,0d,0d,0d,(double)m)));
                }

                List<Integer> clusterCounters = new ArrayList<>(CLUSTER_SIZE);
                for (int y = 0; y < CLUSTER_SIZE; y++) {
                    clusterCounters.add(0);
                }

                // calculate the cluster averages
                for (int i = 0; i < DATA_SIZE; i++) {
                    double clusterValue = finalData.get(i).get(6);

                    for (int t = 0; t < CLUSTER_SIZE; t++) {

                        if(clusterValue == (double)t) {
                            clusterCounters.set(t, clusterCounters.get(t) + 1);

                            for (int j = 0; j < COLUMN_SIZE-1; j++) {
                                double avg = clusterAverages.get(t).get(j);
                                avg += finalData.get(i).get(j);
                                clusterAverages.get(t).set(j, avg);
                            }
                        }
                    }
                }

                for (int i = 0; i < CLUSTER_SIZE; i++) {
                    for (int j = 0; j < COLUMN_SIZE - 1; j++) {
                        if(clusterCounters.get(i) != 0) {
                            clusterAverages.get(i).set(j, clusterAverages.get(i).get(j) / clusterCounters.get(i));
                        }
                    }
                }

                // check if new clusters are same as before
                List<Boolean> boolList = new ArrayList<>(CLUSTER_SIZE);
                for (int i = 0; i < CLUSTER_SIZE; i++) {
                    boolList.add(clusterAverages.get(i).equals(clusters.get(i)));
                }


                /*for (var x : clusters)
                    System.out.println("Clusters: " + x);
                for (var x : clusterAverages)
                    System.out.println("ClusterAverages: " + x);*/


                // change clusters at the end of run
                for (int i = 0; i < CLUSTER_SIZE; i++) {
                    Collections.copy(clusters.get(i), clusterAverages.get(i));
                }

                // end the iterations, if none of the clusters have changed
                if(boolList.contains(false))
                    whileFlag = true;
                else
                    whileFlag = false;

            } while(whileFlag);


            // copy the clusters of entries to nonNormalizedFinalData
            int i = 0;
            for (List<Double> data :
                    finalData) {
                nonNormalizedFinalData.get(i).set(COLUMN_SIZE - 1, data.get(COLUMN_SIZE - 1));
                i++;
            }

            // get the cluster sizes
            List<Integer> counter = new ArrayList<>();
            for (int j = 0; j < CLUSTER_SIZE; j++) {
                counter.add(0);
            }
            for (List<Double> data :
                    finalData) {
                for (int j = 0; j < CLUSTER_SIZE; j++) {
                    if(data.get(6) == j) {
                        counter.set(j, counter.get(j) + 1);
                    }
                }
            }

            // print cluster counts and iteration count
            for (int j = 0; j < counter.size(); j++) {
                System.out.println("Cluster " + (j+1) + " size: " + counter.get(j));
            }
            System.out.println("Iteration: " + iterationCounter);

            // print the statistics of that run
            List<Double> statistics = Utilities.analyzePerformance(clusters, finalData, CLUSTER_SIZE);

            if(debug) {
                // if debug is enabled, save the results and visualizations to folders on desktop
                String newFolder = System.getProperty("user.home") + "\\Desktop" + "\\2018280059_results";
                String newFolderChart = System.getProperty("user.home") + "\\Desktop" + "\\2018280059_charts";

                //create directories
                new File(newFolder).mkdirs();
                new File(newFolderChart).mkdirs();

                // write the results
                FileRW.writeResults(finalData, statistics, clusters, true, p);
                // save the visualization as PNG
                Visualize.visualize(nonNormalizedFinalData, CLUSTER_SIZE, categories.get(0), categories.get(1), p);
            }
            else {
                // if debug is disabled, save the results and visualization to desktop

                // write the results
                FileRW.writeResults(finalData, statistics, clusters, false, -1);
                // save the visualization as PNG
                Visualize.visualize(nonNormalizedFinalData, CLUSTER_SIZE, categories.get(0), categories.get(1), -1);
            }
        }
    } // main
} // Final
