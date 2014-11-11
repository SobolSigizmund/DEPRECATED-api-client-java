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
import com.google.api.client.json.GenericJson;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.cloud.genomics.utils.Paginator;

import java.io.IOException;

/**
 * This class is used for commands that have not yet been refactored to support complex flows,
 * most likely it will become obsolete.
 */
public abstract class SearchCommand extends BaseCommand {
  @Parameter(names = "--pretty_print",
      description = "pretty print json output")
  public boolean prettyPrint = false;

  @Parameter(names = "--fields",
      description = "The fields to be returned with this query. " +
          "Leaving this blank returns all fields.")
  public String fields = null;

  @Parameter(names = "--count",
      description = "The number of results to return for search requests. " +
          "Use -1 for all (this may result in a lot of data!)")
  public Integer resultCount = 10;

  protected int getMaxResults() {
    return resultCount < 0 ? 1024 : resultCount;
  }

  protected <A,
      B extends GenericJson,
      C extends GenomicsRequest<D>,
      D extends GenericJson,
      E extends GenericJson> void printResults(Paginator<A, B, C, D, E> paginator, B request)
      throws IOException {

    int resultsSeen = 0;
    for (E result : paginator.search(request, fields)) {
      prepareResult(result);
      System.out.println(prettyPrint ? result.toPrettyString() : result.toString());
      resultsSeen++;

      if (resultsSeen == resultCount) {
        break;
      }
    }
    if (resultsSeen == 0) {
      System.out.println("No results found");
    }
  }

  protected <E extends GenericJson> void prepareResult(E result) {
    // Subclasses can override to change the result before printing
  }
}
