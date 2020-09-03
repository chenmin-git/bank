package cn.service;

import cn.mapper.BankMapper;
import cn.pojo.Account;
import cn.pojo.Transaction_record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {
    @Resource
    private BankMapper bankMapper;
    @Override
    public boolean login(String cardNo, String password) throws Exception{
        if(null == cardNo || "".equals(cardNo)){
            throw new Exception("卡号有误");
        }
        Account account = bankMapper.getCardByNo(cardNo);
        if(null != account){
            if(account.getPassword().equals(password)){
                if(account.getStatus()==0){
                    throw new Exception("该用户已被冻结");
                }
                return true;
            }else{
                throw new Exception("密码不正确");
            }
        }else{
            throw new Exception("卡号不正确");
        }
    }

    @Override
    public double getBanlance(String cardNo) {
        Account account = bankMapper.getCardByNo(cardNo);
        return account.getBalance();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean transfer(String from , String to ,double money) throws Exception{
        if(from.equals(to)){
            throw new Exception("不能向自己转账");
        }
        Account accountTo = bankMapper.getCardByNo(to);
        if(null == accountTo){
            throw new Exception("转入的账号不存在");
        }else if(accountTo.getStatus() == 0){
            throw  new Exception("转入的账号已冻结");
        }
        Account accountFrom = bankMapper.getCardByNo(from);
        if(money > accountFrom.getBalance()){
            throw new Exception("您的账户余额不足");
        }
        try {
            if(bankMapper.minusBalance(from,money)==1&&bankMapper.addBalance(to,money)==1){
                Transaction_record record = new Transaction_record();
                record.setCardNo(from);
                record.setExpense(money);
                record.setTransactionType("消费");
                record.setBalance(this.getBanlance(from));
                record.setRemark("转账给" + to + " 共计-" + money + "元");
                bankMapper.expense(record);
                Transaction_record recordTo = new Transaction_record();
                recordTo.setCardNo(to);
                recordTo.setIncome(money);
                recordTo.setBalance(this.getBanlance(to));
                recordTo.setTransactionType("存入");
                recordTo.setRemark("来自" + from + " 共计-" + money + "元");
                bankMapper.income(recordTo);
            }else{
                throw new RuntimeException("操作失败,账号已冻结");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("操作失败");
        }
        return true;
    }

    @Override
    public List<Transaction_record> getLog(String from, String to, String cardNo, int index) {
        return bankMapper.getLog(from,to,cardNo,(index-1)*2);
    }

    @Override
    public boolean rePassword(String cardNo, String oldPassword, String newPassword) throws  Exception{
        Account account = bankMapper.getCardByNo(cardNo);
        if(null == account){
            throw  new Exception("账号不存在哦");
        }
        if(!account.getPassword().equals(oldPassword)){
            throw  new Exception("密码错误");
        }
        if(bankMapper.update(newPassword,cardNo)>0){
            return true;
        }
        return false;
    }

    @Override
    public int getCount(String from, String to, String cardNo) {
        return bankMapper.getCount(from,to,cardNo);
    }
}
