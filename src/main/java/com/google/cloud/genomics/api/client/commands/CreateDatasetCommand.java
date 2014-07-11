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

import java.io.IOException;

@Parameters(commandDescription = "Create a new dataset")
public class CreateDatasetCommand extends BaseCommand {

  @Parameter(names = "--name",
      description = "The name of the new dataset",
      required = true)
  public String name;

  @Parameter(names = {"--project_number", "--project_id"},
      description = "The Google Developer's Console project number that will own the new dataset",
      required = true)
  public Long projectId;

  @Parameter(names = "--public",
      description = "Whether the new dataset should be public (by default the dataset is private)")
  public boolean isPublic = false;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    Dataset dataset = new Dataset().setName(name)
        .setProjectId(projectId).setIsPublic(isPublic);
    dataset = genomics.datasets().create(dataset).execute();

    addDatasetToHistory(dataset);
    System.out.println("The new dataset was created: " + dataset.toPrettyString());
  }
}
