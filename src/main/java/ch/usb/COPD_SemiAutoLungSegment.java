package ch.usb;

/**
 *  ImageJ Plugin to Semi-Automatically Segment Lung Tissue from CT Image Data
 *
 *  User identifies lung position in a few key slices and plugin finds the lung
 *  in all slices.
 *
 *  Usage:
 *  1. Load Lung data in ImageJ and save as a multi-slice .tif
 *  2. Start with slice 1 and
 *     Draw a ROI (any shape) which contains at lest 1 voxel of each lung in
 *     all slices of data where lung is present but NO voxels of non-lung air
 *     (GI track, sub-cutaneous emphysema, extra-corporal air)
 *     Save ROI (File->SaveAs->Selection) in the same directory as the
 *     .tif CT image file with the same filename, substituting the
 *     .tif extension with .1.roi.
 *  3. Scroll through consequitive slices confirming that the ROI can
 *     identify both lungs while avoiding non-lung air.  When reaching
 *     a slice for which the ROI is no longer adequate, draw a new
 *     ROI and save it with the number of the first slice for which to
 *     apply it.
 *  4. Continue this process until all slices are covered.  If a slice
 *     has no lung present, a ROI can contain only tissue, but no air.
 *  5. In the end you will have the original image.tif and a set of .roi
 *     files typically 1 per few dozen slices.
 *  6. Now call this plugin.
 *
 *  Input:
 *    Works only on 16bit Multi-slice ImagePlus images
 *    Images must contain Calibration of CT density in Hounsfield Units (HU)
 *    Standard DICOM's imported to ImageJ using the "File->importSequence"
 *    command will normally have this calibration properly set automatically.
 *    ROI files: must match name of original image, be in same directory,
 *    only have extension with first slice number to which apply and .roi.
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
 *
 *  @author thomas.re@usb.ch  - University Hospital of Basel, Switzerland
 *  @date    dec2016
 *
 */

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.io.FileInfo;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.RankFilters;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;

import java.awt.*;

public class COPD_SemiAutoLungSegment implements PlugInFilter {
  // Constants
  static final String VERSION= "ch.usb.COPD_SemiAutoLungSegment version 1.1";
  static final boolean VERBOSE= true;
  static final boolean DEBUG= false;
  static final boolean PREFILTER= true;
  static final double MAX_HU_TISSUE= 2000.0; 
  static final double MIN_HU_TISSUE= -200.0;
  static final double MAX_HU_LUNG= -250.0; 
  static final double MIN_HU_LUNG= -1500.0;
  static final int LUNG_MASK= 0; 
  static final int AIRLUNG_MASK= 123; 
  static final int NONLUNG_MASK= 2048;
  static final double OUTLIER_PREFILTER_RADIUS= 5.0;
  static final double MEAN_PREFILTER_RADIUS= 5.0;

  ImagePlus _imp;
  String    _impURL;


  public int setup(String arg, ImagePlus imp) {
    this._imp = imp;
    _impURL= ij.io.OpenDialog.getLastDirectory()+_imp.getTitle();
    DEBUG("_impURL", _impURL);
    
    if (VERBOSE) System.out.println(VERSION);
    return DOES_16+STACK_REQUIRED+ROI_REQUIRED;
  }

  /** ImageJ plugin method called to perform actual work of segmenting lung
  *  0. Requires user ROI to indicate seed areas for lung
  *  1. PREFILTER if selected.
  *  2. Threshold lung tissue and air (both inside and outside of body) to a AIRLUNG_MASK 
  *     all else to NONLUNG_MASK.
  *  3. FloodFill as LUNG_MASK only voxels which meet lung HU range AND are continuous with a 
  *     user specified region.
  *  5. Restore original voxel values only where LUNG_MASK is set - rest set as NON_LUNG.
  */
  public void run(ImageProcessor ip0) {

    ImageStack istack= _imp.getStack();
    int numOfSlices= istack.getSize();
    double val; int ival;
    for (int sliceN=1;sliceN<=numOfSlices;sliceN++) {
      ImageProcessor ip= istack.getProcessor(sliceN); // process one slice at a time
      if (VERBOSE) _imp.setSlice(sliceN);
      int[][] unfilteredVoxData= ip.getIntArray(); // original voxel data
     
      
      // Check if user has stored a Roi to start using this slice onward
      String roiURL= _impURL+"."+String.valueOf(sliceN)+".roi";
      if (fileExists(roiURL)) ij.IJ.open(roiURL);
      // otherwise continue using previous Roi
      Roi userRoi = _imp.getRoi();
      Rectangle r= new Rectangle(ip.getWidth(), ip.getHeight()); // image limits
    
      // FILTERING
      // First filter image to eliminate extra-corporal outliers 
      // which might be mistaken as tissue (bedding, table, specks):
      if (PREFILTER) {
        RankFilters rf= new RankFilters();
        rf.rank(ip, OUTLIER_PREFILTER_RADIUS, RankFilters.OUTLIERS, 0, (float)50.0);  
        rf.rank(ip, MEAN_PREFILTER_RADIUS, RankFilters.MEAN);
      }


      // Threshold lung - result will include lung and air
      for (int x=r.x; x<(r.x+r.width); x++) {
        for (int y=r.y; y<(r.y+r.height); y++) {
          val= Double.valueOf(ip.getPixelValue(x,y));
          if (isLung(val)) ip.putPixel(x,y,AIRLUNG_MASK);
          else ip.putPixel(x,y,NONLUNG_MASK);
        }
      }
      // Flood Fill AIRLUNG_MASK only if within userROI
      ip.setValue(LUNG_MASK);
      FloodFiller floodFiller= new FloodFiller(ip);
      for (int x=r.x; x<(r.x+r.width); x++) {
        for (int y=r.y; y<(r.y+r.height); y++) {
          if (userRoi.contains(x,y)) {
            ival= ip.getPixel(x,y);
	    if (ival==AIRLUNG_MASK) floodFiller.fill(x,y);
          }
        }
      }

      // Restore Original Data ONLY where LUNG_MASKed
      for (int x=r.x; x<(r.x+r.width); x++) {  
        for (int y=r.y; y<(r.y+r.height); y++) {
          ival= ip.getPixel(x,y);
  	  if (ival!=LUNG_MASK) unfilteredVoxData[x][y]= NONLUNG_MASK;
        }
      }
      ip.setIntArray(unfilteredVoxData);
    } 

  }


  // "in-line" utilities
  public static boolean isLung(double HUval) { return inRange(HUval, MIN_HU_LUNG, MAX_HU_LUNG);} 
  public static boolean inRange(double val, double min, double max) { return (val>=min && val<=max);}
  private static void DEBUG(String msg, java.lang.Object x) {if (DEBUG) System.out.println(msg+String.valueOf(x));}

  private static Boolean fileExists(String url) {return (new java.io.File(url)).exists();} 


}
