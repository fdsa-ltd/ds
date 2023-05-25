package cn.zhumingwu.dataswitch.core.job;

import cn.zhumingwu.dataswitch.core.job.log.JobFileAppender;
import cn.zhumingwu.dataswitch.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.Date;
import java.util.Map;

/**
 *  job context
 *
 * @author zhumingwu
 */
public class JobContext {
    private static Logger logger = LoggerFactory.getLogger(JobFileAppender.class);

    /**
     * job id
     */
    private final long jobId;

    /**
     * job param
     */
    private final Map<String,String> jobParam;

    // ---------------------- for log ----------------------

    /**
     * job log filename
     */
    private final String jobLogFileName;


    // ---------------------- for handle ----------------------

    /**
     * handleCode：The result status of job execution
     *
     *      200 : success
     *      500 : fail
     *      502 : timeout
     *
     */
    private int handleCode;

    /**
     * handleMsg：The simple log msg of job execution
     */
    private String handleMsg;


    public JobContext(long jobId, Map< String,String> jobParam, String jobLogFileName ) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;

    }

    public long getJobId() {
        return jobId;
    }

    public Map< String,String> getJobParam() {
        return jobParam;
    }

    public String getJobLogFileName() {
        return jobLogFileName;
    }

    public  boolean log(String appendLogPattern, Object ... appendLogArguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
        String appendLog = ft.getMessage();
        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }
    /**
     * append log
     *
     * @param callInfo
     * @param appendLog
     */
    private   boolean logDetail(StackTraceElement callInfo, String appendLog) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(DateUtil.formatDateTime(new Date())).append(" ")
                .append("["+ callInfo.getClassName() + "#" + callInfo.getMethodName() +"]").append("-")
                .append("["+ callInfo.getLineNumber() +"]").append("-")
                .append("["+ Thread.currentThread().getName() +"]").append(" ")
                .append(appendLog!=null?appendLog:"");
        String formatAppendLog = stringBuffer.toString();


        String logFileName = this.getJobLogFileName();

        if (logFileName!=null && logFileName.trim().length()>0) {
            JobFileAppender.appendLog(logFileName, formatAppendLog);
            return true;
        } else {
            logger.info(">>>>>>>>>>> {}", formatAppendLog);
            return false;
        }
    }



    public void setHandleCode(int handleCode) {
        this.handleCode = handleCode;
    }

    public int getHandleCode() {
        return handleCode;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    // ---------------------- tool ----------------------

    private static InheritableThreadLocal< JobContext> contextHolder = new InheritableThreadLocal<JobContext>(); // support for child thread of job handler)

    public static void setJobContext(long jobId, Map< String,String> jobParam, String jobLogFileName ){
        contextHolder.set(new JobContext(jobId,jobParam,jobLogFileName));
    }

    public static JobContext getJobContext(){
        return contextHolder.get();
    }

}