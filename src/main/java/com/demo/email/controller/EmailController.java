package com.demo.email.controller;

import com.demo.email.entity.Resume;
import com.demo.email.service.EmailService;
import com.demo.email.util.Result;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

@RestController
@RequestMapping("email")
@PropertySource("classpath:email.properties")
public class EmailController {

    @Resource
    public EmailService emailService;

    @Value("${e.account}")
    private  String account;     //登录用户名
    @Value("${e.pass}")
    private  String pass;        //登录密码
    @Value("${e.host}")
    private  String host;        //服务器地址（邮件服务器）
    @Value("${e.port}")
    private  String port;        //端口
    @Value("${e.protocol}")
    private  String protocol;    //协议

    /**
     * 发送邮件方法
     * @param id
     * @return resume
     */
    @RequestMapping("/send")
    public Result sendEmail(Integer id,String MeetTime){
        //后台查找简历相关信息
        Resume resume = emailService.getResumeById(id);
        //生成邮件文本内容
        String emailContent = CreateEmailContent(resume, "xx", "110", "2019.12.10 14:00");
        System.out.println(emailContent);
        //todo sendEmail
        Result result = sendmail(resume, "xinsy@ui-tech.com.cn", "2019.12.10 14:00", emailContent);
        System.out.println(resume.getDetail());
        return result;
    }

    /**
     * 生成邮件内容
     * @param resume
     * @param sendName
     * @param SendPhone
     * @param MeetTime
     * @return
     */
    public String CreateEmailContent(Resume resume,String sendName,String SendPhone,String MeetTime){
        StringBuffer emailContent = new StringBuffer();
        emailContent.append(resume.getName()+":<br> \n");
        emailContent.append("您好！<br> \n" +
                "<br> \n" +
                "这里是联创瑞鑫人力资源部，很荣幸与您电话沟通交流，特邀您来我公司参加面试，现将具体情况如下：<br> \n" +
                "<br> \n" +
                "1、面试时间：");
        emailContent.append(MeetTime+"<br> \n");
        emailContent.append("2、面试地址：xxx<br> \n" +
                "<br> \n" +
                "请您准时参加，如因其他原因无法前来面试，请提前以邮件或电话形式告知，谢谢！<br> \n" +
                "<br> \n" +
                "备注：参加面试，请携带一份简历,谢谢配合！<br> \n" +
                "<br> \n" +
                "乘车路线：<br> \n" +
                "<br> \n" +
                "自己百度<br> \n" +
                "<br> \n" +
                "联系电话：");
        emailContent.append(SendPhone+"<br> \n");
        emailContent.append("人力资源管理中心（"+sendName+")");
        emailContent.append( "<br> \n" +
                        " <br> \n" +
                        "<br> \n" +
                        "<br>  \n" +
                        "<br><a href=\"http://www.ui-tech.com.cn\">http://www.ui-tech.com.cn/</a> \n" +
                        "                                                   联创瑞鑫");

        return emailContent.toString();
    }

    /**
     * 发送邮件实际方法
     * @param resume
     * @param from
     * @param date
     * @param emailContent
     * @return
     */
    public Result sendmail(Resume resume,String from,String date,String emailContent){
        String message = "邮件发送失败";
        Boolean flag = false;

        //测试用
        from = "xinsy@ui-tech.com.cn";
        //to = "170584078@qq.com";
        //返回结果
        Result result = new Result();
        Properties prop = new Properties();
        //协议
        prop.setProperty("mail.transport.protocol", protocol);
        //服务器
        prop.setProperty("mail.smtp.host", host);
        //端口
        prop.setProperty("mail.smtp.port", port);
        //使用smtp身份验证
        prop.setProperty("mail.smtp.auth", "true");
        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            e1.printStackTrace();
        }
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getDefaultInstance(prop, new MyAuthenricator(account, pass));
        session.setDebug(true);
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            //发件人
            mimeMessage.setFrom(new InternetAddress(account,"联创瑞鑫"));        //可以设置发件人的别名
            //mimeMessage.setFrom(new InternetAddress(account));    //如果不需要就省略
            //收件人
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(resume.getEamil()));
            //主题
            mimeMessage.setSubject("面试邀约");
            //时间
            mimeMessage.setSentDate(new Date());
            //容器类，可以包含多个MimeBodyPart对象
            Multipart mp = new MimeMultipart();

            //MimeBodyPart可以包装文本，图片，附件
            MimeBodyPart body = new MimeBodyPart();
            //HTML正文
            body.setContent(emailContent.toString(), "text/html; charset=UTF-8");
            mp.addBodyPart(body);

            //添加图片&附件
            body = new MimeBodyPart();
            body.attachFile("D:\\地图.png");
            mp.addBodyPart(body);

            //设置邮件内容
            mimeMessage.setContent(mp);
            //仅仅发送文本
            //mimeMessage.setText(content);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage,account,pass);
            flag = true;
            message = "邮件已发送";
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.setFlag(flag);
        result.setMessage(message);
        return result;
    }

    /**
     * 发送邮件用户密码
     */
    public class MyAuthenricator extends Authenticator {
        String u = null;
        String p = null;

        public MyAuthenricator(String u, String p) {
            this.u = u;
            this.p = p;
        }
    }
}
