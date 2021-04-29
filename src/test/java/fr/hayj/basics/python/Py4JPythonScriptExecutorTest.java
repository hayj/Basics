package fr.hayj.basics.python;

import fr.hayj.basics.Basics;

import java.util.ArrayList;

public class Py4JPythonScriptExecutorTest {
    public static void main(String[] args) throws Exception {
        String envPath = "/home/hayj/Programs/anaconda3/envs/main/bin/python";

        // System.out.println(Basics.resourceToString("py4j-test.py"));

        String pythonScript = "/home/hayj/Workspace/Python/Octopeek/scraping-confidence/scrapingconfidence/py4jserver.py";
        // String pythonScript = "py4j-test.py";
        Py4JPythonScriptExecutor server = new Py4JPythonScriptExecutor(envPath, pythonScript,
                Py4JPythonScriptExecutor.RESOURCE_TYPE.FILE_PATH, LogList.class);
        server.start();
        LogList scrapingConfidence = (LogList) server.getEntryPoint();
        ArrayList array = new ArrayList();
        array.add("t");
        array.add("y");
        array.add("y");
        array.add(null);
        array.add(1.2);
        array.add(42.0);
        array.add(42);
        System.out.println(scrapingConfidence.logList(array, "title"));
        server.stop();
    }
}
