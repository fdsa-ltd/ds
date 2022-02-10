package ltd.fdsa.job.admin.service.impl;

import lombok.var;
import ltd.fdsa.job.admin.entity.JobGroup;
import ltd.fdsa.job.admin.entity.JobRegistry;
import ltd.fdsa.job.admin.repository.JobGroupRepository;
import ltd.fdsa.job.admin.repository.JobRegistryRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Resource
    private JobGroupRepository jobGroupRepository;
    @Resource
    private JobRegistryRepository jobRegistryRepository;

    public List<JobGroup> findByAddressList(int i) {
        return this.jobGroupRepository.findAll();
    }

    public List<Integer> findDead(int deadTimeout, Date date) {
        var list = this.jobRegistryRepository.findAll();
        return list.stream().map(x -> {
            return x.getId();
        }).collect(Collectors.toList());
    }

    public List<JobRegistry> findAll(int deadTimeout, Date date) {
        return this.jobRegistryRepository.findAll();
    }
}
