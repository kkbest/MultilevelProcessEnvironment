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
The multilevel business process environment requires SCXML-XQ, an XQuery-based SCXML interpreter, as execution engine as well as the MBAse (read: m-base) XQuery module for the management of multilevel business artifacts.
The multilevel business artifact (MBA) [1,2] allows for the artifact-centric management of multilevel business processes, it is what the process environment uses to feed the execution engine with.

Both SCXML-XQ [3] and MBAse [4] are available on GitHub.
The XQuery modules of these projects must be installed on the XML database management system using the provided .bxs scripts.
Note that you should first install SCXML-XQ before installing MBAse because MBAse provides a customized SCXML-extension module.

###3.) Start the XML database management system in server mode
The process environment will access the XML database as a server.
You don't actually need to create a database to run the examples/test cases.
    
For BaseX see: 
    
http://docs.basex.org/wiki/Startup#Server

###4.) Resolve dependencies
The multilevel business process environment requires several third-party libraries.
We recommend using maven to resolve dependencies.
A pom.xml file is provided in the root directory of the repository.

##Configuration
In the MultilevelProcessEnvironment directory of this repository, there are several properties file that govern the behavior of the environment.
Change the default values to adapt the environment to your specific needs.

###src/main/resources/xqj.properties
The xqj.properties file holds the parameters for the database connection and has the following properties:

- className: The name of the concrete class that implements the abstract XQJ data source class. Default class is the BaseX data source.
- serverName: The address/name of the server that hosts the XML database. Default is localhost.
- port: The port of the XML database. Default is 1984 (BaseX default).
- user: The name of the user that connects with the database. Default is admin (BaseX default).
- password: The corresponding password. Default is admin (BaseX default).

###src/main/resources/environment.properties
The environment.properties file stores the name of the MBAse and collections therein and sets the interval for update checking.
The file has the following properties:
- database: The name of the MBA database the collections of which are observed by the environment and checked for updates.
- collections: A comma-separated list of names of collections in the MBAse as defined by the database property.
- repeatFrequency: The length of the interval in seconds between checks for updates and execution of transitions. Default is 15 (seconds).

###src/main/resources/quartz.properties
Normally, these properties shouldn't be changed at all.
Proceed with caution!

###src/main/resources/log4j.properties
Use this to tweak log4j output.
If you want to store logs in a separate file, this is the point to configure it.

##Startup
The main class is at.jku.dke.mba.environment.Environment, which must be started in order to get the multilevel business process environment running.

The multilevel process environment checks, in a configurable interval, which MBAs in the MBA database have been altered and calls the execution engine.
The execution engine resolves any actions that need to be taken and updates the MBA accordingly.

##Examples

##References
[1] Christoph Schütz, Lois M. L. Delcambre and Michael Schrefl:
Multilevel Business Artifacts.
http://link.springer.com/chapter/10.1007%2F978-3-642-36285-9_35

[2] Christoph Schütz and Michael Schrefl:
Variability in Artifact-Centric Process Modeling: The Hetero-Homogeneous Approach.
http://crpit.com/confpapers/CRPITV154Schutz.pdf

[3] SCXML-XQ: https://github.com/xtoph85/SCXML-XQ

[4] MBAse: https://github.com/xtoph85/MBAse
