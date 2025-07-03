package com.pttdigital.cosmosdb.depedencies;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosDatabaseProperties;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Main {
	
	public void print_databases_and_containers(String cosmos_db_endpoint, String cosmos_db_access_key) {
		
		CosmosClient cosmosClient = null;
		try {
			cosmosClient = new CosmosClientBuilder().endpoint(cosmos_db_endpoint).key(cosmos_db_access_key).consistencyLevel(ConsistencyLevel.EVENTUAL).buildClient();
			CosmosPagedIterable<CosmosDatabaseProperties> databases = cosmosClient.readAllDatabases();
			for (CosmosDatabaseProperties p : databases) {
				System.out.println("database: " + p.getId());
				CosmosDatabase cosmosDatabase = cosmosClient.getDatabase(p.getId());
				CosmosPagedIterable<CosmosContainerProperties> cosmosContainerProperties = cosmosDatabase.readAllContainers();
				for (CosmosContainerProperties cp : cosmosContainerProperties) {
					System.out.println("\t" + "container: " + cp.getId());
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (cosmosClient != null) {
				cosmosClient.close();
			}
		}
	}
	
	public JsonArray query(String cosmos_db_endpoint, String cosmos_db_access_key, String databaseName, String containerName, String query) throws Exception {
		CosmosClient cosmosClient = null;
		try {
			
			JsonArray arr = new JsonArray();
			
			cosmosClient = new CosmosClientBuilder().endpoint(cosmos_db_endpoint).key(cosmos_db_access_key).consistencyLevel(ConsistencyLevel.EVENTUAL).buildClient();
			CosmosDatabase cosmosDatabase = cosmosClient.getDatabase(databaseName);
			CosmosContainer cosmosContainer = cosmosDatabase.getContainer(containerName);
			CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
			options.setMaxDegreeOfParallelism(1);
			CosmosPagedIterable<Object> queryResults = cosmosContainer.queryItems(query, options, Object.class);
			
			Iterator<Object> iterator = queryResults.iterator();
			
			while (iterator.hasNext()) {
				arr.add(new Gson().fromJson(new Gson().toJson(iterator.next()).toString(), JsonObject.class));
			}
			cosmosClient.close();
			return arr;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (cosmosClient != null) {
				cosmosClient.close();
			}
		}
	}
	
	public static void test() throws Exception {
		main(null);
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("java.version: " + System.getProperty("java.version"));
		
		String cosmos_db_endpoint = "https://<YOUR_ENDPOINT>.documents.azure.com";
		String cosmos_db_access_key = "<YOUR_ACCESS_KEY>";
		String databaseName = "<YOUR_DATABASE_NAME>";
		String containerName = "<YOUR_CONTAINER_NAME>";
		String query = "SELECT * FROM " + containerName + " WHERE " + containerName + ".GUID = \"8d7c76e9-9dbd-4d41-8b2d-cd791956d0f4\" OFFSET 0 LIMIT 10 ";
		
		JsonArray arr = new Main().query(cosmos_db_endpoint, cosmos_db_access_key, databaseName, containerName, query);
		Files.writeString(Path.of("arr.json"), arr.toString());
		
		System.out.println("arr.size(): " + arr.size());
	}
}