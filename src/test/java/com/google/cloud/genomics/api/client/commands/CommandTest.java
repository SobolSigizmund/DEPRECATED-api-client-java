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
  @Mock protected Genomics genomics;

  @Mock protected Genomics.Callsets callsets;
  @Mock protected Genomics.Callsets.Search callsetSearch;
  @Mock protected Genomics.Callsets.Patch callsetPatch;
  @Mock protected Genomics.Callsets.Delete callsetDelete;
  
  @Mock protected Genomics.Datasets datasets;
  @Mock protected Genomics.Datasets.Create datasetCreate;
  @Mock protected Genomics.Datasets.Get datasetGet;
  @Mock protected Genomics.Datasets.List datasetList;
  @Mock protected Genomics.Datasets.Patch datasetPatch;

  @Mock protected Genomics.Jobs jobs;
  @Mock protected Genomics.Jobs.Cancel jobCancel;
  @Mock protected Genomics.Jobs.Get jobGet;
  @Mock protected Genomics.Jobs.Search jobSearch;

  @Mock protected Genomics.Referencesets referenceSets;
  @Mock protected Genomics.Referencesets.Search referenceSetSearch;

  @Mock protected Genomics.References references;
  @Mock protected Genomics.References.Search referencesSearch;
  @Mock protected Genomics.References.Bases referenceBases;
  @Mock protected Genomics.References.Bases.List referenceBasesList;

  @Mock protected Genomics.Reads reads;
  @Mock protected Genomics.Reads.Search readSearch;

  @Mock protected Genomics.Readgroupsets readsets;
  @Mock protected Genomics.Readgroupsets.Align readsetAlign;
  @Mock protected Genomics.Readgroupsets.Call readsetCall;
  @Mock protected Genomics.Readgroupsets.Export readsetExport;
  @Mock protected Genomics.Readgroupsets.GenomicsImport readsetImport;
  @Mock protected Genomics.Readgroupsets.Get readsetGet;
  @Mock protected Genomics.Readgroupsets.Search readsetSearch;

  @Mock protected Genomics.Variantsets variantSets;
  @Mock protected Genomics.Variantsets.Create variantSetCreate;
  @Mock protected Genomics.Variantsets.Get variantSetGet;
  @Mock protected Genomics.Variantsets.Export variantExport;
  @Mock protected Genomics.Variantsets.ImportVariants variantImport;

  @Mock protected Genomics.Variants variants;
  @Mock protected Genomics.Variants.Get variantGet;
  @Mock protected Genomics.Variants.Search variantSearch;

  protected ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);

    Mockito.when(genomics.callsets()).thenReturn(callsets);
    Mockito.when(genomics.datasets()).thenReturn(datasets);
    Mockito.when(genomics.jobs()).thenReturn(jobs);
    Mockito.when(genomics.referencesets()).thenReturn(referenceSets);
    Mockito.when(genomics.references()).thenReturn(references);
    Mockito.when(genomics.reads()).thenReturn(reads);
    Mockito.when(genomics.readgroupsets()).thenReturn(readsets);
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
