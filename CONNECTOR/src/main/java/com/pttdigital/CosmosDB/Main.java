package com.pttdigital.CosmosDB;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import com.google.gson.JsonObject;

public class Main extends AbstractConnector {
	
	private String get_request_method(MessageContext messageContext) {
		return messageContext.getProperty("REST_METHOD").toString().toUpperCase();
	}

	private String get_request_path(MessageContext messageContext) {
		String REST_SUB_REQUEST_PATH = ""; try { REST_SUB_REQUEST_PATH = messageContext.getProperty("REST_SUB_REQUEST_PATH").toString(); } catch (Exception ex) { }
		
		while (REST_SUB_REQUEST_PATH.startsWith("/")) {
			REST_SUB_REQUEST_PATH = REST_SUB_REQUEST_PATH.substring(1, REST_SUB_REQUEST_PATH.length());
		}
		
		while (REST_SUB_REQUEST_PATH.endsWith("/")) {
			REST_SUB_REQUEST_PATH = REST_SUB_REQUEST_PATH.substring(0, REST_SUB_REQUEST_PATH.length() - 1);
		}
		
		REST_SUB_REQUEST_PATH = "/" + REST_SUB_REQUEST_PATH;
		
		if (REST_SUB_REQUEST_PATH.equals("/")) {
			REST_SUB_REQUEST_PATH = "";
		}
		
    	String path = REST_SUB_REQUEST_PATH;
    	
    	if (path.contains("?")) {
    		path = path.split("\\?")[0];
    	}
    	
		return path;
	}

	private Map<String, String> extract_headers(org.apache.axis2.context.MessageContext axis2MessageCtx) {
		Map<String, String> map_headers = new HashMap<String, String>();
		if (axis2MessageCtx != null) {
			Object headers = axis2MessageCtx.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
			if (headers != null && headers instanceof Map) {
				Map headersMap = (Map)headers;
				Set<String> ks = headersMap.keySet();
				for (String hn : ks) {
					Object hv = headersMap.get(hn);
					if (hv == null) {
						continue;
					}
					String header_value = hv.toString();
					map_headers.put(hn, header_value);
				}
			}
		}
		return map_headers;
	}

	private Map<String, String> extract_query_params(MessageContext messageContext) {
		Map<String, String> query_string = new HashMap<String, String>();
		String REST_SUB_REQUEST_PATH = ""; try { REST_SUB_REQUEST_PATH = messageContext.getProperty("REST_SUB_REQUEST_PATH").toString(); } catch (Exception ex) { }
		while (REST_SUB_REQUEST_PATH.startsWith("/")) {
			REST_SUB_REQUEST_PATH = REST_SUB_REQUEST_PATH.substring(1, REST_SUB_REQUEST_PATH.length());
		}
		while (REST_SUB_REQUEST_PATH.endsWith("/")) {
			REST_SUB_REQUEST_PATH = REST_SUB_REQUEST_PATH.substring(0, REST_SUB_REQUEST_PATH.length() - 1);
		}
		REST_SUB_REQUEST_PATH = "/" + REST_SUB_REQUEST_PATH;
		if (REST_SUB_REQUEST_PATH.equals("/")) {
			REST_SUB_REQUEST_PATH = "";
		}
		try {
			if (REST_SUB_REQUEST_PATH.contains("?")) {
				String[] qs = REST_SUB_REQUEST_PATH.split("\\?")[1].split("&");
				for (String q : qs) {
					try {
						String[] qx = q.split("=");
						String q_name = java.net.URLDecoder.decode(qx[0], StandardCharsets.UTF_8.name());
						String q_value = java.net.URLDecoder.decode(qx[1], StandardCharsets.UTF_8.name());
						query_string.put(q_name, q_value);
					} catch (Exception ex) { }
				}
			}
		} catch (Exception ex) { }
		return query_string;
	}

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		
	}

	private void submit_result(MessageContext messageContext, Result result) {
		int response_code = result.statusCode;
		if (response_code < 199) {
			response_code = 500;
			JsonObject er = new JsonObject();
			er.addProperty("message", "Internal server error");
			messageContext.setProperty("wresponse", er.toString());
		} else {
			messageContext.setProperty("wresponse", result.content);
		}
		messageContext.setProperty("ftpStatusCode", response_code);
	}

	@Override
	public boolean mediate(MessageContext messageContext) {
		
		try {

			org.apache.axis2.context.MessageContext axis2MessageCtx = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
			
			Map<String, String> headers = extract_headers(axis2MessageCtx);
			
			Map<String, String> query_params = extract_query_params(messageContext);
			
			String path = get_request_path(messageContext);
			
			String method = get_request_method(messageContext);
			
			Result result = new FunctionHandler().getResult(path, method, headers, query_params);
			
			submit_result(messageContext, result);
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
			
			submit_result(messageContext, new Result());
			
		}
		
		return true;
	}
}