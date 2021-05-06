package com.superlcb.graduation_design_server_end

import com.superlcb.graduation_design_server_end.service.EmailService
import com.superlcb.graduation_design_server_end.util.Utils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils

@SpringBootTest
class GraduationDesignServerEndApplicationTests {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var emailService: EmailService


    @Test
    fun contextLoads() {




    }

    @Autowired
    lateinit var utils:Utils

    @Test
    fun testUtilReadAll(){
        println(utils.readAll("templates/email.html"))
    }
//
    @Test
    fun testEmailService(){
        val email="1455979598@qq.com"
        val verifyCode=Utils.emailText.format(86876)
//        println(verifyCode)
        println(emailService.sendMail(email,verifyCode))
    }

}
