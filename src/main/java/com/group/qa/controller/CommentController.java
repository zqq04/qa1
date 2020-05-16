package com.group.qa.controller;

import com.group.qa.dto.CommentCreateDto;
import com.group.qa.dto.CommentDto;
import com.group.qa.dto.ResultDto;
import com.group.qa.entity.Comment;
import com.group.qa.entity.Notification;
import com.group.qa.entity.Question;
import com.group.qa.entity.User;
import com.group.qa.enums.NotificationStatusEnum;
import com.group.qa.enums.notificationEnum;
import com.group.qa.mapper.NotificationMapper;
import com.group.qa.mapper.QuestionMapper;
import com.group.qa.mapper.UserMapper;
import com.group.qa.mapper.CommentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

//回复功能
@Controller
public class CommentController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private NotificationMapper notificationMapper;

    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDto commentCreateDto,
                       HttpServletRequest request){
        //把User写进session
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
        //把评论插入数据库
        Comment comment=new Comment();
        comment.setParent_id(commentCreateDto.getParent_id());
        comment.setContent(commentCreateDto.getContent());
        comment.setType(commentCreateDto.getType());
        comment.setCreatetime(System.currentTimeMillis());
        comment.setCommentor(user.getId());
        commentMapper.insert(comment);

        if (commentCreateDto.getType()==2){
            //把回复评论的通知插入数据库
            Notification notification=new Notification();
            notification.setNotifier(comment.getCommentor());
            Comment comment2=commentMapper.getparentbyid(commentCreateDto.getParent_id());
            notification.setReceiver(comment2.getCommentor());
            notification.setOuterid(commentCreateDto.getParent_id());
            notification.setType(notificationEnum.NOTIFICATION_COMMENT.getType());
            notification.setCreatetime(System.currentTimeMillis());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notificationMapper.insert(notification);

            //增加评论数
            commentMapper.updatecommentcount(commentCreateDto.getParent_id());
        }else {
            //把回复问题的通知插入数据库
            Question question=questionMapper.getbyId(commentCreateDto.getParent_id());
            Notification notification=new Notification();
            notification.setNotifier(user.getId());
            notification.setReceiver(question.getCreateid());
            notification.setOuterid(commentCreateDto.getParent_id());
            notification.setType(notificationEnum.NOTIFICATION_QUESTION.getType());
            notification.setCreatetime(System.currentTimeMillis());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notificationMapper.insert(notification);
            //增加问题回复量
            questionMapper.updatecomment(commentCreateDto.getParent_id());
        }
        ResultDto resultDto=new ResultDto();
        return resultDto.success();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDto<List<CommentDto>> comments(@PathVariable(name = "id") int id,
                                                HttpServletRequest request){
        //查找type=2，即是回复评论的评论
        List<Comment> comments = commentMapper.getCommentByid(id,2);
        List<CommentDto> commentDto=new ArrayList<>();
        //找到User
        Cookie[] cookies = request.getCookies();
        User user = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                String token = cookie.getValue();
                user = userMapper.findBytoken(token);
                break;
            }
        }
        //把二级评论和对应的User写进每个CommentDto集合中
        for (Comment comment:comments){
            CommentDto dto=new CommentDto();
            BeanUtils.copyProperties(comment,dto);
            dto.setUser(user);
            commentDto.add(dto);
        }
        ResultDto resultDto=new ResultDto();
        //返回数据给前端
        return resultDto.success(commentDto);
    }
}
