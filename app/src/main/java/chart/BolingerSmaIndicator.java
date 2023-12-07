package chart;

import com.leadingbyte.stockchart.Series;
import com.leadingbyte.stockchartpro.indicators.AbstractIndicator;
import com.leadingbyte.stockchartpro.indicators.IndicatorConfiguration;
import com.leadingbyte.stockchartpro.indicators.IndicatorData;

/**
 * Created by XTREMSOFT on 19-Apr-2018.
 */

public class BolingerSmaIndicator extends AbstractIndicator {
    public static final int ID_SMA = 10;

    public BolingerSmaIndicator() {
        super(new IndicatorConfiguration(false, new Integer[]{Integer.valueOf(10)}));
    }

    public BolingerSmaIndicator.SmaIterator iterator(Series.IPointAdapter src) {
        return new BolingerSmaIndicator.SmaIterator(src);
    }

    public IndicatorData recalc(Series.IPointAdapter src) {
        IndicatorData.Values v = new IndicatorData.Values();
        BolingerSmaIndicator.SmaIterator i = this.iterator(src);

        while(i.hasNext()) {
            v.add(Double.valueOf(i.getNext()));
        }

        return IndicatorData.create(10, v);
    }

    public class SmaIterator {
        public double smaSumm = 0.0D / 0.0;
        public int index = BolingerSmaIndicator.this.getPeriodsCount() - 1;
        private Series.IPointAdapter fSrc;

        public SmaIterator(Series.IPointAdapter src) {
            this.fSrc = src;
        }

        public boolean hasNext() {
            return this.index < this.fSrc.getPointCount();
        }

        public double getNext() {
            int pk = BolingerSmaIndicator.this.getPeriodsCount();
            int vi = BolingerSmaIndicator.this.getValueIndex();
            if(!Double.isNaN(this.smaSumm)) {
                this.smaSumm += this.fSrc.getPointAt(this.index).getValueAt(vi);
                this.smaSumm -= this.fSrc.getPointAt(this.index - pk).getValueAt(vi);
                ++this.index;
                return this.smaSumm / (double)pk;
            } else {
                this.smaSumm = 0.0D;

                for(int i = 0; i < pk; ++i) {
                    this.smaSumm += this.fSrc.getPointAt(i).getValueAt(vi);
                }

                double result = this.smaSumm / (double)pk;
                ++this.index;
                return result;
            }
        }
    }
}