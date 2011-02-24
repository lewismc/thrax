package edu.jhu.thrax.hadoop.features;

import edu.jhu.thrax.hadoop.datatypes.RuleWritable;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.util.Map;

public class GlueRuleFeature extends SimpleFeature
{
    private static final Text LABEL = new Text("GlueRule");
    private static final IntWritable ZERO = new IntWritable(0);
    private static final IntWritable ONE = new IntWritable(1);

    public void score(RuleWritable r, Map<Text,Writable> map)
    {
        map.put(LABEL, ZERO);
    }

    public void unaryGlueRuleScore(Text nt, Map<Text,Writable> map)
    {
        map.put(LABEL, ONE);
    }

    public void binaryGlueRuleScore(Text nt, Map<Text,Writable> map)
    {
        map.put(LABEL, ONE);
    }
}
