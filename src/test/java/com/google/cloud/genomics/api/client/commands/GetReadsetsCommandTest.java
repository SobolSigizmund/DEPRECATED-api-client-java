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
import com.google.api.services.genomics.model.Readset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class GetReadsetsCommandTest extends CommandTest {

  @Test
  public void testGetReadsets() throws Exception {
    GetReadsetsCommand command = new GetReadsetsCommand();
    command.readsetIds = Lists.newArrayList("r1", "r2");

    Readset readset1 = new Readset().setId("r1").setName("name1");
    Readset readset2 = new Readset().setId("r2").setName("name2");

    Mockito.when(readsets.get("r1")).thenReturn(readsetGet);
    Mockito.when(readsets.get("r2")).thenReturn(readsetGet);
    Mockito.when(readsetGet.execute()).thenReturn(readset1, readset2);

    command.handleRequest(genomics);
  }

}
