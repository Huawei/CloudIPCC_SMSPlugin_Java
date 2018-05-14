
package com.huawei.sms.plugin;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpClientUtil
{
    private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
    

	private static final String UTF_8 = "utf-8";
	
	
	/**
     * 获取httpCLient
     */
    private CloseableHttpClient httpClient;
    
    private String url;
    
    public HttpClientUtil(String url)
    {
        this.url = url;

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(cm);
        httpClient = httpClientBuilder.build();
    }

    
    
    public String readSMS()
    {
        HttpGet httpGet = new HttpGet(url + "/readsms");
        String result = null;
        HttpEntity httpEntity = null;
        try
        {
            // 设置请求编码
            httpGet.addHeader("ContentEncoding", "UTF-8");
            HttpResponse response = httpClient.execute(httpGet);
            
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == 200) {
                httpEntity = response.getEntity();
                result= EntityUtils.toString(httpEntity,"utf-8");
            } 
        }
        catch (RuntimeException e) 
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("read sms message failed. The exception is {}", e.getMessage()); 
            
        }
        finally
        {
            if (null != httpEntity)
            {
                try
                {
                    EntityUtils.consumeQuietly(httpEntity);
                }
                catch (Exception e)
                {
                    log.error("close resource failed. The exception is {}", e.getMessage()); 
                }
            }
        }
        return result;
    }
	
	public void sendSMS(String message)
	{
	    HttpPost httpPost = new HttpPost(url + "/sendfromadapter");
	    HttpEntity httpEntity = null;
	    try
        {
            // 设置请求编码
            httpPost.addHeader("ContentEncoding", "UTF-8");
            List<NameValuePair> params=new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("message",message));
            httpPost.setEntity(new UrlEncodedFormEntity(params, UTF_8));
            // 执行请求
            HttpResponse response =httpClient.execute(httpPost);
            httpEntity = response.getEntity();
            // 获取响应内容
            if (response.getStatusLine().getStatusCode() != 200)
            {
                log.error("send sms message failed. the message is {}", message);
            }
        }
	    catch (RuntimeException e) 
        {
            throw e;
        }
	    catch (Exception e)
        {
	        log.error("send sms message failed. the message is {}. The exception is {}", 
	                message, e.getMessage()); 
        }
	    finally
	    {
	        if (null != httpEntity)
	        {
    	        try
                {
                    EntityUtils.consumeQuietly(httpEntity);
                }
                catch (Exception e)
                {
                    log.error("close resource failed the message is {}. The exception is {}", 
                            message, e.getMessage()); 
                }
	        }
	    }
	}

}
