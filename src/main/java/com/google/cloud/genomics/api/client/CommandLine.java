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
package com.google.cloud.genomics.api.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import com.google.cloud.genomics.api.client.commands.*;

import java.io.IOException;
import java.util.Map;

/**
 * Command line options handler for GenomicsSample
 */
class CommandLine {

  private JCommander parser;
  private BaseCommand command;
  private Map<String, BaseCommand> registeredCommands = Maps.newHashMap();

  public CommandLine() {
    parser = new JCommander();
    parser.setProgramName("genomics-tools-client-java");

    addCommand("importreadsets", new ImportReadsetsCommand());
    addCommand("searchreadsets", new SearchReadsetsCommand());
    addCommand("getreadset", new GetReadsetsCommand());
    addCommand("getjob", new GetJobsCommand());
    addCommand("searchreads", new SearchReadsCommand());
    addCommand("getvariant", new GetVariantsCommand());
    addCommand("searchvariants", new SearchVariantsCommand());
    addCommand("custom", new CustomCommand());
  }

  private void addCommand(String name, BaseCommand command) {
    registeredCommands.put(name, command);
    parser.addCommand(name, command);
  }

  public BaseCommand getCommand() {
    return command;
  }

  public void setArgs(String[] args) {
    parser.parse(args);

    if (Strings.isNullOrEmpty(parser.getParsedCommand())) {
      throw new MissingCommandException("A command is required");
    } else {
      command = registeredCommands.get(parser.getParsedCommand());
    }
  }

  public void printHelp(String headline, Appendable out) throws IOException {
    out.append(headline).append("\n");
    if (Strings.isNullOrEmpty(parser.getParsedCommand())) {
      out.append("Valid commands are: importreadsets searchreadsets getreadset getjob " +
          "searchreads getvariant searchvariants custom").append("\n\n");
    } else {
      parser.usage(parser.getParsedCommand());
    }
  }
}
