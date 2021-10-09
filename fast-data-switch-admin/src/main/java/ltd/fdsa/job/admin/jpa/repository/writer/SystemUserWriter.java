package ltd.fdsa.job.admin.jpa.repository.writer;

import org.springframework.stereotype.Repository;
import ltd.fdsa.job.admin.jpa.entity.SystemUser;
import ltd.fdsa.database.jpa.repository.writer.WriteRepository;
@Repository
public interface SystemUserWriter extends WriteRepository<SystemUser, Integer> {

}
