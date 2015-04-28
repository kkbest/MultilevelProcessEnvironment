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
[1] Christoph Schütz, Lois M. L. Delcambre and Michael Schrefl:
Multilevel Business Artifacts.
http://link.springer.com/chapter/10.1007%2F978-3-642-36285-9_35

[2] Christoph Schütz and Michael Schrefl:
Variability in Artifact-Centric Process Modeling: The Hetero-Homogeneous Approach.
http://crpit.com/confpapers/CRPITV154Schutz.pdf
