package fr.hayj.basics.python;

import fr.hayj.basics.Basics;
import org.apache.commons.io.FileUtils;
import py4j.ClientServer;
import py4j.GatewayServer;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import py4j.reflection.ReflectionUtil;
import py4j.reflection.RootClassLoadingStrategy;

import static fr.hayj.basics.Basics.regexExtract;
import static fr.hayj.basics.Basics.resourceToString;

public class Py4JPythonScriptExecutor extends PythonScriptExecutor {

    protected File tempFile;
    protected Class py4jInterface;
    protected Integer javaPort;
    protected Integer pythonPort;
    protected ClientServer clientServer;
    protected Object entryPoint;
    public static enum RESOURCE_TYPE {FILE_PATH, SCRIPT_CONTENT, INTERNAL_RESOURCE};
    protected final static String PYTHON_TEMPLATE_PATH = "py4j-template.py";


    public Py4JPythonScriptExecutor(String pythonPath, String pathOrScript, String parameters, RESOURCE_TYPE resourceType, Class py4jInterface) throws IOException {
        super(pythonPath, null);
        // These 2 lines solve the "interface X is not visible from class loader" exception:
        RootClassLoadingStrategy rmmClassLoader = new RootClassLoadingStrategy();
        ReflectionUtil.setClassLoadingStrategy(rmmClassLoader);
        String script = pathOrScript;
        switch(resourceType)
        {
            case FILE_PATH:
                script = Basics.fileToString(pathOrScript);
                break;
            case INTERNAL_RESOURCE:
                script = Basics.resourceToString(pathOrScript);
                break;
        }
        // System.out.println(script);
        script = script + "\n\n" + resourceToString(PYTHON_TEMPLATE_PATH);
        this.tempFile = File.createTempFile("py4j-script", ".py");
        Basics.writeStringToFile(this.tempFile, script);
        this.scriptPath = this.tempFile.getPath();
        this.py4jInterface = py4jInterface;
    }

    public Py4JPythonScriptExecutor(String pythonPath, String scriptPath, RESOURCE_TYPE resourceType, Class py4jInterface) throws IOException {
        this(pythonPath, scriptPath, null, resourceType, py4jInterface);
    }

    @Override
    public synchronized void start() throws IOException, InterruptedException {
        super.start();
        String output = null;
        int iterations = 0;
        while(this.javaPort == null && this.pythonPort == null && iterations < 20)
        {
            Thread.sleep(100);
            List<String> lines = this.getOutputLines();
            output = String.join("\n", lines);
            for(int i = 0 ; i < lines.size() ; i++)
            {
                String line = lines.get(i);
                if(line.startsWith("java-port"))
                {
                    this.javaPort = Integer.parseInt(regexExtract(line, "java-port: (\\d+)", 1));
                }
                else if(line.startsWith("python-port"))
                {
                    this.pythonPort = Integer.parseInt(regexExtract(line, "python-port: (\\d+)", 1));
                }
            }
            iterations++;
        }

        if(this.pythonPort == null || this.pythonPort == null)
            throw new IOException("The underlying python script didn't give a free port:\n" + output);

        this.clientServer = new ClientServer(this.javaPort, GatewayServer.defaultAddress(), this.pythonPort,
                GatewayServer.defaultAddress(), GatewayServer.DEFAULT_CONNECT_TIMEOUT,
                GatewayServer.DEFAULT_READ_TIMEOUT, ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(), null);
        // We get an entry point from the Python side
        this.entryPoint = clientServer.getPythonServerEntryPoint(new Class[] { this.py4jInterface });
    }

    public Object getEntryPoint() {
        // In case you get a Connection refused related to the entry point,
        // it's probably because the python script finished on an error
        // and doesn't start the py4j gateway server:
        return entryPoint;
    }

    @Override
    public synchronized void stop() {
        super.stop();
        this.clientServer.shutdown();
        this.tempFile.delete();
    }
}
