package ltd.fdsa.job.admin.repository;


import ltd.fdsa.job.admin.entity.JobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLogRepository extends JpaRepository<JobLog, Integer> {
}
