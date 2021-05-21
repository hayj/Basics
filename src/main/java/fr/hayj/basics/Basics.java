package fr.hayj.basics;

import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonArray;

public class Basics {

    public static void main (String[] args){

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
            while ((line = r.readLine()) != null)
            {
                if(trim)
                    line = line.trim();
                if(line.length() > 0)
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

    public static String regexExtract(String input, String regex, int group)
    {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        if (m.find() && m.groupCount() >= group)
        {
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
//        String content = Files.readString(Paths.get(path));
//        return content;
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

    public static void print(Object o)
    {
        System.out.println(o);
    }
    public static void p(Object o)
    {
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
}
