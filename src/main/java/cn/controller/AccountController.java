package cn.controller;

import cn.pojo.Account;
import cn.pojo.ResultMsg;
import cn.pojo.Transaction_record;
import cn.service.BankService;
import javafx.scene.control.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AccountController {
    @Autowired
    private BankService bankService;
    @GetMapping("/getBalance")
    public String getBalance(HttpServletRequest request, HttpSession session){
        String cardNo = session.getAttribute("loginSession").toString();
        if(null != cardNo && !"".equals(cardNo)){
            request.setAttribute("banlance",bankService.getBanlance(cardNo));
        }else{
            return "redirect:/error";
        }
        return "banlance";
    }

    @PostMapping("/doTransfer")
    @ResponseBody
    public ResultMsg doTransfer(HttpSession session, String to, double money) {
        try {
            String cardNo = session.getAttribute("loginSession").toString();
            if (null != cardNo && !"".equals(cardNo)) {
                try {
                    if(bankService.transfer(cardNo, to, money)){
                        return ResultMsg.succes("操作成功","");
                    }else{
                        return ResultMsg.fail("操作失败");
                    }
                } catch (Exception e) {
                    return ResultMsg.fail(e.getMessage());
                }
            } else {
                return ResultMsg.fail("登录验证过期，请重新登录");
            }
        }catch (Exception e){
            return ResultMsg.fail("登录验证过期，请重新登录");
        }
    }

    @GetMapping("/doLog")
    public String getLog(Model model, @RequestParam("from") String from , @RequestParam("to") String to , HttpSession session , @RequestParam(value = "index",defaultValue = "1",required = false) int index){
        System.out.println("from" + from + "---to" + to + "----index" + index + "---cardNo" + session.getAttribute("loginSession"));
        if(null == session.getAttribute("loginSession")){
            return "redirect:/error";
        }
        String cardNo = session.getAttribute("loginSession").toString();
        model.addAttribute("from",from);
        model.addAttribute("to", to);
        List<Transaction_record> records = bankService.getLog(from,to,cardNo,index);
        int count = bankService.getCount(from,to,cardNo);
        model.addAttribute("count",(count % 2 == 0) ? (count/2):(count/2+1));
        model.addAttribute("list",records);
        model.addAttribute("index",index);
        for(Transaction_record record :records){
            System.out.println(record.getTransactionDate());
        }
        return "log";
    }
    @GetMapping("/rePassword")
    @ResponseBody
    public ResultMsg rePassword(HttpSession session, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword){
        System.out.println(oldPassword+"---"+newPassword);
        try {
            if(null == session.getAttribute("loginSession")){
                return ResultMsg.fail("登录账号已过期");
            }
            System.out.println(oldPassword+"--1-"+newPassword);
            String cardNo = session.getAttribute("loginSession").toString();
            if(bankService.rePassword(cardNo,oldPassword,newPassword)){
                session.removeAttribute("loginSession");
                return ResultMsg.succes("密码更改成功","");
            }else{
                return ResultMsg.fail("密码更改失败，请重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(oldPassword+"--2-"+newPassword);
            return ResultMsg.fail(e.getMessage());
        }
    }
}
