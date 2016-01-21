package ch.usb; /** ch.usb.CTVoxelBox
*   
*   Utilities for a group of Voxel values (without coordinates)
*   Specific for USB CT data which is only in voxels
*
*   @auth drTJRE.com
*   @date jan2015
*/

import java.util.Arrays;

public class CTVoxelBox {
   private static final double EXPANSION_FACTOR= 2;
   private static final boolean DEBUG= true;

   int[] _data;
   int _dataCount=0;
   boolean _unsorted= false;

   /** Construct with initial array size */
   public CTVoxelBox(int startSize) {
     _data= new int[startSize];
   }

   /** returns total number of stored voxel values */
   public int getSize() {return _dataCount;}

   /** add a value to data set for later sorting */
   public void add(int value) {
     if (_dataCount>=_data.length) {
       int[] temp= Arrays.copyOf(_data, (int)(_data.length*EXPANSION_FACTOR));
       _data= temp;
       if (DEBUG) System.out.println(Arrays.toString(_data));
     }
     _data[_dataCount++]= value;
     _unsorted= true;
   }

   /** returns value corresponding to the requested percentile
   *   For example if percentile=15
   *     returns the value with at least 14.9% of other values lower or equal
   *     in value.
   */
   public int getPD(int percentile) {
     sort();
     int rank= (int)Math.ceil(_dataCount*percentile/100);
     return (rank<_dataCount?_data[rank]:_data[_dataCount-1]);
   }

   /** returns number of voxels in set which have a value below @arg value */
   public int getVoxCountBelow(int value) {
     sort();
     int i=0, count=0;
     while (_data[i++]<value) count++;
     return count;
   }

   /** returns Low Attenuation Area 
    *  or number of voxels in set which have a value below or equal
    *  @arg value 
    */
   public int getLAA(int val) {
     return getVoxCountBelow(val+1);
   }


   /** Debug utility to output internals of object */
   public String toString() {
     StringBuffer buffy= new StringBuffer("ch.usb.CTVoxelBox:");
     buffy.append("{");
     buffy.append("5%="+String.valueOf(getPD(5)+","));
     buffy.append("10%="+String.valueOf(getPD(10)+","));
     buffy.append("15%="+String.valueOf(getPD(15)+","));
     buffy.append("20%="+String.valueOf(getPD(20)));
     buffy.append("}");
     return buffy.toString();
   }

   /** Debug utility to output internals of object */
   public String dump() {
     StringBuffer buffy= new StringBuffer("ch.usb.CTVoxelBox:");
     sort();
     buffy.append(Arrays.toString(_data));
     return buffy.toString();
   }

   /** sort all voxel values in set */
   private void sort() {
     if (_unsorted) {
       Arrays.sort(_data, 0, _dataCount);
       _unsorted= false;
     }
   }

   public static void main(String[] args) {
     try {
       CTVoxelBox pd= new CTVoxelBox(1);
       for (int i=0;i<args.length;i++) 
         pd.add(Integer.parseInt(args[i]));
       System.out.println(pd);
       System.out.println(pd.getSize());
       System.out.println(pd.dump());
     }
     catch (Exception e) {
       System.out.println("usage: ch.usb.CTVoxelBox val1 val2 val3 ...");
     }

   }


}
