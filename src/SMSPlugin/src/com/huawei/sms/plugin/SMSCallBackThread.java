
package com.huawei.sms.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.util.utils.JsonUtil;
import com.huawei.adapter.common.util.utils.StringUtils;
import com.huawei.sms.smsadapter.plugin.bean.SMSDeliveryStatus;
import com.huawei.sms.smsadapter.plugin.bean.SMSMessage;


public class SMSCallBackThread extends Thread
{
    private static Logger log = LoggerFactory.getLogger(SMSCallBackThread.class);
    
    private String message;
    
    public SMSCallBackThread(String message)
    {
        this.message = message;
    }
    
    public void run()
    {
        try
        {
            SMSMessage sms = JsonUtil.getBean(message, SMSMessage.class);
            if (sms != null && !StringUtils.isNullOrBlank(sms.getSenderAddress()))
            {
                SMSPlugin.getCallback().onRecvIncomeSMS(sms);
            }
            else
            {
                SMSDeliveryStatus status = JsonUtil.getBean(message, SMSDeliveryStatus.class);
                if (status != null)
                {
                    SMSPlugin.getCallback().onRecvReceipt(status);
                }
            }
        }
        catch (Exception e)
        {
            log.error("convert message failed,the exception is {}", e.getMessage());
        }
    }

}
