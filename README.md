# USB RIQAE
## Building

To produce a jar file, you need to have maven installed on your machine and you simply run 
```
mvn package
```

which will produce a number of files in the ```target/``` directory. The ```-SNAPSHOT.jar```
can be used as an ImageJ/FIJI plugin.

## Overview

Chronic-Obstructive-Pulmonary-Disease (COPD) is a chronic disorder which effects millions
of individuals world wide.  It is typically, although not exclusively, a disease of older adults and is primarily
attributed to chronic exposure to lung damaging substances contained in cigarette smoke and air pollution.
One technique for measuring the extent of lung damage in COPD is via quantitative values calculated from Computer
Tomography (CT) imaging of patients.
Two particularly established quantities are: PD15 and LAA-950 (see paper for details).[Dirksen et al., Exploring the
role of CT densitometry: a randomised study of augmentation therapy Eur Respir J 2009; 33: 1345–1353 ]
[Chapman et al.,
Intravenous augmentation treatment and lung density in severe α1 antitrypsin deficiency (RAPID): a randomised, double-
blind, placebo-controlled trial.  Lancet 2015; 386: 360–68]
[Dirksen A, Piitulainen E, Parr DG, Deng C, Wencker M, Shaker SB, Stockley RA.
Exploring the role of CT densitometry: a randomised study of augmentation therapy in alpha1-antitrypsin deficiency.
Supplementary Online Material.
Eur Respir J. 2009 Jun;33(6):1345-53. doi: 10.1183/09031936.00159408. Epub 2009 Feb 5.
PMID: 19196813]


This Project contains some simple ImageJ based tools for the quantitative study of COPD and was developed at the
Department of Radiology of University Hospital of Basel, Switzerland. The process in using this requires that CT data be
exported from a radiology PACS in standard DICOM format.  These files can then be anonymized and stored as .tif files
for convenience. The following requirements must be followed:

* Images contain a proper header for specifying spacial and density calibration, and such density calibration is in
Hounsfield Units (HU).
For convenience, DICOM image sequences can be stored in .tif file using ImageJ which maintains the necessary calibration
header in doing so.
* CT Volume must include complete thorax including complete lungs and at least one cm of surrounding tissues.

There are currently two ImageJ plugins included with this project:

* COPD_LungSegment
* COPD_PDxLAAx

The first (COPD_LungSegment) segments the lungs from surrounding tissues. Output is the original image with all non-lung
voxels set to an out-of-range value beyond bone (HU2048).
The second (COPD_PDxLAAx) performs the calculation on images which have been segmented.
The two plugins may be combined in future versions, but the advantage of maintaining these as separate is that it allows
better error controls of the intermediate lung segmented process which is still in development.

Current version of COPD_LungSegment does not exclude the upper airways (trachea and bronchi) from the lung tissue.
One limitation is that it also includes pockets of air in the digestive system as lung tissue.  Air-filled cushioning
devices surrounding the patient may also be misinterpreted as lung.  These limitations are  being actively studied and
should be resolved in subsequent versions.

COPD_PDxLAAx produces a list (in a ResultsTable) of various standard lung quantities.  Specifically, the Percentile
Density at programmable percentiles and the Low Attenuation Area (LAA) also at programmable thresholds.  Currently, it
is programmed to produce PD%15, LAA-850, LAA-900 and LAA-950, which are of the most quoted in the medical literature.

COPD_SemiAutoLungSegment is an outdated utility for performing lung segmentation with minimal user interaction.  It is
left in this project for comparison purposes.


