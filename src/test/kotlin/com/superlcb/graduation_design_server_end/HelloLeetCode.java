package com.superlcb.graduation_design_server_end;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class HelloLeetCode {


    public int leastBricks(List<List<Integer>> wall) {
        if(wall==null||wall.size()==0) return 0;
        ArrayList<Set<Integer>> listWalls=new ArrayList<>(wall.size());
        Set<Integer> allBricks=new HashSet<>();
        for(int j=0;j<wall.size();j++){
            List<Integer> itemWall=wall.get(j);
            int sum=0;
            HashSet<Integer> setLine=new HashSet<>();
            for(int i=0;i<itemWall.size()-1;i++){
                sum+=itemWall.get(i);
                setLine.add(sum);
            }
            listWalls.set(j,setLine);
            allBricks.addAll(setLine);
        }
        if(allBricks.isEmpty()) return wall.size();
        int brickCount=Integer.MAX_VALUE;
        for(int brick : allBricks){
            int count=0;
            for(Set<Integer> set : listWalls){
                if(!set.contains(brick)) count++;
            }
            brickCount=Math.min(count,brickCount);
            if(brickCount==0) return 0;
        }
        return brickCount;
    }


    public int reverse(int x) {
        int reverseX=0;
        while(x!=0){
            int mod=x%10;
            x/=10;
            if(judge(reverseX,mod)) return 0;
            reverseX=reverseX*10+mod;
        }
        return reverseX;
    }

    boolean judge(int reverseX,int mod){
        return judgeMin(reverseX,mod)||judgeMin(reverseX,mod);
    }

    boolean judgeMax(int prefix,int postfix){
        int prefixMax=Integer.MAX_VALUE/10;
        int postfixMax=Integer.MAX_VALUE%10;
        return prefix>prefixMax||(prefix==prefixMax&&postfix>postfixMax);
    }

    boolean judgeMin(int prefix,int postfix){
        int prefixMin=Integer.MIN_VALUE/10;
        int postfixMin=Integer.MIN_VALUE%10;
        return prefix<prefixMin||(prefix==prefixMin&&postfix<postfixMin);
    }
    class Employee {
        public int id;
        public int importance;
        public List<Integer> subordinates;
    };


    public int getImportance(List<Employee> employees, int id) {
        HashMap<Integer,Employee> employeeMap=new HashMap<>();
        for(Employee employee : employees){
            employeeMap.put(employee.id,employee);
        }



        Queue<Integer> que=new LinkedList<>();
        int result=0;
        que.offer(id);
        while(!que.isEmpty()){
            int nowId=que.poll();
            Employee nowEmployee=employeeMap.get(nowId);
            result+=nowEmployee.importance;
            que.addAll(nowEmployee.subordinates);
        }
        return result;
    }


    /***
     * @author: lcb
     * @date : 2021/5/2 3:59 下午
     * MethodDescription:
     */
    @Test
    public void test(){




    }

}
