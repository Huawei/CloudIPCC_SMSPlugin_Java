
package com.huawei.sms.plugin;

import java.security.SecureRandom;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.adapter.common.util.utils.JsonUtil;
import com.huawei.sms.smsadapter.plugin.bean.SMSMessage;
import com.huawei.sms.smsadapter.plugin.bean.SMSResult;
import com.huawei.sms.smsadapter.plugin.callback.ISMSCallback;
import com.huawei.sms.smsadapter.plugin.itf.ISMSAdapter;


public class SMSPlugin implements ISMSAdapter
{
    private static Logger log = LoggerFactory.getLogger(SMSPlugin.class);
    
    private static ISMSCallback callback;

    
    private static boolean isRecvReceipt;

    private HttpClientUtil httpClientUtil;
    
    
    public static void setRecReceipt(boolean isRecvReceipt)
    {
        SMSPlugin.isRecvReceipt = isRecvReceipt;
    }
    public static void setCallBack(ISMSCallback callback)
    {
        SMSPlugin.callback = callback;
    }
    
    @Override
    public void init(Properties properties)
    {
        String url = properties.getProperty("WEBSOCKET_URL");
        httpClientUtil = new HttpClientUtil(url);
        SMSReceiveThread.begin(url);
        setRecReceipt("ON".equals(properties.getProperty("RECE_RECEIPT")) ? true :false); 
    }
    

    @Override
    public void registerRecvCallback(ISMSCallback callback)
    {
        setCallBack(callback);
    }

    private String generateId()
    {
        char[] chars="0123456789abcdefghijklmnopqrwtuvzxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] saltchars=new char[16];
        SecureRandom secureRandom = new SecureRandom();
        for(int i=0;i<16;i++)
        {
            int n=secureRandom.nextInt(62);
            saltchars[i]=chars[n];
        }
        return new String(saltchars);
    }
    
    @Override
    public SMSResult sendSMSMessage(SMSMessage message)
    {
        SMSResult result = new SMSResult();
       
        SMSMessageEx ex = new SMSMessageEx();
        if (isRecvReceipt)
        {
            ex.setId(generateId());
        }
        ex.setMessage(message.getMessage());
        ex.setReceiverAddress(message.getReceiverAddress());
        ex.setSenderAddress(message.getSenderAddress());
        String text = JsonUtil.getJsonString(ex);
        try
        {
            httpClientUtil.sendSMS(text);
            result.setSuccess(true);
            result.setReportDeliveryStatus(isRecvReceipt);
            if (isRecvReceipt)
            {
                result.setSerialNo(ex.getId());
            }
        }
        catch (Exception e)
        {
            log.error("send faile. the error is {} ", e.getMessage());
            result.setSuccess(false);
        }
   
        return result;
    }

    public static ISMSCallback getCallback()
    {
        return callback;
    }



   
    
}
