package com.superlcb.graduation_design_server_end.util

import java.util.*
import kotlin.collections.HashMap

object JdbcUtils {


    fun executeSql(sql:String):List<Map<String,String>>{
        val list=LinkedList<Map<String,String>>()

        val connection=DataSourcePool.getConnection()
        connection.autoCommit=true

        val state=connection.createStatement()
        val result=state.executeQuery(sql)

        val metaData=result.metaData
        val colNames= Array<String>(metaData.columnCount){
            metaData.getColumnName(it+1)
        }
        while(result.next()){
            val rowMap=HashMap<String,String>()
            for(colName in colNames){
                rowMap[colName]=result.getString(colName)
            }
            list.add(rowMap)
        }
        return list
    }

    fun runSql(sql:String):Boolean{
        val connection=DataSourcePool.getConnection()
        connection.autoCommit=true
        val statement=connection.createStatement()
        return statement.execute(sql)
    }

    fun runBachSql(sqls:List<String>){
        val connection=DataSourcePool.getConnection()
        connection.autoCommit=false
        val statement=connection.createStatement()
        for(sql in sqls){
            statement.addBatch(sql)
        }
        statement.executeBatch()
        connection.commit()
    }



}