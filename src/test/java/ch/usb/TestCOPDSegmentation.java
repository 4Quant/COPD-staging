package ch.usb; /**
 * 
 */

import static org.junit.Assert.*;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import org.junit.Test;

import org.junit.BeforeClass;

/**
 * @author mader
 *
 */
public class TestCOPDSegmentation {

    public static final boolean headless = false;
    @BeforeClass
    public static void setupImageJ() {
        if(headless) ImageJ.main("--headless".split(" "));
        else ImageJ.main("".split(" "));
    }

    static public ImagePlus createSimpleLung(short fgValue) {
        return new ImagePlus("test lungs",
                TestBasicImageJ.createShortProcessorFromArray(
                        TestBasicImageJ.createTestImage(fgValue, (short) 0)));

    }

    @Test
    public void testSegmentEmptyLung() {
        ImageJ cInst = IJ.getInstance();

        assertNotNull("ImageJ should not be null",cInst);

        ImagePlus imp = createSimpleLung((short) USB_LungSegment.MAX_HU_TISSUE);

        if(!headless) imp.show();


        double preInvert = imp.getStatistics().mean;

        assertTrue("Non-zero Mean: ",preInvert>0);

        assertTrue("Maximum value matches lung maximum",
                imp.getStatistics().max== USB_LungSegment.MAX_HU_TISSUE);

        assertTrue("Minimum value should be 0",
                imp.getStatistics().min==0);

        USB_LungSegment tjPlug = new USB_LungSegment();

        assertEquals(" Ensure plugin returns correct flags",
                tjPlug.setup("",imp),
                PlugInFilter.DOES_16+PlugInFilter.STACK_REQUIRED);

        IJ.saveAsTiff(imp,"empty_lung.tif");

        tjPlug.run(imp.getProcessor());

        IJ.saveAsTiff(imp,"after_segmentation_empty_lung.tif");
        double postInvert = imp.getStatistics().mean;
        System.out.println(tjPlug+" should Increas mean value "+preInvert+" -> "+postInvert);
        assertTrue("Filter should Increas mean value "+preInvert+" -> "+postInvert, postInvert>=preInvert);
    }

    @Test
    public void testSegmentSimpleLung() {
        ImageJ cInst = IJ.getInstance();

        assertNotNull("ImageJ should not be null",cInst);
        assertTrue("Lung values are inside 16-bit range (min) "+Short.MIN_VALUE,
                (USB_LungSegment.MAX_HU_LUNG>Short.MIN_VALUE));

        assertTrue("Lung values are inside 16-bit range (max) "+Short.MAX_VALUE,
                (USB_LungSegment.MAX_HU_LUNG<Short.MAX_VALUE));

        ImagePlus imp = createSimpleLung((short) USB_LungSegment.MAX_HU_LUNG);

        if(!headless) imp.show();

        double preInvert = imp.getStatistics().mean;

        assertTrue("Non-zero Mean: ",preInvert>0);

        //IJ.run(ip,"Invert","");
        USB_LungSegment tjPlug = new USB_LungSegment();

        assertEquals(" Ensure plugin returns correct flags",
                tjPlug.setup("",imp),
                PlugInFilter.DOES_16+PlugInFilter.STACK_REQUIRED);
        IJ.saveAsTiff(imp,"basic_lung.tif");

        tjPlug.run(imp.getProcessor());

        IJ.saveAsTiff(imp,"after_segmentation_basic_lung.tif");
        double postInvert = imp.getStatistics().mean;
        System.out.println(tjPlug+" should Increas mean value "+preInvert+" -> "+postInvert);
        assertTrue("Filter should Increas mean value "+preInvert+" -> "+postInvert, postInvert>=preInvert);

    }


    @Test
    public void testCOPDonDemoPatient() {
        System.out.println("@TEST testCOPDonDemoPatient ===start====" );
        final String thoraxSamplePath= "/thoraxslice.tif";
        IJ.open(TestBasicImageJ.class.getResource(thoraxSamplePath).getPath());
        ImagePlus imp= IJ.getImage();
        assertNotNull(thoraxSamplePath+" not loading", imp);



        System.out.println("==end===@TEST testCOPDonDemoPatient " );
    }


	/**
	 * Test method for {@link tipl.scripts.UFEM#makePoros(tipl.formats.TImg)}.
	 */
	@Test
	public void testLoadImage() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testIsImageJAvailable() {
		return;
	}

	/**
	 * Test method for {@link tipl.scripts.UFEM#makePoros(tipl.formats.TImg)}.
	 */
	@Test
	public void testSegmentLung() {
		fail("Not yet implemented"); // TODO
	}
	
		/**
	 * Test method for {@link tipl.scripts.UFEM#makePoros(tipl.formats.TImg)}.
	 */
	@Test
	public void testCalculateScore() {
		fail("Not yet implemented"); // TODO
	}


	/**
	 * Test method for {@link tipl.scripts.UFEM#makePreview(java.lang.String, tipl.formats.TImg)}.
	 */
	@Test
	public void testMakePreview() {
		fail("Not yet implemented"); // TODO
	}

}
