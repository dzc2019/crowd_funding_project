package com.zcdeng.crowd.util;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;
import com.zcdeng.crowd.constant.CrowdConstant;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CrowdUtil {
    public static ResultEntity<String> sendShortMessage(String host,
                                                        String path,
                                                        String method,
                                                        String appcode, // "c959674907484fab84c1f8d87c943329"
                                                        String mobile
    ) {

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        StringBuilder authCode = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            authCode.append(r.nextInt(10));
        }
        querys.put("content", "【zc_deng的众筹网】您的验证码是" + authCode + "，5分钟内有效。如非本人操作，请忽略本短信");
        querys.put("mobile", mobile);

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            // System.out.println(response.toString());
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            // System.out.println(EntityUtils.toString(response.getEntity()));

            if (statusCode != 200) {
                return ResultEntity.failed(statusLine.getReasonPhrase());
            }
            return ResultEntity.successWithData(authCode.toString());
            //获取response的body
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    public static String md5(String source) {
        if ("".equals(source)) {
            throw new RuntimeException(CrowdConstant.MESSAGE_STRING_INVALIDATE);
        }
        try {
            String algorithm = "md5";
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] output = messageDigest.digest(source.getBytes());
            int signum = 1, radix = 16;
            BigInteger bigInteger = new BigInteger(signum, output);
            return bigInteger.toString(radix);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        String xRequestHeader = request.getHeader("X-Requested-With");
        return (acceptHeader != null && acceptHeader.contains("application/json")) || "XMLHttpRequest".equals(xRequestHeader);
    }

    /**
     * 专门负责上传文件到 OSS 服务器的工具方法
     *
     * @param endpoint        OSS 参数
     * @param accessKeyId     OSS 参数
     * @param accessKeySecret OSS 参数
     * @param inputStream     要上传的文件的输入流
     * @param bucketName      OSS 参数
     * @param bucketDomain    OSS 参数
     * @param originalName    要上传的文件的原始文件名
     * @return 包含上传结果以及上传的文件在 OSS 上的访问路径
     */
    public static ResultEntity<String> uploadFileToOss(
            String endpoint,
            String accessKeyId,
            String accessKeySecret,
            InputStream inputStream,
            String bucketName,
            String bucketDomain,
            String originalName) {
// 创建 OSSClient 实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
// 生成上传文件的目录
        String folderName = new SimpleDateFormat("yyyyMMdd").format(new Date());
// 生成上传文件在 OSS 服务器上保存时的文件名
// 原始文件名： beautfulgirl.jpg
// 生成文件名： wer234234efwer235346457dfswet346235.jpg
// 使用 UUID 生成文件主体名称
        String fileMainName = UUID.randomUUID().toString().replace("-", "");
// 从原始文件名中获取文件扩展名
        String extensionName = originalName.substring(originalName.lastIndexOf("."));
// 使用目录、 文件主体名称、 文件扩展名称拼接得到对象名称
        String objectName = folderName + "/" + fileMainName + extensionName;
        try {
// 调用 OSS 客户端对象的方法上传文件并获取响应结果数据
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName,
                    inputStream);
// 从响应结果中获取具体响应消息
            ResponseMessage responseMessage = putObjectResult.getResponse();
// 根据响应状态码判断请求是否成功
            if (responseMessage == null) {
// 拼接访问刚刚上传的文件的路径
                String ossFileAccessPath = bucketDomain + "/" + objectName;
// 当前方法返回成功
                return ResultEntity.successWithData(ossFileAccessPath);
            } else {
// 获取响应状态码
                int statusCode = responseMessage.getStatusCode();
                // 如果请求没有成功， 获取错误消息
                String errorMessage = responseMessage.getErrorResponseAsString();
// 当前方法返回失败
                return ResultEntity.failed(" 当 前 响 应 状 态 码 =" + statusCode + " 错 误 消 息 =" + errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
// 当前方法返回失败
            return ResultEntity.failed(e.getMessage());
        } finally {
            if (ossClient != null) {
// 关闭 OSSClient。
                ossClient.shutdown();
            }
        }
    }


}
