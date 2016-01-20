package ch.usb;

import static org.junit.Assert.*;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.BeforeClass;

/** created by tomjre on 20/01/16.
 * Test each of the progressively more complex mock Thoraxic CTs
 */
public class TestMockCTs {


    public static final boolean headless = false;
    public static final boolean NON_STOP = false; // do not wait for user to see results

    @BeforeClass
    public static void setupItrue() {
        if(headless) ImageJ.main("--headless".split(" "));
        else ImageJ.main("".split(" "));
    }

    @Ignore
    @Test
    public void testMockLung01() {
        final String mockCTPath= "/mockCTs/01SquareLung1slice4761vox.tif";
        final long EXPECTED_VOXELS= 4761;
            IJ.open(TestBasicImageJ.class.getResource(mockCTPath).getPath());
        ImagePlus imp= IJ.getImage();

        //if (!headless) showAndWait("MockLung01", imp);
        assertNotNull(mockCTPath+" not loading", imp);

        // Check Voxels are as expected
        long lungVoxels= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println(mockCTPath+" lungVoxels="+lungVoxels);
        assertEquals(mockCTPath+" voxels Count not value expected "+EXPECTED_VOXELS, EXPECTED_VOXELS, lungVoxels);

    }

    /**
     * test all mock lung files before segmentation
     */
    @Test
    public void testMock01PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/01SquareLung4761.tif", 4761);
    }

    @Test
    public void testMock02PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/02TwoSquare9522.tif", 9522);
    }

    @Test
    public void testMock03PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/03Lung3slices12658.tif", 12658);
    }

    @Test
    public void testMock04PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/04LungBronchi13345.tif", 13345);
    }

    @Test
    public void testMock05PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/05LungBronchiStomach14146_13345.tif", 14146);
    }

    @Test
    public void testMock06PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/06AddOutsideAir27031_13345.tif", 27031);
    }

    @Test
    public void testMock07PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/07AddNoise26095_13345.tif", 26095);
    }

    @Test
    public void testMock08PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/08AddBedding36603_13345.tif", 36603);
    }

    @Test
    public void testMock09PreSegmentation() {
        System.out.print("PRE-SEGMENT");
        testCT("/mockCTs/09AddSupport32464_13345.tif", 32464);
    }

    @Ignore
    /**
     * test all mock lung files before and after segmentation
     */
    @Test
    public void testMocksPostSegmentation() {
        testCT("/mockCTs/01SquareLung4761.tif", 4761);
        testCT("/mockCTs/02TwoSquare9522.tif", 9522);
        testCT("/mockCTs/03Lung3slices12658.tif", 12658);
        testCT("/mockCTs/04LungBronchi13345.tif", 13345);
        testCT("/mockCTs/05LungBronchiStomach14146_13345.tif", 13345);
        testCT("/mockCTs/06AddOutsideAir27031_13345.tif", 13345);
        testCT("/mockCTs/07AddNoise26095_13345.tif", 13345);
        testCT("/mockCTs/08AddBedding36603_13345.tif", 13345);
        testCT("/mockCTs/09AddSupport32465_13345.tif", 13345);
        testCT("/mockCTs/09AddSupport32464_13345.tif", 13345);
    }

    /** test one mock CT file that it conforms to expected lung voxels
     *
     * @param mockCTPath MockCT file to process
     * @param expectedVoxCount number of voxels which should be classified as lung originally
     */
    public void testCT(String mockCTPath, long expectedVoxCount)  {
        IJ.open(TestBasicImageJ.class.getResource(mockCTPath).getPath());
        ImagePlus imp= IJ.getImage();
        assertNotNull(mockCTPath+" not loading", imp);
        long lungVoxels;

        // Check Voxel count is as expected before segmentation
        lungVoxels= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println(mockCTPath+" lungVoxels="+lungVoxels);
        assertEquals(mockCTPath+" voxels Count not value expected ", expectedVoxCount , lungVoxels);

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

}
