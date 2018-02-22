package com.kthcorp.cmts.util.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by ardilgulez on 27.11.2016.
 * What’s a better place to plug your name than class javadoc
 */
public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context){
        try {
            int resultNumber = 0;
            for(IntWritable value : values){
                resultNumber += value.get();
            }
            IntWritable result = new IntWritable();
            result.set(resultNumber);
            context.write(key, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}