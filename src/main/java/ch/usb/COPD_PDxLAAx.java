package ch.usb;

/**
 *
 *  ImageJ Plugin to calculate:
 *  PercentileDensity (PD) at multiple percentiles
 *  and LowAttenuationArea (LAA) at multiple lower density cut-offs.
 *  from pre-segmented CT Lung images.
 *
 *  WARNING: Pre-process images with COPD_LungSegment or equivalent (see below)
 *
 *  Input:
 *    pre-segmented ImagePlus such that only voxels within lung density range (<380HU)
 *    are considered as lung tissue for example using COPD_LungSegment.
 *
 *    Works only on 16bit Multi-slice ImagePlus images
 *    Images must contain Calibration of CT density in Hounsfield Units (HU)
 *
 *  Output:
 *    Results table with calculated values.
 *    Actual percentiles and lower attenuation limits are set internally.
 *    Alernatively, methods getPD and getLAA can be called directly.
 *
 *  Medical References:
 *    http://www.ncbi.nlm.nih.gov/pubmed/9196813
 *    http://www.ncbi.nlm.nih.gov/pubmed/19196813
 *
 *  @author thomas.re@usb.ch  - University Hospital of Basel, Switzerland
 *  @date    jan2016
 *
 */

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.Info;
import ij.plugin.filter.PlugInFilter;
import ij.measure.ResultsTable;
import ij.measure.Calibration;
import ij.process.ImageProcessor;

public class COPD_PDxLAAx implements PlugInFilter {

    // lung density limits - according to medical literature
  static final int MAX_HU_LUNG= -380;
  static final int MIN_HU_LUNG= -1500;

    static final boolean headless= false;

  static final int RESULTS_PRECISION= 3; // decimal places to display in ResultsTable

    /**
     *  image being processed
     */
  ImagePlus _imp;

    /**
     *  processor to image being processed
     */
  ImageProcessor _ip;


    /**
     * container of voxel data with statistical functionality
     */
    CTVoxelBox _lungDataBox;

    protected long _fovVoxelCount; // total voxels in image
    public long getFOVVoxelCount() {return _fovVoxelCount;}

    /**
     * Low Attenuation limits to calculate
     */
  protected int[] _laas= {-950, -900, -850};
    /**
     * Percentile Density percentiles to calculate (may be multiple)
     */
  protected int[] _pds= {5,15,25}; // percentile densities to calculate

    /**
     * real world voxel volume as determined from image header
     */
  protected double _voxelVolume;

    /**
     * real world volume units as determined from image header
     */
  protected String _volumeUnits;

    /**
     * initialize member values based on image header
     */
  protected void init() {
    // prepare output labels with volume units 
    Calibration cal= _imp.getCalibration();
    _voxelVolume= cal.pixelWidth*cal.pixelHeight*cal.pixelDepth;
    _volumeUnits= cal.getUnit()+"^3";
    _lungDataBox= new CTVoxelBox(_imp.getWidth()*_imp.getHeight()*_imp.getStack().getSize());
  }


    // TODO: 26/01/16 replace this with a DICOM header utility class
    /**
     * Determine gender of subject from DICOM header if available.
     * Useful for some normalization calculations.
     *
     * @return gender of subject [M|F] or NA if not found.
     */
  private String getSexFromDicomHeader() {
    final String DCM_SEX_TAG= "0010,0040"; // standard DICOM identifier
    final int MF_SUBSTR_POS_START = 26;
    final int MF_SUBSTR_POS_END = 27;
    Info infoObj= new Info();
    String dcmHeader= infoObj.getImageInfo(_imp,_ip);
    int ind= dcmHeader.indexOf(DCM_SEX_TAG);
    String gender="NA";
    if (ind>=0) gender= dcmHeader.substring(ind+MF_SUBSTR_POS_START, ind+MF_SUBSTR_POS_END);
    return gender;
  }

    /**
     * utility to check if a specific Hounsfeld Unit is within lung density range.
     *
     * @param HUval Hounsfeld Unit value to check
     * @return true if within lung density range
     */
    public static boolean isLung(double HUval) { return (HUval>=MIN_HU_LUNG && HUval<=MAX_HU_LUNG);}

    // TODO: 26/01/16 parse arguments to set percentiles and lower attenuation limits
    /** initialize instance
     *
     * @param arg should contain settings/options info
     * @param imp image to analyze
     * @return check that image is 16bit-multislice
     */
    public int setup(String arg, ImagePlus imp) {
      this._imp = imp;
      init();
      return DOES_16+STACK_REQUIRED;
    }

    /**
     * Calculate PD/LAA and add results to current ResultsTable
     * @param ip ImageProcessor of image being analyzed.
     */
  public void run(ImageProcessor ip) {
    this._ip= ip;
    double val;
    ImageStack istack= _imp.getStack();
    int width= _imp.getWidth();
    int height= _imp.getHeight();
    int numOfSlices= istack.getSize();
    // Iterate all slices all voxels and fill CTVoxelBox
    for (int sliceN=1;sliceN<=numOfSlices;sliceN++) {
      ImageProcessor ips= istack.getProcessor(sliceN); 
      for (int x=0; x<(width); x++) {
        for (int y=0; y<(height); y++) {
          val= ips.getPixelValue(x,y);
            _fovVoxelCount++;
	      if (isLung(val)) _lungDataBox.add((int)Math.round(val));
        }
      }
    }
    showResults();
  }

    /**
     * Add PD and LAA values to Results Table
     */
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
    int lungVoxels= _lungDataBox.getCount();
    int lungVolume= (int)Math.round(lungVoxels*_voxelVolume);
    String studyTag= _imp.getShortTitle();
    rt.addValue("StudyID", studyTag);
    // rt.addValue("FOV voxels ", getFOVVoxelCount());
    rt.addValue("sex", getSexFromDicomHeader());  // used in normilization
    // rt.addValue("vox volume "+_volumeUnits, _voxelVolume);
    // rt.addValue("LungVol voxs", lungVoxels);
    // rt.addValue("LungVol "+_volumeUnits, lungVolume);
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

    if (!headless) rt.show("COPD Quantitative Results");
  }

    /**
     * determine Percentile Density at pecific percentile
     * (density (HU) for which this percentile of voxels fall below).
     *
     * @param percentile value to test (most common in med literature is 15)
     * @return
     */
    public int getPD(int percentile)  {
      return _lungDataBox.getPD(percentile);
    }

    /**
     * Calculate Low Attenuation Area =
     * number of voxels (or 'area') below a specific attenuation (ie density)
     *
     * @param density value to check in Hensfield Units
     * @return
     */
    public int getLAA(int density){
        return _lungDataBox.getLAA(density);
    }

}
