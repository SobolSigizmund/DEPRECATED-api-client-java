==============
api-client-java
==============
---------------
|Build Status|_
---------------

.. |Build Status| image:: https://travis-ci.org/GoogleCloudPlatform/genomics-tools.png?branch=master
.. _Build Status: https://travis-ci.org/GoogleCloudPlatform/genomics-tools

The projects in this repository are focused around the `Google Genomics API
<https://developers.google.com/genomics>`_:

client-go:
    shows how easy it can be to call into the API with the Go programming
    language.
client-java:
    provides a command line interface for API queries, and demonstrates how a
    more complex Java client might be written.
client-python:
    provides an example web interface that depends on API queries, and
    demonstrates how a more complex Python client might be written. It uses
    `Google App Engine`_ to deploy.
client-r:
    provides a simple R script that transforms API query results into
    GAlignments.
mapreduce-java:
    uses the `MapReduce Java`_ feature of Google App Engine to create a variant similarity matrix. 
    Also includes code to run `PCA`_ over the results.
mapreduce-python:
    uses the `MapReduce Python`_ feature of Google App Engine to do complex calculations over API data.
protobufs:
    includes the protocol buffers used by the API. All client libraries, documentation, and output are auto generated from these files.
readstore-local-java:
    implements the Genomics API locally, reading its data from a local
    `BAM file`_.

.. _Google App Engine: https://developers.google.com/appengine/docs/python/gettingstartedpython27/introduction
.. _MapReduce Java: https://developers.google.com/appengine/docs/java/dataprocessing/
.. _MapReduce Python: https://developers.google.com/appengine/docs/python/dataprocessing/
.. _PCA: http://en.wikipedia.org/wiki/Principal_coordinates_analysis
.. _BAM file: http://samtools.sourceforge.net/SAMv1.pdf


The mailing list
----------------

The `Google Genomics Discuss mailing list <https://groups.google.com/forum/#!forum/google-genomics-discuss>`_ is a good
way to sync up with other people who use genomics-tools including the core developers. You can subscribe
by sending an email to ``google-genomics-discuss+subscribe@googlegroups.com`` or just post using
the `web forum page <https://groups.google.com/forum/#!forum/google-genomics-discuss>`_.


Important constants
-------------------

There are currently three providers of the API:

=============== =========================================== ==================================================
API Providers   Base url                                    Documentation
=============== =========================================== ==================================================
Google          https://www.googleapis.com/genomics/v1beta  http://developers.google.com/genomics
NCBI            http://trace.ncbi.nlm.nih.gov/Traces/gg     http://trace.ncbi.nlm.nih.gov/Traces/gg/index.html
Local readstore See the `README file`_
=============== =========================================== ==================================================

Each one has certain `datasets <https://developers.google.com/genomics/v1beta/reference/datasets>`_ 
exposed to the public. It will eventually be possibly to list all available datasets from the API directly. 
For now, there are some common public values that can be used (in addition to private datasets):

================== ================= ============
Public Dataset IDs Name              API Provider
================== ================= ============
376902546192       1000 Genomes      Google
383928317087       PGP               Google
461916304629       Simons Foundation Google
SRP034507          SRP034507         NCBI
SRP029392          SRP029392         NCBI
(Any NCBI Study)                     NCBI
================== ================= ============

Within a dataset, the API has 
`a call <https://developers.google.com/genomics/v1beta/reference/readsets/search>`_ 
for getting all the readsets. The IDs that come back have different 
values based on the provider. They are always strings.

========================= ============ ===========================  
Example Readset IDs       API Provider Description
========================= ============ ===========================  
CJDmkYn8ChCh4IH4hOf4gacB  Google       Google generated ID
SRR960599 or SRR960599.1  NCBI         NCBI Experiment Run or Read
========================= ============ ===========================  





.. _README file: https://github.com/GoogleCloudPlatform/genomics-tools/tree/master/readstore-local-java
