package ltd.fdsa.ds.api.model;

import lombok.*;
import ltd.fdsa.ds.api.container.Plugin;
import ltd.fdsa.ds.api.props.Props;

import java.io.Serializable;
import java.util.Map;

@Data
@ToString
@Builder
public class NewService  implements Serializable {
    private static final long serialVersionUID = 42L;
    /*
    唯一
     */
    private String id;
    /*
     * 同类
     * */
    private String name;

    private String value;
    /*
     * 可处理的插件
     * */
    private Map<String, Props> handles;
    private String url;
    private Map<String, String> meta;

}
