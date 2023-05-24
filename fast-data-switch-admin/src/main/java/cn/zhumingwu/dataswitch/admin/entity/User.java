package cn.zhumingwu.dataswitch.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User extends BaseEntity<Integer> {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String password; // 密码
    // type 角色：0-普通用户、1-管理员
    @Column(name = "email_address")
    private String email;
    @Column(name = "locked")
    private boolean locked;
    @Column(name = "lock_time")
    private LocalDateTime lockTime;
    private String salt;
}
