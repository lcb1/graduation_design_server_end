package com.superlcb.graduation_design_server_end

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper
import com.superlcb.graduation_design_server_end.repo.RepoUtils
import com.superlcb.graduation_design_server_end.repo.WordQueryTable
import com.superlcb.graduation_design_server_end.util.*
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.tomcat.util.codec.binary.Base64
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.w3c.dom.Node
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.Security
import java.security.acl.Owner
import java.sql.DriverManager
import javax.crypto.Cipher
import javax.print.attribute.IntegerSyntax
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties

class MyTest {

    val okHttpClient by lazy {
        OkHttpClient()
    }
    val url="http://www.baidu.com/s?wd=你好"

    val request by lazy {
        Request.Builder().url(url).get().build()
    }

    val htmlPage by lazy {
        okHttpClient.newCall(request).execute().body?.string()
    }
    val gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }


    @Test
    fun helloJunit(){
        println("Hello Junit!")
    }
    @Test
    fun testOkHttpClient(){
        val body=okHttpClient.newCall(request).execute().body?:return
        println(body.string())
    }

    @Test
    fun testWriteAll(){
        val requireHtmlPage=htmlPage?:return
        writeAll("data.html",requireHtmlPage)

    }

    fun writeAll(crawlerFile:String,html:String){
        val out=FileOutputStream(crawlerFile)
        out.write(html.toByteArray())
        out.close()
    }

    @Test
    fun testUtilsEmailValidate(){
        println(Utils.emailValidate("1993508718@qq.com"))
    }


    @Test
    fun testSystem(){
        println(System.getProperty("file.separator"))
        println(System.getProperty("path.separator"))
    }

    @Test
    fun testUtilReadAll(){
        println(Utils.readAll("templates/email.html").format("123456"))
    }

    class TestInit() {


        companion object{

            val testInit  by lazy {
                TestInit()
            }

            init {
                println("companion object.init()")
            }
        }

        init {
            println("TestInit.constructor()")
        }

        init {
            println("TestInit.init()")
        }

    }

    enum class AudioType(val label:String){
        PODCAST("podcast"),
        EPISODE("episode"),
    }


    @Test
    fun testTestInit(){
        println(AudioType.PODCAST)
    }


    /***
     * spring, summer, autumn and winter
     */
    enum class EnumSeason(val label:String){
        SPRING("spring"),
        SUMMER("summer"),
        AUTUMN("autumn"),
        WINTER("winter");

        fun size():Int{
            return values().size
        }

        fun next():EnumSeason{
            val nextOrdinal=(this.ordinal+1)%size()
            return values().find { nextOrdinal==it.ordinal }?:SPRING
        }

        override fun toString(): String {

            return "ordinal:${ordinal}, name:${name.toLowerCase()}"
        }

    }
    @Test
    fun testEnumSeason(){

        var season=EnumSeason.SPRING
        repeat(10){
            println(season.toJson())
            season=season.next()
        }

    }

    /***
    author: liuchengbiao
    date_time: 2021/4/21 6:17 下午
    */
    @Test
    fun testJdbcConnectIseEnable(){
        val connection=DriverManager.getConnection(DataBaseConfig.url,DataBaseConfig.username,DataBaseConfig.password)
        println(connection.isClosed)
    }
    /***
    author: liuchengbiao
    date_time: 2021/4/21 6:44 下午
    */
    @Test
    fun testJdbcUtils(){
        val sql = "select * from user_table"
        val data=JdbcUtils.executeSql(sql)
        println(data.toJson())
    }


    /***
    author: liuchengbiao
    date_time: 2021/4/21 9:08 下午
    */
    @Test
    fun testCrawlerGetHtml(){
        CrawlerUtils.getHtml("Hello")
    }
    /***
    author: liuchengbiao
    date_time: 2021/4/22 5:56 下午
    */
    @Test
    fun testJsoup(){
        val page = CrawlerUtils.getPage("记得带伞哦 的英文翻译")
        val doc= Jsoup.parse(page)
        val el=doc.select(".c-border span,.c-border p")

        val outHtml=el.outerHtml()
        val spanTag="</span>"
        var i=0
        val builder = StringBuilder()
        while(i+spanTag.length<= outHtml.length){
            val sub=outHtml.substring(i,i+spanTag.length)
            if(sub==spanTag){
                builder.append("\n\r")
            }
            builder.append(outHtml[i])
            i++
        }
        val seg=Jsoup.parse(builder.toString())

        val spanEl=el.tagName("span")

        val texts= el.eachText()
        println("first: ${texts.first()}")

        for(i in 1 until texts.size step 2){
            println("${texts[i]} ${if(i+1<texts.size) texts[i+1] else ""}")
        }

    }

    /***
    author: liuchengbiao
    date_time: 2021/5/5 8:54 下午
    */
    @Test
    fun testUtilsTranslation(){
        println(CrawlerUtils.getTargetLangByExample("action"))
    }






    /***
    author: liuchengbiao
    date_time: 2021/4/29 9:14 下午
    */
    @Test
    fun testJsoupParseAndroidAmanifest(){
        val absPath="/Users/admin/Downloads/TTBach-dev/app/src/main/AndroidManifest.xml"
        val doc=Jsoup.parse(absPath,Charsets.UTF_8.name())
        val userPermissionTags=doc.getElementsByTag("uses-permission")
        println(userPermissionTags.size)
    }

    @Test
    fun testDomParseXml(){
        val absPath="/Users/admin/Downloads/TTBach-dev/app/src/main/AndroidManifest.xml"
        val domFactory=DocumentBuilderFactory.newInstance()
        val domBuilder=domFactory.newDocumentBuilder()
        val domDoc=domBuilder.parse(File(absPath))
        val userPermissionTags=domDoc.getElementsByTagName("application")

        val tag=userPermissionTags.item(0)
        testTag(tag)
    }

    fun testTag(tag :Node){
        val sttrs=tag.attributes
        repeat(sttrs.length){
            val sttr=sttrs.item(it)
            println(sttr.nodeName)
        }
    }


    fun printFields(any :Any){
    }



    val ttBachPath="/Users/admin/Downloads/TTBach-dev/"
    val targetFileName="AndroidManifest.xml"
    fun searchFiles(dir:String=ttBachPath,name:String=targetFileName):List<File>{
        val file=File(dir)
        if(!file.exists()) return emptyList()
        if(file.isFile&&file.name==targetFileName) return listOf(file)
        if(!file.isDirectory) return emptyList()

        val kids=file.listFiles()
        val list= ArrayList<File>()
        for(kid in kids){
            val retFiles=searchFiles(kid.absolutePath,targetFileName)
            list.addAll(retFiles)
        }
        return list
    }
    /***
    author: liuchengbiao
    date_time: 2021/4/30 10:07 上午
    */
    @Test
    fun testSearchFiles(){
        val retFiles=searchFiles()
        val distinctList=retFiles.distinctBy { it.readText() }.map {
            val absPath=it.absolutePath
            val domFactory=DocumentBuilderFactory.newInstance()
            val domBuilder=domFactory.newDocumentBuilder()
            val domDoc=domBuilder.parse(File(absPath))
            val tags = domDoc.getElementsByTagName("uses-permission")
            val ret=ArrayList<Node>()
            repeat(tags.length){
                ret.add(tags.item(it))
            }
            ret
        }.flatMap {
            return@flatMap it
        }.map { it.attributes.getNamedItem("android:name").nodeValue }.distinct()
        repeat(distinctList.size){
            println(distinctList[it])
        }
    }

    /***
    author: liuchengbiao
    date_time: 2021/4/30 2:56 下午
    */
    @Test
    fun testMessageDigest(){
        val messageDigest=MessageDigest.getInstance("md5")
        val digest=messageDigest.digest("liuchengbiao".toByteArray())
        println(Base64.encodeBase64(digest).decodeToString().length)
    }

    /***
    author: liuchengbiao
    date_time: 2021/4/30 3:43 下午
    */
    @Test
    fun testUtilsMd5(){
        println(Utils.md5("Hello"))
    }
    /***
    author: liuchengbiao
    date_time: 2021/4/30 4:50 下午
    */
    @Test
    fun testMessageDigestAlgorithm(){
        val md=MessageDigest.getInstance("md5")
        println(md.digestLength)
        val securityProviders=Security.getProviders()
        securityProviders.map { it.services }.flatMap { it }.map { it.algorithm }
            .sorted()
            .forEach(::println)
    }

    /***
    author: liuchengbiao
    date_time: 2021/4/30 6:20 下午
    */
    @Test
    fun testGetTargetLang(){
        val rawText="hello"
        val targetLang="中文"
        val targetText=CrawlerUtils.getTargetLang(rawText,targetLang)
        println(targetText)
        val map=HashMap<String,Int>()
    }



    /***
    author: liuchengbiao
    date_time: 2021/5/6 9:28 上午
    */
    @Test
    fun testCrawlerHeaders(){
        CrawlerUtils.headers
    }

    /***
    author: liuchengbiao
    date_time: 2021/5/6 9:38 上午
    */
    @Test
    fun testUtilsReadLines(){
        Utils.readAllLines("templates/headers.txt").forEach(::println)
    }
    /***
    author: liuchengbiao
    date_time: 2021/5/6 9:46 上午
    */
    @Test
    fun testGetLinesKeyValue(){
        Utils.getLinesAllKeyValue().forEach(::println)
    }

    /***
    author: liuchengbiao
    date_time: 2021/5/6 9:47 上午
    */
    @Test
    fun testBuildRequest(){
        val request=CrawlerUtils.builderRequest("http://www.baidu.com")

        println(request.headers)

    }

    /***
    author: liuchengbiao
    date_time: 2021/5/6 10:13 上午
    */
    @Test
    fun testReadAllCET6_2(){
        val lines=Utils.readAllLines("static/Level8luan_2.json")

        val wordListMaps=ArrayList<Map<String,JsonObject>>()


        for(line in lines){
            val jsonMap=gson.fromJson<Map<String,JsonObject>>(line,object:TypeToken<Map<String,JsonElement>>(){}.type)
            wordListMaps.add(jsonMap)
        }

        println(wordListMaps.first())

        val firstMap=wordListMaps.first()



        for((key,value) in firstMap){




        }



    }


    /***
    author: liuchengbiao
    date_time: 2021/5/6 11:43 上午
    */
    @Test
    fun testParseJsonEl(){
        val first=Utils.parseJsonEl().first()
        val firstMap=Utils.parseJsonMap(first)
        for((key,value) in firstMap){
            println(key)
            println(value)
        }


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
    @Test
    fun testInsertDatabase(){

    }

    /***
    author: liuchengbiao
    date_time: 2021/5/6 3:53 下午
    12197
    */
    @Test
    fun testParseTargetByJsonElementApi11(){
        val maps=Utils.parseJsonMapApi11()
        maps.map { it["target"] }.forEach(::println)
    }


    /***
    author: liuchengbiao
    date_time: 2021/5/6 4:14 下午
    12197 11946
    */
    @Test
    fun testInsertWordQueryTable() {
        val maps = Utils.parseJsonMapApi11()
        var count=0
        for (map in maps) {
            val rawWord = map["headWord"] ?: ""
            val ukPhone = map["content_word_content_ukphone"] ?: ""
            val target = map["target"] ?: ""
            count+=if(RepoUtils.insertWordQueryTable(rawWord, target, ukPhone)) 1 else 0
            println(count)
        }
    }

    /***
    author: liuchengbiao
    date_time: 2021/5/6 4:29 下午
    */
    @Test
    fun testGenerateSqlFile(){
        val filename="word_query.sql"
        val maps = Utils.parseJsonMapApi11()
        var count=0
        val builder=StringBuilder()
        val sqls=ArrayList<String>()
        for (map in maps) {
            val rawWord = map["headWord"] ?: ""
            val ukPhone = map["content_word_content_ukphone"] ?: ""
            val target = map["target"] ?: ""
//            count+=if(RepoUtils.insertWordQueryTable(rawWord, target, ukPhone)) 1 else 0
//            println(count)
            val sql=WordQueryTable.getInsertSql(rawWord,target,ukPhone)
            count++
//            println("${count}   ${sql}")
            builder.append(sql).append(";\n")
            sqls.add(sql)
        }

//        JdbcUtils.runBachSql(sqls)

        writeAll(filename, builder.toString())
    }

    /***
    author: liuchengbiao
    date_time: 2021/5/6 4:58 下午
    */
    @Test
    fun testGetQueryString(){

        println(WordQueryTable.getQueryStringSub("Hello"))

    }



    /***
    author: liuchengbiao
    date_time: 2021/5/6 5:46 下午
    */
    @Test
    fun testToReal(){
        println("\"Hello's\"".toReal())
    }


    /***
    author: liuchengbiao
    date_time: 2021/5/6 5:57 下午
    */
    @Test
    fun testGetTargetByRawWord(){
        println(RepoUtils.getTargetByRawWord("h"))
    }









}

class MyLazy<T>(private val callback:()->T){

    private var callResult:T?=null

    operator fun getValue(thisRef: Owner, property: KProperty<*>):T{
        callResult?.let {
            return it
        }
        callResult=callback()
        return callResult?:callback()
    }

}
class User{

}


