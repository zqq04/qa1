package com.group.qa.service;

import com.group.qa.dto.CommentDto;
import com.group.qa.dto.PageDto;
import com.group.qa.entity.Comment;
import com.group.qa.entity.User;
import com.group.qa.mapper.CommentMapper;
import com.group.qa.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserMapper userMapper;

    public List<CommentDto> getByid(int id) {
        //通过问题id找到所有回复
        List<Comment> comments=commentMapper.getByid(id);
        //创建要给CommentDto的list
        List<CommentDto> commentDtoList=new ArrayList<>();
        //遍历每个Comment
        for(Comment comment:comments){
            //找到回复人
            User user=userMapper.findById(comment.getCommentor());
            CommentDto commentDto=new CommentDto();
            //将第一个元素复制到第二个元素中
            BeanUtils.copyProperties(comment,commentDto);
            commentDto.setUser(user);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }


    public PageDto list(int userid, int page, int size) {
        //通过用户id找到所有回复
        PageDto pageDto = new PageDto();
        int totalcount = commentMapper.countbyid(userid);
        pageDto.setPagination(totalcount,page,size);
        //size*{page-1}
        int offset = size * (page - 1);
        //每页只展示5条

        List<Comment> comments = commentMapper.listbyid(userid,offset, size);
        List<CommentDto> commentDtoList = new ArrayList<>();
        //遍历每个Comment
        for (Comment comment : comments) {
            //找到回复人
            User user = userMapper.findById(comment.getCommentor());
            CommentDto commentDto = new CommentDto();

            //将第一个元素复制到第二个元素中
            BeanUtils.copyProperties(comment,commentDto);
            commentDto.setUser(user);
            commentDtoList.add(commentDto);

        }
        pageDto.setData(commentDtoList);
        return pageDto;
    }
}
