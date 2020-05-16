package com.group.qa.controller;

import com.group.qa.Cache.HotTagCache;
import com.group.qa.dto.PageDto;
import com.group.qa.entity.Question;
import com.group.qa.entity.User;
import com.group.qa.mapper.NotificationMapper;
import com.group.qa.mapper.UserMapper;
import com.group.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

//首页
@Controller
public class indexController {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;
    @Resource
    private NotificationMapper notificationMapper;
    @Autowired
    private HotTagCache hotTagCache;

//    @Autowired
//    private QuestionQueryDTO questionQueryDTO;
    @GetMapping("/index")
    public String index(HttpServletRequest request, Model model,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        @RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "tag", required = false) String tag,
                        @RequestParam(name = "sort", required = false) String sort) {
        //查找cookies，观察是否有token存在
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "login";
        }
        User user = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                String token = cookie.getValue();
                user = userMapper.findBytoken(token);
                if (user != null) {
                    request.getSession().setAttribute("user", user);
                    //获取未读的消息数量
                    int unreadnum=notificationMapper.getunreadcount(user.getId());
                    request.getSession().setAttribute("unreadnum",unreadnum);
                }
                break;
            }
        }
        PageDto pagination = questionService.list(search, tag, sort, page, size);
        List<String> tags = hotTagCache.getHots();
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tags);
        model.addAttribute("sort", sort);

        //获取阅读量最高的十篇问题
        // 热议话题
        List<Question> questions= questionService.gettopten();
        model.addAttribute("topquestion",questions);

        //搜索

        //最新sort='new'
        //最热，7天最热，sort == 'hot7'



        return "index";

    }

}
