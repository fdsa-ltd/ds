package ltd.fdsa.ds.core.job.handler;


import ltd.fdsa.ds.core.job.enums.HttpCode;
import ltd.fdsa.ds.core.job.model.Result;

import java.util.Map;

public interface JobHandler {

    /**
     * success
     */
    public static final Result<Object> SUCCESS = Result.success();
    /**
     * fail
     */
    public static final Result<Object> FAIL = Result.fail(500, null);
    /**
     * fail timeout
     */
    public static final Result<Object> FAIL_TIMEOUT = Result.fail(HttpCode.REQUEST_TIMEOUT);


    Result<Object> execute(Map<String, String> context) ;

    /**
     * init handler, invoked when JobThread init
     */
    default void init() {

    }

    /**
     * destroy handler, invoked when JobThread destroy
     */
    default void destroy() {
    }
}
