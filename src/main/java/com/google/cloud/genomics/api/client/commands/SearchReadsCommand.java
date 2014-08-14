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
package com.google.cloud.genomics.api.client.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.cloud.genomics.utils.Paginator;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Parameters(commandDescription = "Search over reads")
public class SearchReadsCommand extends SearchCommand {

  @Parameter(names = "--readset_id",
      description = "The IDs of readsets you want to get reads for.",
      required = true)
  public List<String> readsetIds;

  @Parameter(names = "--page_token",
      description = "Deprecated. Use --count instead.",
      hidden = true)
  public String pageToken = "";

  @Parameter(names = { "--sequence_name", "--contig" },
      description = "The sequence name to query over (e.g. 'X', '23')")
  public String sequenceName;

  @Parameter(names = { "--sequence_start", "--start_position" },
      description = "The start position (1-based) of this query.")
  public Integer sequenceStart;

  @Parameter(names = {"--sequence_end", "--end_position" },
      description = "The end position (1-based, inclusive) of this query.")
  public Integer sequenceEnd;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    if (!pageToken.isEmpty()) {
      System.out.println("--page_token is now deprecated. Use --count instead.");
      return;
    }

    SearchReadsRequest request = new SearchReadsRequest()
        .setReadsetIds(readsetIds)
        .setPageToken(pageToken)
        .setSequenceName(sequenceName)
        .setMaxResults(getMaxResults());
    if (sequenceStart != null) {
        request.setSequenceStart(BigInteger.valueOf(sequenceStart));
    }
    if (sequenceEnd != null) {
        request.setSequenceEnd(BigInteger.valueOf(sequenceEnd));
    }

    printResults(Paginator.Reads.create(genomics), request);
  }
}
