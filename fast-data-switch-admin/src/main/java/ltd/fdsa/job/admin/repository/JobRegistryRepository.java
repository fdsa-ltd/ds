package ltd.fdsa.job.admin.repository;

import ltd.fdsa.job.admin.entity.JobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JobRegistryRepository extends JpaRepository<JobRegistry, Integer> {

}
