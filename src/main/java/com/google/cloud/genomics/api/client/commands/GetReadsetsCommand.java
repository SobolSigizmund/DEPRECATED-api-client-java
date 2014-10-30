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

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Get read group sets by ID")
public class GetReadsetsCommand extends SimpleCommand {

  @Parameter(names = {"--id", "--read_group_set_id"},
      description = "The Genomics API read group set ID.",
      required = true)
  public List<String> readGroupSetIds;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    for (String id : readGroupSetIds) {
      executeAndPrint(genomics.readgroupsets().get(id));
    }
  }
}
