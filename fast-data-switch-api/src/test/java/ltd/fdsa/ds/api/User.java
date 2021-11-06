package ltd.fdsa.ds.api;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class User {
    Integer id;
    String name;
    List<String> types;
    User[] friends;
    Double age;
    Date createTime;
}
