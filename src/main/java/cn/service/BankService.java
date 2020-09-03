package cn.service;

import cn.pojo.Transaction_record;

import java.util.List;

public interface BankService {
    boolean login(String cardNo,String password) throws Exception;

    double getBanlance(String cardNo);

    boolean transfer(String from , String to ,double money) throws Exception;

    List<Transaction_record> getLog(String from ,String to , String cardNo , int index);

    boolean rePassword(String cardNo ,String oldPassword, String newPassword) throws  Exception;

    int getCount(String from ,String to , String cardNo);
}
