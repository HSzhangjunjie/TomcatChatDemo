package cn.test.servlet;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: HandSomeMaker
 * @date: 2020/2/13 2:34
 */
@WebServlet(name = "loginServlet",urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    /**
     * 未定义数据库，故暂时写死
     */
    private static final String PASSWORD = "123456";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置编码
        resp.setCharacterEncoding("UTF-8");
        //接受页面传递的参数，用户名/密码
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Map<String, Object> resultMap = new HashMap<>();
        //判定用户名密码是否正确
        //如果正确，响应登陆成功信息
        if (PASSWORD.equals(password)) {
            resultMap.put("success", true);
            resultMap.put("message", "登陆成功");
            //保存登录信息
            req.getSession().setAttribute("username", username);
        }
        //如果不正确响应登陆失败信息
        else {
            resultMap.put("success", false);
            resultMap.put("message", "登陆失败，用户名或者密码错误");
        }
        //转变为JSON数据
        resp.getWriter().write(JSON.toJSONString(resultMap));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}

