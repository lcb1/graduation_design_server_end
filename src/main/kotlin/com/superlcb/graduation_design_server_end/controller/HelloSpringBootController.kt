package com.superlcb.graduation_design_server_end.controller

import org.jetbrains.annotations.TestOnly
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class HelloSpringBootController {

    @GetMapping("/Hello/{name}")
    fun hello(@PathVariable("name") name:String):String{
        return name
    }

}