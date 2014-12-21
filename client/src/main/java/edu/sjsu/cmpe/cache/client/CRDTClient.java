package edu.sjsu.cmpe.cache.client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

public class CRDTClient {
	private ConcurrentHashMap<String, Integer> getValuesMap = new ConcurrentHashMap<String, Integer>();
	private AtomicInteger successCounter = new AtomicInteger();
	private int getSuccessCounter ;
	public void insertKeys(String string,
			String string2, String string3) {
		String []servers =new String[3];
		servers[0]=string;
		servers[1]=string2;
		servers[2]=string3;
		
		for(int i = 0; i < servers.length; i++){
			putAsync(1, "a",servers[i]);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(successCounter.get()<2){
			deleteAsync(1,string);
			deleteAsync(1,string3);
			deleteAsync(1,string2);
		}
	}
	
    public void putAsync(long key, String value,String url) {
    	
    	Future<HttpResponse<JsonNode>> future = Unirest.put(url + "/cache/{key}/{value}")
    			  .header("accept", "application/json")
    			  .routeParam("key", Long.toString(key))
    			  .routeParam("value", value)
    			  .asJsonAsync(new Callback<JsonNode>() {

    			    public void failed(UnirestException e) {
    			        System.out.println("The request has failed");
    			    }

    			    public void completed(HttpResponse<JsonNode> response) {
    			         int code = response.getStatus();
    			         Map<String, List<String>> headers = response.getHeaders();
    			         JsonNode body = response.getBody();
    			         InputStream rawBody = response.getRawBody();
    			         
    			         if(code==200){
    			        	 successCounter.incrementAndGet();
    			         }
    			    }

    			    public void cancelled() {
    			        System.out.println("The request has been cancelled");
    			    }

    			});
    }
    
    public void deleteAsync(long key,String url) {
    	Future<HttpResponse<JsonNode>> future = Unirest.delete(url + "/cache/{key}")
  			  .header("accept", "application/json")
  			  .routeParam("key", Long.toString(key))
  			  .asJsonAsync(new Callback<JsonNode>() {

  			    public void failed(UnirestException e) {
  			        System.out.println("The request has failed");
  			    }

  			    public void completed(HttpResponse<JsonNode> response) {
  			         int code = response.getStatus();
  			         Map<String, List<String>> headers = response.getHeaders();
  			         JsonNode body = response.getBody();
  			         InputStream rawBody = response.getRawBody();
  			         
  			         if(code==204){
  			        	 System.out.println("delete key success");
  			         }
  			    }

  			    public void cancelled() {
  			        System.out.println("The request has been cancelled");
  			    }

  			});

    }

	public void readKeys(String string, String string2, String string3) {
		// TODO Auto-generated method stub
		String []servers =new String[3];
		servers[0]=string;
		servers[1]=string2;
		servers[2]=string3;
		
		for(int i = 0; i < servers.length; i++){
			getAsync(1,servers[i]);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//if there are values inconsistent
		if(getValuesMap.contains(2)){
			putAsync(1,"b" , string);
			putAsync(1, "b", string2);
			putAsync(1, "b", string3);
		}
	}
	
	 public void getAsync(long key,String url) {
	    	Future<HttpResponse<JsonNode>> future = Unirest.get(url + "/cache/{key}")
	  			  .header("accept", "application/json")
	  			  .routeParam("key", Long.toString(key))
	  			  .asJsonAsync(new Callback<JsonNode>() {

	  			    public void failed(UnirestException e) {
	  			        System.out.println("The request has failed");
	  			    }

	  			    public void completed(HttpResponse<JsonNode> response) {
	  			         int code = response.getStatus();
	  			         Map<String, List<String>> headers = response.getHeaders();
	  			         JsonNode body = response.getBody();
	  			         InputStream rawBody = response.getRawBody();
	  			         String value = body.getObject().get("value").toString();
	  			         if(code==200){
	  			        	
	  			        	if(getValuesMap.isEmpty()){
	  			        		getValuesMap.put(value, 1);
	  			        	}else if(getValuesMap.containsKey(value)){
	  			        		Integer i =getValuesMap.remove(value);
	  			        		getValuesMap.put(value, i++);
	  			        	}else{
	  			        		getValuesMap.put(value, 1);
	  			        	}
	  			        	 System.out.println("get key success "+value);
	  			         }
	  			    }

	  			    public void cancelled() {
	  			        System.out.println("The request has been cancelled");
	  			    }

	  			});

	    }

	
}
