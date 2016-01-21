package ch.usb;
/** ImageJ Plugin to Segment Lung Tissue from CT Image Data
*  
*  Uses adhoc algorithm  to identify lung:
*  1. Eliminate extra-corporal solid (non-air) objects (CT table, bedding, random noise)
*     by large radius noise-filtering and blurring.
*  2. Threshold lung tissue and air (both inside and outside of body) to a LUNGLIKE_MASK 
*     all else to NONLUNG_MASK.
*  3. FloodFill extra-corporal LUNGLIKE_MASK (ie air) to EXTCORP_MASK.
*  4. Consider Remaining LUNGLIKE_MASK voxels as true lung (an aprox.; airways, and corporal air GI) and
*     Restore original voxel values only where LUNGLIKE_MASK is set - rest set as NONLUNG.
*  
*  @author drTJRE.com University Hospital of Basel
*  @date   nov2015
*/

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.RankFilters;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;


public class COPD_LungSegment implements PlugInFilter {
  // Constants
  static final String VERSION= "ch.usb.COPD_LungSegment version 0.41";
  static final boolean VERBOSE= true;
  static final boolean DEBUG= true;
  public static final double MAX_HU_TISSUE= 2047.0;
  public static final double MIN_HU_TISSUE= -200.0;
  public static final double MAX_HU_LUNG= -380.0;
  public static final double MIN_HU_LUNG= -1500.0;

  static final int LUNGLIKE_MASK= 1024; // distinctive non-tissue value 
  static final int NONLUNG_MASK= 4096; // set above bone with distinct number
  static final int EXCORP_MASK= 2048; // set above bone with distinct number
  static final double OUTLIER_PREFILTER_RADIUS= 5.0;
  static final double MEAN_PREFILTER_RADIUS= 5.0;
  static final boolean PREFILTER= true;
    /**
     * This is set during @link setup and used during @link run
     */
  ImagePlus _imp;

    /**
     *
     * @param arg - parameter passing
     * @param imp - image to work on
     * @return alert if not of proper image type
     */
  public int setup(String arg, ImagePlus imp) {
    this._imp = imp;
    if (VERBOSE) System.out.println(VERSION);
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
  public void run(ImageProcessor ip) {
    ImageStack istack= _imp.getStack();
    int numOfSlices= istack.getSize();
    double val; int ival;
    for (int sliceN=1;sliceN<=numOfSlices;sliceN++) {
      ImageProcessor ips= istack.getProcessor(sliceN); // process one slice at a time
      int[][] unfilteredVoxData= ips.getIntArray(); // original voxel data

      int width= _imp.getWidth();
      int height= _imp.getHeight();

      // FILTERING
      // First filter image to eliminate extra-corporal outliers
      // which might be mistaken as tissue (bedding, table, specks):
      if (PREFILTER) {
        RankFilters rf= new RankFilters();
        rf.rank(ips, OUTLIER_PREFILTER_RADIUS, RankFilters.OUTLIERS, 0, (float)50.0);
        rf.rank(ips, MEAN_PREFILTER_RADIUS, RankFilters.MEAN);
      }


      // Threshold lung - result will include lung and extra-corporal air
      // will provide solid regions for facilitating next flood-fill step.
      for (int x=0; x<width; x++) {
        for (int y=0; y<height; y++) {
          val= ips.getPixelValue(x,y);
          if (isLung(val)) ips.putPixel(x,y,LUNGLIKE_MASK);
          else ips.putPixel(x,y,NONLUNG_MASK);
        }
      }
      // Flood Fill extra-corporal touching image outer edge with a NON Lung mask
      ips.setValue(EXCORP_MASK);
      FloodFiller floodFiller= new FloodFiller(ips);
      int boarderWidth= 3;
      for (int ie=0;ie<boarderWidth;ie++) {
          int xleft = ie, xright = width - 1-ie;
          int ytop = ie, ybottom = height - 1-ie;
          for (int x = 0; x < width; x++) {  // walk top end bottom edges
              if (ips.getPixel(x, ytop) == LUNGLIKE_MASK) floodFiller.fill(x, ytop);
              if (ips.getPixel(x, ybottom) == LUNGLIKE_MASK) floodFiller.fill(x, ybottom);
          }
          for (int y = 0; y < height; y++) {  // walk sides
              if (ips.getPixel(xleft, y) == LUNGLIKE_MASK) floodFiller.fill(xleft, y);
              if (ips.getPixel(xright, ybottom) == LUNGLIKE_MASK) floodFiller.fill(xright, y);
          }
      }
      // Restore Original Data ONLY in surviving LUNGLIKE_MASKed
      for (int x=0; x<width; x++) {
        for (int y=0; y<height; y++) {
          ival= ips.getPixel(x,y);
  	      if (ival!=LUNGLIKE_MASK) unfilteredVoxData[x][y]= NONLUNG_MASK;
        }
      }
      ips.setIntArray(unfilteredVoxData);
    } 

  }


  // "in-line" utilities
  public static boolean isLung(double HUval) { return inRange(HUval, MIN_HU_LUNG, MAX_HU_LUNG);} 
  public static boolean isTissue(double HUval) { return inRange(HUval, MIN_HU_TISSUE, MAX_HU_TISSUE);} 
  public static boolean inRange(double val, double min, double max) { return (val>=min && val<=max);}
  private static void DEBUG(String msg) {if (VERBOSE) System.out.println(msg);}
  private static void DEBUG(String msg, java.lang.Object x) {if (VERBOSE) System.out.println(msg+String.valueOf(x));}


}
