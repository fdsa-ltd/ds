package cn.zhumingwu.dataswitch.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "job_registry")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class JobRegistry extends BaseEntity<Integer> {

    @Id
    @Column(name = "registry_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String registryGroup;
    private String registryKey;
    private String registryValue;

    private String dead;
    private String findAll;
}
