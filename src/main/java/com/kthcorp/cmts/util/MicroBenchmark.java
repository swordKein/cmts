package com.kthcorp.cmts.util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

public class MicroBenchmark {
    @Benchmark
    @Fork(jvmArgsAppend = "-XX:+PrintGCDetails -Djava.io.tmpdir=e:/tmp -Djmh.ignoreLock=true")
    public void helloWorld() {

        int a = 1;
        int b = 2;
        int sum = a + b;
    }
}