SELECT page_id, page_title FROM `wikipedia-links`.page
INTO OUTFILE 'c:/wamp/tmp/page_titles.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
ESCAPED BY '\\'
LINES TERMINATED BY '\n';

