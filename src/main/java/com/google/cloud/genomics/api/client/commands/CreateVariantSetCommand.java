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

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.VariantSet;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;
import java.util.List;

/**
 * Creates a new variant set (initially empty).
*/
@Parameters(commandDescription = "Create a new (empty) variant set")
public class CreateVariantSetCommand extends BaseCommand {

  @Parameter(names = "--dataset_id",
      description = "The dataset ID in which to create the new variant set.",
      required = true)
  public String datasetId;

  @Override
  public List<String> getScopes() {
    List<String> scopes = super.getScopes();
    scopes.add(GenomicsScopes.DEVSTORAGE_READ_WRITE);
    return scopes;
  }

  @Override
  public void handleRequest(Genomics genomics) throws IOException {

    // Get the name
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Creating variant set in dataset: " + dataset.getName() +" (id: " + datasetId + ")");

    VariantSet variantSet = new VariantSet().setDatasetId(datasetId);

    VariantSet ret = genomics.variantsets().create(variantSet).execute();

    System.out.println("Created variant set id: "+ret.getId());
  }
}
