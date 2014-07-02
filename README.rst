==============
api-client-java |Build Status|_
==============

.. |Build Status| image:: https://travis-ci.org/googlegenomics/api-client-java.png?branch=master
.. _Build Status: https://travis-ci.org/googlegenomics/api-client-java

Getting started
---------------

This Java client allows users to call the `Google Genomics API`_ through the
command line.

* To use, first build the client using `Apache Maven`_::

    cd api-client-java
    mvn package

* Then, follow the `sign up instructions`_ to generate a valid
  ``client_secrets.json`` file.

* Move the ``client_secrets.json`` file into the client-java directory.
  (Authentication will take place the first time you make an API call.)

* You can then perform API queries like fetching readsets or
  reads::

    java -jar target/genomics-tools-client-java-v1beta.jar searchreadsets --dataset_id 376902546192 --fields "readsets(id,name)"

    java -jar target/genomics-tools-client-java-v1beta.jar searchreads --readset_id "CJDmkYn8ChCh4IH4hOf4gacB" --sequence_name 1 --sequence_start 10000 --sequence_end 10000

* The default API provider is Google, but you can also query against NCBI.
  (Be sure to specify the fields parameter as some of the NCBI fields don't pass the strict type checking done by the Java JSON parser.)::

    java -jar target/genomics-tools-client-java-v1beta.jar searchreadsets --root_url "http://trace.ncbi.nlm.nih.gov/Traces/gg/" --dataset_id "SRP034507" --fields "readsets(id,name,fileData),pageToken"

    java -jar target/genomics-tools-client-java-v1beta.jar searchreads --root_url "http://trace.ncbi.nlm.nih.gov/Traces/gg/" --readset_id "SRR1050536" --sequence_name "gi|333959|gb|M74568.1|RSHSEQ" --sequence_start 1 --sequence_end 100 --fields "pageToken,reads(name,position,flags)"

    

* See `the docs <http://google-genomics.readthedocs.org/en/latest/api-client-java/usage.html>`_ for the full set of options

* Note that not all of Google's APIs are callable at this time. The docs have 
  `a list <http://google-genomics.readthedocs.org/en/latest/auth_requirements.html>`_ 
  of which APIs are available.

.. _Google Genomics API: https://developers.google.com/genomics
.. _Apache Maven: http://maven.apache.org/download.cgi
.. _sign up instructions: https://developers.google.com/genomics

Code layout
-----------

`CommandLine.java <src/main/java/com/google/cloud/genomics/api/client/CommandLine.java>`_:
    defines all of the possible command line arguments using the `args4j library
    <http://args4j.kohsuke.org/index.html>`_.

`GenomicsSample.java <src/main/java/com/google/cloud/genomics/api/client/GenomicsSample.java>`_:
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
The work involved is small. The ``custom`` request type can always be used to call
a method that has not been fully integrated.



The mailing list
----------------

The `Google Genomics Discuss mailing list <https://groups.google.com/forum/#!forum/google-genomics-discuss>`_ is a good
way to sync up with other people who use genomics-tools including the core developers. You can subscribe
by sending an email to ``google-genomics-discuss+subscribe@googlegroups.com`` or just post using
the `web forum page <https://groups.google.com/forum/#!forum/google-genomics-discuss>`_.
