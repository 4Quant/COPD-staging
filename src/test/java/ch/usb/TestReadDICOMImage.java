package ch.usb;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;


/**
 * Created by tomjre on 15/01/16.
 */
public class TestReadDICOMImage {

    final int mode = 1;
    private String getTestImage() {
        String thoraxFile = this.getClass().getResource("/thoraxslice.tif").getPath();
        return thoraxFile;
    }

    /**
     * storage for fetched image
     */
    private ImagePlus _imp;


    @Before
    public void testLoadSampleLungImage() {
        System.out.println("@Test testLoadSampleLungImage() ");
        IJ.open(getTestImage());
        _imp= IJ.getImage();
        assertNotNull("fetched image is null", _imp);
    }

    @Test
    public void testReadCalibration() {
        System.out.println("@Test testReadCalibration()");
        Calibration cal= _imp.getCalibration();
        assertNotNull("Calibration not found", cal);
        System.out.println(cal);
        System.out.println("Value at 0:"+cal.getCValue((int)0) );
        System.out.println("Value at 4096:"+cal.getCValue((int)4096) );



        float[] ctable = cal.getCTable();
        System.out.println(ctable.length);
        for (int i=0;i<3;i++)
            System.out.println(ctable[i]);

    }

    @Test
    public void testImageProcessorMatchesCalibration() {
        Calibration cal= _imp.getCalibration();
        ImageProcessor ip = _imp.getProcessor();
        double pixVal = ip.getPixelValue(10,10);
        double conVal = ip.getPixel(10,10);
        assertNotEquals("Calibration should be changing the values",pixVal,conVal);

        assertEquals("Looking up should be the same as the ImageProcessor",cal.getRawValue(pixVal), conVal,1.0);


    }


    @Test
    public void testImageRegion() {

        fail("Check that it is a CT and thorax");
    }

    @Test
    public void testCalculatingStatistics() {
        TestBasicImageJ.LungStatistics lstats = TestBasicImageJ.LungStatistics.fromImp(_imp);
        System.out.println("Lung Stats:"+lstats);
        assertTrue("Lung Volume greater than 0", lstats.lungVoxels>0);
        assertTrue("Mean Value greater than lung", lstats.meanVal>USB_LungSegmentTJ.MIN_HU_LUNG);
    }

    @Test
    public void testTextFileFromCalibration() throws IOException {
        String calibrationName = File.createTempFile("calibration",".txt").getPath();
        System.out.println("Saving calibration as:"+calibrationName);
        IJ.run(_imp,"Calibrate...", "save="+calibrationName);

    }

}
