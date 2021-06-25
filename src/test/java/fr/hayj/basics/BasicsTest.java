package fr.hayj.basics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class BasicsTest extends TestCase
{



	private static String getString(boolean go)
	{
		if(go)
		{
			return "aaa";
		}
		else
		{
			return null;
		}
	}

	private static int log(Object o)
	{
		if(o instanceof Exception)
		{
			return 1;
		}
		else if(o instanceof String)
		{
			return 2;
		}
		else
		{
			return 3;
		}
	}

	public void testGetStackTrace()
	{
		boolean gotAnException = false;
		try
		{
			String aaa = getString(false);
			aaa.trim();
		}
		catch (Exception e)
		{
			Object o = (Object) e;
			Exception o2 = (Exception) o;
			gotAnException = true;
			String stackTrace = Basics.getStackTrace(o2);
			assertTrue(stackTrace.length() > 200);
			assertTrue(log(e) == 1);
			assertTrue(log("rrr") == 2);
			assertTrue(log(new Integer(5)) == 3);
		}
		assertTrue(gotAnException);
	}

	public void testFileToJsonArray() throws IOException
	{
		String json = "[{\"a\": 1}, {\"a\": 2}]";
		File file = Basics.stringToTmpFile(json);
		String path = file.getAbsolutePath();
		JsonArray array = Basics.fileToJsonArray(path);
		this.assertTrue(array.size() > 1);
		array = Basics.fileToJsonArray(file);
		this.assertTrue(array.size() > 1);
	}

	public void testPrimitive() throws IOException, URISyntaxException
	{
		JsonArray json = (JsonArray) Basics.resourceToJsonArray("sample.json");
		Basics.p(json);
		for(int i = 0; i < json.size(); i++)
		{
			JsonElement element = json.get(i);
			Object o = Basics.jsonElementToObject(element);
			Basics.p(o);
			if(o != null)
				Basics.p(o.getClass().getName());
			Basics.p("\n");
		}
	}
	
	private static void test3()
	{
		String value = "a:0.3,b:55,v:2.3";
		String[] array = value.split(",");
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Double> values = new ArrayList<Double>();
		for(int i = 0; i < array.length; i++)
		{
			String[] currentKeyValue = array[i].split(":");
			keys.add(currentKeyValue[0]);
			values.add(Double.parseDouble(currentKeyValue[1]));
		}
		double sum = 0;
		for(int i = 0; i < values.size(); i++)
		{
			sum += values.get(i);
		}
		for(int i = 0; i < values.size(); i++)
		{
			values.set(i, values.get(i) / sum);
		}
		HashMap<String, Double> data = new HashMap<String, Double>();
		for(int i = 0; i < values.size(); i++)
		{
			data.put(keys.get(i), values.get(i));
		}
		Basics.p(data);
	}
	
	private static void test1()
	{
		String value = "{\"a\": \"a\", \"b\": {\"e\": \"e\"}}";
		JsonObject o = Basics.stringToJsonObject(value);
		Basics.p(o);
	}
}