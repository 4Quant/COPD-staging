package ch.usb;

/**
 *
 *  ImageJ Plugin to Segment Lung Tissue from CT Image Data
 *
 *  Input:
 *    Works only on 16bit Multi-slice ImagePlus images
 *    Images must contain Calibration of CT density in Hounsfield Units (HU)
 *    Standard DICOM's imported to ImageJ using the "File->importSequence"
 *    command will normally have this calibration properly set automatically.
 *
 *  Output:
 *    In-place modification of input image with voxels identified as "lung"
 *    with their original value.  All other voxels set to bone/metal HU mask
 *    for easy distinction in subsequent plugins which analyze lungs.
 *
 *  Segmentation Algorithm Overview:
 *    1. Identify voxels within lung density HU range  (<380HU for example)
 *       Since lung contains tissue and air voxels, this range also indentifies
 *       non-lung intra/extra-corporal air spaces.
 *    2. Eliminate extra-corporal air spaces based on continuity with outer boarder of image.
 *
 *  Limitations:
 *    Air contained in Trachea, GI track, and even subcutaneous emphysema considered as lung.
 *
 *  Medical References:
 *    http://www.ncbi.nlm.nih.gov/pubmed/18515044
 *    http://www.ncbi.nlm.nih.gov/pubmed/19196813
 *    http://www.ncbi.nlm.nih.gov/pubmed/22697325
 *
 *  @author thomas.re@usb.ch  - University Hospital of Basel, Switzerland
 *  @date    jan2016
 *
 */

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.RankFilters;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;


public class COPD_LungSegment implements PlugInFilter {

    public static final String STATUSBAR_TITLE= "COPD_LungSegment";
  public static final double MAX_HU_LUNG= -380.0;
  public static final double MIN_HU_LUNG= -1500.0;
  public static final int BOARDER_WIDTH= 3;

  // MASKS: Distinct bone/metal HU values used for temporarily taging voxels
  static final int LUNGLIKE_MASK= 1024;
  static final int NONLUNG_MASK= 4096;
  static final int EXCORP_MASK= 2048;

  // TODO: 26/01/16 have these settings passed as setup string
  static final double OUTLIER_PREFILTER_RADIUS= 5.0;
  static final double MEAN_PREFILTER_RADIUS= 5.0;
  static final boolean PREFILTER= true;

  /**
   * image being processed - passed in setup
   */
  ImagePlus _imp;

  // TODO: 26/01/16 read arguments to set options
    /**
     * Initialize plugin
     * Accepts only 16bit Multi-slice images
     *
     * @param arg - parameter passing
     * @param imp - image to work on
     * @return alert if not of proper image type
     */
  public int setup(String arg, ImagePlus imp) {
    this._imp = imp;
    return DOES_16+STACK_REQUIRED;
  }

    /**
     * ImageJ plugin method called to perform actual work of segmenting lung
     *  1. Eliminate extra-corporal solid (non-air) objects (CT table, bedding, random noise)
     *     by large radius noise-filtering and blurring.
     *  2. Threshold lung tissue and air (both inside and outside of body) to a LUNGLIKE_MASK
     *     all else to NONLUNG_MASK.
     *  3. FloodFill extra-corporal LUNGLIKE_MASK (ie air) to EXTCORP_MASK.
     *  4. Consider Remaining LUNGLIKE_MASK voxels as true lung (an aprox.; airways, and corporal air GI).
     *  5. Restore original voxel values only where LUNGLIKE_MASK is set - rest set as NONLUNG.
     * @param ip (NOT USED)
     */


    /**
     * Perform segmentation
     * 1. Eliminate extra-corporal thin items such as bedding using pre-filtering.
     * 2. Identify lung-like voxels based on density (HU range)
     * 3. Eliminate extra-corporal spaces based on continuity with outer boarders.
     *
     * results: original image non-lung voxels replaced with "metal" density mask.
     *          lung voxels left as original.
     *
     * @param ip - ImageProcessor of input image to process
     */
    public void run(ImageProcessor ip) {
      IJ.showStatus(STATUSBAR_TITLE+":"+_imp.getTitle());
      ImageStack istack= _imp.getStack();
      int numOfSlices= istack.getSize();
      double val; int ival;
      for (int sliceN=1;sliceN<=numOfSlices;sliceN++) {
        IJ.showProgress(sliceN, numOfSlices);
        ImageProcessor ips= istack.getProcessor(sliceN); // process one slice at a time
        int[][] unfilteredVoxData= ips.getIntArray(); // original voxel data

        int width= _imp.getWidth();
        int height= _imp.getHeight();

        // FILTERING
        // First filter image to eliminate extra-corporal outliers
        // which might be mistaken as non-lung tissue (bedding, table, noise):
        if (PREFILTER) {
          RankFilters rf= new RankFilters();
          rf.rank(ips, OUTLIER_PREFILTER_RADIUS, RankFilters.OUTLIERS, 0, (float)50.0);
          rf.rank(ips, MEAN_PREFILTER_RADIUS, RankFilters.MEAN);
        }


      // Identify voxels in lung density range
      for (int x=0; x<width; x++) {
        for (int y=0; y<height; y++) {
          val= ips.getPixelValue(x,y);
          if (isLung(val)) ips.putPixel(x,y,LUNGLIKE_MASK);
          else ips.putPixel(x,y,NONLUNG_MASK);
        }
      }

      // Tag as non-lung extra-corporal spaces of lung density
      // but continuous with outer boarders of image
      ips.setValue(EXCORP_MASK);
      FloodFiller floodFiller= new FloodFiller(ips);
      for (int ie=0;ie<BOARDER_WIDTH;ie++) {
          int xleft = ie, xright = width - 1-ie;
          int ytop = ie, ybottom = height - 1-ie;
          for (int x = 0; x < width; x++) {
              if (ips.getPixel(x, ytop) == LUNGLIKE_MASK) floodFiller.fill(x, ytop);
              if (ips.getPixel(x, ybottom) == LUNGLIKE_MASK) floodFiller.fill(x, ybottom);
          }
          for (int y = 0; y < height; y++) {
              if (ips.getPixel(xleft, y) == LUNGLIKE_MASK) floodFiller.fill(xleft, y);
              if (ips.getPixel(xright, ybottom) == LUNGLIKE_MASK) floodFiller.fill(xright, y);
          }
      }

      // Restore Original voxel values only in remaining voxels previously
      // identified as having lung density.
      for (int x=0; x<width; x++) {
        for (int y=0; y<height; y++) {
          ival= ips.getPixel(x,y);
  	      if (ival!=LUNGLIKE_MASK) unfilteredVoxData[x][y]= NONLUNG_MASK;
        }
      }
      ips.setIntArray(unfilteredVoxData);
    } 

  }


  // utilities

  /**
   * utility to check if a Hounsfeld Unit is within lung density.
   *
   * @param HUval Hounsfeld Unit value to check
   * @return true if within lung density range
     */
  public static boolean isLung(double HUval) { return (HUval>=MIN_HU_LUNG && HUval<=MAX_HU_LUNG);}


}
