#!/bin/bash

cd urlshortener

mvn clean package -DskipTests

cd ..

cd urlshortener-status

mvn clean package -DskipTests

cd ..
