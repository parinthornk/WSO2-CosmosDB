package com.pttdigital.CosmosDB;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
public class FunctionHandler {
	public Result getResult(String path, String method, Map<String, String> headers, Map<String, String> query_params) throws Exception {
		
		String current_action = "start";
		
		if (!method.equalsIgnoreCase("get")) {
			
			throw new Exception("method not allow");
			
		}
		
		String[] ps = path.split("/");
		
		if (ps.length != 3) {
			
			throw new Exception("not implemented");
			
		}
		
		try {
			
			current_action = "String database_name = ps[1];";
			String database_name = ps[1];
			
			current_action = "String container_name = ps[2];";
			String container_name = ps[2];
			
			int offset = 0;
			
			int limit = 100;
			
			if (query_params != null) {
				
				try {
					
					offset = Integer.parseInt(query_params.get("offset"));
					
				} catch (Exception ex) { }
				
				try {
					
					limit = Integer.parseInt(query_params.get("limit"));
					
				} catch (Exception ex) { }
				
			}
			
			String cosmos_db_endpoint = "";
			
			String cosmos_db_access_key = "";
			
			try {
				
				String b64 = headers.get("Authorization").replace("Basic ", "");
				
				String decodedStr = new String(Base64.getDecoder().decode(b64));
				
				String[] s2 = decodedStr.split(":");
				
				String _x_cosmos_db_access_key = s2[s2.length - 1];
				
				String _x_cosmos_db_endpoint = decodedStr.substring(0, decodedStr.length() - (":" + _x_cosmos_db_access_key).length());
				
				if (!_x_cosmos_db_endpoint.startsWith("https://")) {
					_x_cosmos_db_endpoint = "https://" + _x_cosmos_db_endpoint;
				}
				
				cosmos_db_endpoint = _x_cosmos_db_endpoint;
				
				cosmos_db_access_key = _x_cosmos_db_access_key;
				
			} catch (Exception ex) {
				
			}
			
			String query = "SELECT * FROM " + container_name + " OFFSET " + offset + " LIMIT " + limit;
			
			current_action = "String response_body = new CosmosHandler().query(" + cosmos_db_endpoint + ", " + cosmos_db_access_key + ", " + database_name + ", " + container_name + ", " + query + ").toString();";
			String response_body = new CosmosHandler().query(cosmos_db_endpoint, cosmos_db_access_key, database_name, container_name, query).toString();
			
			Result r = new Result();
			
			r.statusCode = 200;
			
			r.content = response_body;
			
			return r;
			
		} catch (Exception ex) {
			throw new Exception("error while " + current_action + ", " + ex.getMessage());
		}
	}
}