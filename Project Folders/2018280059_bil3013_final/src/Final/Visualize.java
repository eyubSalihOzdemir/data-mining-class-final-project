package Final;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Visualize {
    // change these values as you please. names are self-explanatory.
    private static final int SHAPE_SIZE = 5; // incrase up to 12 if it's hard to see
    private static final int IMAGE_WIDTH = 1920;
    private static final int IMAGE_HEIGHT = 1080;
    private static final Color BACKGROUND_COLOR = Color.white; // change to darkGray if it's hard to see

    public static int shapeOffset = SHAPE_SIZE / 2;

    public static void visualize(List<List<Double>> nonNormalizedFinalData, int clusterSize, String categoryY, String categoryX, int p) {
        // get index values of categories using map
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("Sports", 0);
        map.put("Religious", 1);
        map.put("Nature", 2);
        map.put("Theatre", 3);
        map.put("Shopping", 4);
        map.put("Picnic", 5);
        int indexX = map.get(categoryX);
        int indexY = map.get(categoryY);
        double valueX, valueY;
        int size = nonNormalizedFinalData.get(0).size();

        // add necessary cluster series
        List<XYSeries> clusterSeriesList = new ArrayList<>();
        for (int i = 0; i < clusterSize; i++) {
            clusterSeriesList.add(new XYSeries("clusterSeries"+i));
        }

        // add category values to the series
        double cluster;
        for (List<Double> data :
                nonNormalizedFinalData) {
            cluster = data.get(size-1);

            valueX = data.get(indexX);
            valueY = data.get(indexY);

            for (int i = 0; i < clusterSize; i++) {
                if(i == cluster) {
                    clusterSeriesList.get(i).add(valueX, valueY);
                }
            }
        }

        var dataset = new XYSeriesCollection();
        for (var x :
                clusterSeriesList) {
            dataset.addSeries(x);
        }

        JFreeChart chart = createChart(dataset, categoryY, categoryX, clusterSize);

        chart.getPlot().setBackgroundPaint(BACKGROUND_COLOR);

        String desktop = System.getProperty("user.home") + "\\Desktop";

        String path;
        String fileName = "\\2018280059_chart.png";
        if(p == -1) {
            path = desktop + fileName;
        }
        else {
            fileName = "\\2018280059_charts\\"+p+".png";
            path = desktop + fileName;
        }
        try {
            ChartUtilities.saveChartAsPNG(new File(path), chart, IMAGE_WIDTH, IMAGE_HEIGHT);
            System.out.println("Chart is saved to desktop as '" + fileName + "'");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // for visualization
    private static JFreeChart createChart(final XYDataset dataset, String categoryY, String categoryX, int clusterCount) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                categoryX + " / " + categoryY,
                categoryX,
                categoryY,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        Shape shape = new Rectangle(shapeOffset, shapeOffset, SHAPE_SIZE, SHAPE_SIZE);
        renderer.setShape(shape);

        Map<Integer, Color> colorMap = new HashMap<Integer, Color>();
        colorMap.put(0, Color.RED);
        colorMap.put(1, Color.BLUE);
        colorMap.put(2, Color.GREEN);
        colorMap.put(3, Color.MAGENTA);
        colorMap.put(4, Color.PINK);
        colorMap.put(5, Color.BLACK);
        colorMap.put(6, Color.CYAN);

        // set 7 different colors of series to be able to use later on
        for (int i = 0; i < colorMap.size(); i++) {
            renderer.setSeriesPaint(i, colorMap.get(i));
            renderer.setSeriesLinesVisible(i, false);
        }

        LegendItemCollection legends = new LegendItemCollection();

        // define legend names and add to chart
        for (int i = 0; i < clusterCount; i++) {
            String label = "Cluster " + (i+1);
            LegendItem legend = new LegendItem(label, "", "", "", shape, colorMap.get(i));
            legends.add(legend);
        }
        plot.setFixedLegendItems(legends);

        renderer.setDrawSeriesLineAsPath(true);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);

        chart.setTitle(new TextTitle(categoryX + " / " + categoryY,
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }
}
