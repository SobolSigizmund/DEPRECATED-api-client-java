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

import com.google.api.services.genomics.model.Job;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CancelJobCommandTest extends CommandTest {

  @Test
  public void testCancelJob() throws Exception {
    CancelJobCommand command = new CancelJobCommand();
    command.jobId = "j1";

    Mockito.when(jobs.cancel("j1")).thenReturn(jobCancel);

    Mockito.when(jobs.get("j1")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(
        new Job().setStatus("cancelled"));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("cancelled"));
  }

}
