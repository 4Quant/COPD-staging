package ch.usb;

import ij.IJ;
import ij.ImagePlus;

/**
 * The class directly called by the web demo
 * Created by mader on 1/19/16.
 */
public class LungWebDemo {
    public static void main(String[] args) {
        IJ.open(args[1]);
        USB_LungSegment tjPlug = new USB_LungSegment();
        ImagePlus cImage = IJ.getImage();
        tjPlug.setup(args[3],cImage);
        tjPlug.run(cImage.getProcessor());
        IJ.save(cImage,args[2]);
    }
}
