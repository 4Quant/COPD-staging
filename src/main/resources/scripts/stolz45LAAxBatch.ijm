/** Project STOLZ45
*   Batch load all pre-segmented lung cases
*   and calculate PDx and LAAx 
*   output is in a resultsTable
*
*   @author drTJRE.com
*   @date 23DEC2015
*/

open("/STOLZ45/S052_USB0002069243_20110419.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S081_USB0003075530_20110327.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S102_USB0003243676_20140901.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S109_USB0003152149_20140621.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S110_USB0003239810_20150716.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S122_USB0002394795_20130729.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S123_USB0002176164_20140201.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S135_USB0003209931_20110309.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S139_USB0002255402_20100727.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S142_USB0003138461_20150529.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S154_USB0002100910_20120215.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S156_USB0002214097_20110418.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S164_USB0002054182_20150630.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S175_USB0003212738_20140804.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S182_USB0003310251_20140106.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S188_USB0003035432_20120823.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S203_USB0003380046_20130115.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S213_USB0002127280_20131211.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S215_USB0002342759_20130507.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S218_USB0002052933_20130308.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S224_USB0002349377_20140930.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S240_USB0002092611_20120318.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S242_USB0003141274_20141218.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S243_USB0002094535_20111219.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S258_USB0002392897_20120619.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S289_USB0002369017_20150106.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S296_USB0002062908_20130902.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S305_USB0003392287_20151130.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S306_USB0003034284_20150806.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S310_USB0002339130_20150611.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S318_USB0002381203_20150322.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S322_USB0003167953_20110919.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S325_USB0003459065_20140212.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S328_USB0002240466_20140508.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S338_USB0002158853_20151016.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S340_USB0003016250_20150316.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S341_USB0002067497_20150107.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S344_USB0002224788_20121014.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S347_USB0003314052_20150218.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S354_USB0003236645_20131007.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S357_USB0003110656_20131127.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S370_USB0002197577_20140113.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/S373_USB0003463993_20150723.lung.tif"); run("USB PDx"); run("Close All");
open("/STOLZ45/SXXX_USB0002345103_20120702.lung.tif"); run("USB PDx"); run("Close All");
saveAs("Results", "/STOLZ45/stolz45PDLAAxResultsEdgy.xls");

open("/STOLZ45/S052_USB0002069243_20110419.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S081_USB0003075530_20110327.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S102_USB0003243676_20140901.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S109_USB0003152149_20140621.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S110_USB0003239810_20150716.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S122_USB0002394795_20130729.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S123_USB0002176164_20140201.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S135_USB0003209931_20110309.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S139_USB0002255402_20100727.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S142_USB0003138461_20150529.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S154_USB0002100910_20120215.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S156_USB0002214097_20110418.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S164_USB0002054182_20150630.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S175_USB0003212738_20140804.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S182_USB0003310251_20140106.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S188_USB0003035432_20120823.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S203_USB0003380046_20130115.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S213_USB0002127280_20131211.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S215_USB0002342759_20130507.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S218_USB0002052933_20130308.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S224_USB0002349377_20140930.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S240_USB0002092611_20120318.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S242_USB0003141274_20141218.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S243_USB0002094535_20111219.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S258_USB0002392897_20120619.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S289_USB0002369017_20150106.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S296_USB0002062908_20130902.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S305_USB0003392287_20151130.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S306_USB0003034284_20150806.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S310_USB0002339130_20150611.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S318_USB0002381203_20150322.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S322_USB0003167953_20110919.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S325_USB0003459065_20140212.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S328_USB0002240466_20140508.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S338_USB0002158853_20151016.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S340_USB0003016250_20150316.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S341_USB0002067497_20150107.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S344_USB0002224788_20121014.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S347_USB0003314052_20150218.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S354_USB0003236645_20131007.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S357_USB0003110656_20131127.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S370_USB0002197577_20140113.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/S373_USB0003463993_20150723.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");
open("/STOLZ45/SXXX_USB0002345103_20120702.lung.tif"); run("Smooth","stack");run("USB PDx"); run("Close All");

saveAs("Results", "/STOLZ45/stolz45PDLAAxResultsSmoothed.xls");
