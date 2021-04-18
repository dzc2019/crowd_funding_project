package com.dzc.crowd.service.api;

import com.dzc.crowd.entity.vo.DetailProjectVO;
import com.dzc.crowd.entity.vo.PortalTypeVO;
import com.dzc.crowd.entity.vo.ProjectVO;
import com.zcdeng.crowd.util.ResultEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ProjectService {

    void saveProject(ProjectVO projectVO, Integer memberId);

    List<PortalTypeVO> getPortalTypeVO();

    DetailProjectVO getDetailProjectVO(Integer projectId);

}
