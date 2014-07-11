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
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.SearchVariantsRequest;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Search over variants")
public class SearchVariantsCommand extends SimpleCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to get variants for.",
      required = true)
  public String datasetId;

  @Parameter(names = "--callset_id",
      description = "Only return calls from these callsets.")
  public List<String> callsetIds;

  @Parameter(names = "--page_token",
      description = "The token used to retrieve additional pages in paginated API methods.")
  public String pageToken = "";

  @Parameter(names = { "--sequence_name", "--contig" },
      description = "The sequence name to query over (e.g. 'X', '23')",
      required = true)
  public String sequenceName;

  @Parameter(names = { "--sequence_start", "--start_position" },
      description = "The start position (1-based) of this query.",
      required = true)
  public Long sequenceStart;

  @Parameter(names = {"--sequence_end", "--end_position" },
      description = "The end position (1-based, inclusive) of this query.",
      required = true)
  public Long sequenceEnd;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Getting variants from: " + dataset.getName());

    SearchVariantsRequest request = new SearchVariantsRequest()
        .setDatasetId(datasetId)
        .setPageToken(pageToken)
        .setContig(sequenceName)
        .setStartPosition(sequenceStart)
        .setEndPosition(sequenceEnd);

    if (callsetIds != null) {
      request.setCallsetIds(callsetIds);
    }

    executeAndPrint(genomics.variants().search(request));
  }
}
