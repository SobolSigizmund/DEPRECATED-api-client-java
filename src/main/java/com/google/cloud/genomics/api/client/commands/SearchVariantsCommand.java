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
import com.beust.jcommander.internal.Lists;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.*;
import com.google.cloud.genomics.utils.Paginator;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Search over variants")
public class SearchVariantsCommand extends SearchCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to get variants for.",
      required = true)
  public String datasetId;

  @Parameter(names = "--callset_id",
      description = "Only return calls from these callsets.")
  public List<String> callsetIds;

  @Parameter(names = "--callset_name",
      description = "An alternative to callset_id, only return calls from callsets " +
          "with these names.")
  public List<String> callsetNames;

  @Parameter(names = "--page_token",
      description = "Deprecated. Use --count instead.",
      hidden = true)
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
    if (!pageToken.isEmpty()) {
      System.out.println("--page_token is now deprecated. Use --count instead.");
      return;
    }

    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Getting variants from: " + dataset.getName());

    if (callsetNames != null) {
      getCallsetsByName(genomics);
      if (callsetIds.isEmpty()) {
        // We couldn't find any valid callsets to query
        return;
      }
    }

    SearchVariantsRequest request = new SearchVariantsRequest()
        .setVariantsetId(datasetId)
        .setPageToken(pageToken)
        .setContig(sequenceName)
        .setStartPosition(sequenceStart)
        .setEndPosition(sequenceEnd)
        .setMaxResults(getMaxResults());

    if (callsetIds != null) {
      request.setCallsetIds(callsetIds);
    }

    printResults(Paginator.Variants.create(genomics), request);
  }

  private void getCallsetsByName(Genomics genomics) throws IOException {
    List<String> datasetIds = Lists.newArrayList();
    datasetIds.add(datasetId);

    if (callsetIds == null) {
      callsetIds = Lists.newArrayList();
    }

    for (String name : callsetNames) {
      SearchCallsetsRequest request = new SearchCallsetsRequest()
          .setVariantsetIds(datasetIds).setName(name);
      List<Callset> callsets = genomics.callsets().search(request).execute().getCallsets();
      if (callsets == null || callsets.isEmpty()) {
        System.out.println("No callsets found with the name " + name);
        continue;
      }
      for (Callset callset : callsets) {
        callsetIds.add(callset.getId());
      }
    }
  }
}
