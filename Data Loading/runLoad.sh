#!/bin/bash

# Run drop.sql batch file to drop existing tables. Drop them ONLY if they exist.
mysql < drop.sql

# Run create.sql batch file to create the database and tables
mysql < create.sql

# Compile and run the convertor
javac MySAX.java
java MySAX items-*.xml

# Run the load.sql batch file to load the data
mysql ad < load.sql

# remove all temporary files
rm Item.csv
rm Seller.csv
rm Bidder.csv
rm Bids.csv
rm ItemCategory.csv
rm ItemLocation.csv
rm Buy_Price.csv
rm BidderLocation.csv
rm BidderCountry.csv
