package fr.hayj.basics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonArray;

import java.math.BigInteger;

public class Basics {

	public static void dumpNestedJsonArraysAndObjects(JsonObject o) {
		// java.lang.NoSuchMethodError: com.google.gson.JsonObject.keySet()
		// Solution:
		// https://stackoverflow.com/questions/31094305/java-gson-getting-the-list-of-all-keys-under-a-jsonobject
		Set<Map.Entry<String, JsonElement>> entries = o.entrySet();
		HashMap<String, String> hmap = new HashMap<String, String>();
		for (Map.Entry<String, JsonElement> entry : entries) {
			String key = entry.getKey();
			JsonElement element = o.get(key);
			if (element.isJsonArray() || element.isJsonObject()) {
				String dump = element.toString();
				hmap.put(key, dump);
			}
		}
		for (String key : hmap.keySet()) {
			String dump = hmap.get(key);
			o.remove(key);
			o.addProperty(key, dump);
		}
	}

	public static String textToHash(String text, String algorithm) throws NoSuchAlgorithmException {
		  final MessageDigest md = MessageDigest.getInstance(algorithm);
		  md.update(text.getBytes(StandardCharsets.UTF_8));
		  byte[] digest = md.digest();
		  final BigInteger number = new BigInteger(1, digest);
		  final String hexHash = number.toString(16);
		  return hexHash;

//		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
//		messageDigest.update(text.getBytes(StandardCharsets.UTF_8));
//		String stringHash = new String(messageDigest.digest());
//		return stringHash;
	}

	public static String textToHash(String text) throws NoSuchAlgorithmException {
		return textToHash(text, "MD5");
	}

	public static void main(String[] args) {
		String value = "{\"t\": [2, 3], \"a\": \"a\", \"c\": null, \"d\": 1, \"b\": {\"e\": \"e\"}}";
		JsonObject o = Basics.stringToJsonObject(value);
		Basics.p(o);
		dumpNestedJsonArraysAndObjects(o);
		Basics.p(o);
	}

	public static ArrayList<String> resourceToList(String path) throws FileNotFoundException {
		return resourceToList(path, true);
	}

	public static ArrayList<String> resourceToList(String path, boolean trim) throws FileNotFoundException {
		ArrayList<String> list = new ArrayList<String>();
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(path);
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = r.readLine()) != null) {
				if (trim)
					line = line.trim();
				if (line.length() > 0)
					list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	public static String regexExtract(String input, String regex, int group) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		if (m.find() && m.groupCount() >= group) {
			String theGroup = m.group(group);
			return theGroup;
		}
		return null;
	}

	public static List<String> fileToList(String path) throws IOException {
		Stream<String> lines = Files.lines(Paths.get(path));
		List<String> list = lines.collect(Collectors.toList());
		return list;
	}

	public static String fileToString(String path) throws IOException {
		Stream<String> lines = Files.lines(Paths.get(path));
		String content = lines.collect(Collectors.joining(System.lineSeparator()));
		return content;
		// For java 11:
		// String content = Files.readString(Paths.get(path));
		// return content;
	}

	public static String resourceToString(String path) throws FileNotFoundException {
		return String.join("\n", (List) Basics.resourceToList(path, false));
	}

	public static void writeStringToFile(File file, String script) throws IOException {
		FileUtils.writeStringToFile(file, script, Charset.defaultCharset());
	}

	public static void stringToFile(String text, String path) throws IOException {
		File file = new File(path);
		file.createNewFile();
		Basics.writeStringToFile(file, text);

	}

	public static File stringToTmpFile(String text) throws IOException {
		File tempFile = File.createTempFile("tmp-file-", ".unknown");
		Basics.writeStringToFile(tempFile, text);
		return tempFile;
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static void p(Object o) {
		Basics.print(o);
	}

	public static JsonArray resourceToJsonArray(String relativePath) throws FileNotFoundException, URISyntaxException {
		String absolutePath = Basics.getResourceFilePath(relativePath);
		return Basics.fileToJsonArray(absolutePath);
	}

	public static JsonArray fileToJsonArray(String path) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(br).getAsJsonArray();
		return array;
	}

	public static JsonArray fileToJsonArray(File file) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(br).getAsJsonArray();
		return array;
	}

	public static String request(String url) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet request = new HttpGet(url);
			try (CloseableHttpResponse response = httpClient.execute(request)) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return it as a String
					String result = EntityUtils.toString(entity);
					return result;
				}
			}
			httpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JsonObject stringToJsonObject(String json) {
		JsonParser parser = new JsonParser();
		return parser.parse(json).getAsJsonObject();
	}

	public static boolean fileExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static String getResourceFilePath(String relativePath) throws URISyntaxException {
		URL res = Basics.class.getClassLoader().getResource(relativePath);
		File file = Paths.get(res.toURI()).toFile();
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}

	public static String getResourceFilePath(String relativePath, Class targetClass) throws URISyntaxException {
		URL res = targetClass.getClassLoader().getResource(relativePath);
		File file = Paths.get(res.toURI()).toFile();
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}

	public static void sleep(double seconds) throws InterruptedException {
		Thread.sleep((int) seconds * 1000);
	}

	public static Object jsonElementToObject(JsonElement element) {
		JsonPrimitive p;
		try {
			p = element.getAsJsonPrimitive();
		} catch (Exception e) {
			return null;
		}
		if (p.isNumber()) {
			Double v = p.getAsNumber().doubleValue();
			if ((v == Math.floor(v)) && !Double.isInfinite(v))
				return p.getAsNumber().longValue();
			else
				return v;
		} else if (p.isBoolean()) {
			return p.getAsBoolean();
		} else if (p.isString()) {
			return p.getAsString();
		} else if (p.isJsonNull()) {
			return null;
		}
		return null;
	}
}
