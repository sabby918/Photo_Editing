package com.example.sarah.editpicture;

import android.widget.SeekBar;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
}
// Unit test
    @Test
    public void RBG_Values() throws Exception {
        int barR=1;
        int  barG=1;
        int  barB=1;
        int  barAlpha=1;
        int ColorValues;

        ColorValues = barAlpha+barB+barG+barR;
        assertEquals(ColorValues, 4);


    }
}