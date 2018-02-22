package com.kthcorp.cmts.util;

import scala.collection.convert.Decorators;
import scala.collection.convert.WrapAsJava$;
import scala.collection.mutable.WrappedArray;

import java.util.List;

public class ScalaUtil {
    /*
    public static <T> java.util.List<T> convert(scala.collection.Seq<T> seq) {
        return scala.collection.JavaConverters.seqAsJavaList(seq);
    }
    public static <T> Decorators.AsJava<Iterable<String>> convert(WrappedArray<String> seq) {
        return scala.collection.JavaConverters.asJavaIterableConverter(seq);
    }

    public static <T> Decorators.AsJava<List> convert(WrappedArray<String> seq) {
        return scala.collection.JavaConverters.seqAsJavaList(seq);
    }
    */
    static java.util.List<String> convert(WrappedArray<String> seq) {
        return WrapAsJava$.MODULE$.seqAsJavaList(seq);
    }
}
