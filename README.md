# Wikipedia-separation: Degree of Separation on Wikipedia
This project aims to help calculating the shortest paths between Wikipedia pages (and thus its degree of separation) using a Neo4j graph database.
We also employ community detection to enable the program to block certain less interesting communities to appear in the found paths.
This project was created for the KU Leuven course *Analysis of Large Scale Social Networks*. 

## Setting up

### Required programs
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or higher to run the code
* [InteliJ IDEA](https://www.jetbrains.com/idea/) to edit, load, compile the code.
Load the repository by opening the build.gradle file in IntelliJ, which will load all of the dependencies (e.g. Spark, GraphX etc)
* A MySQL server, e.g. [WAMP](http://www.wampserver.com/en/) or [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
* A program to run MySQL queries on the MySQL database (e.g. [MySQL Workbench](https://www.mysql.com/products/workbench/)).
This program is used to create the CSV file required by the graph database.
* [Neo4J](https://neo4j.com/) to run the graph database which stores all wikipedia data used in this program.

### Loading the Neo4J Graph Database data

#### Converting the Wikipedia data dump
For this project three tables from the Wikipedia data dump will be used: _page_, _pagelinks_ and _redirect_. To download these
tables, go to any wikimedia datadump of wikipedia, e.g. the [Dutch March 20th 2018 wikipedia datadump](https://dumps.wikimedia.org/nlwiki/20180320/), 
and download `nlwiki-20180320-page.sql.gz` as well as `nlwiki-20180320-pagelinks.sql.gz` and `nlwiki-20180320-redirect.sql.gz`.
To download the data dump of another language and/or date, replace _nlwiki_ and the date in the file names.

These three files each contain a data table in SQL:
* `page`: a collection of all the pages on Wikipedia. Has columns containing id, namespace, title, etc. It also contains
some columns with booleans like `is_redirect` to indicate if a page is a redirect page or not.
* `pagelinks`: a collection of all the links between pages on Wikipedia.
* `redirect`: a collection with the redirect targets of all redirect pages. When a page is a redirect page, this table is
used to extract the redirect target.

Both `page` and `pagelinks` will be used in Neo4j but have to be preprocessed and converted to CSV files first.
To create the right CSV files, import all three SQL files into a MySQL database, and use a program such as MySQL Workbench.

An intermediary table `pagelinks_with_rd` will be created. In this table only pages with namespace 0 will be used and every
occurrence of a redirect page will be replace by its redirect target. In order to create this table, use the following query:
```
CREATE TABLE nlwiki.pagelinks_with_rd AS
SELECT DISTINCT temp2.pl_from as from_id, p2.page_id as to_id
FROM 
    (SELECT pl.pl_from,
            temp.page_is_redirect,
            IF(temp.page_is_redirect = 0, temp.page_title_direct, temp.page_title_redirect) as page_title
    FROM (SELECT * FROM nlwiki.pagelinks AS pl2 LEFT JOIN nlwiki.page AS p3 ON pl2.pl_from = p3.page_id WHERE p3.page_namespace = 0) AS pl
    INNER JOIN
        (SELECT p.page_title as page_title_direct, p.page_is_redirect, rd.rd_title as page_title_redirect
        FROM (SELECT * FROM nlwiki.page WHERE page_namespace = 0)  AS p
        LEFT JOIN nlwiki.redirect AS rd
        ON p.page_id = rd.rd_from
        WHERE page_namespace = 0)
        AS temp
    ON pl.pl_title = temp.page_title_direct
    ORDER by pl_from)
    AS temp2
INNER JOIN
    (SELECT * FROM nlwiki.page WHERE page_namespace = 0) AS p2 
    ON p2.page_title = temp2.page_title
```
This query first does a left on a filtered page table (with namespace 0) and the redirect table. Then an inner join is used
on this table and the `pagelinks` table. The `pagelinks` then contains links in which each target is the original one or a
replaced one if the original one was a redirect. To store this table in CSV format, use the next query:
````
SELECT * 
INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/page_links.csv'
FIELDS TERMINATED BY ','
ESCAPED BY '\\'
LINES TERMINATED BY '\n'
FROM nlwiki.pagelinks_with_rd
````
When the `page_links.csv` is created, the `page_titles.csv` file containing the page_titles table can be generated.
Use the following query to store the page titles in CSV format:
```
SELECT page_id, page_title FROM $SCHEME_NAME.page
INTO OUTFILE 'C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/page_titles.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
ESCAPED BY '\\'
LINES TERMINATED BY '\n';
```
Both CSV files will be used in Neo4j so be sure to remember where you stored them!

#### Loading the Wikipedia data into a Graph Database

Now that the CSV files have been created, it's time to load them into a database.
1. Open Neo4j.
2. Create a new database and give it an appropriate title like **wikipedia-links**.
3. Go to your `%NEO4J_HOME%` location and find the folder `\.Neo4jDesktop\neo4jDatabases\database-[database_identifier_code]\installation-3.3.4\import`.
A simpler way is to open your database in Neo4j, click **Manage** and then use the **Open Folder** button, this will open the aforementioned folder. Then open the import folder.
4. Put the created CSVs in this folder.
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

### OPTIONAL: Streaming Community Detection Algorithm (SCoDA)

The SCoDA implementation that was used in this project originated from the [GitHub repository](https://github.com/ahollocou/scoda) of the original author.
We have already provided calculated communities in the `Python` folder of this repository.
You can however rerun this algorithm to create your own new communities with different sizes.

#### Defining paramters and input
The earlier referrenced 'page_links.csv' will be process through the [python script](../master/Python/wiki_edges.py).
This script will do the following:
1. `input_graph.txt`: A tab-separated conversion of page_links.csv
2. `MAX_NODE_ID`: The node identifier with the highest value
3. `DEGREE_THRESHOLD`: The mode of the degrees of nodes in the graph

#### Run the SCoDA algorithm
Run the minimal C-code algorithm with the following command:
```
./scoda MAX_NODE_ID DEGREE_THRESHOLD IGNORE_LINES < input_graph.txt > output_communities.txt
```

#### Post-processing
Re-run the python script to create an overview file 'overview_communities.csv' of the communities in the graph.


### Loading the communities into a Graph Database

The following files from the `Python` folder have to be added to `\.Neo4jDesktop\neo4jDatabases\database-[database_identifier_code]\installation-3.3.4\import`:
* `overview_communities.csv`
* `output_communities.csv`

Then the following commands can be executed in Neo4j:

```
USING PERIODIC COMMIT 500
LOAD CSV FROM 'file:///overview_communities.csv' AS line
CREATE (com:Community { id: toInteger(line[0])})
```

```
CREATE CONSTRAINT ON (com:Community) ASSERT com.id IS UNIQUE
```

```
USING PERIODIC COMMIT 500
LOAD CSV FROM 'file:///output_communities.csv' AS line
MATCH (page1:Page{id: toInteger(line[0])}),
(com:Community{id: toInteger(line[1])})
CREATE (page1)-[:PART_OF_COM]->(com)
```

## Running the program

### Basic shortest path between pages
To find the shortest path between two wikipedia pages, the main method of `WikipediaSeparationGUI` can be run.
This program can be given several arguments using the command line to instantiate default parameters, namely:

| Argument               | Description               |
| ---------------------- | ------------------------- |
| `-db_url` | The url where the Neo4J database containing the Wikipedia links is running. Default value: `bolt://localhost:7687` |
| `-db_login` | The login name of the Neo4J database. |
| `-db_pw` | The password of the Neo4J database. |
| `-from` | The Wikipedia page to start from. |
| `-to` | The goal Wikipedia page to end on and find the shortest path to.  |

For example, possible program arguments are:
`-db_login neo4j -db_pw admin  -db_url bolt://localhost:11002 -from Katholieke_Universiteit_Leuven -to Socrates_(filosoof)`
Mainly the first three parameters will be important when running your own Neo4j database.

