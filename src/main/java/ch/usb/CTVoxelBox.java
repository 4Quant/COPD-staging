package ch.usb;

/** ch.usb.CTVoxelBox
*
 *   Utility to collect a list of CT Hounsfield Unit (HU) values
 *   and then calculate COPD specific quantities based on these values.
 *
 *   Specifically:
 *     PD - Percentile Density (in HU) at any percentile (ie. PD15, PD20,...)
 *     LAA - Low Attenuation Area at any Hounsfield Unit cut-off
 *
 *  @author thomas.re@usb.ch  - University Hospital of Basel, Switzerland
 *  @date    jan2016
 */

import java.util.Arrays;

public class CTVoxelBox {
   private static final double EXPANSION_FACTOR= 2;
   private static final boolean DEBUG= true;

   int[] _data;
   int _dataCount=0;
   boolean _unsorted= false;

    /**
     * Constructor
     *
     * @param startSize estimate of data size
     */
   public CTVoxelBox(int startSize) {
       _data= new int[startSize];
   }


    /**
     * @return  number of values currently stored in collection
     * */
   public int getCount() {return _dataCount;}

    /**
     * Add value to collection
     * @param value value in HU to add
     */
   public void add(int value) {
     if (_dataCount>=_data.length) {
       int[] temp= Arrays.copyOf(_data, (int)(_data.length*EXPANSION_FACTOR));
       _data= temp;
       if (DEBUG) System.out.println(Arrays.toString(_data));
     }
     _data[_dataCount++]= value;
     _unsorted= true;
   }


    /**
     * Calculates value, in HU's, corresponding to the requested percentile
     *
     * @param percentile percentile of value to seek
     * @return value in HU at requested percentile
     */
   public int getPD(int percentile) {
       int value;
       if (_dataCount>0) {
           sort();
           int rank = (int) Math.ceil(_dataCount * percentile / 100);
           value= (rank < _dataCount ? _data[rank] : _data[_dataCount - 1]);
       }
       else {
           value= 0;  // impossible value for lung - must be no lung found
       }
       return value;
   }

    /**
     * Calculates number of voxels in set which have a value below HUvalue
     *
     * @param HUvalue Houndsfield Unit value maximum cut-off
     * @return
     */
   public int getVoxCountBelow(int HUvalue) {
     sort();
     int i=0, count=0;
     while (i<_dataCount && _data[i++]<HUvalue) count++;
     return count;
   }

   /**
    *  Calculates Low Attenuation Area
    *  or number of voxels in set which have a value below or equal to
    *  Note difference with getVoxCountBelow is that it includes the
    *  value (not just those below it)
    *
    *  @arg val
    */
   public int getLAA(int HUvalue) {
     return getVoxCountBelow(HUvalue+1);
   }


    /**
     * Sort all voxel values in set
     */
   private void sort() {
     if (_unsorted) {
       Arrays.sort(_data, 0, _dataCount);
       _unsorted= false;
     }
   }



}
