package com.yuand.study.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


public class SpiderFromBaidu {

    public static void main(String[] args) throws Exception {
        WebClient webClient=new WebClient();
        webClient.setCssEnabled(true);
        webClient.setJavaScriptEnabled(true);
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setJavaScriptTimeout(600*1000);

        String url = "http://image.baidu.com/";

        HtmlPage page=webClient.getPage(url);
        sop("get page success...");

        final HtmlForm form = page.getFormByName("f1");
        final HtmlTextInput textField = form.getInputByName("word");
        textField.setValueAttribute("初音未来");

        List list = page.getByXPath("//form/span/input[@type=\"submit\"]");
        HtmlSubmitInput go = (HtmlSubmitInput)list.get(0);
        HtmlPage p =(HtmlPage)go.click();
        webClient.setJavaScriptTimeout(3*1000);

        List imgList = p.getByXPath("//div[@class='list']/div/div[@class='imgshadow']");
        HtmlDivision imgDiv = null;
        HtmlAnchor link = null;
        HtmlElement element = null;
        String str=null;
        int begin=0;
        int end = 0;
        int k=1;
        for(int i=0;i<imgList.size();i++){
            imgDiv =(HtmlDivision)imgList.get(i);
            element = (HtmlElement) imgDiv.getLastChild().getLastChild();
            str = element.toString();
            if(str.contains("url") && str.contains(".jpg")){
                begin = str.indexOf("url")+4;
                end = str.indexOf(".jpg")+4;
                str = str.substring(begin,end);
                str = URLDecoder.decode(str);
                download(str,"D:\\SpiderFromBD\\");
                sop("下载成功：");
            }else{
                str = "";
            }
            if(!str.equals("")){
                sop("百度图片地址"+k+++": "+str);
            }
        }
    }
    public static void sop(Object obj){
        System.out.println(obj);
    }
    //根据图片网络地址下载图片
    public static void download(String url,String path){
        File file= null;
        File dirFile=null;
        FileOutputStream fos=null;
        HttpURLConnection httpCon = null;
        URLConnection  con = null;
        URL urlObj=null;
        InputStream in =null;
        byte[] size = new byte[1024];
        int num=0;
        try {
            String downloadName= url.substring(url.lastIndexOf("/")+1);
            dirFile = new File(path);
            if(!dirFile.exists()){
                if(dirFile.mkdir()){
                    if(path.length()>0){
                        sop("creat document file \""+path.substring(0,path.length()-1)+"\" success...\n");
                    }
                }
            }else{
                file = new File(path+downloadName);
                fos = new FileOutputStream(file);
                if(url.startsWith("http")){
                    urlObj = new URL(url);
                    con = urlObj.openConnection();
                    httpCon =(HttpURLConnection) con;
                    in = httpCon.getInputStream();
                    while((num=in.read(size)) != -1){
                        for(int i=0;i<num;i++)
                            fos.write(size[i]);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                fos.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}