package com.pexat.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

public class SimpleHttp
{

	public static String get(String url)
	{
		BufferedReader reader = null;
		String page = "";

		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			request.setURI(new URI(url));

			HttpResponse response = client.execute(request);

			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			StringBuffer buffer = new StringBuffer("");
			String line = "";
			String newline = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null)
			{
				buffer.append(line + newline);
			}

			reader.close();

			page = buffer.toString();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return page;
	}

	public static void post(String url, HttpEntity data)
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);

		try
		{
			httpost.setEntity(data);
			httpclient.execute(httpost);
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void put(String url, HttpEntity data)
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPut httpput = new HttpPut(url);

		try
		{
			httpput.setEntity(data);
			httpclient.execute(httpput);
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
