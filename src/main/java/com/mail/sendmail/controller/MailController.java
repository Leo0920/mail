package com.mail.sendmail.controller;

import com.mail.sendmail.service.MailService;
import com.mail.sendmail.vo.MailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MailController {
    @Autowired
    private MailService mailService;

    @GetMapping("/")
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("mail/sendmail");
        mv.addObject("from",mailService.getMailSendFrom());
        return  mv;
    }

    @PostMapping("mail/sendmail")
    public MailVo sendMail(MailVo mailVo, MultipartFile[] files){
        mailVo.setMultipartFiles(files);
        return mailService.sendMail(mailVo);//发送邮件和附件
    }
}
