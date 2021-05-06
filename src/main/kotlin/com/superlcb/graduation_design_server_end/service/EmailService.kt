package com.superlcb.graduation_design_server_end.service

import com.superlcb.graduation_design_server_end.util.Result

interface EmailService {

    fun sendMail(email:String,verifyCode:String):Result?

}