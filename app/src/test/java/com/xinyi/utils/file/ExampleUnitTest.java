package com.xinyi.utils.file;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import com.xinyi.timenexus.DateTimeNexus;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testWeek() {
        System.out.println("getWeekDayName = " + DateTimeNexus.getWeekDayName());
        System.out.println("getWeekDayShortName = " + DateTimeNexus.getWeekDayShortName());
    }
}