# Multilevel Process Environment
A multilevel business process environment written in Java and XQuery.

##Prerequisites
The multilevel business process environment works in conjunction with an XML database management system.

###1.) Download and install an XML database management system

We recommend BaseX: http://basex.org/

In theory, other database management systems with an XQJ driver should work as well. 
Note, however, that the use of another system requires a change in the xqj.properties file as well as the inclusion of the driver implementation in the classpath.
For BaseX, the properties file does not have to be changed, except for changes in the server address (default: localhost), username (default: admin) or password (default: admin).

###2.) Start the XML database management system in server mode
The process environment will access the XML database as a server.
You don't actually need to create a database to run the examples/test cases.
    
For BaseX see: 
    
http://docs.basex.org/wiki/Startup#Client.2FServer

##Startup

##Examples
