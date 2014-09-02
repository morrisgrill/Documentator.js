Documentator.js
===============

This is a Java application which makes Markdown documents for your Node.js projects with Express framework and Mongoose, all you need is to put the jar file inside your main project folder and run it.

#Application Details:
Documentator for Node.js projects
- Author: Mauricio Zarate Barrera
- E-mail: morrisgrill@hotmail.com
- Date of release: September 2th, 2014
- Version: 0.2

##Installation
Simply put the documentator.jar inside the main folder of your Node.js project

##Usage
To run this application type in your terminal: java -jar documentator.jar
In each Javascript File in the Routes folder, you can use these tags:

####At any place of your Javascript file
@maindescription= Main description of the REST Service
    Example: // @maindescription= This is the main description of my document

@author= Name and e-mail of the author
    Example: // @author= Mauricio Zarate Barrera <morrisgrill@hotmail[DOT]com

####Inside of your functions
@description= A general function description (This will be inside your functions.
    Example: // @description= This is a function description

@example= How you will call this function?
    Example: // @example= /route/to/your/function?with=some&params=ok

@param= Param details received in your function
    Definition: @param= [Type], [Name], [Receive], [Description]
    Example: //@param= String, param1, Value, Filter by param1

##Notes
The builder will save all documents in the /docs folder, probably you may 
need to create this folder. In order to run properly this application 
the /routes and /models folders must exist.

In this version, the builder mode is predefined for Testing Purposes, this 
will add the tag 'rev' in each document name you make, if you want to change this, 
go to builder mode options and select Production Mode, but it should be chosen 
each time you start this application. 

##Lastest updates
#### Version 0.2
- Param recognition
- Changes on the documentation of this application
- Some bug fixes

#### Version 0.1
- Generate bulk or single document for each file in /routes folder
- Read schema model
- Read routes and it's functions
- Read file dependencies
