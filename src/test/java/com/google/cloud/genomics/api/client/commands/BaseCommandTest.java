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
import com.google.api.services.genomics.model.Job;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class BaseCommandTest {
  @Test
  public void testIsJobFinished() throws Exception {
    BaseCommand command = new BaseCommand() {
      @Override
      public void handleRequest(Genomics genomics) throws IOException {}
    };

    Job job = new Job();
    assertFalse(command.isJobFinished(job));

    job.setStatus("running");
    assertFalse(command.isJobFinished(job));

    job.setStatus("success");
    assertTrue(command.isJobFinished(job));

    job.setStatus("failure");
    assertTrue(command.isJobFinished(job));
  }
}
