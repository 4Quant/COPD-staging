/** ImageJ Plugin
*   Percentile Denisty PD 
*   for multiple Percentile Values.
*
*   Requires segmented lung stack as input
*
*  @author drTJRE.com University Hospital of Basel
*  @date   dec2015
*/

import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import ij.measure.ResultsTable;
import ij.measure.Calibration;

public class USB_PDx implements PlugInFilter {
  // Constants
  static final String VERSION= "USB_PDx version -380";
  static final int MAX_LUNG_HU= -380;
  static final int MIN_LUNG_HU= -1500;
  static final int DEBUG_MASK= 4096;
  static final boolean DEBUG= true;
  static final boolean VERBOSE= true;
  static String MEASUREMENT_LABEL= "LAA";

  ImagePlus _imp;
  ImageProcessor _ip;

  USBVoxBox _lungDataBox;


  // permit multiple LAA and PD cutoffs 
  int[] _laas= {-1024, -950, -900};
  int[] _pds= {15}; // percentile densities to calculate

  double _voxelVolume; 
  String _volumeUnits;

  /** initialize member values as needed. */
  protected void init() {  
    // prepare output labels with volume units 
    Calibration cal= _imp.getCalibration();
    _voxelVolume= cal.pixelWidth*cal.pixelHeight*cal.pixelDepth;
    _volumeUnits= cal.getUnit()+"^3";
    _lungDataBox= new USBVoxBox(_imp.getWidth()*_imp.getHeight()*_imp.getStack().getSize());
  }

  private boolean isLung(double val) {return val>=MIN_LUNG_HU && val<=MAX_LUNG_HU;}

  public int setup(String arg, ImagePlus imp) {
    this._imp = imp;
    if (VERBOSE) System.out.println(VERSION);
    init();
    return DOES_16+STACK_REQUIRED;
  }

  public void run(ImageProcessor ip0) {
    this._ip= ip0;
    double val;
    ImageStack istack= _imp.getStack();
    Rectangle rec= new Rectangle(_imp.getWidth(), _imp.getHeight()); 
    int numOfSlices= istack.getSize();
    // fill histogram
    for (int sliceN=1;sliceN<=numOfSlices;sliceN++) {
      ImageProcessor ips= istack.getProcessor(sliceN); 
      for (int x=rec.x; x<(rec.x+rec.width); x++) {  
        for (int y=rec.y; y<(rec.y+rec.height); y++) {
          val= Double.valueOf(ips.getPixelValue(x,y)); 
	  if (isLung(val)) {
	      if (DEBUG) ips.putPixel(x,y,DEBUG_MASK);
	      _lungDataBox.add((int)Math.round(val));
	  }
        }
      }
    } // next slice in line - step down:)
    // output results - for now to stdio - soon to table
    showResults();
  }


  static final int RESULTS_PRECISION= 3; // number of decimal places to display

  /** Fill PD and LAAX value in Results table */
  private void showResults() {
    ResultsTable rt= Analyzer.getResultsTable();
    String tag;
    int voxs;
    rt.setPrecision(RESULTS_PRECISION);
    if (rt==null) {
      rt= new ResultsTable();
      Analyzer.setResultsTable(rt);
    }
    rt.incrementCounter();
    // Values specific to this CT
    int lungVoxels= _lungDataBox.getSize();
    int lungVolume= (int)Math.round(lungVoxels*_voxelVolume);
    String studyTag= _imp.getShortTitle();
    rt.addValue("StudyID", studyTag);
    rt.addValue("sex", USButil.getSex(_imp, _ip));
    rt.addValue("vox volume "+_volumeUnits, _voxelVolume);
    rt.addValue("LungVol voxs", lungVoxels);
    rt.addValue("LungVol "+_volumeUnits, lungVolume);
    if (_volumeUnits.equals("mm^3")) 
      rt.addValue("LungVol (Liters)", Math.round(lungVolume/1000.0)/1000.0);

    // PDs
    for (int i=0;i<_pds.length;i++) {
      int pd= _pds[i];
      tag= "PD"+String.valueOf(pd);
      voxs= _lungDataBox.getPD(pd);
      rt.addValue(tag, voxs);
    }
    // LAAs
    for (int i=0;i<_laas.length;i++) {
      double vol, perc;
      int laa= _laas[i];
      voxs= _lungDataBox.getLAA(laa);
      vol= voxs*_voxelVolume;
      perc= Math.round(voxs*1000.0/lungVoxels)/10.0;
      tag= "LAA"+String.valueOf(laa)+_volumeUnits;
      rt.addValue(tag, vol);
      tag= "LAA"+String.valueOf(laa)+"%";
      rt.addValue(tag, perc);
    }

    rt.show("PD Results");
  }



  // "in-line" utilities
  private static void DEBUG(String msg) {if (VERBOSE) System.out.println(msg);}
  private static void DEBUG(String msg, java.lang.Object x) {if (VERBOSE) System.out.println(msg+"="+String.valueOf(x));}
  private static void show(String msg, java.lang.Object x) {IJ.showMessage("USB", msg+"="+String.valueOf(x));}
  private static void show(String msg) {IJ.showMessage("USB",msg);}

  /** tester */
  public static void main(String[] args) {
      try {
      }
      catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("usage: USB_LAAX  ");
      }
      catch (Exception e) {
        System.out.println(e);
      }

  }

}
