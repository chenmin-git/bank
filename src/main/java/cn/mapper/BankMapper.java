package cn.mapper;

import cn.pojo.Account;
import cn.pojo.Transaction_record;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BankMapper {
    @Select("select * from account where cardno = #{cardNo}")
    Account getCardByNo(@Param("cardNo") String cardNo);

    @Insert("insert into transaction_record(cardno,expense,balance,transaction_type,remark) values(#{cardNo},#{expense},#{balance},#{transactionType},#{remark})")
    int expense(Transaction_record record);

    @Insert("insert into transaction_record(cardno,income,balance,transaction_type,remark) values(#{cardNo},#{income},#{balance},#{transactionType},#{remark})")
    int income(Transaction_record record);

    @Update("update account set balance = balance-#{money} where cardNo = #{cardNo}")
    int minusBalance(@Param("cardNo") String cardNo, @Param("money") double money);

    @Update("update account set balance = balance+#{money} where cardNo = #{cardNo}")
    int addBalance(@Param("cardNo") String cardNo, @Param("money") double money);

    @Update("update account set password = #{password} where cardNo = #{cardNo}")
    int update(@Param("password") String password, @Param("cardNo") String cardNo);

    @Select("SELECT * FROM transaction_record WHERE transaction_date BETWEEN #{from} AND #{to} and cardno = #{cardNo} order by transaction_date desc limit #{index},2 ")
    List<Transaction_record> getLog(String from ,String to,String cardNo,int index);


    @Select("SELECT count(1) FROM transaction_record WHERE transaction_date BETWEEN #{from} AND #{to} and cardno = #{cardNo} ")
    int getCount(String from ,String to,String cardNo);
}
