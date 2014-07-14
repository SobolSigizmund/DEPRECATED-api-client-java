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
import com.google.api.client.json.GenericJson;
import com.google.api.services.genomics.GenomicsRequest;

import java.io.IOException;

/**
 * This class is used for commands that have not yet been refactored to support complex flows,
 * most likely it will become obsolete.
 */
public abstract class SimpleCommand extends BaseCommand {
  @Parameter(names = "--pretty_print",
      description = "pretty print json output")
  public boolean prettyPrint = false;

  @Parameter(names = "--fields",
      description = "The fields to be returned with this query. " +
          "Leaving this blank returns all fields.")
  public String fields = "";

  protected void executeAndPrint(GenomicsRequest<? extends GenericJson> req) throws IOException {
    req.setDisableGZipContent(true); // Required to hit non-Google APIs
    if (!fields.isEmpty()) {
      req.setFields(fields);
    }
    GenericJson result = req.execute();
    if (result == null) {
      // Not all API calls have responses.
      System.out.println("success");
    } else {
      System.out.println("result: " + (prettyPrint ? result.toPrettyString() : result.toString()));
    }
  }
}
