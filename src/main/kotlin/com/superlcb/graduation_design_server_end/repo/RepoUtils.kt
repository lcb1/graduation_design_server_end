package com.superlcb.graduation_design_server_end.repo

import com.superlcb.graduation_design_server_end.util.JdbcUtils
import com.superlcb.graduation_design_server_end.util.toReal

object RepoUtils {


    fun insertTranslationTable(rawText:String,targetText:String):Boolean{
        val sql= TranslationTable.getInsertSql(rawText,targetText)
        return JdbcUtils.runSql(sql)
    }

    fun findTranslationByRawText(rawText: String):String?{
        val sql = TranslationTable.getFindSql(rawText)
        val repoData=JdbcUtils.executeSql(sql).apply {
            if(isNullOrEmpty()) return@findTranslationByRawText null
        }.first()
        return repoData[TranslationTable.TARGET_TEXT.label]
    }



    fun insertWordQueryTable(rawWord:String,target:String,ukPhone:String):Boolean{
        val sql=WordQueryTable.getInsertSql(rawWord,target,ukPhone)
//        println(sql)
        return JdbcUtils.runSql(sql)
    }

    fun getTargetByRawWord(rawWord: String,pageN: Int=10):List<Map<String,String>>{
        val sql=WordQueryTable.getFindTargetByRawWord(rawWord,pageN)
        return JdbcUtils.executeSql(sql)
    }

    fun getRealTargetByRawWord(rawWord: String,pageN: Int=10):List<Map<String,String>>{
        val sql=WordQueryTable.getRealFindTargetByRawWord(rawWord,pageN)
        return JdbcUtils.executeSql(sql)
    }


    fun getRawWordByTarget(target: String,pageN: Int=10):List<Map<String,String>>{
        val sql=WordQueryTable.getFindRawWordByTarget(target,pageN)
        return JdbcUtils.executeSql(sql)
    }





}

enum class WordQueryTable(val label:String){
    TABLE_NAME("word_query_table"),
    RAW_WORD("raw_word"),
    TARGET("target"),
    UK_PHONE("uk_phone");

    override fun toString(): String {
        return this.label
    }
    companion object{
        private val PAGE_N=10
        fun getInsertSql(rawWord: String,target: String,ukPhone:String):String{
            return "insert into ${TABLE_NAME}(${RAW_WORD},${TARGET},${UK_PHONE}) values(${rawWord.toReal()},${target.toReal()},${ukPhone.toReal()})"
        }

        fun getFindTargetByRawWord(rawWord: String,pageN:Int= PAGE_N):String{
            return getRealFindTargetByRawWord(getQueryStringSub(rawWord),pageN)
        }
        fun getRealFindTargetByRawWord(rawWord: String,pageN: Int= PAGE_N):String{
            return "select ${RAW_WORD},${TARGET},${UK_PHONE} from ${TABLE_NAME} where ${RAW_WORD} like ${rawWord.toReal()} limit ${pageN}"
        }


        fun getFindRawWordByTarget(target: String,pageN: Int= PAGE_N):String{
            return  "select ${RAW_WORD},${TARGET},${UK_PHONE} from ${TABLE_NAME} where ${TARGET} like ${getQueryStringAll(target).toReal()} limit ${pageN}"
        }


        fun getQueryStringSub(query:String):String{
            val rawQuery=query.trim().toLowerCase()
            val builder=StringBuilder()
//            builder.append('%')
            for(c in rawQuery){
                builder.append(c)
                builder.append('%')
            }

            return builder.toString()
        }
        fun getQueryStringAll(query: String):String{
            val rawQuery=query.trim().toLowerCase()
            val builder=StringBuilder()

            for(c in rawQuery){
                builder.append('%')
                builder.append(c)
            }
            builder.append('%')
            return builder.toString()
        }



    }


}


enum class TranslationTable(val label:String){
    TABLE_NAME("translation_text_table"),
    RAW_TEXT("raw_text"),
    TARGET_TEXT("target_text");

    override fun toString(): String {
        return this.label
    }
    companion object{
        fun getInsertSql(rawText: String,targetText: String):String{
            return "insert into  ${TABLE_NAME}(${RAW_TEXT},${TARGET_TEXT}) values(${rawText.toReal()},${targetText.toReal()})"
        }
        fun getFindSql(rawText: String):String{
            return "select ${TARGET_TEXT} from ${TABLE_NAME} where ${RAW_TEXT}=${rawText.toReal()}"
        }
    }





}