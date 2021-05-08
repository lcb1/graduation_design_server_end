package com.superlcb.graduation_design_server_end.controller

import com.superlcb.graduation_design_server_end.repo.RepoUtils
import com.superlcb.graduation_design_server_end.repo.WordQueryTable
import com.superlcb.graduation_design_server_end.util.Result
import com.superlcb.graduation_design_server_end.util.Utils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class WordQueryController {


    companion object{
        private const val PATH_RAW_WORD="raw_word"
        private const val PATH_TARGET="target"
        private const val PAGE_N="page_N"
    }


    @GetMapping("/word/query/raw_word/{${PATH_RAW_WORD}}/{${PAGE_N}}")
    fun queryByRawWord(@PathVariable(PATH_RAW_WORD) rawWord:String,@PathVariable(PAGE_N) pageN:Int=10):Result{
        val candicates=RepoUtils.getTargetByRawWord(rawWord,pageN)
        return Result.isOk {
            candicates
        }
    }

    @GetMapping("/word/query/target/{${PATH_TARGET}}/{${PAGE_N}}")
    fun queryByTarget(@PathVariable(PATH_TARGET) target:String,@PathVariable(PAGE_N) pageN: Int=10):Result{
        return Result.isOk {
            RepoUtils.getRawWordByTarget(target,pageN)
        }
    }


    @GetMapping("/word/query/raw_word/api11/{${PATH_RAW_WORD}}/{${PAGE_N}}")
    fun queryByRawWordApi11(@PathVariable(PATH_RAW_WORD) rawWord: String,@PathVariable(PAGE_N) pageN: Int=10):Result{
        val retData=ArrayList<Map<String,String>>(pageN)
        val set=HashSet<String>()
        Utils.insertByStep(rawWord){

            val data=RepoUtils.getRealTargetByRawWord(it,pageN)
                .map{ inner->
                    inner to inner[WordQueryTable.RAW_WORD.label]!!
                }.filter { inner->
                    val flag=set.contains(inner.second)
                    set.add(inner.second)
                    !flag
                }.map{ inner->
                    inner.first
                }
                .sortedBy { item->
                item[WordQueryTable.RAW_WORD.label]?.length
            }
            retData.addAll(data)
            retData.size<pageN
        }
        return Result.isOk {
            if(retData.size>pageN) retData.subList(0,pageN) else retData
        }
    }





}