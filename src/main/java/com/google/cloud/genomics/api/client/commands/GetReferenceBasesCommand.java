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
import com.google.cloud.genomics.utils.Paginator;

import java.io.IOException;

@Parameters(commandDescription = "Get reference bases")
public class GetReferenceBasesCommand extends BaseCommand {

  @Parameter(names = {"--id", "--reference_id"},
      description = "The ID of the reference you want to get bases for.",
      required = true)
  public String referenceId;

  @Parameter(names = "--start",
      description = "The start position (0-based) of this query.")
  public Long start;

  @Parameter(names = "--end",
      description = "The end position (0-based, exclusive) of this query.")
  public Long end;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    Paginator.References.Bases paginator = Paginator.References.Bases.create(genomics);
    Paginator.GenomicsRequestInitializer<Genomics.References.Bases.List> initializer
        = new Paginator.GenomicsRequestInitializer<Genomics.References.Bases.List>() {
      @Override
      public void initialize(Genomics.References.Bases.List search) {
        search.setStart(start);
        search.setEnd(end);
      }
    };

    for (String bases : paginator.search(referenceId, initializer)) {
      System.out.print(bases);
    }
    System.out.println();
  }
}
