package ch.usb; /** ch.usb.USButil
*   Utility Class provides Static Utilities 
*   for ImageJ specific to USB
*
*   @author drTJRE.com
*   @date   jan2016
*/

import ij.plugin.filter.Info;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class USButil {


   /** Extract gender information from image header */
   public static String getSex(ImagePlus imp, ImageProcessor ip) {
     final String DCM_SEX_TAG= "0010,0040"; // standard DICOM identifier
     final int MF_SUBSTR_POS_START = 26; 
     final int MF_SUBSTR_POS_END = 27;
     Info infoObj= new Info();
     String dcmHeader= infoObj.getImageInfo(imp,ip);
     int ind= dcmHeader.indexOf(DCM_SEX_TAG);
     String gender="NA";
     if (ind>=0) gender= dcmHeader.substring(ind+MF_SUBSTR_POS_START, ind+MF_SUBSTR_POS_END);
     return gender;
   }


}
