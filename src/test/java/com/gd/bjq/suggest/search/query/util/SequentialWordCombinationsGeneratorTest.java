package com.gd.bjq.suggest.search.query.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SequentialWordCombinationsGeneratorTest {
    @Test
    public void testGenerateWith1Word(){
        List<String> initialList = Arrays.asList("a");
        List<List<String>> expectedList = new ArrayList<>(Arrays.asList(new ArrayList<>(initialList)));
        List<List<String>> combinations = new SequentialWordCombinationsGenerator(initialList).generate();
        assertEquals(expectedList, combinations);
    }

    @Test
    public void testGenerateWith2Words(){
        List<String> initialList = Arrays.asList("a","b");
        List<List<String>> expectedList = new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList("a","b")),
                        new ArrayList<>(Arrays.asList("a b"))
        ));
        List<List<String>> combinations = new SequentialWordCombinationsGenerator(initialList).generate();
        assertEquals(expectedList, combinations);
    }

    @Test
    public void testGenerateWith3Words(){
        List<String> initialList = Arrays.asList("a","b","c");
        List<List<String>> expectedList = new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList("a","b","c")),
                        new ArrayList<>(Arrays.asList("a","b c")),
                        new ArrayList<>(Arrays.asList("a b","c")),
                        new ArrayList<>(Arrays.asList("a b c"))
        ));

        List<List<String>> combinations = new SequentialWordCombinationsGenerator(initialList).generate();
        assertEquals(expectedList, combinations);
    }
}
