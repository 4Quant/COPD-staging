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
        assertEquals(mockCTPath+" voxels not value expected "+EXPECTED_VOXELS, EXPECTED_VOXELS, lungVoxels);

    }

    @Test
    /**
     * test all mock lung files before and after segmentation
     */
    public void testMocksPostSegmentation() {
        testMockCT("/mockCTs/01SquareLung4761.tif", 4761, 4761);
        testMockCT("/mockCTs/02TwoSquare9522.tif", 9522, 9522);
        testMockCT("/mockCTs/03Lung3slices12658.tif", 12658, 12658);
        testMockCT("/mockCTs/04LungBronchi13345.tif", 13345, 13345);
        testMockCT("/mockCTs/05LungBronchiStomach14146_13345.tif", 14146, 13345);
        testMockCT("/mockCTs/06AddOutsideAir27031_13345.tif", 27031, 13345);
        testMockCT("/mockCTs/07AddNoise26050_13345.tif", 26050, 13345);
        testMockCT("/mockCTs/08AddBedding36603_13345.tif", 36603, 13345);
        testMockCT("/mockCTs/09AddSupport32465_13345.tif", 32465, 13345);
    }

    /** test one mock CT file that it conforms to expected pre and post Segmentation lung voxels
     *
     * @param mockCTPath MockCT file to process
     * @param expectedVoxelsBefore number of voxels which should be classified as lung originally
     * @param expectedVoxelsAfter number of voxels which should be classified as lung after
     */
    public void testMockCT(String mockCTPath, long expectedVoxelsBefore, long expectedVoxelsAfter){
        IJ.open(TestBasicImageJ.class.getResource(mockCTPath).getPath());
        ImagePlus imp= IJ.getImage();
        assertNotNull(mockCTPath+" not loading", imp);
        long lungVoxels;

        // Check Voxel count is as expected before segmentation
        lungVoxels= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println(mockCTPath+" BEFORE SEGMENT lungVoxels="+lungVoxels);
        assertEquals("PRE-Segmentation: "+mockCTPath+" voxels not value expected ", expectedVoxelsBefore , lungVoxels);

        // Segmentation

        // Check Voxel count is as expected AFTER segmentation
        lungVoxels= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println(mockCTPath+" AFTER SEGMENT lungVoxels="+lungVoxels);
        assertEquals("POST-Segmentation: "+mockCTPath+" voxels not value expected ", expectedVoxelsAfter , lungVoxels);

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
