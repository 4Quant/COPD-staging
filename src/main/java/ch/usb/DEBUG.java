package ch.usb;

/** Simple Debug Logger
*
*  @author drTJRE.com
*  @date  dec2015
*/

public class DEBUG {


  static final boolean VERBOSE= true;

  public static void log(String msg) {
    if (VERBOSE) System.out.println(msg);
  }

  public static void log(String msg, Object obj) {
    log(msg+"="+obj.toString());
  }

}
