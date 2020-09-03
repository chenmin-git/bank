package cn.controller;

import cn.pojo.ResultMsg;
import cn.service.BankService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class LoginController {
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Autowired
    BankService bankService;

    @GetMapping("/exit")
    public String exit(HttpSession session){
        session.removeAttribute("loginSession");
        return "redirect:/login";
    }
    @PostMapping("/doLogin")
    @ResponseBody
    public ResultMsg doLogin(HttpSession session,String cardNo, String password,String kaptcha) {
     if(session.getAttribute("kaptcha") == null ||  !session.getAttribute("kaptcha").equals(kaptcha) ){
            return ResultMsg.fail("验证码错误");
        }
        System.out.println("carNo"+cardNo+"-password"+password+"-kaptcha"+kaptcha);
     try {
         if(bankService.login(cardNo,password)){
             session.setAttribute("loginSession",cardNo);
             return ResultMsg.succes("登录成功","");
         }
     }catch (Exception e){
         return ResultMsg.fail(e.getMessage());
     }
        return null;
    }

    @GetMapping("/getKaptcha")
    @ResponseBody
    public void getKaptcha(HttpServletResponse response, HttpSession session)  throws IOException {
        byte[] image = null;
        ByteOutputStream byteOutputStream = new ByteOutputStream();
        try {
            String kaptcha = defaultKaptcha.createText();
            BufferedImage bufferedImage = defaultKaptcha.createImage(kaptcha);
            ImageIO.write(bufferedImage, "jpg", byteOutputStream);
            session.setAttribute("kaptcha",kaptcha);
        }catch (Exception e){
            e.printStackTrace();
        }
        response.setContentType("image/jpg");
        image = byteOutputStream.getBytes();
        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(image);
        servletOutputStream.flush();
        servletOutputStream.close();

    }
}
