package com.superlcb.graduation_design_server_end.controller

import com.superlcb.graduation_design_server_end.repo.RepoUtils
import com.superlcb.graduation_design_server_end.util.CrawlerUtils
import com.superlcb.graduation_design_server_end.util.Result
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TranslationController {
    companion object{
        private const val PATH_RAW_TEXT="rawText"
    }
    @GetMapping("/translation/{${PATH_RAW_TEXT}}")
    fun translation(@PathVariable(PATH_RAW_TEXT) rawText:String):Result{
        val retData=RepoUtils.findTranslationByRawText(rawText)
        if(retData!=null) return Result.isOk { retData }
        val targetText=CrawlerUtils.getTargetLang(rawText)
        RepoUtils.insertTranslationTable(rawText,targetText)
        return Result.isOk {
            targetText
        }
    }
    @GetMapping("/translation/list/all/{${PATH_RAW_TEXT}}")
    fun translationListAll(@PathVariable(PATH_RAW_TEXT) rawText: String):Result{
        return Result.isOk {
            CrawlerUtils.getEachText(rawText)
        }
    }


    @GetMapping("/translation/api11/{${PATH_RAW_TEXT}}")
    fun translationApi11(@PathVariable(PATH_RAW_TEXT) rawText: String):Result{
        val retData=RepoUtils.findTranslationByRawText(rawText)
        if(!retData.isNullOrBlank()) return Result.isOk { retData }
        val targetText =CrawlerUtils.getTargetLangByTrFilter(rawText)
        RepoUtils.insertTranslationTable(rawText,targetText)
        return Result.isOk {
            targetText
        }
    }







}