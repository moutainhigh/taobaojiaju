package com.xinshan.utils.shortMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.BatchSmsAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.xinshan.model.UserCheckCode;

/**
 * Created by mxt on 17-6-16.
 * 阿里短信
 */
public class AliMessage {

    private static String accessId = "LTAIpyavQLSijwrr";
    private static String accessKey = "v8hKUB0lr2T1whNiiCpPDVGfyawLGq";
    private static String MNSEndpoint = "http://1200730911250382.mns.cn-hangzhou.aliyuncs.com/";
    private static String topic = "sms.topic-cn-hangzhou";

    public static boolean sendAliCheckCode(String mobile, String checkCode, UserCheckCode userCheckCode) {
        boolean result = true;
        CloudAccount account = new CloudAccount(accessId, accessKey, MNSEndpoint);
        MNSClient client = account.getMNSClient();

        CloudTopic cloutT = client.getTopicRef(topic);
        /**
         * Step 2. 设置SMS消息体（必须）
         *
         * 注：目前暂时不支持消息内容为空，需要指定消息内容，不为空即可。
         */
        RawTopicMessage msg = new RawTopicMessage();
        msg.setMessageBody("sms-message");
        /**
         * Step 3. 生成SMS消息属性
         */
        MessageAttributes messageAttributes = new MessageAttributes();
        BatchSmsAttributes batchSmsAttributes = new BatchSmsAttributes();
        // 3.1 设置发送短信的签名（SMSSignName）
        batchSmsAttributes.setFreeSignName("凤凰家居城");

        // 3.2 设置发送短信使用的模板（SMSTempateCode）
        batchSmsAttributes.setTemplateCode("SMS_75760163");

        // 3.3 设置发送短信所使用的模板中参数对应的值（在短信模板中定义的，没有可以不用设置）
        BatchSmsAttributes.SmsReceiverParams smsReceiverParams = new BatchSmsAttributes.SmsReceiverParams();
        smsReceiverParams.setParam("code", checkCode);
        // 3.4 增加接收短信的号码
        batchSmsAttributes.addSmsReceiver(mobile, smsReceiverParams);
        messageAttributes.setBatchSmsAttributes(batchSmsAttributes);
        try {
            /**
             * Step 4. 发布SMS消息
             */
            TopicMessage ret = cloutT.publishMessage(msg, messageAttributes);
            System.out.println("MessageId: " + ret.getMessageId());
            System.out.println("MessageMD5: " + ret.getMessageBodyMD5());
            userCheckCode.setMsg_result(JSON.toJSONString(ret));
        } catch (ServiceException e){
            e.printStackTrace();
            result = false;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorCode", e.getErrorCode());
            jsonObject.put("message", e.getMessage());
            userCheckCode.setMsg_result(jsonObject.toJSONString());
        } finally{
            client.close();
        }
        return result;
    }
}
