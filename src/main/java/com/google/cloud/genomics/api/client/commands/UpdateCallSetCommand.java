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
import com.google.api.services.genomics.model.CallSet;

import java.io.IOException;

@Parameters(commandDescription = "Updates a call set")
public class UpdateCallSetCommand extends BaseCommand {

  @Parameter(names = {"--id", "--call_set_id" },
      description = "The ID of the call set that will be changed",
      required = true)
  public String callSetId;

  @Parameter(names = "--name",
      description = "The new name of the call set",
      required = true)
  public String name;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    CallSet callSet = new CallSet().setName(name);
    callSet = genomics.callsets().patch(callSetId, callSet).execute();

    System.out.println("The call set was updated: " + callSet.toPrettyString());
  }
}
