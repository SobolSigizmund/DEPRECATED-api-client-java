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
import com.beust.jcommander.internal.Lists;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import com.google.cloud.genomics.api.client.commands.*;
import com.google.cloud.genomics.api.client.commands.align.AlignBamsCommand;
import com.google.cloud.genomics.api.client.commands.align.AlignInterleavedFastqsCommand;
import com.google.cloud.genomics.api.client.commands.align.AlignPairedFastqsCommand;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Command line options handler for GenomicsSample
 */
class CommandLine {

  private JCommander parser;
  private BaseCommand command;
  private Map<String, BaseCommand> registeredCommands = Maps.newLinkedHashMap();

  public CommandLine() {
    parser = new JCommander();
    parser.setProgramName("genomics-tools-client-java");

    // The ordering of these commands is preserved in help messages
    addCommand("listdatasets", new ListDatasetsCommand());
    addCommand("getdataset", new GetDatasetsCommand());
    addCommand("createdataset", new CreateDatasetCommand());
    addCommand("updatedataset", new UpdateDatasetCommand());

    addCommand("searchreadgroupsets", new SearchReadGroupSetsCommand());
    addCommand("getreadgroupset", new GetReadGroupSetsCommand());

    addCommand("searchreads", new SearchReadsCommand());
    addCommand("importreads", new ImportReadsCommand());
    addCommand("exportreads", new ExportReadsCommand());

    addCommand("alignbam", new AlignBamsCommand());
    addCommand("alignfastq", new AlignInterleavedFastqsCommand());
    addCommand("alignpairedfastq", new AlignPairedFastqsCommand());

    addCommand("getvariantset", new GetVariantSetsCommand());

    addCommand("searchvariants", new SearchVariantsCommand());
    addCommand("getvariant", new GetVariantsCommand());
    addCommand("importvariants", new ImportVariantsCommand());
    addCommand("exportvariants", new ExportVariantsCommand());

    addCommand("searchcallsets", new SearchCallSetsCommand());
    addCommand("updatecallset", new UpdateCallSetCommand());

    addCommand("listjobs", new ListJobsCommand());
    addCommand("getjob", new GetJobsCommand());
    addCommand("canceljob", new CancelJobCommand());

    // Custom escape hatch
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
      out.append("Valid commands are:\n");
      describeCommands(out);
      out.append("\n");
    } else {
      StringBuilder sb = new StringBuilder();
      parser.usage(parser.getParsedCommand(), sb);
      out.append(sb.toString());
    }
  }

  private void describeCommands(Appendable out) throws IOException {
    List<String> commands = Lists.newArrayList(registeredCommands.keySet());

    for (String command : commands) {
      out.append(String.format("%-20s%s%n", command, parser.getCommandDescription(command)));
    }
  }
}
