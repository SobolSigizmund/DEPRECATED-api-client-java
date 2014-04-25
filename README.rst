==============
genomics-tools
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
------------

The `Google Genomics Discuss mailing list <https://groups.google.com/forum/#!forum/google-genomics-discuss>`_ is a good
way to sync up with other people who use genomics-tools including the core developers. You can subscribe
by sending an email to ``google-genomics-discuss+subscribe@googlegroups.com`` or just post using
the `web forum page <https://groups.google.com/forum/#!forum/google-genomics-discuss>`_.


Contributing changes
--------------------

See `CONTRIBUTING <CONTRIBUTING.rst>`__.

Licensing
---------

See `LICENSE <LICENSE>`__.
