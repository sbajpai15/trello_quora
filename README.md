# Trello_Quora: Upgrad Group Assignment

This project was implemeneted as part of UpGrad Full Stack Development group assignment. Contributing members of the project are:

1. *Pryaan Sinha*
2. *Rakesh Baravathaya*
3. *Shrish Bajpai*
4. *Rupesh Narayana*

<h2> Git/GitHUb best practice </h2>
Each contirbuting member of the project forked the master and created a dev branch for this forked repo. Each member fetched from the upstream repository into their local repo so they are akways working on latest commits and updates of the various branches
After making any local commits, the respective member will pushed their committs to their remote dev repo and raised pull requests to the master. As a rule of thumb the same member cannot raise pull requests and merge merged/approved to the master

<h2> Project Structure </h2>
Project follows a definite structure in order to help the co-developers and reviewers for easy understanding. The main module is divided into three sub-modules —  quora-api, quora-db, and quora-service.

**1. quora-api**

config - This directory consists of all the required configuration files of the project (if any). 
controller - This directory consists of all the controller classes required for the project.
exception - This directory consists of the exception handlers for all the exceptions. 
endpoints - This directory consists of the JSON files which are used to generate the Request and Response models.

**2. quora-db**

config - This directory consists of the database properties and environment properties for local development.
sql - This directory consists of all the SQL queries to create database schema tables.
 
**3. quora-service**
business - This directory consists of all the implementations of the business logic of the application.
dao - This directory allows us to isolate the application/business layer from the persistence layer and consists of the implementation of all the data access object classes.
entity - This directory consists of all the entity classes related to the project to map these class objects with the database. 
exception - This directory consists of all the exceptions related to the project. 

