package ltd.fdsa.ds.api.job.log;

import ltd.fdsa.ds.api.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;

public class JobLogger {
    private static Logger logger = LoggerFactory.getLogger("logger");

    /**
     * [LogTime]-[ClassName]-[MethodName]-[LineNumber]-[ThreadName] msg";
     */
    private static String LogFormatPattern = "[{}]-[{}]-[{}]-[{}] {}";

    /**
     * append log
     *
     * @param callInfo
     * @param appendLog
     */
    private static void logDetail(StackTraceElement callInfo, String appendLog) {
        String formatAppendLog = MessageFormat.format(LogFormatPattern,
                DateUtil.formatDateTime(new Date()),
                callInfo.getClassName(),
                callInfo.getLineNumber(),
                Thread.currentThread().getName(), appendLog);
        // append log
        String logFileName = JobFileAppender.contextHolder.get();
        if (logFileName != null && logFileName.trim().length() > 0) {
            JobFileAppender.appendLog(logFileName, formatAppendLog);
        } else {
            logger.info(">>>>>>>>>>> {}", formatAppendLog);
        }
    }

    /**
     * append log with pattern
     *
     * @param appendLogPattern   like "aaa {} bbb {} ccc"
     * @param appendLogArguments like "111, true"
     */
    public static void log(String appendLogPattern, Object... appendLogArguments) {
         String appendLog =  MessageFormat.format(appendLogPattern, appendLogArguments);
        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        logDetail(callInfo, appendLog);
    }

    /**
     * append exception stack
     *
     * @param e
     */
    public static void log(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String appendLog = stringWriter.toString();
        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        logDetail(callInfo, appendLog);
    }
}
