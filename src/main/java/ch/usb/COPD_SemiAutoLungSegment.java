package ch.usb; /** ImageJ Plugin to Segment Lung Tissue from CT Image Data
*
*  Requires user interaction to chose a ROI containing at least
*  one voxel of left and right lung in all slices, but not 
*  containing any non-lung air voxels.
*  
*  @author drTJRE.com University Hospital of Basel
*  @date   dec2015
*/

import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import ij.io.FileInfo;

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
  public static boolean isTissue(double HUval) { return inRange(HUval, MIN_HU_TISSUE, MAX_HU_TISSUE);} 
  public static boolean inRange(double val, double min, double max) { return (val>=min && val<=max);}
  private static void DEBUG(String msg) {if (DEBUG) System.out.println(msg);}
  private static void DEBUG(String msg, java.lang.Object x) {if (DEBUG) System.out.println(msg+String.valueOf(x));}

  private static Boolean fileExists(String url) {return (new java.io.File(url)).exists();} 

  /** tester */
  public static void main(String[] args) {
      try {
        double v0, v2, v1;
        v0= Double.valueOf(args[0]);
        v1= Double.valueOf(args[1]);
        v2= Double.valueOf(args[2]);
        // test range 
        System.out.println("inRange="+String.valueOf(inRange(v0, v1,v2)));
        System.out.println("isLung="+String.valueOf(isLung(v0)));
        System.out.println("isTissue="+String.valueOf(isTissue(v0))); 
      }
      catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("usage: ch.usb.COPD_SemiAutoLungSegment  val1 val2 val3");
      }
      catch (Exception e) {
        System.out.println(e);
      }

  }

}
