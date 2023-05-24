package cn.zhumingwu.dataswitch.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_post")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Post extends BaseEntity<Integer> {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;




    @Column(name = "category")
    private String category;

    @Column(name = "location")
    private String location;
    @Column(name = "lat")
    private float lat;
    @Column(name = "lng")
    private float lng;


    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;
}
