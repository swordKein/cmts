package com.kthcorp.cmts.util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)

public class WordCountUtilExam {

    @Param({"toConcurrentMap", "toMap", "groupingBy", "groupingByConcurrent"})
    public String method;

    @Param({"2", "5", "10"})
    public int wordLength;

    @Param({"1000", "10000" })
    public int count;

    private List<String> list;

    @Setup
    public void initList()
    {
        list = createRandomStrings(count, wordLength, new Random(0));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testMethod(Blackhole bh)
    {

        if (method.equals("toMap"))
        {
            Map<String, Integer> counts =
                    list.stream().collect(
                            Collectors.toMap(
                                    w -> w, w -> 1, Integer::sum));
            bh.consume(counts);
        }
        else if (method.equals("toConcurrentMap"))
        {
            Map<String, Integer> counts =
                    list.parallelStream().collect(
                            Collectors.toConcurrentMap(
                                    w -> w, w -> 1, Integer::sum));
            bh.consume(counts);
        }
        else if (method.equals("groupingBy"))
        {
            Map<String, Long> counts =
                    list.stream().collect(
                            Collectors.groupingBy(
                                    Function.identity(), Collectors.<String>counting()));
            bh.consume(counts);
        }
        else if (method.equals("groupingByConcurrent"))
        {
            Map<String, Long> counts =
                    list.parallelStream().collect(
                            Collectors.groupingByConcurrent(
                                    Function.identity(), Collectors.<String> counting()));
            bh.consume(counts);
        }
    }

    private static String createRandomString(int length, Random random)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int c = random.nextInt(26);
            sb.append((char) (c + 'a'));
        }
        return sb.toString();
    }

    private static List<String> createRandomStrings(
            int count, int length, Random random)
    {
        List<String> list = new ArrayList<String>(count);
        for (int i = 0; i < count; i++)
        {
            list.add(createRandomString(length, random));
        }
        return list;
    }
}