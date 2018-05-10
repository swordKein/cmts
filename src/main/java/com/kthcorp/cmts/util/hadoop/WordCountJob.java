package com.kthcorp.cmts.util.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountJob {

    public static void main(String[] args){
        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "WordCountJob");
            job.setJarByClass(WordCountJob.class);
            job.setMapperClass(WordCountMapper.class);
            job.setCombinerClass(WordCountReducer.class);
            job.setReducerClass(WordCountReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            //FileInputFormat.addInputPath(job, new Path("e:/tmp/hadoop-wodus77/in"));
            FileInputFormat.addInputPath(job, new Path("tmp/hadoop/in"));
            int i = (int) (Math.random() * 10) + 1;
            //FileOutputFormat.setOutputPath(job, new Path("e:/tmp/hadoop-wodus77/out"+i));
            FileOutputFormat.setOutputPath(job, new Path("/tmp/hadoop/out"+i));
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}