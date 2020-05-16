package com.group.qa.controller;

import com.group.qa.dto.NotificationDto;
import com.group.qa.dto.PageDto;
import com.group.qa.entity.Question;
import com.group.qa.entity.Comment;
import com.group.qa.entity.User;
import com.group.qa.mapper.NotificationMapper;
import com.group.qa.mapper.UserMapper;
import com.group.qa.service.CommentService;
import com.group.qa.service.NotificationService;
import com.group.qa.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

//个人中心
@Controller
public class PersonalController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private QuestionService questionService;
    @Resource
    private CommentService commentService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private NotificationMapper notificationMapper;
    @GetMapping("/personal/{action}")
    public String personal(@PathVariable(name = "action")String action,
                           Model model,
                           HttpServletRequest request,
                           @RequestParam(name = "page",defaultValue = "1")int page,
                           @RequestParam(name = "size",defaultValue = "10")int size){
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
        if (action.equals("questions")){
            model.addAttribute("section","questions");
            model.addAttribute("sectionname","我的问题");
            PageDto<Question> pagination=questionService.list(user.getId(),page,size);
            model.addAttribute("pagination", pagination);
        }
        if (action.equals("comments")){
            model.addAttribute("section","comments");
            model.addAttribute("sectionname","我的回答");
            PageDto<Comment> pagination=commentService.list(user.getId(),page,size);
            model.addAttribute("pagination", pagination);
        }
        else if (action.equals("information")){
            model.addAttribute("section","information");
            model.addAttribute("sectionname","我的消息");
            PageDto<NotificationDto> notifications= notificationService.list(user.getId(),page,size);
            model.addAttribute("notifications",notifications);
        }


        return "personal";
    }
}
