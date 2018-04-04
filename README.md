# wikipedia-separation
Project for the KU Leuven course *Analysis of Large Scale Social Networks*. 

## Setting up

### Required programs
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or higher to run the code
* [InteliJ IDEA](https://www.jetbrains.com/idea/) to edit, load, compile the code.
Load the repository by opening the build.gradle file in IntelliJ, which will load all of the dependencies (e.g. Spark, GraphX etc)
* A MySQL server, e.g. [WAMP](http://www.wampserver.com/en/)
* A program to run MySQL queries on the MySQL database (e.g. [MySQL Workbench](https://www.mysql.com/products/workbench/)).
This program is used to create the CSV file required by the graph database.
* [Neo4J](https://neo4j.com/) to run the graph database which stores all wikipedia data used in this program.

### Loading the Neo4J Graph Database data

#### Converting the Wikipedia datadump

Go to any wikimedia datadump of wikipedia, e.g. the [Dutch March 20th 2018 wikipedia datadump](https://dumps.wikimedia.org/nlwiki/20180320/), and download *name-date*-nlwiki-20180320-page_props.sql.gz as well as *name-date*-nlwiki-20180320-pagelinks.sql.gz.
These two files are SQL data containing information about which page id has a reference to what other page title, as well as what page id corresponds to which page title.

These files have to be converted to CSV files in order to be loaded into Neo4J.
To create the right CSV files, import both SQL files into a MySQL database, and use a program such as MySQL Workbench to run the following query:
Note: the *outfile* location might need to be updated depending on where your MySQL database has access to files.

```
SELECT l.pl_from AS from_id, p.page_id AS to_id
INTO OUTFILE 'c:/wamp/tmp/page_links.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
ESCAPED BY '\\'
LINES TERMINATED BY '\n'
FROM pagelinks AS l
INNER JOIN page AS p ON p.page_title=l.pl_title
```

```
SELECT page_id, page_title FROM `wikipedia-links`.page
INTO OUTFILE 'c:/wamp/tmp/page_titles.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
ESCAPED BY '\\'
LINES TERMINATED BY '\n';
```

#### Loading the Wikipedia data into a Graph Database

Now that the CSV files have been created, it's time to load them into a database.
1. Open Neo4J
2. Create a new database called **wikipedia-links**.
3. Go to your `%NEO4J_HOME%` location and find the folder `\.Neo4jDesktop\neo4jDatabases\database-[database_identifier_code]\installation-3.3.4\import`.
4. Put the created CSVs in this folder
5. Run the following Cypher query in Neo4J if your database was not empty *(e.g. due to previous experiments)* in order to delete all previous data:
```
MATCH (n)
DETACH DELETE n
```
6. Load in the pages by running the following command in Neo4J
```
USING PERIODIC COMMIT 500
LOAD CSV FROM 'file:///page_titles.csv' AS line
CREATE (:Page { id: toInteger(line[0]), title: line[1]})
```
7. Create a constraint on the pages that each id should be unique *(this is mandatory to establish the relations in the next step)*
```
CREATE CONSTRAINT ON (page:Page) ASSERT page.id IS UNIQUE
```
8. Load in the relations with the following command
```
USING PERIODIC COMMIT 500
LOAD CSV FROM 'file:///page_links.csv' AS line
MATCH (page1:Page{id: toInteger(line[0])}),(page2:Page{id: toInteger(line[1])})
CREATE (page1)-[:REFERENCES_TO]->(page2)
```
