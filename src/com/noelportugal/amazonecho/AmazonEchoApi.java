/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noelportugal.amazonecho;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author nportuga
 */
public class AmazonEchoApi {

    private final String BASE_URL;
    private final String USERNAME;
    private final String PASSWORd;
    private final HttpClient httpclient = HttpClientBuilder.create().build();
    private String LastText = "";
    //static final Logger log= new Logger();
    

    public AmazonEchoApi(String base_url, String username, String password) {
        
        this.BASE_URL = base_url;
        this.USERNAME = username;
        this.PASSWORd = password;
    }

    public String httpGet(String url) {
        String output = "";
        try {

            HttpGet httpGet = new HttpGet(BASE_URL + url);
            httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
            HttpResponse httpResponse = httpclient.execute(httpGet);
            StatusLine responseStatus = httpResponse.getStatusLine();
            //  System.out.println(responseStatus);
            int statusCode = responseStatus.getStatusCode();
            if (statusCode == 200) {
                httpResponse.getEntity();
                output = new BasicResponseHandler().handleResponse(httpResponse);
            }
        } catch (Exception e) {
            System.err.println("httpGet Error: " + e.getMessage());
        }

        return output;

    }

    public boolean httpLogin() {

        try {
            String output = httpGet("");

            Document doc = Jsoup.parse(output);
            Elements forms = doc.select("form");
            String action = forms.attr("action");

            if (action.isEmpty()) {
                return false;
            }

            Elements hidden = doc.select("input[type=hidden]");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("email", USERNAME));
            nvps.add(new BasicNameValuePair("password", PASSWORd));
            nvps.add(new BasicNameValuePair("create", "0"));

            for (Element el1 : hidden) {
                nvps.add(new BasicNameValuePair(el1.attr("name"), el1.attr("value")));
            }

            HttpPost httpPost = new HttpPost(action);
            httpPost.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
            httpPost.setHeader(HttpHeaders.REFERER, BASE_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse httpResponse = httpclient.execute(httpPost);

            httpResponse.getEntity();
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                EntityUtils.consume(entity);
            }
            System.out.println("Login successful");
            return true;

        } catch (Exception e) {
            System.out.println("Login Error:" + e.getMessage());
            return false;
        }
    }

    public boolean checkItemId(String itemId) {
        boolean ret = false;
        if (itemId.equalsIgnoreCase(LastText)) {
            ret = true;
        }

        LastText = itemId;
        return ret;
    }

    public String getLatestTodo() throws IOException {

        String output = httpGet("/api/todos?type=TASK&size=1");
        //   System.out.println(output);
        // Parse JSON
        Object obj = JSONValue.parse(output);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray values = (JSONArray) jsonObject.get("values");
        JSONObject item = (JSONObject) values.get(0);
//System.out.println(item);
        // Get text and itemId
        String text = item.get("text").toString();
        String itemId = item.get("itemId").toString();

        if (!checkItemId(itemId)) {

            return text;
        } else {
            return null;
        }
    }

    public String getLatestCards() throws IOException {

        String output = httpGet("/api/cards");
        System.out.println(output);
        // Parse JSON
        Object obj = JSONValue.parse(output);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray values = (JSONArray) jsonObject.get("cards");
//System.out.println(values.size()); 

        JSONObject item = (JSONObject) values.get(0);

//System.out.println(item.toString());
        // Get text and itemId
        String text = item.get("title").toString();
        String itemId = item.get("title").toString();
//System.out.println(text);

        if (!checkItemId(itemId)) {

            return text;
        } else {
            return null;
        }
    }

    public String getLatestHistory() throws IOException {
        String output = httpGet("/api/activities?startTime=&size=1&offset=-1");
      //System.out.println(output);
        

        // Parse JSON
        Object obj = JSONValue.parse(output);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray values = (JSONArray) jsonObject.get("activities");
        JSONObject item = (JSONObject) values.get(0);
        String text = item.get("description").toString();
//Get the summary text
        Object obj2 = JSONValue.parse(text);
        JSONObject jsonObject2 = (JSONObject) obj2;
        text = jsonObject2.get("summary").toString();

        if (!checkItemId(text)) {

            return text;
        } else {
            return null;
        }
    }
}
