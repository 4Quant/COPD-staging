package ch.usb;


import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests of the CTVoxelBox class
 *
 * Created by tomjre on 26/01/16.
 */
public class TestCTVoxelBox {

    public void setup() {

    }

    @Test
    public void testPD() {
        final int length= 100;
        CTVoxelBox vbox= new CTVoxelBox(length);
        for (int i=0;i<length;i++) vbox.add(i);

        assertEquals("Count of items in box not match expected", length, vbox.getCount());
        assertEquals("PD15 not as expected", 15, vbox.getPD(15));
        assertEquals("PD20 not as expected", 20, vbox.getPD(20));
        assertEquals("PD25 not as expected", 25, vbox.getPD(25));
        assertEquals("PD30 not as expected", 30, vbox.getPD(30));
        assertEquals("PD40 not as expected", 40, vbox.getPD(40));

    }

    @Test
    public void testLAA() {
        final int[] values= {-1024, 0, 500, -1000, 7, -899, -898, 10, 33, 1, 1000, -802};
        CTVoxelBox vbox= new CTVoxelBox(values.length);
        for (int i=0;i<values.length;i++) vbox.add(values[i]);

        assertEquals("LAA-1024 not as expected",  1, vbox.getLAA(-1024));
        assertEquals("LAA-950 not as expected", 2, vbox.getLAA(-950));
        assertEquals("LAA-900 not as expected", 2, vbox.getLAA(-900));
        assertEquals("LAA-850 not as expected", 4, vbox.getLAA(-850));
        assertEquals("LAA-800 not as expected", 5, vbox.getLAA(-800));
        assertEquals("LAA+800 not as expected", values.length, vbox.getLAA(1024));

    }
}
