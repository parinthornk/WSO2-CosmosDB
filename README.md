# CosmosDB Connector for WSO2 ESB

This document provides a technical deployment guide for integrating and deploying a CosmosDB connector within a WSO2 ESB environment.

## Prerequisites

### Your Development Environment

- **Java 11** (recommended)
- **Maven 3.9.9** (recommended)
- **curl** for testing

### WSO2 ESB Server

- **wso2ei-7.1.0** (recommended)

## Build Process

### 1. Clone the Repository

```bash
git clone https://github.com/parinthornk/WSO2-CosmosDB.git
```

### 2. Build Dependencies

Navigate to the dependencies directory and build:

```bash
cd DEPENDENCIES
mvn clean install
```

This will generate the dependencies file at:
```
/DEPENDENCIES/target/java-11-connector-cosmosdb-dependencies-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### 3. Build the Connector

Navigate to the connector directory and build:

```bash
cd CONNECTOR
mvn clean install
```

This will generate the connector zip file at:
```
/CONNECTOR/target/CosmosDB-connector-1.0.0.zip
```

### 4. Pack Carbon Artifact

1. Copy the zip file from `/CONNECTOR/target/CosmosDB-connector-1.0.0.zip` to the folder:
   ```
   /CONNECTOR/COSMOSDB_CONNECTOR/COSMOSDB_CONNECTOR-connector_1.0.0
   ```

2. Zip the folder `/CONNECTOR/COSMOSDB_CONNECTOR` as `COSMOSDB_CONNECTOR.car`
   
   **Important:** Do not use RAR format - use ZIP format only.

## Deployment

### 1. Stop the Micro Integrator Server

Stop your WSO2 Micro Integrator server before deploying the connector.

### 2. Deploy Dependencies

Copy the JAR file to the lib directory:

```bash
cp /DEPENDENCIES/target/java-11-connector-cosmosdb-dependencies-0.0.1-SNAPSHOT-jar-with-dependencies.jar <micro_integrator_folder>/lib
```

### 3. Deploy Carbon Artifact

Copy the carbon artifact file to the deployment directory:

```bash
cp COSMOSDB_CONNECTOR.car <micro_integrator_folder>/repository/deployment/server/carbonapps
```

### 4. Start the Micro Integrator Server

Start your WSO2 Micro Integrator server to complete the deployment.

## Testing

You can test the deployed service using the following curl command:

```bash
curl --location 'http://<micro_integrator_server_ip>:8290/COSMOSDB_CONNECTOR/<database_name>/<container_name>?limit=2&offset=10' \
     --header 'Authorization: Basic <base64(cosmosdb_url:access_key)>'
```

### Parameters

- `<micro_integrator_server_ip>`: IP address of your Micro Integrator server
- `<database_name>`: Name of your CosmosDB database
- `<container_name>`: Name of your CosmosDB container
- `limit`: Number of records to return (optional)
- `offset`: Number of records to skip (optional)
- `<base64(cosmosdb_url:access_key)>`: Base64 encoded string of your CosmosDB URL and access key

## Repository

For more information and source code, visit: [https://github.com/parinthornk/WSO2-CosmosDB](https://github.com/parinthornk/WSO2-CosmosDB)

---

*Last updated: July 3, 2025*