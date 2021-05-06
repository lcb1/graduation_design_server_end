package com.superlcb.graduation_design_server_end.controller

import com.superlcb.graduation_design_server_end.repo.RepoUtils
import com.superlcb.graduation_design_server_end.repo.WordQueryTable
import com.superlcb.graduation_design_server_end.util.Result
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class WordQueryController {


    companion object{
        private const val PATH_RAW_WORD="raw_word"
        private const val PATH_TARGET="target"
    }


    @GetMapping("/word/query/raw_word/{${PATH_RAW_WORD}}")
    fun queryByRawWord(@PathVariable(PATH_RAW_WORD) rawWord:String):Result{
        val candicates=RepoUtils.getTargetByRawWord(rawWord)
        return Result.isOk {
            candicates
        }
    }

    @GetMapping("/word/query/target/{${PATH_TARGET}}")
    fun queryByTarget(@PathVariable(PATH_TARGET) target:String):Result{
        return Result.isOk {
            RepoUtils.getRawWordByTarget(target)
        }
    }




}