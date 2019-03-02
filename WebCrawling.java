package com.test.ge;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebCrawling {

	//to store successfully crawled links
	private HashSet<String> success;
	//to store all looped links
	private HashMap<String,Integer> loopedLinks;
	//to store skipped links
	private HashSet<String> skipped;
	//to store error links
	private HashSet<String> error;

	public WebCrawling() {
		success = new LinkedHashSet<String>();
		loopedLinks = new HashMap<String,Integer>();
		skipped = new LinkedHashSet<String>();
		error = new LinkedHashSet<String>();
	}

	public void getAddress(JSONArray pages) {

		for (Object pageObject : pages) {			
			JSONObject addressObj = (JSONObject) pageObject;
			String address = (String) addressObj.get("address");		
			if (!success.contains(address)) { // add root address to success set
				success.add(address);
				if (!loopedLinks.containsKey(address)) { // add root address to loopedLinks set
					loopedLinks.put(address,1);
				}
			}
			JSONArray addLinks = (JSONArray) addressObj.get("links");
			for (Object objectLinks : addLinks) {// for each link in that root address
				getPageLinks(objectLinks.toString());
			}
		}
		
		for(Map.Entry<String, Integer> entry:loopedLinks.entrySet()){// for each entry in loopedLinks map
			
			if(entry.getValue() > 1){// if entry exists more than once, it is skipped
				skipped.add(entry.getKey());
			}else if(entry.getValue() == 1 && !success.contains(entry.getKey())){
				error.add(entry.getKey());
			}
		}
		
		System.out.println("total success--> " + success); //result
		System.out.println("total skipped--> " + skipped); //result
		System.out.println("total error--> " + error); //result
	}

	public void getPageLinks(String URL) {//check and add to loopedLinks		
		if (!loopedLinks.containsKey(URL)) {
			loopedLinks.put(URL,1);
		} else {
			loopedLinks.put(URL,loopedLinks.get(URL)+1);
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		Object obj = parser
				.parse(new FileReader("internet_1.json"));//path for file
		/*Object obj = parser
				.parse(new FileReader("./internet_2.json"));*/
		JSONArray array = new JSONArray();
		array.add(obj);

		for (Object o : array) {
			JSONObject person = (JSONObject) o;
			JSONArray arrays = (JSONArray) person.get("pages");
			new WebCrawling().getAddress(arrays);// pass each page object of array
		}
	}

}
