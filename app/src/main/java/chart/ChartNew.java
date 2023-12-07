package chart;

import android.app.Activity;
import android.os.Bundle;

import com.leadingbyte.stockchart.Area;
import com.leadingbyte.stockchart.Series;
import com.leadingbyte.stockchart.Side;
import com.leadingbyte.stockchart.points.AbstractPoint;
import com.leadingbyte.stockchart.points.Point2;
import com.leadingbyte.stockchart.points.StockPoint;
import com.leadingbyte.stockchart.renderers.CandlestickStockRenderer;
import com.leadingbyte.stockchart.renderers.HistogramRenderer;
import com.leadingbyte.stockchart.utils.StockDataGenerator;
import com.leadingbyte.stockchartpro.StockChartViewPro;

import com.ventura.venturawealth.R;


/**
 * Created by XtremsoftTechnologies on 13/12/16.
 */

public class ChartNew extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity layout from resources
        setContentView(R.layout.graph_layout);

        // Get the chart view (use StockChartView class for free version)
        StockChartViewPro chart = (StockChartViewPro) this.findViewById(R.id.stockChartView);

        // Create the random stock data feed and get 100 points
        StockDataGenerator gen = new StockDataGenerator();
        final StockDataGenerator.Point[] points = gen.getNext(100);

        // Create as many areas as you want. In this example we need two areas - one for price, another for volume
        Area a1 = chart.addArea();
        Area a2 = chart.addArea();

        // Add series to the first are
        Series price = chart.addSeries(a1);

        // Set the name of the series and the axis it will project to
        price.setName("price");
        price.setYAxisSide(Side.RIGHT);

        // The renderer determines the display style of the series. In this example we want the series to be displayed as set of candlesticks
        price.setRenderer(new CandlestickStockRenderer());

        // The point adapter gets the data from the data source. It's not necessary to create the point instance each time getPointAt is called
        // so we can just update the values
        price.setPointAdapter(new Series.IPointAdapter()
        {
            private final StockPoint fPoint = new StockPoint();

            @Override
            public int getPointCount()
            {
                return points.length;
            }

            @Override
            public AbstractPoint getPointAt(int i)
            {
                StockDataGenerator.Point p = points[i];
                fPoint.setValues(p.o, p.h, p.l, p.c);
                return fPoint;
            }
        });

        // The same we do for the volume series
        Series volume = chart.addSeries(a2);
        volume.setRenderer(new HistogramRenderer());
        volume.setName("volume");
        volume.setYAxisSide(Side.RIGHT);

        volume.setPointAdapter(new Series.IPointAdapter()
        {
            private final Point2 fPoint = new Point2();

            @Override
            public int getPointCount()
            {
                return points.length;
            }

            @Override
            public AbstractPoint getPointAt(int i)
            {
                StockDataGenerator.Point p = points[i];
                fPoint.setValues(p.v, 0.0);
                return fPoint;
            }
        });

        // Don't forget to call invalidate() each time you do any chart modifications
        chart.invalidate();
    }

}
