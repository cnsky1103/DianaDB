package com.cnsky1103.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PairTest {
    @Test
    public void testEquals() {
        Pair<String, Integer> p1 = new Pair<String, Integer>("abc", 1);
        Pair<String, Integer> p2 = new Pair<String, Integer>("abc", 1);
        assertTrue(p1.equals(p2));
    }
}
