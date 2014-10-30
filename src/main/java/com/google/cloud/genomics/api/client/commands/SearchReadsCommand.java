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

  @Parameter(names = {"--id", "--read_group_set_id"},
      description = "The IDs of read group sets you want to get reads for.",
      required = true)
  public List<String> readGroupSetIds;

  @Parameter(names = "--reference_name",
      description = "The reference name to query over (e.g. 'X', '23')")
  public String referenceName;

  @Parameter(names = "--start",
      description = "The start position (0-based) of this query.")
  public Integer start;

  @Parameter(names = "--end",
      description = "The end position (0-based, exclusive) of this query.")
  public Integer end;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    SearchReadsRequest request = new SearchReadsRequest()
        .setReadGroupSetIds(readGroupSetIds)
        .setReferenceName(referenceName)
        .setPageSize(getMaxResults());
    if (start != null) {
      request.setStart(BigInteger.valueOf(start));
    }
    if (end != null) {
      request.setEnd(BigInteger.valueOf(end));
    }

    printResults(Paginator.Reads.create(genomics), request);
  }
}
