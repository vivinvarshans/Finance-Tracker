#!/bin/bash

# Finance Tracker Backend - Quick Start Script

echo "================================================"
echo "Finance Tracker Backend - Spring Boot Setup"
echo "================================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check Java version
echo -e "${YELLOW}Checking Java version...${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    echo -e "${GREEN}✓ Java version: $JAVA_VERSION${NC}"
    
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo -e "${RED}✗ Java 17 or higher is required!${NC}"
        exit 1
    fi
else
    echo -e "${RED}✗ Java is not installed!${NC}"
    exit 1
fi

echo ""

# Check Maven
echo -e "${YELLOW}Checking Maven...${NC}"
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo -e "${GREEN}✓ Maven found: $MVN_VERSION${NC}"
else
    echo -e "${RED}✗ Maven is not installed!${NC}"
    exit 1
fi

echo ""

# Check PostgreSQL
echo -e "${YELLOW}Checking PostgreSQL...${NC}"
if command -v psql &> /dev/null; then
    echo -e "${GREEN}✓ PostgreSQL is installed${NC}"
else
    echo -e "${YELLOW}⚠ PostgreSQL not found. Make sure it's running on localhost:5432${NC}"
fi

echo ""
echo "================================================"
echo "Building the project..."
echo "================================================"

mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Build successful!${NC}"
    echo ""
    echo "================================================"
    echo "Starting the application..."
    echo "================================================"
    echo ""
    echo -e "${YELLOW}API will be available at: http://localhost:8080/api${NC}"
    echo ""
    
    mvn spring-boot:run
else
    echo ""
    echo -e "${RED}✗ Build failed!${NC}"
    exit 1
fi
