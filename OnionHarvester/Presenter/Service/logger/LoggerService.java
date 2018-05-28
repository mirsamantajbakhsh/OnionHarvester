package OnionHarvester.Presenter.Service.logger;

import OnionHarvester.Helper.Helper;
import OnionHarvester.Presenter.Service.logger.TextLoggers.FileWriterService;

public class LoggerService {
    private static LoggerService ourInstance = new LoggerService();

    private LoggerService() {
        System.setProperty("org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY", "ERROR");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    public static synchronized LoggerService getInstance() {
        return ourInstance;
    }

    public void Log(Class className, String Message, LogLevel Level) {
        FileWriterService.getInstance(WriterType.Log).write("[" + Level.name() + "] " + Message + " <" + className.getName() + "> @ " + Helper.getCurrentTime());
    }

    public enum LogLevel {Debug, Warning, Info, Error}
}
