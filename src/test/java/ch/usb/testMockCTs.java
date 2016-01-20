package ch.usb;

import static org.junit.Assert.*;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.BeforeClass;

/** created by tomjre on 20/01/16.
 * Test each of the progressively more complex mock Thoraxic CTs
 */
public class TestMockCTs {

    private final double POSTSEG_TOLERANCE= 0.1;
    private final double PRESEG_TOLERANCE= 0.0;
    public static final boolean headless = false;
    public static final boolean NON_STOP = false; // do not wait for user to see results
    static final String MOCK01="/mockCTs/01SquareLung4761.tif";
    static final String MOCK02="/mockCTs/02TwoSquare9522.tif";
    static final String MOCK03="/mockCTs/03Lung3slices12658.tif";
    static final String MOCK04="/mockCTs/04LungBronchi13345.tif";
    static final String MOCK05="/mockCTs/05LungBronchiStomach14146_13345.tif";
    static final String MOCK06="/mockCTs/06AddOutsideAir27031_13345.tif";
    static final String MOCK07="/mockCTs/07AddNoise26095_13345.tif";
    static final String MOCK08="/mockCTs/08AddBedding36603_13345.tif";
    static final String MOCK09="/mockCTs/09AddSupport32464_13345.tif";
    static final long[] MOCK01EXP= {4761, 4761}; // expected values before and after segmentation
    static final long[] MOCK02EXP= {9522, 9522};
    static final long[] MOCK03EXP= {12658, 12658};
    static final long[] MOCK04EXP= {13345, 13345};
    static final long[] MOCK05EXP= {14146, 13345};
    static final long[] MOCK06EXP= {27031, 13345};
    static final long[] MOCK07EXP= {26095, 13345};
    static final long[] MOCK08EXP= {36603, 13345};
    static final long[] MOCK09EXP= {32464, 13345};

    double _tolerance= 0.0;

    @BeforeClass
    public static void setupItrue() {
        if(headless) ImageJ.main("--headless".split(" "));
        else ImageJ.main("".split(" "));
    }


    /**
     * test all mock lung files before segmentation
     */
    @Test
    public void testMock01PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK01,MOCK01EXP[0]);
    }

    /**
     * test all mock lung files after segmentation
     */
    @Test
    public void testMock01PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK01, MOCK01EXP[1]);
    }

    @Test
    public void testMock02PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK02, MOCK02EXP[1]);
    }

    @Test
    public void testMock03PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK03, MOCK03EXP[1]);
    }

    @Test
    public void testMock04PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK04, MOCK04EXP[1]);
    }

    @Test
    public void testMock05PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK05, MOCK05EXP[1]);
    }

    @Test
    public void testMock06PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK06, MOCK06EXP[1]);
    }

    @Test
    public void testMock07PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK07, MOCK07EXP[1]);
    }

    @Test
    public void testMock08PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK08, MOCK08EXP[1]);
    }

    @Test
    public void testMock09PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT");
        testSegmentation(MOCK09, MOCK09EXP[1]);
    }

    @Test
    public void testMock02PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK02, MOCK02EXP[0]);
    }

    @Test
    public void testMock03PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK03, MOCK03EXP[0]);
    }

    @Test
    public void testMock04PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK04, MOCK04EXP[0]);
    }

    @Test
    public void testMock05PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK05, MOCK05EXP[0]);
    }

    @Test
    public void testMock06PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK06, MOCK06EXP[0]);
    }

    @Test
    public void testMock07PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK07, MOCK07EXP[0]);
    }

    @Test
    public void testMock08PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK08, MOCK08EXP[0]);
    }

    @Test
    public void testMock09PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT");
        checkCTLungVoxelCount(MOCK09, MOCK09EXP[0]);
    }

    /**
     * check that the lung region voxel numer matches expected
     * @param mockCTPath path to image file
     * @param expectedVoxCount expected lung voxel count
     */
    public void checkCTLungVoxelCount(String mockCTPath, long expectedVoxCount) {
        IJ.open(TestBasicImageJ.class.getResource(mockCTPath).getPath());
        ImagePlus imp = IJ.getImage();
        assertNotNull(mockCTPath + " not loading", imp);
        checkCTLungVoxelCount(imp, expectedVoxCount);
    }

    /**
     * check that the lung region voxel numer matches expected
     * @param imp  image to check
     * @param expectedVoxCount expected lung voxel count
     */
    public void checkCTLungVoxelCount(ImagePlus imp, long expectedVoxCount)  {
        long lungVoxels;
        String mockCTPath= imp.getTitle();
        // Check Voxel count is as expected before segmentation
        lungVoxels= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println(mockCTPath+" lungVoxels="+lungVoxels);
        assertEquals(mockCTPath+" voxels Count not value expected ", expectedVoxCount , lungVoxels,
                expectedVoxCount*_tolerance);
    }

    /**
     * display an image via imagej and wait for user to respond.
     * Intended for visual feedback during testing and must be disabled
     * in final test.
     * WARNING: THIS BREAKS LungStatistics performed afterwards!
     *
     * @param msg message to display
     * @param imp image to display
     */
    private static void showAndWait(String msg, ImagePlus imp) {
        imp.show();
        if (!NON_STOP) IJ.runMacro("waitForUser(\""+msg+"\");run(\"Close All\");");
    }

    /**
     * Perform segmentation and test result
     * @param CTPath  image file to process
     * @param expectedVoxels total lung voxl count expected as result
     */
    public void testSegmentation(String CTPath, long expectedVoxels) {
        IJ.open(TestBasicImageJ.class.getResource(CTPath).getPath());
        ImagePlus imp= IJ.getImage();
        assertNotNull(CTPath+" not loading", imp);

        USB_LungSegment segPlug = new USB_LungSegment();

        assertEquals(" Ensure plugin returns correct flags",
                segPlug.setup("",imp), PlugInFilter.DOES_16+PlugInFilter.STACK_REQUIRED);

        segPlug.run(imp.getProcessor());
        //showAndWait("remember to comment out", imp);

        checkCTLungVoxelCount(imp, expectedVoxels);

    }
}
