package OnionHarvester.Presenter.Service.logger.TextLoggers;

import OnionHarvester.Presenter.Service.logger.LoggerService;
import OnionHarvester.Presenter.Service.logger.WriterType;
import OnionHarvester.Variables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileWriterService extends TextLoggerService {
    private static Map<WriterType, FileWriterService> writers = new ConcurrentHashMap<>();

    private WriterType _type;
    private OutputStreamWriter fw;

    private FileWriterService(WriterType name, boolean append) {
        this._type = name;
        try {
            File f = new File(Variables.outputFolder);
            f.mkdirs();
            fw = new OutputStreamWriter(new FileOutputStream(f.getPath() + File.separator + name + ".txt", append), Charset.forName("UTF-8"));
        } catch (Exception ex) {
            LoggerService.getInstance().Log(FileWriterService.class, "Cannot open " + name + ".txt file.", LoggerService.LogLevel.Error);
        }
    }

    public synchronized static FileWriterService getInstance(WriterType name) {
        FileWriterService result = writers.get(name);

        if (result == null) {
            FileWriterService fws = new FileWriterService(name, name == WriterType.Log);
            result = writers.putIfAbsent(name, fws);

            if (result == null) {
                result = fws;
            }
        }

        return result;
    }

    public WriterType get_type() {
        return _type;
    }

    @Override
    public void write(String message) {
        try {
            fw.write(message + "\r\n");
            fw.flush();
        } catch (Exception ex) {
            LoggerService.getInstance().Log(FileWriterService.class, "Cannot write to " + get_type() + ".", LoggerService.LogLevel.Error);
        }
    }

    @Override
    public void done() {
        try {
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            LoggerService.getInstance().Log(FileWriterService.class, "Cannot close " + get_type() + ".", LoggerService.LogLevel.Error);
        }
    }
}
