package ltd.fdsa.job.admin.repository;


import ltd.fdsa.job.admin.entity.JobGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobGroupRepository extends JpaRepository<JobGroup, Integer> {

}

