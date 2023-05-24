package cn.zhumingwu.dataswitch.admin.controller;


import cn.zhumingwu.dataswitch.admin.repository.JobGroupRepository;
import cn.zhumingwu.dataswitch.admin.repository.JobInfoRepository;
import cn.zhumingwu.dataswitch.admin.repository.JobRegistryRepository;
import lombok.var;
import cn.zhumingwu.dataswitch.admin.entity.JobGroup;
import cn.zhumingwu.dataswitch.admin.entity.JobRegistry;
import cn.zhumingwu.dataswitch.core.job.enums.RegistryConfig;
import cn.zhumingwu.dataswitch.core.model.Result;
import cn.zhumingwu.dataswitch.core.util.I18nUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * job group controller
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

    @Resource
    public JobInfoRepository JobInfoDao;
    @Resource
    public JobGroupRepository jobGroupRepository;
    @Resource
    private JobRegistryRepository JobRegistryDao;

    @RequestMapping
    public String index(Model model) {

        // job group (executor)
        List<JobGroup> list = jobGroupRepository.findAll();

        model.addAttribute("list", list);
        return "jobgroup/jobgroup.index";
    }

    @RequestMapping("/save")
    @ResponseBody
    public Result<String> save(JobGroup JobGroup) {

        // valid
        if (JobGroup.getName() == null || JobGroup.getName().trim().length() == 0) {
            return Result.fail(500, (I18nUtil.getInstance("").getString("system_please_input") + "AppName"));
        }
        if (JobGroup.getName().length() < 4 || JobGroup.getName().length() > 64) {
            return Result.fail(500, I18nUtil.getInstance("").getString("jobgroup_field_appName_length"));
        }
        if (JobGroup.getTitle() == null || JobGroup.getTitle().trim().length() == 0) {
            return Result.fail(500,
                    (I18nUtil.getInstance("").getString("system_please_input") + I18nUtil.getInstance("").getString("jobgroup_field_title")));
        }
        if (JobGroup.getType() != 0) {
            if (JobGroup.getAddressList() == null || JobGroup.getAddressList().trim().length() == 0) {
                return Result.fail(500, I18nUtil.getInstance("").getString("jobgroup_field_addressType_limit"));
            }
            String[] addresss = JobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (item == null || item.trim().length() == 0) {
                    return Result.fail(
                            500, I18nUtil.getInstance("").getString("jobgroup_field_registryList_invalid"));
                }
            }
        }

        var ret = jobGroupRepository.save(JobGroup);
        return (ret != null) ? Result.success() : Result.fail(500, "error");
    }

    @RequestMapping("/update")
    @ResponseBody
    public Result<String> update(JobGroup JobGroup) {
        // valid
        if (JobGroup.getName() == null || JobGroup.getName().trim().length() == 0) {
            return Result.fail(500, (I18nUtil.getInstance("").getString("system_please_input") + "AppName"));
        }
        if (JobGroup.getName().length() < 4 || JobGroup.getName().length() > 64) {
            return Result.fail(500, I18nUtil.getInstance("").getString("jobgroup_field_appName_length"));
        }
        if (JobGroup.getTitle() == null || JobGroup.getTitle().trim().length() == 0) {
            return Result.fail(
                    500,
                    (I18nUtil.getInstance("").getString("system_please_input") + I18nUtil.getInstance("").getString("jobgroup_field_title")));
        }
        if (JobGroup.getType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(JobGroup.getName());
            String addressListStr = null;
            if (registryList != null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                addressListStr = "";
                for (String item : registryList) {
                    addressListStr += item + ",";
                }
                addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
            }
            JobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            if (JobGroup.getAddressList() == null || JobGroup.getAddressList().trim().length() == 0) {
                return Result.fail(500, I18nUtil.getInstance("").getString("jobgroup_field_addressType_limit"));
            }
            String[] address = JobGroup.getAddressList().split(",");
            for (String item : address) {
                if (item == null || item.trim().length() == 0) {
                    return Result.fail(
                            500, I18nUtil.getInstance("").getString("jobgroup_field_registryList_invalid"));
                }
            }
        }

        var ret = jobGroupRepository.save(JobGroup);
        return (ret != null) ? Result.success() : Result.fail(500, "error");
    }

    private List<String> findRegistryByAppName(String appNameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
        List<JobRegistry> list = JobRegistryDao.findAll();
        if (list != null) {
            for (JobRegistry item : list) {
                if (RegistryConfig.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appName = item.getRegistryKey();
                    List<String> registryList = appAddressMap.get(appName);
                    if (registryList == null) {
                        registryList = new ArrayList<String>();
                    }

                    if (!registryList.contains(item.getRegistryValue())) {
                        registryList.add(item.getRegistryValue());
                    }
                    appAddressMap.put(appName, registryList);
                }
            }
        }
        return appAddressMap.get(appNameParam);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public Result<String> remove(int id) {

        jobGroupRepository.deleteById(id);
        return Result.success();
    }

    @RequestMapping("/loadById")
    @ResponseBody
    public Result<JobGroup> loadById(int id) {
        JobGroup jobGroup = jobGroupRepository.findById(id).get();
        if (jobGroup == null) {
            return Result.fail(500, "error");
        }
        return Result.success(jobGroup);
    }
}
