import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by tomjre on 14/01/16.
 */
public class BasicTests {
    @Test
    public void testStringConcatenation() {
        double v0, v2, v1;
        String[] args = "-487 2.0 5".split(" ");

        v0= Double.valueOf(args[0]);
        v1= Double.valueOf(args[1]);
        v2= Double.valueOf(args[2]);
        // test ranges
        assertTrue( "Should be inside the lung range",
                USB_LungSegmentTJ.inRange(v0, USB_LungSegmentTJ.MIN_HU_LUNG,USB_LungSegmentTJ.MAX_HU_LUNG)
        );
        assertTrue( "Test lung values",USB_LungSegmentTJ.isLung(v0) );
        assertTrue( "Test tissue values",USB_LungSegmentTJ.isTissue(v1) );

    }
    @Test
    public void testCreateDemoLung() {
        ImagePlus ip = IJ.createImage("Test Image",400,400,1,8);

        IJ.runMacro("makeRectangle(80,80,260,260);run('Invert');");
        assertTrue("Non-zero area",ip.getStatistics().areaFraction>0);
        assertTrue("Less than 100% area",ip.getStatistics().areaFraction<1.0);
    }

    @Test
    public void testStartImageJ() {
        // Main context
        //ImageJ testIJ = new ImageJ();
        // Run a simple macro

        ImagePlus ip = IJ.createImage("Test Image","8-bit black",400,400,1);

        IJ.run("Show Info...");

        IJ.runMacro("makeRectangle(80,80,260,260);run('Invert');");

        USB_LungSegmentTJ tjPlug = new USB_LungSegmentTJ();
        tjPlug.setup("",ip);
        tjPlug.run(ip.getProcessor());

    }

    @Test
    public void testPluginLoading() {
        //TODO fix plugin loading later
        IJ.run("USB_LungSegmentTJ");
    }

}
