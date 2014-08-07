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

import com.google.api.client.json.GenericJson;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.api.services.genomics.GenomicsScopes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CustomCommandTest extends CommandTest {

  @Test
  public void testScopes() throws Exception {
    CustomCommand command = new CustomCommand();
    assertFalse(command.getScopes().contains(GenomicsScopes.BIGQUERY));

    command.requireAllScopes = true;
    assertTrue(command.getScopes().contains(GenomicsScopes.BIGQUERY));
  }

  @Test
  public void testCommand() throws Exception {
    CustomCommand command = new CustomCommand() {
      @Override
      protected void executeAndPrint(GenomicsRequest<? extends GenericJson> req)
          throws IOException {
        assertEquals(genomics, req.getAbstractGoogleClient());
        assertEquals("endpoint", req.getUriTemplate());
        assertEquals("POST", req.getRequestMethod());
      }
    };


    command.customEndpoint = "endpoint";
    command.handleRequest(genomics);
  }

}
