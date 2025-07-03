package com.pttdigital.CosmosDB;

public class Result {
	public int statusCode = -1;
	public String content = null;
	public Exception exception = new Exception("default exception");
	
	public static Result getResult(Exception ex) {
		Result result = new Result();
		result.exception = ex;
		return result;
	}
}