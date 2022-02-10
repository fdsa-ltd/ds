package ltd.fdsa.job.admin.repository;


import ltd.fdsa.job.admin.entity.JobLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLogReportRepository extends JpaRepository<JobLogReport, Integer> {
}
