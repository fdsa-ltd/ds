package ltd.fdsa.job.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_post_image")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class PostImages extends BaseEntity<Integer> {
    @Id
    @Column(name = "post_image_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "url")
    private String url;
}
