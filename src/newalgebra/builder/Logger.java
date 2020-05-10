package newalgebra.builder;

import java.io.Serializable;

public class Logger implements Serializable {

    public enum LoggerMode{
        ERRORS,
        NORMAL,
        WARNINGS

    }

    private static Logger systemLogger = new Logger();

    private boolean printErrors;
    private boolean printNormal;
    private boolean printWarnings;

    /**
     * enables the given mode
     * @param loggerMode
     */
    public void enable(LoggerMode loggerMode){
        switch (loggerMode){
            case ERRORS: printErrors = true; break;
            case NORMAL: printNormal = true; break;
            case WARNINGS: printWarnings = true; break;
        }
    }

    /**
     * enables all
     */
    public void enable(){
        printErrors = true;
        printNormal = true;
        printWarnings = true;
    }

    /**
     * disables the given mode
     * @param loggerMode
     */
    public void disable(LoggerMode loggerMode){
        switch (loggerMode){
            case ERRORS: printErrors = false; break;
            case NORMAL: printNormal = false; break;
            case WARNINGS: printWarnings = false; break;
        }
    }

    /**
     * disables all
     */
    public void disable(){
        printErrors = false;
        printNormal = false;
        printWarnings = false;
    }


    public void addError(String error){
        if(printErrors){
            System.err.println("[ERROR]"+error);
        }
    }

    public void addWarning(String error){
        if(printWarnings){
            System.out.println("[WARNING]"+error);
        }
    }

    public void addNormal(StringBuilder normal){
        if(printNormal){
            System.out.println(normal);
        }
    }

    public static Logger getLogger(){
        return systemLogger;
    }

    static {
        Logger.getLogger().enable();
    }
}
