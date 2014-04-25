client-java
===========

Getting started
---------------

This Java client allows users to call the `Google Genomics API`_ through the
command line.

* To use, first build the client using `Apache Maven`_::

    cd genomics-tools/client-java
    mvn package

* Then, follow the `authentication instructions`_ to generate a valid
  ``client_secrets.json`` file.

* Move the ``client_secrets.json`` file into the client-java directory.
(Authentication will take place the first time you make an API call.)

* You can then perform API queries like fetching readsets or
  reads::

    java -jar target/genomics-tools-client-java-v1beta.jar searchreadsets --dataset_ids 376902546192 --fields "readsets(id,name)"

    java -jar target/genomics-tools-client-java-v1beta.jar searchreads --readset_ids "CJDmkYn8ChCh4IH4hOf4gacB" --sequence_name 1 --sequence_start 10000 --sequence_end 10000

* The default API provider is Google, but you can also query against NCBI.::

    java -jar target/genomics-tools-client-java-v1beta.jar searchreadsets --root_url "http://trace.ncbi.nlm.nih.gov/Traces/gg/" --dataset_ids "SRP034507" --fields "readsets(id,name,fileData),pageToken"

    java -jar target/genomics-tools-client-java-v1beta.jar searchreads --root_url "http://trace.ncbi.nlm.nih.gov/Traces/gg/" --readset_ids "SRR1050536" --sequence_name "gi|333959|gb|M74568.1|RSHSEQ" --sequence_start 1 --sequence_end 100 --fields "pageToken,reads(name,position,flags)"

(Be sure to specify the fields parameter as some of the NCBI fields don't pass the strict type checking done by the Java JSON parser.)


.. _Google Genomics API: https://developers.google.com/genomics
.. _Apache Maven: http://maven.apache.org/download.cgi
.. _authentication instructions: https://developers.google.com/genomics#authenticate

Code layout
-----------

Most of the Java code is a generated client library. This includes everything under
``com/google/api/services``. Currently, the generated client library 
(part of this `parent project <https://code.google.com/p/google-api-java-client/>`_) is only creatable by Google. 
Very shortly though it will become a proper Maven dependency 
(artifactId ``google-api-services-genomics`` under groupId ``com.google.apis``).

There are only 2 non-generated files:

CommandLine.java:
    defines all of the possible command line arguments using the `args4j library
    <http://args4j.kohsuke.org/index.html>`_.

GenomicsSample.java:
    provides the bulk of the logic. In its ``main`` method, the user's request is
    dispatched to either make a call to the Genomics API or to authenticate the
    user. Most of the code deals with OAuth.


Project status
--------------

Goals
~~~~~
* Provide a command line interface to the Google Genomics APIs 
  (to make importing, querying, and other methods more accessible)
* Provide an example of how to use the generated Java client library.


Current status
~~~~~~~~~~~~~~
This code is mostly static, there are no known feature requests. 
All bug fixes will be addressed but it's unlikely the overall structure and 
featureset will change much. 

There is an ongoing need to integrate more API calls as they become available. 
The work involved is small.

