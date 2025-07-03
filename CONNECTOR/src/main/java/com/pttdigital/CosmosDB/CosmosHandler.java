package com.pttdigital.CosmosDB;

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

public class CosmosHandler {
	
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
		
		//System.out.println("cosmos_db_endpoint[" + cosmos_db_endpoint + "], cosmos_db_access_key[" + cosmos_db_access_key + "], databaseName[" + databaseName + "], containerName[" + containerName + "], query[" + query + "]");
		
		String current_action = "CosmosClient cosmosClient = null;";
		
		CosmosClient cosmosClient = null;
		try {
			
			JsonArray arr = new JsonArray();
			
			current_action = "cosmosClient = new CosmosClientBuilder().endpoint(" + cosmos_db_endpoint + ").key(" + cosmos_db_access_key + ").consistencyLevel(ConsistencyLevel.EVENTUAL).buildClient();";
			cosmosClient = new CosmosClientBuilder().endpoint(cosmos_db_endpoint).key(cosmos_db_access_key).consistencyLevel(ConsistencyLevel.EVENTUAL).gatewayMode().buildClient();
			
			current_action = "CosmosDatabase cosmosDatabase = cosmosClient.getDatabase(" + databaseName + ");";
			CosmosDatabase cosmosDatabase = cosmosClient.getDatabase(databaseName);
			
			current_action = "CosmosContainer cosmosContainer = cosmosDatabase.getContainer(" + containerName + ");";
			CosmosContainer cosmosContainer = cosmosDatabase.getContainer(containerName);
			
			current_action = "CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();";
			CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
			
			current_action = "options.setMaxDegreeOfParallelism(1);";
			options.setMaxDegreeOfParallelism(1);
			
			current_action = "CosmosPagedIterable<Object> queryResults = cosmosContainer.queryItems(" + query + ", options, Object.class);";
			CosmosPagedIterable<Object> queryResults = cosmosContainer.queryItems(query, options, Object.class);
			
			current_action = "Iterator<Object> iterator = queryResults.iterator();";
			Iterator<Object> iterator = queryResults.iterator();
			
			current_action = "while (iterator.hasNext())";
			while (iterator.hasNext()) {
				
				current_action = "arr.add(new Gson().fromJson(new Gson().toJson(iterator.next()).toString(), JsonObject.class));";
				arr.add(new Gson().fromJson(new Gson().toJson(iterator.next()).toString(), JsonObject.class));
				
			}
			
			return arr;
		} catch (Exception ex) {
			throw new Exception("failed to " + current_action + ", " + ex.getMessage());
		} finally {
			if (cosmosClient != null) {
				try {
					cosmosClient.close();
				} catch (Exception ex) {
					
				}
			}
		}
	}
}