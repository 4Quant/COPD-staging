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

    @Ignore
    @Test
    /** another example where only real lung is in lung range
     *  therefore - the segmentation should correspond to LungStatistics
     */
    public void testMockLung02() {
        final String mockCTPath= "/mockCTs/02TwoSquareLung1slice9522.tif";
        final long EXPECTED_VOXELS= 9522;
        IJ.open(TestBasicImageJ.class.getResource(mockCTPath).getPath());
        ImagePlus imp= IJ.getImage();

        //if (!headless) showAndWait("MockLung01", imp);
        assertNotNull(mockCTPath+" not loading", imp);

        // Check Voxels are as expected
        long lungVoxels= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println(mockCTPath+" lungVoxels="+lungVoxels);

        //assertEquals(mockCTPath+"LungStats Voxs not equal to Segmented");

    }

    @Test
    /**
     * test all mock lung files
     */
    public void testMocks() {
        testMockCT("/mockCTs/01SquareLung1slice4761vox.tif", 4761, 4761);
        testMockCT("/mockCTs/02TwoSquareLung1slice9522.tif", 9522, 9522);
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

        // Check Voxel count is as expected before segmentation
        long lungVoxels= TestBasicImageJ.LungStatistics.fromImp(imp).lungVoxels;
        System.out.println(mockCTPath+" BEFORE SEGMENT lungVoxels="+lungVoxels);
        assertEquals(mockCTPath+" voxels not value expected ", expectedVoxelsBefore , lungVoxels);
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
