package ch.usb;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.process.ShortProcessor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Created by tomjre on 15/01/16.
 */
public class TestReadDICOMImage {

    final int mode = 0;
    private String getTestImage() {
        if(mode==0) return "/Users/tomjre/Desktop/Stolz45Data/sampleLung2slices.tif";
        else return "sample.dcm";
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
        System.out.println(cal.getCValue((int)0) );
        System.out.println(cal.getCValue((int)4096) );
        float[] ctable= cal.getCTable();
        System.out.println(ctable.length);
        for (int i=0;i<3;i++)
            System.out.println(ctable[i]);

    }

    @Test
    public void testImageRegion() {
        fail("Check that it is a CT and thorax");
    }

}
