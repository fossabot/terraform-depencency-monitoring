#!/bin/bash 
DIR=$(dirname "${BASH_SOURCE[0]}")
find $DIR -type d -depth 2 | xargs -I {} sh -c "cd {} && mvn clean compile package"
