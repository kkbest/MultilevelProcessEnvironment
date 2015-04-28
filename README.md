# Multilevel Process Environment
A multilevel business process environment written in Java and XQuery.

##Prerequisites
The multilevel business process environment works in conjunction with an XML database management system.

###1.) Download and install an XML database management system

We recommend BaseX: http://basex.org/

In theory, other database management systems with an XQJ driver should work as well. 
Note, however, that the use of another system requires a change in the xqj.properties file as well as the inclusion of the driver implementation in the classpath.
For BaseX, the properties file does not have to be changed, except for changes in the server address (default: localhost), username (default: admin) or password (default: admin).

###2.) Install required XQuery modules
The multilevel business process environment requires the MBAse (read: m-base) module for the management of multilevel business artifacts.
The multilevel business artifact (MBA) [1,2] allows for the artifact-centric management of multilevel business processes.

###3.) Start the XML database management system in server mode
The process environment will access the XML database as a server.
You don't actually need to create a database to run the examples/test cases.
    
For BaseX see: 
    
http://docs.basex.org/wiki/Startup#Server

##Startup

##Examples

##References
---
references:
- id: fenner2012a
  title: One-click science marketing
  author:
  - family: Fenner
    given: Martin
  container-title: Nature Materials
  volume: 11
  URL: 'http://dx.doi.org/10.1038/nmat3283'
  DOI: 10.1038/nmat3283
  issue: 4
  publisher: Nature Publishing Group
  page: 261-263
  type: article-journal
  issued:
    year: 2012
    month: 3
---
