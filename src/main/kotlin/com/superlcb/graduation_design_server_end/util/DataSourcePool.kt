package com.superlcb.graduation_design_server_end.util

import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.ConcurrentLinkedDeque

object DataSourcePool {
    val pool by lazy {
        ConcurrentLinkedDeque<Connection>()
    }
    val poolDefaultSize by lazy {
        10
    }
    val url by lazy {
        DataBaseConfig.url
    }
    val username by lazy {
        DataBaseConfig.username
    }
    val password by lazy {
        DataBaseConfig.password
    }


    fun getConnection():Connection{
        while(pool.isNotEmpty()){
            val connection= pool.poll()
            if(!connection.isClosed) return connection
        }
        return getNewConnection()
    }
    fun recycle(connection: Connection){
        pool.offer(connection)
    }


    private fun getNewConnection():Connection{
        return DriverManager.getConnection(url, username, password)
    }

    private fun fullPool(){
        repeat(poolDefaultSize){
            pool.offer(getNewConnection())
        }
    }



}