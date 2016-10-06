package edu.jhu.thrax.tools;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.jerboa.util.FileManager;
import edu.jhu.thrax.hadoop.distributional.SignatureWritable;

public class SequenceToSignatures {
  
  private static final Logger LOG = LoggerFactory.getLogger(SequenceToSignatures.class);

  private static void writeConfig(String config_file, int num_bits) throws IOException {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(config_file));
    out.writeInt(num_bits);
    // Does not contain support.
    out.writeBoolean(false);
    out.flush();
    out.close();
  }

  private static void usage() {
    String usage = "Usage: java edu.jhu.thrax.tools.SequenceToSignature"
        + "\t -i sequence_file \t Sequence file from Thrax signature extraction."
        + "\t -o prefix \t\t Prefix for signature files: <prefix>.0001.keyz.gz."
        + "\t -c chunk_size \t\t Number of keys per signature chunk.";
    LOG.error(usage);
  }

  public static void main(String[] args) throws Exception {
    boolean local = true;
    String input_file = null;
    int chunk_size = 500000;
    String output_prefix = null;

    if (args.length < 4 || args[0].toLowerCase(Locale.ROOT).equals("-h")) {
      usage();
      System.exit(0);
    }
    for (int i = 0; i < args.length; i++) {
      if ("-i".equals(args[i]) && (i < args.length - 1)) {
        input_file = args[++i];
      } else if ("-o".equals(args[i]) && (i < args.length - 1)) {
        output_prefix = args[++i];
      } else if ("-c".equals(args[i]) && (i < args.length - 1)) {
        chunk_size = Integer.parseInt(args[++i]);
      }
    }
    if (input_file == null) {
      LOG.error("No input file specified.");
      usage();
      System.exit(0);
    }
    if (output_prefix == null) {
      LOG.error("No output prefix specified.");
      usage();
      System.exit(0);
    }

    LOG.error("Looking for {} on {} .", input_file, (local ? "local filesystem" : "HDFS"));

    Configuration config = new Configuration();
    SignatureWritable signature = new SignatureWritable();


    SequenceFile.Reader reader;
    if (local) {
      Path path = new Path(input_file);
      Option fFile = SequenceFile.Reader.file(path);
      reader = new SequenceFile.Reader(config, fFile);
    } else {
      // TODO: Only works for completely specified URLs (i.e. hdfs://name-node/...), currently
      // disabled until I figure out how to get simple paths to work in HDFS.
      Path path = new Path(input_file);
      Option fFile = SequenceFile.Reader.file(path);
      reader = new SequenceFile.Reader(config, fFile);
    }

    int chunk_id = 0;
    int key_count = 0;

    FileOutputStream bytes_out = null;
    BufferedWriter strengths_writer = null;
    BufferedWriter keys_writer = null;

    while (reader.next(signature)) {
      if (key_count % chunk_size == 0) {
        if (key_count != 0) {
          keys_writer.close();
          bytes_out.close();
          strengths_writer.close();
        }
        String chunk_tag = String.format(Locale.ROOT, "-%05d", chunk_id);
        writeConfig(output_prefix + chunk_tag + ".config", signature.bytes.length * 8);
        bytes_out = new FileOutputStream(output_prefix + chunk_tag + ".bytes");
        strengths_writer = FileManager.getWriter(output_prefix + chunk_tag + ".strengths.gz");
        keys_writer = FileManager.getWriter(output_prefix + chunk_tag + ".keys.gz");
        chunk_id++;
      }
      keys_writer.write(signature.key.toString());
      keys_writer.newLine();
      bytes_out.write(signature.bytes);
      strengths_writer.write("" + signature.strength.get());
      strengths_writer.newLine();
      key_count++;
    }
    reader.close();
    keys_writer.close();
    bytes_out.close();
    strengths_writer.close();
  }
}
