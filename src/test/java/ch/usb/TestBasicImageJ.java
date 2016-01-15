package ch.usb;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
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
        ShortProcessor shortprocessor = new ShortProcessor(width, height);
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

    static final short[][] createTestImage() {
        short[][] is = new short[400][400];
        for(int i = 50;i<100; i++) {
            for(int j = 50;j<100;j++) {
                is[i][j] = 100;
            }
        }
        return is;
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

        ImagePlus imp = new ImagePlus("test Image",createShortProcessorFromArray(createTestImage()));
        long pcount = imp.getStatistics().pixelCount;
        assertTrue("Right sized object "+pcount,pcount == (400 * 400));

        double meanValue = imp.getStatistics().mean;
        System.out.println("Non-zero values:"+meanValue);
        assertTrue("Non-zero area "+meanValue,meanValue>0);
    }

    @Test
    public void testCreateTestImage() {


        ImageJ cInst = IJ.getInstance();

        assertNotNull("ImageJ should not be null",cInst);

        ImagePlus ip = new ImagePlus("test Image",createShortProcessorFromArray(createTestImage()));
        double preInvert = ip.getStatistics().mean;

        assertTrue("Non-zero Mean: ",preInvert>0);

        IJ.run(ip,"Invert","");

        double postInvert = ip.getStatistics().mean;

        assertTrue("Inversion Increased Mean "+preInvert+" -> "+postInvert, postInvert>=preInvert);
    }

    //@Test
    public void testPluginLoading() {
        //TODO fix plugin loading later
        IJ.run("ch.usb.USB_LungSegmentTJ");
    }

}
