#!/bin/bash

# Copyright 2015 and onwards Sanford Ryza, Uri Laserson, Sean Owen and Joshua Wills
#
# See LICENSE file for further information.

echo Downloading data for $1

curl -o $2/$1.csv "http://www.google.com/finance/historical?q=$1&startdate=Oct+23%2C+2009&enddate=Oct+23%2C+2014&output=csv"
