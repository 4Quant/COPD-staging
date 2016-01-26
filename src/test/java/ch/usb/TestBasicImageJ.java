package ch.usb;


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import ij.measure.Calibration;
import ij.process.ShortProcessor;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;



/**
 * Created by tomjre on 14/01/16.
 */
public class TestBasicImageJ {

    static final private boolean headless = true;


   static private ij.ImageJ imgj = null;

    static private Calibration validCalibration = null;


    @BeforeClass
    public static void setupImageJ() {
        if(headless) ImageJ.main("--headless".split(" "));
        else ImageJ.main("".split(" "));
        imgj = IJ.getInstance();
        IJ.open(TestBasicImageJ.class.getResource("/sampleCTs/thoraxslice.tif").getPath());
        validCalibration = IJ.getImage().getLocalCalibration();
    }


    static ShortProcessor createShortProcessorFromArray(short[][] is) {
        int i = 0;
        int height = is.length;
        int width = is[0].length;
        // create signed short processor
        ShortProcessor shortprocessor = new ShortProcessor(width, height, false);
        short[] sp = (short[]) shortprocessor.getPixels();
        int h = 0;
        while (h < height) {
            int w = 0;
            while (w < width) {
                sp[i] = is[h][w];
                w++;
                i++;
            }
            i = ++h * width;
        }
        return shortprocessor;
    }

    static short[][] createTestImage(short fgValue, short bgValue) {
        short[][] is = new short[400][400];
        for(int i = 0;i<is.length; i++) {
            for(int j = 0;j<is[0].length;j++) {
                if ((i>50) & (i<100) & (j>50) & (j<100)) is[i][j] = fgValue;
                else is[i][j] = bgValue;
            }
        }
        return is;
    }

    public static final double HU_LUNG= -500.0;
    public static final double HU_NONLUNG= 100.0;
    public static final double HU_OFFSET= -1024.0; // mapping int to HU simple shift


    static protected interface LungROI {
        boolean isInside(long[] pos);
    }

    static protected class SquareROI implements LungROI {
        final public int x,y,w,h;
        public SquareROI(int ix, int iy, int iw, int ih) {
            x = ix;
            y = iy;
            w = iw;
            h = ih;
        }

        @Override
        public boolean isInside(long[] pos) {
            if((pos[0]>x) && (pos[0]<=(x+w)))
                if((pos[1]>y) && (pos[1]<=(y+h)))
                    return true;

            return false;
        }
    }

    /**
     * Generates an ImagePlus with Calibration equivalent to typical
     * USB Thorax CT and
     * Containing one square with pixel values in lung range.
     *
     * @return ImagePlus containing a square with
     */
    static ImagePlus createMockCTLungImp(LungROI cROI) {
        // create coefficients

        // Setup lookup table to map integer to HU value and wrap in Calibration
        final float[] ctable = new float[65536];
        for (int i=0;i<65536;i++)
            ctable[i] = (float)HU_OFFSET+i;
        Calibration cal = new Calibration();
        cal.setCTable(ctable,"HU");
        System.out.println("Is calibrated:"+cal.calibrated()+" "+cal);

        final short lungValue = (short) cal.getRawValue(HU_LUNG);
        final short bgValue =(short) cal.getRawValue(HU_NONLUNG);

        // build image
        int w=512,h=512, numOfSlices=3;
        ArrayImgFactory<ShortType> aif = new ArrayImgFactory<ShortType>();
        Img<ShortType> emptyImage = aif.create(new long[]{w,h,numOfSlices},new ShortType());

        Cursor<ShortType> cr = emptyImage.localizingCursor();

        long[] pos = new long[emptyImage.numDimensions()];
        while (cr.hasNext()) {
            cr.fwd();
            cr.localize(pos);
            if (cROI.isInside(pos))
                cr.get().set(lungValue);
            else
                cr.get().set(bgValue);
        }

        ImagePlus imp = ImageJFunctions.wrap(emptyImage,"TestCTthorax");
        System.out.println("synt: "+cal+"\nreal: "+validCalibration);

        //TODO figure out why this does not work
        // imp.setCalibration(cal);

        imp.setCalibration(validCalibration);

        if(!headless) waitForIPClosing(imp);

        return imp;
    }

    /**
     * Test that the calibration table converts correctly
     */
    @Test
    public void testCalibrationValues() {
        // create coefficients

        // Setup lookup table to map integer to HU value and wrap in Calibration
        final float[] ctable = new float[65536];
        for (int i=0;i<65536;i++)
            ctable[i] = (float)HU_OFFSET+i;
        Calibration cal = new Calibration();
        cal.setCTable(ctable,"HU");
        System.out.println("Is calibrated:"+cal.calibrated()+" "+cal);

        final short lungValue = (short) cal.getRawValue(HU_LUNG);
        final short bgValue =(short) cal.getRawValue(HU_NONLUNG);

        assertEquals("The calibration table should shift by an offset",ctable[lungValue],HU_LUNG,0.1f);

        assertEquals("The calibration table should shift by an offset",ctable[bgValue],HU_NONLUNG,0.1f);

        assertEquals("Round trip should match up",cal.getCValue(cal.getRawValue(HU_LUNG)),
                HU_LUNG,1f);

        assertEquals("Round trip should match up",cal.getCValue(cal.getRawValue(HU_NONLUNG)),
                HU_NONLUNG,1f);
    }

    /**
     * Calculate basic statistics of the lung tissue from the image
     */
    public static class LungStatistics {
        public final long lungVoxels;
        public final long totalVoxels;
        public final double maxVal;
        public final double minVal;
        public final double sumVal;
        public final double meanVal;

        public LungStatistics(Img<FloatType> ift, double offset) {
            Cursor<FloatType> cr = ift.localizingCursor();
            long lungVol = 0,totalVol=0;
            double min=Float.MAX_VALUE,max=Float.MIN_VALUE;
            double sum = 0.0;
            long[] pos = new long[ift.numDimensions()];
            while (cr.hasNext()) {
                cr.fwd();
                cr.localize(pos);
                double curValue = cr.get().get()+offset;
                totalVol++;
                if((curValue>= COPD_LungSegment.MIN_HU_LUNG) && (curValue<= COPD_LungSegment.MAX_HU_LUNG)) lungVol++;
                if(curValue>max) max = curValue;
                if(curValue<min) min = curValue;
                sum+=curValue;
            }

            lungVoxels = lungVol;
            totalVoxels = totalVol;
            maxVal = max;
            minVal = min;
            sumVal = sum;
            meanVal = sum/totalVol;
        }
        public static LungStatistics fromImp(ImagePlus imp) {
            ImageJFunctions.convertFloat(imp);

            return new LungStatistics(ImageJFunctions.convertFloat(imp),
                    imp.getCalibration().getCValue(0));
        }
        @Override
        public String toString() {
            return  lungVoxels+" "+totalVoxels+" "+
            minVal+" "+maxVal+" "+meanVal;
        }
    }

    @Test
    public void testStringArgumentParsing() {
        double v0, v2, v1;
        String[] args = "-487 2.0 5".split(" ");

        v0 = Double.valueOf(args[0]);
        v1 = Double.valueOf(args[1]);
        v2 = Double.valueOf(args[2]);
        // test ranges
        assertTrue( "Test lung values", COPD_LungSegment.isLung(v0) );

    }

    @Test
    public void testCreateShortImage() {
        ImagePlus imp = new ImagePlus("test Image",createShortProcessorFromArray(
                createTestImage((short) 100,(short) 0)));
        Calibration cal= new Calibration();
        //cal.setCTable();

        long pcount = imp.getStatistics().pixelCount;
        assertTrue("Right sized object "+pcount,pcount == (400 * 400));

        double meanValue = imp.getStatistics().mean;
        System.out.println("Non-zero values:"+meanValue);
        assertTrue("Non-zero area "+meanValue,meanValue>0);
    }

    @Test
    public void testCreateTestImage() {
        System.out.println("@Test testCreateTestImage");
        ImagePlus imp = createMockCTLungImp(new SquareROI(20,20,40,40));

        System.out.println("Value at (25,25,0) " + imp.getProcessor().getPixelValue(25,25));

        System.out.println("Value at (1,1,0) " + imp.getProcessor().getPixelValue(1,1));

        assertEquals(
                "Calibration should be set and point 25,25 " +
                        "should be inside the lung",
                HU_LUNG,imp.getProcessor().getPixelValue(25,25),1.0f);

        assertEquals(
                "Calibration should be set and point 1,1 " +
                        "should be outside the lung",
                HU_NONLUNG,imp.getProcessor().getPixelValue(1,1),1.0f);

        double preInvertLung = LungStatistics.fromImp(imp).meanVal;
        System.out.println("Lung Statistics:"+LungStatistics.fromImp(imp));
        assertTrue("Non-zero Mean: ",preInvertLung>0);
        assertEquals("Area matches given image area",512*512*3,
                LungStatistics.fromImp(imp).totalVoxels, 1.0);
        assertEquals("Area matches given lung area",40*40.0*3,
                LungStatistics.fromImp(imp).lungVoxels, 1.0);


        IJ.run(imp,"Invert","stack");
        if(!headless) waitForIPClosing(imp);

        double postInvertLung = LungStatistics.fromImp(imp).meanVal;
        System.out.println("Inversion Increased Mean "+preInvertLung+" -> "+postInvertLung);
        assertTrue("Inversion Increased Mean "+preInvertLung+" -> "+postInvertLung, postInvertLung>=preInvertLung);
    }

    public static void waitForIPClosing(ImagePlus imp) {
        imp.show("Close to continue...");
        while(imp.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
