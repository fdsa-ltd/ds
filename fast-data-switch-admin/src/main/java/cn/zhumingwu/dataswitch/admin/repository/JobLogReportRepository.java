package cn.zhumingwu.dataswitch.admin.repository;


import cn.zhumingwu.dataswitch.admin.entity.JobLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLogReportRepository extends JpaRepository<JobLogReport, Integer> {
}
