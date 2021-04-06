package fr.hayj.basics;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

public class Basics {
    public static ArrayList<String> fileToList(String path) throws FileNotFoundException {
        ArrayList<String> list = new ArrayList<String>();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(path);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = r.readLine()) != null)
            {
                line = line.trim();
                if(line.length() > 0)
                {
                    list.add(line);
                }
            }
            System.out.println(list);
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
}
