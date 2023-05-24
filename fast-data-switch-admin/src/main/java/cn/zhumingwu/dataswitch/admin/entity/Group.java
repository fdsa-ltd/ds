package cn.zhumingwu.dataswitch.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_group")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Group extends BaseEntity<Integer> {
    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
}
