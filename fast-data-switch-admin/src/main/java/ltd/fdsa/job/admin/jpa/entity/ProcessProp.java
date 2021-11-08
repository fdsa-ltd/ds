package ltd.fdsa.job.admin.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ltd.fdsa.database.entity.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_job_process_prop")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ProcessProp extends BaseEntity<Integer> {
    @Id
    @Column(name = "process_prop_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "process_id")
    private Integer processId;
    @Column(name = "value")
    private String value;
}
