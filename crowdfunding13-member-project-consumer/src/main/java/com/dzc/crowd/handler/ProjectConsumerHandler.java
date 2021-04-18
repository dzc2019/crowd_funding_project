package com.dzc.crowd.handler;

import com.dzc.crowd.api.MySQLRemoteService;
import com.dzc.crowd.config.OSSProperties;
import com.dzc.crowd.entity.vo.*;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.util.CrowdUtil;
import com.zcdeng.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class ProjectConsumerHandler {
    @Autowired
    private OSSProperties ossProperties;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/create/project/information")
    public String saveProjectBasicInfo(
            ProjectVO projectVO,
            // 接收上传的头图
            MultipartFile headerPicture,
            // 接收上传的详情图片
            List<MultipartFile> detailPictureList,
            // 用来将收集了一部分数据的 ProjectVO 对象存入 Session 域
            HttpSession session,
            // 用来在当前操作失败后返回上一个表单页面时携带提示消息
            ModelMap modelMap
    ) throws IOException {
        boolean headerPictureEmpty = headerPicture.isEmpty();
        if (headerPictureEmpty) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_HEADER_PIC_EMPTY);
            return "project-launch";
        }

        ResultEntity<String> uploadHeaderPicResultEntity = CrowdUtil.uploadFileToOss(
                ossProperties.getEndPoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                headerPicture.getInputStream(),
                ossProperties.getBucketName(),
                ossProperties.getBucketDomain(),
                Objects.requireNonNull(headerPicture.getOriginalFilename())
        );
        String result = uploadHeaderPicResultEntity.getOperationResult();
        if (ResultEntity.SUCCESS.equals(result)) {
            String headerPicturePath = uploadHeaderPicResultEntity.getQueryData();
            projectVO.setHeaderPicturePath(headerPicturePath);
        } else {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_HEADER_PIC_UPLOAD_FAILED);
            return "project-launch";
        }

        ArrayList<String> paths = new ArrayList<>();
        if (detailPictureList == null || detailPictureList.isEmpty()) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_EMPTY);
            return "project-launch";
        }
        for (MultipartFile detailPic : detailPictureList) {
            if (detailPic.isEmpty()) {
                modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_EMPTY);
                return "project-launch";
            }
            ResultEntity<String> detailPicResultEntity = CrowdUtil.uploadFileToOss(
                    ossProperties.getEndPoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret(),
                    detailPic.getInputStream(),
                    ossProperties.getBucketName(),
                    ossProperties.getBucketDomain(),
                    Objects.requireNonNull(detailPic.getOriginalFilename())
            );

            String detailUploadResult = detailPicResultEntity.getOperationResult();
            if (ResultEntity.FAILED.equals(detailUploadResult)) {
                modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_DETAIL_PIC_UPLOAD_FAILED);
                return "project-launch";
            } else {
                String filePath = detailPicResultEntity.getQueryData();
                paths.add(filePath);
            }
        }
        projectVO.setDetailPicturePathList(paths);
        session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
        return "redirect:http://localhost:8081/project/return/info/page";
    }

    @ResponseBody
    @RequestMapping("/create/upload/return/picture.json")
    public ResultEntity<String> uploadReturnPicture(@RequestParam("returnPicture") MultipartFile returnPicture) throws IOException {
        ResultEntity<String> uploadReturnPicResultEntity = CrowdUtil.uploadFileToOss(
                ossProperties.getEndPoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                returnPicture.getInputStream(),
                ossProperties.getBucketName(),
                ossProperties.getBucketDomain(),
                Objects.requireNonNull(returnPicture.getOriginalFilename()));
// 2.返回上传的结果
        return uploadReturnPicResultEntity;
    }

    @ResponseBody
    @RequestMapping("/create/save/return.json")
    public ResultEntity<String> saveReturn(ReturnVO returnVO, HttpSession session) {
        try {
// 1.从 session 域中读取之前缓存的 ProjectVO 对象
            ProjectVO projectVO = (ProjectVO)
                    session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
// 2.判断 projectVO 是否为 null
            if (projectVO == null) {
                return ResultEntity.failed(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
            }
            // 3.从 projectVO 对象中获取存储回报信息的集合
            List<ReturnVO> returnVOList = projectVO.getReturnVOList();
// 4.判断 returnVOList 集合是否有效
            if (returnVOList == null || returnVOList.size() == 0) {
                // 5.创建集合对象对 returnVOList 进行初始化
                returnVOList = new ArrayList<>();
// 6.为了让以后能够正常使用这个集合， 设置到 projectVO 对象中
                projectVO.setReturnVOList(returnVOList);
            }
            // 7.将收集了表单数据的 returnVO 对象存入集合
            returnVOList.add(returnVO);
// 8.把数据有变化的 ProjectVO 对象重新存入 Session 域， 以确保新的数据最终能够存入 Redis
            session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);
// 9.所有操作成功完成返回成功
            return ResultEntity.successWithoutData();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    @RequestMapping("/create/confirm")
    public String saveConfirm(ModelMap modelMap, HttpSession session, MemberConfirmInfoVO memberConfirmInfoVO) {
        ProjectVO projectVO = (ProjectVO)
                session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
// 2.如果 projectVO 为 null
        if(projectVO == null) {
            throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
        }
        // 3.将确认信息数据设置到 projectVO 对象中
        projectVO.setMemberConfirmInfoVO(memberConfirmInfoVO);
// 4.从 Session 域读取当前登录的用户
        MemberLoginVO memberLoginVO = (MemberLoginVO)
                session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
        Integer memberId = memberLoginVO.getId();
// 5.调用远程方法保存 projectVO 对象
        ResultEntity<String> saveResultEntity =
                mySQLRemoteService.saveProjectVORemote(projectVO, memberId);
// 6.判断远程的保存操作是否成功
        String result = saveResultEntity.getOperationResult();
        if(ResultEntity.FAILED.equals(result)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,
                    saveResultEntity.getOperationMessage());
            return "project-confirm";
        }
// 7.将临时的 ProjectVO 对象从 Session 域移除
        session.removeAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);
// 8.如果远程保存成功则跳转到最终完成页面
        return "redirect:http://localhost:8081/project/create/success";
    }

    @RequestMapping("/get/project/detail/{id}")
    public String getProjectDetail(@PathVariable("id") Integer projectId, Model model){

        ResultEntity<DetailProjectVO> resultEntity =
                mySQLRemoteService.getDetailProjectVORemote(projectId);
        if(ResultEntity.SUCCESS.equals(resultEntity.getOperationResult())) {
            DetailProjectVO detailProjectVO = resultEntity.getQueryData();
            model.addAttribute("detailProjectVO", detailProjectVO);
        }
        return "project-show-detail";
    }

}
