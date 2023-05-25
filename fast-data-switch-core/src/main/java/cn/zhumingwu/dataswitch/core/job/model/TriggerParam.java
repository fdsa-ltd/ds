package cn.zhumingwu.dataswitch.core.job.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Data
@ToString
public class TriggerParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private Long jobId;

    private String executorHandler;
    private Map<String,String> executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;

    private Long logId;
    private long logDateTime;
    private int broadcastIndex;
    private int broadcastTotal;


}
