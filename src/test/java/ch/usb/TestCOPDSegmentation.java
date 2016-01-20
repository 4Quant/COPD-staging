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
    public static final boolean NON_STOP = false; // do not wait for user to see results

    @BeforeClass
    public static void setupItrue() {
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

        //if(!headless) showAndWait("testSegmentEmptyLung", imp);


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

        if(!headless) showAndWait("testSegmentSimpleLung", imp);

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


    /**
     * display an image via imagej and wait for user to respond.
     * Intended for visual feedback during testing and must be disabled
     * in final test.
     * @param msg message to display
     * @param imp image to display
     */
    private static void showAndWait(String msg, ImagePlus imp) {
        imp.show();
        if (!NON_STOP) IJ.runMacro("waitForUser(\""+msg+"\");run(\"Close All\");");
    }


    @Test
    public void testMockLung01() {
        System.out.println("@TEST testMockLung01 ===start====" );
        final String mockCTPath= "/mockCTs/01SquareLung1slice4761vox.tif";
        IJ.open(TestBasicImageJ.class.getResource(mockCTPath).getPath());
        ImagePlus imp= IJ.getImage();
        if (!headless) showAndWait("MockLung01", imp);
        assertNotNull(mockCTPath+" not loading", imp);

        TestBasicImageJ.LungStatistics lstats= TestBasicImageJ.LungStatistics.fromImp(imp);
        long lungVox= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println("lungVox="+String.valueOf(lungVox));


        System.out.println("==end===@TEST testMockLung01 " );
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
