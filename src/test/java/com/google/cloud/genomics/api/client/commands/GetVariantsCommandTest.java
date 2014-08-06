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

import com.beust.jcommander.internal.Lists;
import com.google.api.services.genomics.model.Variant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class GetVariantsCommandTest extends CommandTest {

  @Test
  public void testGetVariants() throws Exception {
    GetVariantsCommand command = new GetVariantsCommand();
    command.variantIds = Lists.newArrayList("v1", "v2");

    Mockito.when(variants.get("v1")).thenReturn(variantGet);
    Mockito.when(variants.get("v2")).thenReturn(variantGet);
    Mockito.when(variantGet.execute()).thenReturn(
        new Variant().setContig("contig1"),
        new Variant().setContig("contig2"));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("contig1"));
    assertTrue(output, output.contains("contig2"));
  }

}
