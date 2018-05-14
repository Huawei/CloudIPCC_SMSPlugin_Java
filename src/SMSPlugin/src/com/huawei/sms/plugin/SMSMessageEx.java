
package com.huawei.sms.plugin;

import com.huawei.sms.smsadapter.plugin.bean.SMSMessage;

public class SMSMessageEx extends SMSMessage
{
    private String id;
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
