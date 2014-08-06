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

  @Mock Genomics.Datasets datasets;
  @Mock Genomics.Datasets.Create datasetCreate;
  @Mock Genomics.Datasets.Get datasetGet;

  @Mock Genomics.Readsets readsets;
  @Mock Genomics.Readsets.Get readsetGet;

  @Mock Genomics.Variants variants;
  @Mock Genomics.Variants.Get variantGet;

  ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);

    Mockito.when(genomics.callsets()).thenReturn(callsets);
    Mockito.when(genomics.datasets()).thenReturn(datasets);
    Mockito.when(genomics.readsets()).thenReturn(readsets);
    Mockito.when(genomics.variants()).thenReturn(variants);
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
