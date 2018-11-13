package com;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class JdzwOW_NEW {

    private static final Logger log = LogManager.getLogger(JdzwOW_NEW.class);
    private String User;
    private String Pas;

    private  String Host;




    /**
     *  构造函数
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:54
     * @param 
     * @param use
     * @param pas
     * @param host
     * @return 
     */
    public JdzwOW_NEW(String use ,String pas ,String host) {
        this.User=use;
        this.Pas=pas;
        this.Host=host;
    }


    /**
     *  挂机整套流程调用
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:54
     * @param 
     * @return java.lang.String
     */
    public String OW_execute() throws Exception {
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom()
                .setConnectTimeout(120000)
                .setSocketTimeout(10000)
                .setCookieSpec(CookieSpecs.STANDARD).build();
        // 创建cookie store的本地实例
        CookieStore cookieStore = new BasicCookieStore();
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        // 创建一个HttpClient
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .setDefaultCookieStore(cookieStore).build();

        CloseableHttpResponse res = null;

        try{

            HttpPost PublicPost = null;

            HttpGet CookieGet = new HttpGet(Host+"/kod5.aspx?&userType=1&userType=");

            res = httpClient.execute(CookieGet, context);

            log.info(ShowHttpResponseEntity(res));  //显示应答

            if(!Login_OW(PublicPost, httpClient, res, context)){     //登陆
                throw new Exception("账号密码错误");
            }
            log.info(SiginIN_OW(PublicPost, httpClient, res, context));    //签到

            SiginINGetReward(PublicPost, httpClient, res, context);   //签到奖励

            log.info(Share_OW(PublicPost, httpClient, res, context));  //分享

            log.info(Explore_OW(PublicPost, httpClient, res, context));  //探索

            log.info(ShowInforma_OW(PublicPost, httpClient, res, context,"16", "1")); //获取账号信息

            log.info(On_HookOW(PublicPost, httpClient, res, context));   //进入挂机

            log.info(Hook_TimeHeartOW(PublicPost, httpClient, res, context,Host)); //维持挂机心跳包
        }finally {
            try {
                if(res!=null){
                    res.close();
                }
            } catch (IOException e) {
                log.error(e);
            }

            try {
                if(httpClient!=null){
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error(e);
            }


        }

        return "远程服务调用结束";
        

    }


    public static void main(String[] args) throws IOException {

//        JdzwOW_NEW a = new JdzwOW_NEW("","","");     //直接new对象



        JdzwOW_NEW a =utils.getConnection();         //从root.properties中获取参数来new对象

        while (true){
            try {
                a.OW_execute();
            }catch (Exception e) {
                log.error("顶层异常：" + e);
//                for (int i = 0; i < e.getStackTrace().length; i++) {
//                    log.error(e.getStackTrace()[i]);
//                }
                    if(e.getMessage().equals("账号密码错误")){
                        return;
                    }
            }

        }



    }

    /**
     *  登陆接口，用于判断账号密码是否正确
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:54
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @return boolean
     */
    private boolean Login_OW(HttpPost post, CloseableHttpClient httpclient, CloseableHttpResponse response, HttpClientContext context)throws IOException {


        StringBuilder EntityKeyNew = new StringBuilder("Cs5\"login\"a2{s")
                .append(User.length())
                .append("\"")
                .append(User)
                .append("\"s")
                .append(Pas.length())
                .append("\"")
                .append(Pas)
                .append("\"}z");

        StringEntity myEntity = new StringEntity(EntityKeyNew.toString(),
                ContentType.create("text/plain", "UTF-8"));
        post = new HttpPost(Host+":8086/");
        post.setEntity(myEntity);
        response = httpclient.execute(post, context);

        HttpEntity entity = response.getEntity();

        if (entity != null) {
            StringBuilder re = new StringBuilder(EntityUtils.toString(entity, "UTF-8"));
            if(re.indexOf("Rs")!=-1){
                return true;
            }
        }
        return false;
    }

    /**
     *  签到接口
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:55
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @return java.lang.String
     */
    private String SiginIN_OW(HttpPost post, CloseableHttpClient httpclient, CloseableHttpResponse response, HttpClientContext context) throws IOException {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
        LocalDateTime today = LocalDateTime.now();

        log.info(today.format(formatter));

        StringBuilder EntityKeyOld = new StringBuilder("19@r&");
        EntityKeyOld.append(User).append("@r&").append(today.format(formatter));

        StringBuilder EntityKeyNew = new StringBuilder("Cs13\"getserverinfo\"a1{s")
                .append(EntityKeyOld.length())
                .append("\"")
                .append(EntityKeyOld)
                .append("\"}z");

        StringEntity myEntity = new StringEntity(EntityKeyNew.toString(),
                ContentType.create("text/plain", "UTF-8"));
        post = new HttpPost(Host+":8086/");
        post.setEntity(myEntity);
        response = httpclient.execute(post, context);

        return "签到" + ShowHttpResponseEntity(response);
    }

    /**
     *  获取累计签到状态，以及领取签到累积奖励
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:55
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @return void
     */
    private void SiginINGetReward(HttpPost post, CloseableHttpClient httpclient, CloseableHttpResponse response, HttpClientContext context) throws IOException {

        int flageDay = 0;
        StringBuilder EntityKeyOld = new StringBuilder("18@r&");
        EntityKeyOld.append(User);

        StringBuilder EntityKeyNew = new StringBuilder("Cs13\"getserverinfo\"a1{s")
                .append(EntityKeyOld.length())
                .append("\"")
                .append(EntityKeyOld)
                .append("\"}z");

        StringEntity myEntity = new StringEntity(EntityKeyNew.toString(),
                ContentType.create("text/plain", "UTF-8"));
        post = new HttpPost(Host+":8086/");
        post.setEntity(myEntity);
        response = httpclient.execute(post, context);
        String arry=ShowHttpResponseEntity(response);
        String[] arrylink=arry.split("@r&");
        if(arrylink[1].indexOf(",")!=-1){
            flageDay=arrylink[1].split(",").length;
            log.info("签到天数"+arrylink[1]+"  签到累计天数："+flageDay);
        }else{
            flageDay=1;
            log.info("签到累计结果中没有逗号");
            log.info("签到天数"+arrylink[1]);
        }
        
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("H");

        LocalDateTime today = LocalDateTime.now();


        int flageHour =Integer.valueOf(today.format(formatterHour));
        if(flageDay==2){
            log.info("签到奖励2领取:" + ShowInforma_OW(post,httpclient,response,context,"24","2"));
        }
        if(flageDay==5){
            log.info("签到奖励5领取:" + ShowInforma_OW(post,httpclient,response,context,"24","5"));
        }

        if(flageDay==18){
            log.info("签到奖励18领取:" + ShowInforma_OW(post,httpclient,response,context,"24","18"));
        }

        if(flageDay==28){
            if(flageHour>=19){
                log.info("签到奖励10领取:" + ShowInforma_OW(post,httpclient,response,context,"24","10"));
                log.info("签到奖励28领取:" + ShowInforma_OW(post,httpclient,response,context,"24","28"));
            }
        }

    }


    /**
     *  获取分享奖励
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:55
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @return java.lang.String
     */
    private String Share_OW(HttpPost post, CloseableHttpClient httpclient, CloseableHttpResponse response, HttpClientContext context) throws IOException {

        StringBuilder EntityKeyOld = new StringBuilder(  "63@r&1@r&");
        EntityKeyOld.append(User);


        StringBuilder EntityKeyNew = new StringBuilder("Cs13\"getserverinfo\"a1{s")
                .append(EntityKeyOld.length())
                .append("\"")
                .append(EntityKeyOld)
                .append("\"}z");

        StringEntity myEntity = new StringEntity(EntityKeyNew.toString(),
                ContentType.create("text/plain", "UTF-8"));
        post = new HttpPost(Host+":8086/");
        post.setEntity(myEntity);
        response = httpclient.execute(post, context);

        log.info("分享before："+ShowHttpResponseEntity(response));

        return "分享:" + ShowInforma_OW(post,httpclient,response,context,"63@r&2","1");
    }

    /**
     *  探索，共有21处探索区域，每天可以探索一次，雨露均沾探索算法是  日%21+1
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:55
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @return java.lang.String
     */
    private   String Explore_OW(HttpPost post, CloseableHttpClient httpclient, CloseableHttpResponse response, HttpClientContext context) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
        LocalDateTime today = LocalDateTime.now();
        int flage = Integer.valueOf(today.format(formatter)) % 21 + 1;


        StringBuilder EntityKeyOld = new StringBuilder("26@r&");
        EntityKeyOld.append(User).append("@r&1").append("@r&").append(flage);


        StringBuilder EntityKeyNew = new StringBuilder("Cs13\"getserverinfo\"a1{s")
                .append(EntityKeyOld.length())
                .append("\"")
                .append(EntityKeyOld)
                .append("\"}z");

        StringEntity myEntity = new StringEntity(EntityKeyNew.toString(),
                ContentType.create("text/plain", "UTF-8"));
        post = new HttpPost(Host+":8086/");
        post.setEntity(myEntity);
        response = httpclient.execute(post);

        return "探索" + ShowHttpResponseEntity(response);


    }

    /**
     * 发送固定格式的POST请求
     *
     *  Cs13"getserverinfo"a1{s“X”“FT”@r&“USER”@r&“LT” }z
     *
     *  “X”为除了s“X”以外，{}中字符串的长度
     *  “FT”为消息体前缀
     *  “USER”为用户名
     *  “LT”为消息体后缀
     *
     * @author Seniorei
     * @date 2018/11/13 0013 15:55
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @param type
     * @param LastType
     * @return java.lang.String
     */
    private  String ShowInforma_OW(HttpPost post, CloseableHttpClient httpclient, CloseableHttpResponse response,
                                  HttpClientContext context,String type,String LastType) throws IOException {

        StringBuilder EntityKeyOld = new StringBuilder(type + "@r&");
        EntityKeyOld.append(User).append("@r&").append(LastType);


        StringBuilder EntityKeyNew = new StringBuilder("Cs13\"getserverinfo\"a1{s")
                .append(EntityKeyOld.length())
                .append("\"")
                .append(EntityKeyOld)
                .append("\"}z");

        StringEntity myEntity = new StringEntity(EntityKeyNew.toString(),
                ContentType.create("text/plain", "UTF-8"));
        post = new HttpPost(Host+":8086/");
        post.setEntity(myEntity);
        response = httpclient.execute(post, context);

        return "本次请求获取的信息" + ShowHttpResponseEntity(response);
    }

    /**
     *  发送get请求，进入挂机页面
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:56
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @return java.lang.String
     */
    private  String On_HookOW(HttpPost post, CloseableHttpClient httpclient, CloseableHttpResponse response, HttpClientContext context) throws IOException {

        StringBuilder HookURL = new StringBuilder(Host+"/TCG/href.aspx?~");

        HookURL.append(ShowInforma_OW(post, httpclient, response, context,"17", "1").split("@r&")[1].split("\"")[0])
                .append("~xiuxian~");   //获取挂机请求参数并拼入

        HttpGet CookieGet = new HttpGet(HookURL.toString());

        response = httpclient.execute(CookieGet, context);

        try {
            if(response!=null){
                response.close();
            }
        } catch (IOException e) {
            log.error(e);
        }


        CookieGet = new HttpGet(Host+"/TCG/GameOnHook.aspx");

        response = httpclient.execute(CookieGet, context);


        return ShowHttpResponseEntity(response);

    }

    /**
     *  发送POST心跳包，维持挂机
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:56
     * @param 
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @param Host
     * @return java.lang.String
     */
    public static String Hook_TimeHeartOW(HttpPost post, CloseableHttpClient httpclient,
                                          CloseableHttpResponse response, HttpClientContext context,
                                          String Host) throws IOException {
        int xx = 10800;                                         //心跳包中的剩余秒数

        Map<String, String> TimeHeart = new HashMap<String, String>();
        TimeHeart.put("_EVENTARGUMENT", "");
        TimeHeart.put("_EVENTTARGET", "TimHeartBeat");
        TimeHeart.put("_VIEWSTATE", "/wEPDwUJMjU0NTIyMDM2D2QWAgIDD2QWFgICDw8WAh4EVGV4dAUDMTE3ZGQCAw8PFgIfAAUDMjAwZGQCBA8PF" +
                "gIfAAUGdzYzODExZGQCBQ8WAh4Dc3JjBUVodHRwOi8vd3d3Lm15b2NnLmNuL2Jicy9hdmF0YXJzL3VwbG9hZC8wMDIvNjEvNDEvMDhfYXZhdG" +
                "FyX21lZGl1bS5qcGdkAgYPDxYCHwAFMDxmb250IGNvbG9yPSIjRkY5OTAwIj7mqZnprYLlhrPmlpfogIUz5q61PC9mb250PmRkAgcPDxYCHwA" +
                "FAzE2N2RkAgkPDxYCHwAFETIwMTgvOS84IDIxOjAyOjU4ZGQCCg8PFgIfAAURMjAxOC85LzggMjE6MTc6NThkZAILDw8WAh8ABQExZGQCDA8PF" +
                "gIfAAUBMGRkAg0PFgIeCEludGVydmFsAqD CmQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgEFCmltZ0J0blF1aXQ=");
        TimeHeart.put("xx", String.valueOf(xx));

        for (int count = 0; count < 60; count++) {
            try {

                if (count == 59) {
                    TimeHeart.put("imgBtnQuit.x", "29");
                    TimeHeart.put("imgBtnQuit.y", "10");
                    log.info("最后的心跳包：");
                    HookDoPost(Host+"/TCG/GameOnHook.aspx", TimeHeart, post, httpclient, response, context);
                    try {
                        if(response!=null){
                            response.close();
                        }
                    } catch (IOException e) {
                        log.error(e);
                    }
                    break;
                }

                log.info("第" + count + "次心跳包");
                HookDoPost(Host+"/TCG/GameOnHook.aspx", TimeHeart, post, httpclient, response, context);
                try {
                    if(response!=null){
                        response.close();
                    }
                } catch (IOException e) {
                    log.error(e);
                }

                xx -= 180;
                TimeHeart.put("xx", String.valueOf(xx));

                Thread.currentThread().sleep(180000);    //180秒一次   3分钟一次
            } catch(java.net.SocketTimeoutException e){
                log.error("相应超时");
                count--;
            }catch(org.apache.http.conn.HttpHostConnectException e){
                log.error("连接超时");
                count--;
            }
            catch (Exception e) {
                log.error("心跳包异常：" + e);
            }finally {
                try {
                    if(response!=null){
                        response.close();
                    }
                } catch (IOException e) {
                    log.error(e);
                }
            }


        }

        return "挂机结束！";
    }

    /**
     *  模拟POST表单，发送挂机心跳包
     * 
     * @author Seniorei
     * @date 2018/11/13 0013 15:56
     * @param 
     * @param url
     * @param TimeHeart
     * @param post
     * @param httpclient
     * @param response
     * @param context
     * @return void
     */
    public static void HookDoPost(String url, Map TimeHeart, HttpPost post,
                                  CloseableHttpClient httpclient, CloseableHttpResponse response, HttpClientContext context) throws IOException {
        try {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Iterator iter = TimeHeart.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(TimeHeart.get(name));
                nvps.add(new BasicNameValuePair(name, value));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
            post = new HttpPost(url);
            post.setEntity(entity);


            response = httpclient.execute(post, context);

            if (response.getStatusLine().getStatusCode() == 200) {    //请求成功
                log.info("请求成功");
            } else {
                log.warn("状态码： " + response.getStatusLine().getStatusCode());
            }

        }finally {
            try {
                if(response!=null){
                    response.close();
                }
            } catch (IOException e) {
                log.error(e);
            }
        }

    }

    /**
     *  显示请求应答内容
     *
     * @author Seniorei
     * @date 2018/11/13 0013 15:56
     * @param
     * @param response
     * @return java.lang.String
     */
    public static String ShowHttpResponseEntity(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            StringBuilder re = new StringBuilder(EntityUtils.toString(entity, "UTF-8"));

            if (re.length() < 2048) {
                return "返回参数，" + re;
            } else {
                if (re.indexOf("挂机区服务器人数") != -1)
                    return "进入挂机区";
                else
                    return "初始加载网页";
            }

        } else {
            return "无实体";
        }
    }

}
