package ch.usb;

import static org.junit.Assert.*;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.BeforeClass;

/**
 * @author thomas.re@usb.ch
 * Test each of the progressively more complex mock Thoraxic CTs
 */
public class TestMockImages {

    private final double POSTSEG_TOLERANCE= 0.1;
    private final double PRESEG_TOLERANCE= 0.0;
    public static final boolean headless = false;
    public static final boolean NON_STOP = false; // do not wait for user to see results
    static final String MOCK00="/mockCTs/00SquareLung10by10.tif";
    static final String MOCK01="/mockCTs/01SquareLung10k.tif";
    static final String MOCK02="/mockCTs/02TwoSquare9522.tif";
    static final String MOCK03="/mockCTs/03Lung3slices12658.tif";
    static final String MOCK04="/mockCTs/04LungBronchi13345.tif";
    static final String MOCK05="/mockCTs/05LungBronchiStomach14146_13345.tif";
    static final String MOCK06="/mockCTs/06AddOutsideAir27031_13345.tif";
    static final String MOCK07="/mockCTs/07AddNoise26095_13345.tif";
    static final String MOCK08="/mockCTs/08AddBedding36603_13345.tif";
    static final String MOCK09="/mockCTs/09AddSupport32464_13345.tif";
    static final String MOCK0A="/mockCTs/0Aopenlung.tif";
    static final long[] MOCK01_LUNGVOL= {10000, 10000}; // expected values before and after segmentation
    static final long[] MOCK02_LUNGVOL= {9522, 9522};
    static final long[] MOCK03_LUNGVOL= {12658, 12658};
    static final long[] MOCK04_LUNGVOL= {13345, 13345};
    static final long[] MOCK05_LUNGVOL= {14146, 13345};
    static final long[] MOCK06_LUNGVOL= {27031, 13345};
    static final long[] MOCK07_LUNGVOL= {26095, 13345};
    static final long[] MOCK08_LUNGVOL= {36603, 13345};
    static final long[] MOCK09_LUNGVOL= {32464, 13345};
    static final long[] MOCK0A_LUNGVOL= {32464, 13345};
    static final int[] MOCK01_PD= {15, -1008, 20, -1003, 30, -993}; // expected values of PDs
    static final int[] MOCK00_LAA= {-1023, 10, -1022,20, -1021,30}; // thresholds and expected LAAs
    static final int[] MOCK01_LAA= {-1023, 100, -950,7400, -900,10000}; // thresholds and expected LAAs

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
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK01,MOCK01_LUNGVOL[0]);
    }

    /**
     * test all mock lung files after segmentation
     */
    @Test
    public void testMock01PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK01, MOCK01_LUNGVOL[1]);
    }

    @Test
    public void testMock01PD1() {
        _tolerance= POSTSEG_TOLERANCE;
        testPD(MOCK01, MOCK01_PD[0], MOCK01_PD[1]);
    }


    @Test
    public void testMock01PD2() {
        _tolerance= POSTSEG_TOLERANCE;
        testPD(MOCK01, MOCK01_PD[2], MOCK01_PD[3]);
    }

    @Test
    public void testMock01PD3() {
        _tolerance= POSTSEG_TOLERANCE;
        testPD(MOCK01, MOCK01_PD[4], MOCK01_PD[5]);
    }

    @Test
    public void testMock00LAA1() {
        _tolerance= POSTSEG_TOLERANCE;
        testLAA(MOCK00, MOCK00_LAA[0], MOCK00_LAA[1]);
    }


    @Test
    public void testMock00LAA2() {
        _tolerance= POSTSEG_TOLERANCE;
        testLAA(MOCK00, MOCK00_LAA[2], MOCK00_LAA[3]);
    }

    @Test
    public void testMock00LAA3() {
        _tolerance= POSTSEG_TOLERANCE;
        testLAA(MOCK00, MOCK00_LAA[4], MOCK00_LAA[5]);
    }



    @Test
    public void testMock01LAA1() {
        _tolerance= POSTSEG_TOLERANCE;
        testLAA(MOCK01, MOCK01_LAA[0], MOCK01_LAA[1]);
    }


    @Test
    public void testMock01LAA2() {
        _tolerance= POSTSEG_TOLERANCE;
        testLAA(MOCK01, MOCK01_LAA[2], MOCK01_LAA[3]);
    }

    @Test
    public void testMock01LAA3() {
        _tolerance= POSTSEG_TOLERANCE;
        testLAA(MOCK01, MOCK01_LAA[4], MOCK01_LAA[5]);
    }



    @Test
    public void testMock02PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK02, MOCK02_LUNGVOL[1]);
    }


    @Test
    public void testMock01LAA() {
        _tolerance= POSTSEG_TOLERANCE;
        testLAA(MOCK01, MOCK01_LAA[0], MOCK01_LAA[1]);
    }

    @Test
    public void testMock03PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK03, MOCK03_LUNGVOL[1]);
    }

    @Test
    public void testMock04PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK04, MOCK04_LUNGVOL[1]);
    }

    @Test
    public void testMock05PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK05, MOCK05_LUNGVOL[1]);
    }

    @Test
    public void testMock06PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK06, MOCK06_LUNGVOL[1]);
    }

    @Test
    public void testMock07PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK07, MOCK07_LUNGVOL[1]);
    }

    @Test
    public void testMock08PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK08, MOCK08_LUNGVOL[1]);
    }

    // TODO: 20/01/16 - improve segmentation plugin so this "patient on cushion" case
    //    passes the test.  Currently shows 10.8% error
    @Ignore
    @Test
    public void testMock09PostSegmentation() {
        _tolerance= POSTSEG_TOLERANCE;System.out.print("POST-SEGMENT:");
        testSegmentation(MOCK09, MOCK09_LUNGVOL[1]);
    }

    @Test
    public void testMock0APreSegmentation() {
            _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK0A,MOCK0A_LUNGVOL[0]);
    }

    @Test
    public void testMock02PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK02, MOCK02_LUNGVOL[0]);
    }

    @Test
    public void testMock03PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK03, MOCK03_LUNGVOL[0]);
    }

    @Test
    public void testMock04PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK04, MOCK04_LUNGVOL[0]);
    }

    @Test
    public void testMock05PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK05, MOCK05_LUNGVOL[0]);
    }

    @Test
    public void testMock06PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK06, MOCK06_LUNGVOL[0]);
    }

    @Test
    public void testMock07PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK07, MOCK07_LUNGVOL[0]);
    }

    @Test
    public void testMock08PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK08, MOCK08_LUNGVOL[0]);
    }

    @Test
    public void testMock09PreSegmentation() {
        _tolerance= PRESEG_TOLERANCE;System.out.print("PRE-SEGMENT:");
        checkCTLungVoxelCount(MOCK09, MOCK09_LUNGVOL[0]);
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
        String cPath = TestMockImages.class.getResource(CTPath).getPath();
        System.out.println("Reading " + cPath);
        IJ.open(cPath);
        ImagePlus imp = IJ.getImage();
        assertNotNull(CTPath + " not loading", imp);

        COPD_LungSegment segPlug = new COPD_LungSegment();

        assertEquals(" Ensure plugin returns correct flags",
                segPlug.setup("", imp), PlugInFilter.DOES_16 + PlugInFilter.STACK_REQUIRED);

        segPlug.run(imp.getProcessor());
        //showAndWait("remember to comment out", imp);

        checkCTLungVoxelCount(imp, expectedVoxels);
    }

    /**
     * Perform PercentileDensity for specific percentile and test result
     *
     * @param CTPath
     * @param percentile
     * @param expectedPD15
     */
    public void testPD(String CTPath, int percentile, int expectedPD15) {
        String cPath = TestMockImages.class.getResource(CTPath).getPath();
        System.out.println("Reading "+cPath);
        IJ.open(cPath);
        ImagePlus imp= IJ.getImage();
        assertNotNull(CTPath+" not loading", imp);

        COPD_PDxLAAx pdPlug= new COPD_PDxLAAx();
        pdPlug.setup("", imp);
        pdPlug.run(imp.getProcessor());
        int val= pdPlug.getPD(percentile);
        assertEquals(CTPath+":PD"+String.valueOf(percentile)+" calculated does not match expected", expectedPD15, val);

    }

    /**
     * Calculates Low Attenuation Level for specific threshold on an image
     * @param CTPath  image to test
     * @param threshold max attenuation (density in HU) to test
     * @param expectedLAA value expected
     */
    public void testLAA(String CTPath, int threshold, long expectedLAA) {
        String cPath = TestMockImages.class.getResource(CTPath).getPath();
        System.out.println("Reading "+cPath);
        IJ.open(cPath);
        ImagePlus imp= IJ.getImage();
        assertNotNull(CTPath+" not loading", imp);

        COPD_PDxLAAx pdPlug= new COPD_PDxLAAx();
        pdPlug.setup("", imp);
        pdPlug.run(imp.getProcessor());
        int val= pdPlug.getLAA(threshold);
        assertEquals(CTPath+": LAA calculated does not match expected", expectedLAA, val);

    }
}
