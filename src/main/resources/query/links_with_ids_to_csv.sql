SELECT l.pl_from AS from_id, p.page_id AS to_id

INTO OUTFILE 'c:/wamp/tmp/page_links.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
ESCAPED BY '\\'
LINES TERMINATED BY '\n'

FROM pagelinks AS l
INNER JOIN page AS p ON p.page_title=l.pl_title