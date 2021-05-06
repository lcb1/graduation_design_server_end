package com.superlcb.graduation_design_server_end.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


fun <T> T.toJson():String{
    return Utils.gson.toJson(this)
}

fun String.toReal():String{

    if(this.isBlank()) return this

    val firstChar=this.first()
    val lastChar=this.last()
    var other=this
    if(firstChar==lastChar&&(firstChar=='\''||lastChar=='\"')){
        other=this.substring(1,this.length-1)
    }

    val builder=StringBuilder()
    for(c in other){
        if(c=='\''||c=='\"'){
            builder.append('\\')
        }
        builder.append(c)
    }


    return "\"${builder}\""
}





typealias MapType=Map<String,JsonElement>


@Service
object Utils {




    val emailFilePath="templates/email.html"

    val wordQueryJsonFilePath="static/Level8luan_2.json"
    val typeToken by lazy {
        object : TypeToken<MapType>(){}.type
    }


    val emailText by lazy {
        readAll(emailFilePath)
    }



    fun parseJsonEl(filename: String=this.wordQueryJsonFilePath):List<MapType>{
        val lines= readAllLines(filename)
        val jsonEls=ArrayList<MapType>()

        for(line in lines){
            val jsonEl=gson.fromJson<MapType>(line, typeToken)
            jsonEls.add(jsonEl)
        }
        return jsonEls
    }


    fun parseJsonMap(mapType:MapType):Map<String,String>{

        val que = LinkedList<Pair<LinkedList<String>,MapType>>()
        que.offer(LinkedList<String>() to mapType)
        val hashMap=HashMap<String,String>()
        while(que.isNotEmpty()){
            val now=que.poll()
            val prefix=now.first
            val nowMapType=now.second
            for((key,value) in nowMapType){
                val prefixClone=LinkedList(prefix)
                prefixClone.add(key)
                if(value.isJsonObject){

                    val pairSecond= gson.fromJson<MapType>(value, typeToken)
                    que.offer(prefixClone to pairSecond)
                }else{

                    hashMap[prefixClone.joinToString("_")]=value.toString()
                }
            }

        }
        return hashMap
    }

    /***
    author: liuchengbiao
    date_time: 2021/5/6 3:28 下午
    desc:
    content_word_content_trans 单词翻译
    tranCn 翻译
    pos 音标
    content_word_content_ukphone 美式发音
    //    content_word_content_usphone 英式发音
    //     wordRank 1
    headWord 原单词


    // id raw_word target ukphone
     */
    fun getTargetByJsonElement(jsonElement: JsonElement):String{

        val jsonArr=jsonElement.asJsonArray

        val builder=StringBuilder()
        val start=0
        for(i in start until jsonArr.size()){
            val jsonItem=jsonArr[i].asJsonObject
            val tranCn=jsonItem.get("tranCn").asString

            val pos=if(jsonItem.has("pos")) jsonItem.get("pos").asString else "?"
            val seg="${pos}. $tranCn"
            builder.append(seg)
            if(i!=jsonArr.size()-1) builder.append("   ")
        }
        return builder.toString()
    }

    fun parseJsonMapApi11(filename: String=this.wordQueryJsonFilePath):List<Map<String,String>>{
        val listMaps=ArrayList<Map<String,String>>()
        val listEl= parseJsonEl()

        for(el in listEl){
            //content_word_content_trans
            val rawMap= parseJsonMap(el).toMutableMap()
            val rawWordTarget=rawMap["content_word_content_trans"]?:return emptyList()
            val type=object :TypeToken<JsonElement>(){}.type
            val rawEl=gson.fromJson<JsonElement>(rawWordTarget,type)
            val target= getTargetByJsonElement(rawEl)
            rawMap["target"]=target
            listMaps.add(rawMap)
        }
        return listMaps
    }







    fun getFile(filename:String): File? {
        val pathSeparator=System.getProperty("path.separator")//:
        val fileSeparator= System.getProperty("file.separator")// /
        val pathList=System.getProperty("java.class.path").split(pathSeparator)
        for(path in pathList){
            val realPath="${path}${fileSeparator}${filename}"
            println(realPath)
            val file=File(realPath)
            if(file.exists()){
                return file
            }
        }
        return null
    }


    fun readAll(filename:String):String{
        return getInputStream(filename).readBytes().toString(Charset.forName("utf-8"))
    }

    fun getInputStream(filename: String): InputStream {
        return ClassPathResource(filename).inputStream
    }

    fun getAbsoluteFilePath(filename: String):String{
        return ClassPathResource(filename).file.absolutePath
    }


    fun readAllLines(filename: String):List<String>{
        val allData= FileReader(getAbsoluteFilePath(filename))
        return allData.readLines()
    }

    fun getLinesAllKeyValue(filename: String="templates/headers.txt",separator:String=CrawlerUtils.HEADER_SEPARATOR):List<Pair<String,String>>{
        return readAllLines(filename).map {
            val keyValue=it.split(separator)
            if(keyValue.isNullOrEmpty()||keyValue.size!=2) return@map "null" to "null"
            return@map keyValue[0] to keyValue[1]
        }
    }





    val emailPatternStr by lazy {
       "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$"
    }

    val passwordValidateSize=6

    val emailPattern by lazy {
        Pattern.compile(emailPatternStr)
    }

    val gson by lazy {
        Gson().newBuilder().setPrettyPrinting().create()
    }


    fun getResult(status: ResultStatus,data:Any=status.label):Result{
        return Result(status.code,data)
    }

    fun emailValidate(email:String):Boolean{
        val matcher=emailPattern.matcher(email)

        return matcher.matches()
    }
    fun md5(content:String):String{
        val md=MessageDigest.getInstance("md5")
        val hexMap="0123456789ABCDEF".toCharArray()
        val bytes=md.digest(content.toByteArray())
        val builder=StringBuilder()
        for(byte in bytes){
            val high=hexMap[byte.toInt().shr(4) and 0x0f]
            val lower=hexMap[byte.toInt() and 0x0f]
            builder.append(high).append(lower)
        }
        return builder.toString()
    }











}




enum class ResultStatus(val code:Int,val label:String){



    IS_OK(200,"is ok"),
    ACCOUNT_ERROR(201,"账号错误"),
    ACCOUNT_FORMAT_ERROR(202,"账号格式错误"),
    PASSWORD_ERROR(203,"密码错误"),
    PASSWORD_FORMAT_ERROR(204,"密码格式错误"),
    USER_EXISTED(205,"用户已经存在"),

    INTERNAL_ERROR(206,"内部错误"),
    EMAIL_SEND_ERROR(207,"验证码发送错误"),
    VERIFY_CODE_TIMEOUT_ERROR(208,"验证码超时"),
    VERIFY_CODE_ERROR(209,"验证码错误"),
    EMAIL_VERIFY_CODE_NOT_MATCH_ERROR(210,"邮箱与验证码不匹配"),
    EMAIL_REGISTERED_ERROR(211,"邮箱已经被注册");
    fun toResult():Result{
        return Utils.getResult(this)
    }
}

data class Msg(val msg:String)

data class Result(val code:Int,val data:Any){
    companion object{
        fun isOk(callback:()->Any?):Result{
            val ret=callback()
            val retData=if(ret==null) {
                "null"
            }else{
                ret
            }
            return Result(200,retData)
        }
    }
}
object Config{
    val presetVerifyCode by lazy {
        listOf("122686","199910","137432")
    }

}
object DataBaseConfig{
    val url by lazy {
        "jdbc:mysql://1.116.71.60:13306/user_db?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&serverTimezone=Asia/Shanghai"
    }
    val username by lazy {
        "root"
    }
    val password by lazy {
        "123456"
    }
}

enum class TranslationWay(val modes:Array<String>){
    AUTO(arrayOf("auto","自动")),
    ENGLISH(arrayOf("english","en","英语","yy")),
    CHINESE(arrayOf("zh","zw","中文")),
    JAPAN(arrayOf("jp","日语","ry"));
    companion object{
        fun of(lang:String):TranslationWay{
            return values().find{
                it.modes.any { mode->
                    mode.startsWith(lang)
                }
            } ?:AUTO
        }
    }
}
