/*
Copyright 2014 Google Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.google.cloud.genomics.api.client;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Command line options handler for GenomicsSample
 */
class CommandLine {

  public static enum RequestType {
    IMPORTREADSETS, SEARCHREADSETS, GETREADSET, GETJOB, SEARCHREADS, SEARCHVARIANTS, GETVARIANT, CUSTOM
  }

  CmdLineParser parser;

  @Argument(usage = "The type of API request to perform. " +
      "Must be one of: importreadsets, searchreadsets, getreadset, getjob, searchreads, " +
      "searchvariants, getvariant, custom",
      metaVar = "<request_type>",
      required = true)
  public RequestType requestType = null;

  @Option(name = "--root_url",
      metaVar = "<url>",
      usage = "set the Genomics API root URL")
  public String rootUrl = "https://www.googleapis.com/genomics/v1beta";

  @Option(name = "--client_secrets_filename",
      metaVar = "<client_secrets_filename>",
      usage = "Path to client_secrets.json")
  public String clientSecretsFilename = "client_secrets.json";

  @Option(name = "--pretty_print",
      usage = "pretty print json output")
  public boolean prettyPrint = false;

  @Option(name = "--dataset_id",
      metaVar = "<dataset_id>",
      usage = "The Genomics API dataset ID.")
  public List<String> datasetIds = new ArrayList<String>();

  @Option(name = "--job_id",
      metaVar = "<job_id>",
      usage = "The Genomics API job ID.")
  public List<String> jobIds = new ArrayList<String>();

  @Option(name = "--readset_id",
      metaVar = "<readsetId>",
      usage = "The Genomics API readset ID.")
  public List<String> readsetIds = new ArrayList<String>();

  @Option(name = "--variant_id",
      metaVar = "<variantId>",
      usage = "The Genomics API variant ID.")
  public List<String> variantIds = new ArrayList<String>();

  @Option(name = "--page_token",
      metaVar = "<page_token>",
      usage = "The token used to retrieve additional pages in paginated API methods.")
  public String pageToken = "";

  @Option(name = "--fields",
      metaVar = "<field>",
      usage = "The fields to be returned with this query. " +
      "Leaving this blank returns all fields.")
  public String fields = "";

  @Option(name = "--bam_file",
      metaVar = "<bamFile>",
      usage = "A BAM file (as Google Cloud Storage gs:// URL) to be be imported." +
          " Use the flag multiple times to import multiple BAM files at a time.")
  public List<String> bamFiles = new ArrayList<String>();

  @Option(name = "--sequence_name",
      metaVar = "<sequenceName>",
      aliases = "--contig",
      usage = "The sequence name to query over (e.g. 'X', '23')")
  public String sequenceName = "";

  @Option(name = "--sequence_start",
      metaVar = "<sequenceStart>",
      aliases = "--start_position",
      usage = "The start position (1-based) of this query.")
  public Integer sequenceStart = 0;

  @Option(name = "--sequence_end",
      metaVar = "<sequenceEnd>",
      aliases = "--end_position",
      usage = "The end position (1-based, inclusive) of this query.")
  public Integer sequenceEnd = 0;

  @Option(name = "--custom_endpoint",
      metaVar = "<URL path>",
      usage = "set the Genomics API endpoint for custom requests (e.g. 'readsets/search')")
  public String customEndpoint = "";

  @Option(name = "--custom_body",
      metaVar = "<JSON blob>",
      usage = "set the JSON POST body for custom requests (e.g. {'datasetId': '5'})")
  public String customBody = null;

  @Option(name = "--custom_method",
      metaVar = "<http method>",
      usage = "set the http method for custom requests (e.g. 'POST', 'GET') Defaults to POST.")
  public String customMethod = "POST";

  public CommandLine() {
    parser = new CmdLineParser(this);
  }

  public void setArgs(String[] args) throws CmdLineException {
    parser.parseArgument(args);
  }

  public void printHelp(String headline, Appendable out) throws IOException {
    out.append(headline).append("\n").append(getUsage());
  }

  public String getUsage() {
    StringWriter sw = new StringWriter();
    sw.append("Usage: GenomicsSample request_type [flags...]\n");
    parser.printUsage(sw, null);
    return sw.toString();
  }

}
