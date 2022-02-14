package ltd.fdsa.job.admin.repository;


import ltd.fdsa.job.admin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserRepository extends JpaRepository<User, Integer> {
}
