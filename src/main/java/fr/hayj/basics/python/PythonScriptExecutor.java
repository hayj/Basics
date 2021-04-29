package fr.hayj.basics.python;

import org.apache.commons.exec.*;
import org.apache.commons.exec.LogOutputStream;

import java.io.IOException;
import java.util.ArrayList;

public class PythonScriptExecutor {
    public static class OutputStreamCollector extends LogOutputStream {
        private final ArrayList<String> lines = new ArrayList<String>();
        @Override protected void processLine(String line, int level) {
            lines.add(line);
        }
        public ArrayList<String> getLines() {
            return lines;
        }
    }

    protected String pythonPath;
    protected String scriptPath;
    protected String parameters;
    protected Executor executor;
    protected boolean started = false;
    protected OutputStreamCollector outputStream;

    public PythonScriptExecutor(String pythonPath, String scriptPath) {
        this.pythonPath = pythonPath;
        this.scriptPath = scriptPath;
    }

    public PythonScriptExecutor(String pythonPath, String scriptPath, String parameters) {
        this.pythonPath = pythonPath;
        this.scriptPath = scriptPath;
        this.parameters = parameters;
    }

    synchronized public void start() throws IOException, InterruptedException {
        if(!this.started) {
            this.started = true;
            String command = pythonPath + " " + scriptPath;
            if(parameters != null)
            {
                command = command + " " + parameters;
            }
            CommandLine commandline = CommandLine.parse(command);
            this.outputStream = new OutputStreamCollector();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            ExecuteWatchdog watchdog = new ExecuteWatchdog(30*24*60*1000);
            this.executor = new DefaultExecutor();
            executor.setStreamHandler(streamHandler);
            executor.setExitValue(1);
            executor.setWatchdog(watchdog);
            executor.execute(commandline, resultHandler);
        }
    }

    synchronized public void stop()
    {
        if(this.started) {
            this.executor.getWatchdog().destroyProcess();
            this.started = false;
        }
    }

    synchronized public ArrayList<String> getOutputLines()
    {
        return (ArrayList<String>) this.outputStream.getLines();
    }
}
