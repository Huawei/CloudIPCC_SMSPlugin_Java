
package com.huawei.sms.plugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SMSReceiveThread extends Thread
{   
    public static final BlockingQueue<String> QUEUE = new LinkedBlockingQueue<String>();
    
    private static Logger log = LoggerFactory.getLogger(SMSReceiveThread.class);
    
    private static SMSReceiveThread task = null;
    
    private boolean isAlive = true;
    
    private HttpClientUtil httpClientUtil;
    
    private static ExecutorService threads =  Executors.newFixedThreadPool(300);
    
    @Override
    public void run()
    {

        while (isAlive)
        {
            String message;
            try
            {
                message = httpClientUtil.readSMS();
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
                continue;
            }
            if (message == null || "".equals(message))
            {
                continue;
            }
            
            threads.execute(new SMSCallBackThread(message));
            
        }
    }
    
    public static void begin(String url)
    {
        if (null != task)
        {
            return;
        }
        
        task = new SMSReceiveThread();
        task.setName("SMSReceiveThread");
        task.httpClientUtil = new HttpClientUtil(url);
        task.start(); 
    }
    
    
    public static void add(String message)
    {
        try
        {
            QUEUE.put(message);
        }
        catch (InterruptedException e)
        {
            log.error(e.getMessage());
        }
    }

}
