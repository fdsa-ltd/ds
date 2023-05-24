package cn.zhumingwu.dataswitch.core.job.enums;

/**
 * 结果枚举接口
 *
 * @date 2019/2/13
 */
public interface ResultCode {

    /**
     * 获取状态编码
     *
     * @return 编码
     */
    int getCode();

    /**
     * 获取提示信息
     *
     * @return 提示信息
     */
    String getMessage();

}
