package com.superlcb.graduation_design_server_end.serviceimpl

import com.superlcb.graduation_design_server_end.service.EmailService
import com.superlcb.graduation_design_server_end.util.Result
import com.superlcb.graduation_design_server_end.util.ResultStatus
import com.superlcb.graduation_design_server_end.util.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.internet.MimeMessage


@Service
class EmailServiceImpl :EmailService {


    @Value("\${spring.mail.from}")
    lateinit var from:String

    val subject by lazy {
        "翻译App verifyCode"
    }

    @Autowired
    lateinit var javaMailSender: JavaMailSender

    override fun sendMail(email: String, verifyCode:String): Result? {

        val mimeMessage=javaMailSender.createMimeMessage()
        mimeMessage.addRecipients(MimeMessage.RecipientType.CC,"superlcb2021@163.com")
        val mimeHelper=MimeMessageHelper(mimeMessage,true)
        mimeHelper.setFrom(from)
        println(from)
        mimeHelper.setTo(email)
        mimeHelper.setSubject(subject)
        mimeHelper.setText(Utils.emailText.format(verifyCode),true)
        try {
            javaMailSender.send(mimeMessage)
        }catch (e:Exception){
            return Utils.getResult(ResultStatus.EMAIL_SEND_ERROR,"验证码发送失败:${e.message}")
        }
        return null
    }
}