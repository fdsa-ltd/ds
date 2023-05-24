package cn.zhumingwu.dataswitch.core.job.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import cn.zhumingwu.dataswitch.core.model.Result;


import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HandleCallbackParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private int logId;
    private long logDateTim;

    private Result<Object> executeResult;

}
