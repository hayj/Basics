package fr.hayj.basics;

import com.google.gson.JsonArray;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class BasicsTest extends TestCase {

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
        if (o instanceof Exception) {
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
        catch(Exception e)
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

    public void testFileToJsonArray() throws IOException {
        String json = "[{\"a\": 1}, {\"a\": 2}]";
        File file = Basics.stringToTmpFile(json);
        String path = file.getAbsolutePath();
        JsonArray array = Basics.fileToJsonArray(path);
        this.assertTrue(array.size() > 1);
        array = Basics.fileToJsonArray(file);
        this.assertTrue(array.size() > 1);
    }
}