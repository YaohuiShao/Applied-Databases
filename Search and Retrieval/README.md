#  SEARCH AND RETRIEVAL
The purpose of this assignment is to
1.use JDBC to fetch data from the "ad" database,
2.insert this data into a Lucene index for full text search, and
3.provide a search function that combines Lucene's full text search with MySQL's spatial queries.
To efficiently carry out spatial search in MySQL, you are asked to convert the longitude and latitude information of items into points (i.e., into MySQL's POINT data type), and to build a spacial index over those points.