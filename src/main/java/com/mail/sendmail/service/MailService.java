package com.mail.sendmail.service;

import com.mail.sendmail.vo.MailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class MailService {
    private Logger logger = LoggerFactory.getLogger(getClass());//提供日志类

    @Autowired
    private JavaMailSenderImpl javaMailSender;

     public MailVo sendMail(MailVo mailVo){
         try {
             checkMail(mailVo);
             sendMimeMail(mailVo);

         }catch (Exception e){
            logger.info("邮件发送失败");
            mailVo.setStatus("fail");
            mailVo.setError(e.getMessage());
         }
         return  mailVo;
     }

    private void sendMimeMail(MailVo mailVo) {
         try {
             MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(),true);
             mimeMessageHelper.setFrom(javaMailSender.getJavaMailProperties().getProperty("from"));   //发件人
             mimeMessageHelper.setTo(mailVo.getTo().split(","));
             mimeMessageHelper.setSubject(mailVo.getSubject());
             mimeMessageHelper.setText(mailVo.getText());
             if (!StringUtils.isEmpty(mailVo.getCc())) {//抄送
                 mimeMessageHelper.setCc(mailVo.getCc().split(","));
             }
             if (!StringUtils.isEmpty(mailVo.getBcc())) {//密送
                 mimeMessageHelper.setCc(mailVo.getBcc().split(","));
             }
             if (mailVo.getMultipartFiles() != null) {//添加邮件附件
                 for (MultipartFile multipartFile : mailVo.getMultipartFiles()) {
                     mimeMessageHelper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
                 }
             }
             if (StringUtils.isEmpty(mailVo.getSentDate())) {//发送时间
                 mailVo.setSentDate(new Date());
                 mimeMessageHelper.setSentDate(mailVo.getSentDate());
             }
             javaMailSender.send(mimeMessageHelper.getMimeMessage());//正式发送邮件
             mailVo.setStatus("ok");
             logger.info("发送邮件成功：{}->{}", mailVo.getFrom(), mailVo.getTo());
         }catch (Exception e){
             throw new RuntimeException(e);//发送失败
         }
    }

    private void checkMail(MailVo mailVo) {
         if (StringUtils.isEmpty(mailVo.getTo())){
             throw new RuntimeException("邮件发件人不能为空!");
         }
         if(StringUtils.isEmpty(mailVo.getFrom())){
             throw new RuntimeException("邮件收件人不能为空!");
         }
         if(StringUtils.isEmpty(mailVo.getText())){
             throw new RuntimeException("邮件内容不能为空!");
         }
    }
    //获取邮件发信人
    public String getMailSendFrom() {
        return javaMailSender.getJavaMailProperties().getProperty("from");
    }
}
