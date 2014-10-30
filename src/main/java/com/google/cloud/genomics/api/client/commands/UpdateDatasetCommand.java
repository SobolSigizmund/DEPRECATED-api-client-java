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

@Parameters(commandDescription = "Change the fields of an existing dataset")
public class UpdateDatasetCommand extends BaseCommand {

  @Parameter(names = {"--id", "--dataset_id"},
      description = "The ID of the dataset that will be changed",
      required = true)
  public String datasetId;

  @Parameter(names = "--name",
      description = "The new name of the dataset")
  public String name;

  @Parameter(names = "--public",
      description = "Change the visibility of the dataset by specifying this flag " +
          "followed by 'true' or 'false'. By default the visibility won't be changed.",
      arity = 1)
  public Boolean isPublic;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    if (name == null && isPublic == null) {
      System.out.println("No new field values were specified. " +
          "Either the 'name' flag or the 'public' flag needs to be set.");
      return;
    }

    // Validate the datasetId
    if (getDataset(genomics, datasetId) == null) {
      return;
    }

    Dataset dataset = new Dataset().setId(datasetId);
    if (name != null) {
      dataset.setName(name);
    }
    if (isPublic != null) {
      dataset.setIsPublic(isPublic);
    }
    dataset = genomics.datasets().patch(datasetId, dataset).execute();

    addDatasetToHistory(dataset);
    System.out.println("The dataset was updated: " + dataset.toPrettyString());
  }
}
