package ch.usb;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.process.ShortProcessor;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;



/**
 * Created by tomjre on 14/01/16.
 */
public class TestBasicImageJ {

    static final private boolean headless = true;

    static final ShortProcessor createShortProcessorFromArray(short[][] is) {
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

    static final short[][] createTestImage(short fgValue, short bgValue) {
        short[][] is = new short[400][400];
        for(int i = 0;i<is.length; i++) {
            for(int j = 0;j<is[0].length;j++) {
                if ((i>50) & (i<100) & (j>50) & (j<100)) is[i][j] = fgValue;
                else is[i][j] = bgValue;
            }
        }
        return is;
    }

    public static final double MAX_HU_LUNG= -380.0;
    public static final double MIN_HU_LUNG= -1500.0;
    public static final double HU_OFFSET= -1024.0; // mapping int to HU simple shift

    /**
     * Generates an ImagePlus with Calibration equivalent to typical
     * USB Thorax CT and
     * Containing one square with pixel values in lung range.
     *
     * @return ImagePlus containing a square with
     */
    static ImagePlus createTestSingleSquareCTLungImagePlus() {
        short fg= (short)(USB_LungSegmentTJ.MAX_HU_LUNG-HU_OFFSET);
        short bg= (short)(USB_LungSegmentTJ.MIN_HU_TISSUE-HU_OFFSET);
        ImagePlus imp = new ImagePlus("test Image",createShortProcessorFromArray(
                createTestImage(fg,bg)));

        // Setup lookup table to map integer to HU value and wrap in Calibration
        float[] ctable= new float[65536];
        float val= (float)HU_OFFSET;
        for (int i=0;i<65536;i++)
            ctable[i]= val++;
        Calibration cal= new Calibration();
        cal.setCTable(ctable,"HU");

        imp.setCalibration(cal);

        return imp;
    }

    @BeforeClass
    public static void setupImageJ() {
        if(headless) ImageJ.main("--headless".split(" "));
        else ImageJ.main("".split(" "));
    }

    @Test
    public void testStringArgumentParsing() {
        double v0, v2, v1;
        String[] args = "-487 2.0 5".split(" ");

        v0= Double.valueOf(args[0]);
        v1= Double.valueOf(args[1]);
        v2= Double.valueOf(args[2]);
        // test ranges
        assertTrue( "Should be inside the lung range",
                USB_LungSegmentTJ.inRange(v0, USB_LungSegmentTJ.MIN_HU_LUNG, USB_LungSegmentTJ.MAX_HU_LUNG)
        );
        assertTrue( "Test lung values", USB_LungSegmentTJ.isLung(v0) );
        assertTrue( "Test tissue values", USB_LungSegmentTJ.isTissue(v1) );

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
        ImageJ cInst = IJ.getInstance();

        assertNotNull("ImageJ should not be null",cInst);

        ImagePlus imp = createTestSingleSquareCTLungImagePlus();

        //TODO this is copy and pasted and should be replaced
        double preInvert = imp.getStatistics().mean;
        assertTrue("Non-zero Mean: ",preInvert>0);

        IJ.run(imp,"Invert","");

        double postInvert = imp.getStatistics().mean;

        assertTrue("Inversion Increased Mean "+preInvert+" -> "+postInvert, postInvert>=preInvert);
    }



    //@Test
    public void testPluginLoading() {
        //TODO fix plugin loading later
        IJ.run("ch.usb.USB_LungSegmentTJ");
    }

}
