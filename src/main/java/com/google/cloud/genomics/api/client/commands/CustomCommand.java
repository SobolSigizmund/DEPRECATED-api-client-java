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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.api.services.genomics.GenomicsScopes;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Execute any API against Google Genomics by specifying the URL, " +
        "method, and post body explicitly")
public class CustomCommand extends SimpleCommand {

  @Parameter(names = "--custom_endpoint",
      description = "set the Genomics API endpoint for custom requests (e.g. 'readsets/search')",
      required = true)
  public String customEndpoint = "";

  @Parameter(names = "--custom_body",
      description = "set the JSON POST body for custom requests (e.g. {'datasetId': '5'})")
  public String customBody = null;

  @Parameter(names = "--custom_method",
      description = "set the http method for custom requests (e.g. 'POST', 'GET').")
  public String customMethod = "POST";

  @Parameter(names = "--require_all_scopes",
      description = "Uncommon. If specified, the user will be asked for all " +
          "Genomics related OAuth scopes.")
  public boolean requireAllScopes = false;

  @Override
  public List<String> getScopes() {
    List<String> scopes = super.getScopes();
    if (requireAllScopes) {
      scopes.add(GenomicsScopes.DEVSTORAGE_READ_WRITE);
      scopes.add(GenomicsScopes.BIGQUERY);
    }
    return scopes;
  }

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    GenericJson json = customBody == null ? null :
        JacksonFactory.getDefaultInstance().createJsonParser(customBody)
            .parseAndClose(GenericJson.class);
    executeAndPrint(new GenomicsRequest<GenericJson>(genomics, customMethod, customEndpoint,
        json, GenericJson.class) {});
  }
}
