package ch.usb;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;

import java.io.IOException;

/**
 * The class is a simple wrapper for the functionality and called by the web demo
 * @author Kevin Mader (kevin.mader@4quant.com)
 * Created by mader on 1/19/16.
 */
public class LungWebDemo {
    public static void main(String[] args) {
        String mriImage = args[0];
        String outImage = args[1];
        String segArgs = args[2];
        String outTable = args[3];
        String pdxArgs = args[4];

        System.out.println("Open: "+mriImage);
        IJ.open(mriImage);
        USB_LungSegment tjPlug = new USB_LungSegment();
        ImagePlus cImage = IJ.getImage();
        tjPlug.setup(segArgs,cImage);
        tjPlug.run(cImage.getProcessor());
        IJ.save(cImage,outImage);
        USB_PDx pdxPlug = new USB_PDx();
        pdxPlug.setup("",cImage);
        pdxPlug.run(cImage.getProcessor());
        ResultsTable rt = Analyzer.getResultsTable();
        try {
            rt.saveAs(outTable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Results Table\n");
        String prefix = "rtout, ";

        for(int i=0;i<ResultsTable.MAX_COLUMNS;i++) {
            if(!rt.columnExists(i)) break;
            String tempOut = rt.getColumnHeading(i);
            for(double d : rt.getColumnAsDoubles(i)) {
                tempOut += ", "+d;

            }
            System.out.println(prefix+tempOut);
        }



    }
}
