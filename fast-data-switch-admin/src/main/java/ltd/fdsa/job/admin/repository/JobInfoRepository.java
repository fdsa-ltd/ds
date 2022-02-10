package ltd.fdsa.job.admin.repository;


import ltd.fdsa.job.admin.entity.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Integer> {
}
