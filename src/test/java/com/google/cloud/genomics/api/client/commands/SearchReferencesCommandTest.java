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

import com.google.api.services.genomics.model.Reference;
import com.google.api.services.genomics.model.SearchReferencesRequest;
import com.google.api.services.genomics.model.SearchReferencesResponse;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class SearchReferencesCommandTest extends CommandTest {

  @Test
  public void testCommand() throws Exception {
    SearchReferencesCommand command = new SearchReferencesCommand();
    command.referenceSetId = "refSetId";

    Mockito.when(references.search(Mockito.any(SearchReferencesRequest.class)))
        .thenReturn(referencesSearch);
    Mockito.when(referencesSearch.execute()).thenReturn(new SearchReferencesResponse()
        .setReferences(Lists.<Reference>newArrayList()));

    command.handleRequest(genomics);
  }

}
