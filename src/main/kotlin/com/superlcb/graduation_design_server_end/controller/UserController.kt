package com.superlcb.graduation_design_server_end.controller

import com.superlcb.graduation_design_server_end.service.EmailService
import com.superlcb.graduation_design_server_end.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.lang.StringBuilder


@RestController
class UserController {
    companion object{
        const val USER_EMAIL="user_email"
        const val PASSWORD="password"
        const val LOGIN_TIPS="登录成功"
        const val VERIFY_CODE="verify_code"
        const val TIME_STAMP="time_stamp"
        const val TIMEOUT:Long=5*60*1000
    }
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @GetMapping("insert/{${USER_EMAIL}}/{${PASSWORD}}")
    fun insertUser(@PathVariable("${USER_EMAIL}") username:String, @PathVariable("${PASSWORD}") password:String):Result{
        val sql="insert into user_table (user_email,password) values(${username.toReal()},${password.toReal()})"
        val effect=jdbcTemplate.update(sql)
        return if(effect!=0){
            Result(200,Msg("Insert successfull"))
        }else{
            Result(201, Msg("Insert fail"))
        }
    }

    @GetMapping("select/all")
    fun selectAll():Result{
        val sql="select * from user_table"
        val sqlResult=jdbcTemplate.queryForList(sql)
        return Result(200,sqlResult)
    }




    @GetMapping("/login/{${USER_EMAIL}}/{${PASSWORD}}")
    fun login(@PathVariable(USER_EMAIL) userEmail:String, @PathVariable(PASSWORD) password: String):Result{
        checkAccountAndPasswordFormat(userEmail,password)?.let { result->
            return result
        }
        val queryList=findUserByUserEmail(userEmail)
        if(queryList.size>1){
            return Utils.getResult(ResultStatus.INTERNAL_ERROR)
        }
        if(queryList.size==0){
            return Utils.getResult(ResultStatus.ACCOUNT_ERROR)
        }
        val realPassword=queryList.first()[PASSWORD]
        if(realPassword!=password){
            return Utils.getResult(ResultStatus.PASSWORD_ERROR)
        }
        return Utils.getResult(ResultStatus.IS_OK, LOGIN_TIPS)
    }



    fun checkAccountAndPasswordFormat(email:String,password: String):Result?{
        if(!Utils.emailValidate(email)){
            return Utils.getResult(ResultStatus.ACCOUNT_FORMAT_ERROR)
        }
        if(password.length<Utils.passwordValidateSize){
            return Utils.getResult(ResultStatus.PASSWORD_FORMAT_ERROR)
        }
        return null
    }

    fun findUserByUserEmail(userEmail:String):List<Map<String,Any>>{
        val sql="select user_email,password from user_table where user_email=${userEmail.toReal()}"
        return jdbcTemplate.queryForList(sql)
    }
    fun insertVerifyCode(userEmail:String,verifyCode: String,timeStamp:Long){
        val sql="insert into verify_code_table values(${userEmail.toReal()},${verifyCode.toReal()},${timeStamp})"
        jdbcTemplate.update(sql)
    }

    fun generateVerifyCode(length:Int=6):String{
        val builder=StringBuilder()
        for(i in 0 until 6){
            val number=(0..9).random()
            builder.append(number)
        }
        return builder.toString()
    }
    @Autowired
    lateinit var emailService: EmailService

    @GetMapping("send/email/{${USER_EMAIL}}")
    fun sendEmail(@PathVariable(USER_EMAIL) userEmail: String):Result{
        if(!Utils.emailValidate(userEmail)) return Utils.getResult(ResultStatus.ACCOUNT_ERROR)

        val userList=findUserByUserEmail(userEmail)
        if(userList.isNotEmpty()){
            return Utils.getResult(ResultStatus.USER_EXISTED)
        }

        val verifyCode=generateVerifyCode()
        val emailText=Utils.emailText.format(verifyCode)
        val result=emailService.sendMail(userEmail,emailText)
        if(result!=null){
            return result
        }
        insertVerifyCode(userEmail,verifyCode,System.currentTimeMillis())
        return Utils.getResult(ResultStatus.IS_OK)
    }


    fun findVerifyCodeByUserEmail(userEmail: String):List<Map<String,Any>>{
        val sql="select user_email,verify_code,time_stamp from verify_code_table where user_email=${userEmail.toReal()}"
        val resultList=jdbcTemplate.queryForList(sql)
        return resultList
    }

    fun insertByUserAndPassword(userEmail: String,password: String){
        val sql="insert into user_table (user_email,password) values(${userEmail.toReal()},${password.toReal()})"
        jdbcTemplate.execute(sql)
    }


    @GetMapping("/register/{${USER_EMAIL}}/{${PASSWORD}}/{${VERIFY_CODE}}")
    fun register(@PathVariable(USER_EMAIL) userEmail: String,@PathVariable(PASSWORD) password: String,
                    @PathVariable(VERIFY_CODE) verifyCode:String):Result{
        checkAccountAndPasswordFormat(userEmail,password)?.let { result->
            return result
        }

        val loginUsers=findUserByUserEmail(userEmail)
        if(loginUsers.isNotEmpty()){
            return ResultStatus.USER_EXISTED.toResult()
        }

        if(Config.presetVerifyCode.contains(verifyCode)){
            insertByUserAndPassword(userEmail,password)
            return ResultStatus.IS_OK.toResult()
        }




        val repoList=findVerifyCodeByUserEmail(userEmail)
        if(repoList.isEmpty()){
            return Utils.getResult(ResultStatus.EMAIL_VERIFY_CODE_NOT_MATCH_ERROR)
        }

        for(repo in repoList){

            val realTimeStamp=(repo[TIME_STAMP] as String).toLong()
            val now=System.currentTimeMillis()
            val realCode=repo[VERIFY_CODE] as String
            if(realCode==verifyCode){
                if(realTimeStamp-now> TIMEOUT){
                    return Utils.getResult(ResultStatus.VERIFY_CODE_TIMEOUT_ERROR)
                }
                return Utils.getResult(ResultStatus.IS_OK)
            }
        }

        return Utils.getResult(ResultStatus.VERIFY_CODE_ERROR)
    }

















}