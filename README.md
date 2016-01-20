# USB RIQAE
## Building

To produce a jar file, you need to have maven installed on your machine and you simply run 
```
mvn package
```

which will produce a number of files in the ```target/``` directory. The ```dwi-dkfz-ij-0.1-SNAPSHOT.jar``` can be used as an ImageJ/FIJI plugin.

## Overview

This Project is for quantitative study of COPD (Chronic-Obstructive-Pulmonary-Disease).
The current version is limited to imaging-based quantitative values derived from x-ray CT (Computer Tomography) images
with the lungs completely contained within the volume of study.  Images will be DICOMs with proper calibration to
Hensfield Units (HU)  (a standard of density to x-ray measurements).

Images obtained from all sources, are first Anonymized (using an open source) ImageJ plugin (Anonymize_IJ_DICOM),
and packed into a single .tif file per CT study  ("study" radiology logo for= 1 patient/1 date/1 sitting).

Critical to all quantification values is to first Segment the lungs from the images.
The USB_LungSegment is the workhorse of this segmentation.  It is an ImageJ PluginFilter which segment's the lungs from
a multi-slice CT .tif file.  All voxels determined to contain lung parenchyma are left unchanged by the plugin, but
 voxels considered non-lung are masked to a value well out of CT tissue range (>1000 of bone) so that such non-lung
  items are not included in quantitative algorithms (discussed below).

USB_SemiAutoLungSegment is a utility ImageJ PluginFilter for performing lung segmentation with the help of
a human observer.  It is intended as support for testing USB_LungSegment results.

USB_PDx is a quantification ImageJ PluginFilter which requires previously lung-segmented .tif files as input and
produces a list of various standard lung quantities.  Most importantly the Percentile Density at programmable
percentiles and the Low Attenuation Area (LAA) also at programmable thresholds.  Currently, it is
programmed to produce PD%15, and LAA-950, which are of the most quoted in the medical literature.
Results are added to an ImageJ ResultsTable.

USButil and USBVoxBox are support classes...


