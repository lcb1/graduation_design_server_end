package com.superlcb.graduation_design_server_end.util

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File

object CrawlerUtils {

    private val selectFilter by lazy {
        ".c-border span,.c-border p"

    }
     val selectFilterByTr by lazy {
        ".c-border tr,.c-border p.op_sp_fanyi_line_two"
    }


    private val selectFilterByTable by lazy {
        ".c-border table"
    }

    private val selectFilterByExample by lazy {
        "div.op_dict3_lineone_result,div.op_dict_linetwo_result,.c-border tr"
    }

    val isSetCookie=false
    private const val COOKIE_HEADER="Cookie"
    const val HEADER_SEPARATOR=": "
    private const val HEADER_FILE_NAME="templates/headers.txt"
    private const val isSetHeader=false

    private val okHttpClient by lazy {
        OkHttpClient()
    }
    private val baseUrl by lazy {
        "http://www.baidu.com/s?wd="
    }

    private val isPrintHeaders=true
    val headers by lazy {
        Utils.getLinesAllKeyValue()
    }




    private fun isPrintln(msg:Any?,isPrint:Boolean=true){
        if(isPrint) println(msg)
    }


    fun getAllHeaders():List<Pair<String,String>>{
        val allData=Utils.readAll(HEADER_FILE_NAME)
        val allPair=allData.split(HEADER_SEPARATOR)
        val listPair=ArrayList<Pair<String,String>>()
        val start=1
        for(i in start until allPair.size step 2){
            listPair.add(allPair[i-1] to allPair[i])
        }
        return listPair
    }



    fun getPage(url:String):String{
        val realUrl="${baseUrl}${url}"
        val request= builderRequest(realUrl)
        val response=okHttpClient.newCall(request).execute()
        return response.body?.string()?:""
    }

    fun builderRequest(realUrl:String):Request{

        return Request.Builder().url(realUrl).apply {
            setHeader(this)
            url(realUrl)
        }.build()
    }

    fun setHeader(reqBuilder:Request.Builder){
        if(!isSetHeader) return
        for(header in headers){

            if(!isSetCookie&&ignoreIsEquals(header.first, COOKIE_HEADER)) continue
            reqBuilder.addHeader(header.first,header.second)
        }
    }

    private fun ignoreIsEquals(first:String,second:String):Boolean{
        return first.trim().equals(second.trim(), ignoreCase = true)
    }



    fun getHtml(url:String,filepath:String="crawler.html"){
        val pageText= getPage(url)
        File(filepath).writeText(pageText)
    }
//    fun getOutHtml(rawText:String,target:String):String{
//        val url = getRealUrlPost(rawText,target)
//        val page= getPage(url)
//        val doc=Jsoup.parse(page)
//        doc.select("c-board")
//        return doc.outerHtml()
//    }
    fun isEnglish(text:String):Boolean{
        return text.toByteArray().size==text.length
    }
    fun getRealUrlPost(rawText: String,target: String="auto"):String{
        val realTarget=if(target=="auto"){
            if(isEnglish(rawText)) "中文" else "英文"
        }else{
            target
        }
        return "${rawText} 的${realTarget}翻译"
    }




    fun getEachText(rawText: String,target: String="auto"):List<String>{
        return  getEachTextByFilter(rawText,target=target)
    }



    fun getTargetLang(rawText: String,target: String="auto"):String{
        val filterTagsText = getEachText(rawText,target)
        val builder=StringBuilder()
        val start=1
        val end=filterTagsText.size-(if(filterTagsText.size==2) 0 else 1)
        for(i in start until end step 2){
            builder.append(filterTagsText[i])
            builder.append("  ")
            if(i+1<end){ builder.append(filterTagsText[i+1]) }
            builder.append("\n")
        }
        return builder.toString()
    }


    fun getTargetLangByTrFilter(rawText: String,target: String="auto"):String{
        val filterTagsText= getEachTextByFilter(rawText,this.selectFilterByTr,target)
        val builder=StringBuilder()
        val start=0
        for (i in start until filterTagsText.size){
            builder.append(filterTagsText[i])
            builder.append("\n")
        }
        return builder.toString()
    }


    fun getEachTextByFilter(rawText:String,selectFilter:String=this.selectFilter,target: String="auto"):List<String>{
        val urlPost= getRealUrlPost(rawText,target)
        val page= getPage(urlPost)

        val doc=Jsoup.parse(page)
        val filterTags=doc.select(selectFilter)

        val filterTagsText=filterTags.eachText()
        return filterTagsText
    }


    fun getTargetLangByExample(rawText: String,target: String="auto"):String{
        val texts=getEachTextByFilter(rawText,this.selectFilterByExample,target)
        val builder=StringBuilder()

        if(texts.isNullOrEmpty()){
            return "null"
        }
        var start=0
        if(texts.first().trim()==rawText.trim()){
            start=1
        }

        for(i in start until texts.size){
            builder.append(texts[i])
            builder.append("\n")

        }
        return builder.toString()
    }



}