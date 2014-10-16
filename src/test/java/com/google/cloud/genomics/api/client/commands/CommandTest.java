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
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class CommandTest {
  @Mock Genomics genomics;

  @Mock Genomics.Callsets callsets;
  @Mock Genomics.Callsets.Search callsetSearch;
  @Mock Genomics.Callsets.Patch callsetPatch;

  @Mock Genomics.Datasets datasets;
  @Mock Genomics.Datasets.Create datasetCreate;
  @Mock Genomics.Datasets.Get datasetGet;
  @Mock Genomics.Datasets.List datasetList;
  @Mock Genomics.Datasets.Patch datasetPatch;

  @Mock Genomics.Jobs jobs;
  @Mock Genomics.Jobs.Cancel jobCancel;
  @Mock Genomics.Jobs.Get jobGet;
  @Mock Genomics.Jobs.Search jobSearch;

  @Mock Genomics.Reads reads;
  @Mock Genomics.Reads.Search readSearch;

  @Mock Genomics.Readsets readsets;
  @Mock Genomics.Readsets.Export readsetExport;
  @Mock Genomics.Readsets.GenomicsImport readsetImport;
  @Mock Genomics.Readsets.Get readsetGet;
  @Mock Genomics.Readsets.Search readsetSearch;

  @Mock Genomics.Variantsets variantSets;
  @Mock Genomics.Variantsets.Get variantSetGet;

  @Mock Genomics.Variants variants;
  @Mock Genomics.Variants.Export variantExport;
  @Mock Genomics.Variants.GenomicsImport variantImport;
  @Mock Genomics.Variants.Get variantGet;
  @Mock Genomics.Variants.Search variantSearch;

  ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);

    Mockito.when(genomics.callsets()).thenReturn(callsets);
    Mockito.when(genomics.datasets()).thenReturn(datasets);
    Mockito.when(genomics.jobs()).thenReturn(jobs);
    Mockito.when(genomics.reads()).thenReturn(reads);
    Mockito.when(genomics.readsets()).thenReturn(readsets);
    Mockito.when(genomics.variantsets()).thenReturn(variantSets);
    Mockito.when(genomics.variants()).thenReturn(variants);

    Mockito.when(readsetGet.setFields(Mockito.anyString())).thenReturn(readsetGet);
    Mockito.when(readsetSearch.setFields(Mockito.anyString())).thenReturn(readsetSearch);
    Mockito.when(variantSetGet.setFields(Mockito.anyString())).thenReturn(variantSetGet);
  }

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void cleanUpStreams() {
    System.setOut(null);
  }
}
