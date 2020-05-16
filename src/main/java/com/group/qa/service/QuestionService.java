package com.group.qa.service;

import com.group.qa.dto.PageDto;
import com.group.qa.dto.QuestionQueryDTO;
import com.group.qa.dto.Questiondto;
import com.group.qa.entity.Question;
import com.group.qa.entity.User;
import com.group.qa.enums.SortEnum;
import com.group.qa.mapper.CommentMapper;
import com.group.qa.mapper.QuestionMapper;
import com.group.qa.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Resource

@Service
public class QuestionService {

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    public PageDto list(String search, String tag, String sort, Integer page, Integer size) {

        if (StringUtils.isNotBlank(search)) {
            String[] tags = StringUtils.split(search, " ");
            search = Arrays
                    .stream(tags)
                    .filter(StringUtils::isNotBlank)
                    .map(t -> t.replace("+", "").replace("*", "").replace("?", ""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("|"));
        }

        PageDto PageDto = new PageDto();

        Integer totalPage;

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        if (StringUtils.isNotBlank(tag)) {
            tag = tag.replace("+", "").replace("*", "").replace("?", "");
            questionQueryDTO.setTag(tag);
        }

        for (SortEnum sortEnum : SortEnum.values()) {
            if (sortEnum.name().toLowerCase().equals(sort)) {
                questionQueryDTO.setSort(sort);

                if (sortEnum == SortEnum.HOT7) {
                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7);
                }
//                if (sortEnum == SortEnum.HOT30) {
//                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
//                }
                break;
            }
        }

        PageDto pageDto = new PageDto();
        int totalcount = questionMapper.count();
        pageDto.setPagination(totalcount,page,size);
        //size*{page-1}
        int offset = size * (page - 1);
        //每页只展示5条

        List<Question> questions = questionMapper.list(offset, size);


        List<Questiondto> questiondtoList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreateid());
            Questiondto questiondto = new Questiondto();
            //把第一个对象的所有属性拷贝到第二个对象中
            BeanUtils.copyProperties(question, questiondto);
            questiondto.setUser(user);
            questiondtoList.add(questiondto);
        }
        pageDto.setData(questiondtoList);
        return pageDto;
    }

//    public PageDto list(int page, int size) {
//        PageDto pageDto = new PageDto();
//        int totalcount = questionMapper.count();
//        pageDto.setPagination(totalcount,page,size);
//        //size*{page-1}
//        int offset = size * (page - 1);
//        //每页只展示5条
//        List<Question> questions = questionMapper.list(offset, size);
//        List<Questiondto> questiondtoList = new ArrayList<>();
//
//        for (Question question : questions) {
//            User user = userMapper.findById(question.getCreateid());
//            Questiondto questiondto = new Questiondto();
//            //把第一个对象的所有属性拷贝到第二个对象中
//            BeanUtils.copyProperties(question, questiondto);
//            questiondto.setUser(user);
//            questiondtoList.add(questiondto);
//        }
//        pageDto.setData(questiondtoList);
//        return pageDto;
//    }

    public PageDto list(int userid, int page, int size) {
        PageDto pageDto = new PageDto();
        int totalcount = questionMapper.countbyid(userid);
        pageDto.setPagination(totalcount,page,size);
        //size*{page-1}
        int offset = size * (page - 1);
        //每页只展示5条
        List<Question> questions = questionMapper.listbyid(userid,offset, size);
        List<Questiondto> questiondtoList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreateid());
            Questiondto questiondto = new Questiondto();
            //把第一个对象的所有属性拷贝到第二个对象中
            BeanUtils.copyProperties(question, questiondto);
            questiondto.setUser(user);
            questiondtoList.add(questiondto);
        }
        pageDto.setData(questiondtoList);
        return pageDto;
    }

    public Questiondto getbyid(int id) {
        Questiondto questiondto=new Questiondto();
        Question question=questionMapper.getbyId(id);
        //把第一个对象的所有属性拷贝到第二个对象中
        BeanUtils.copyProperties(question, questiondto);
        User user = userMapper.findById(question.getCreateid());
        questiondto.setUser(user);
        return questiondto;
    }

    public void increaseview(int id) {
        questionMapper.updateView(id);
    }

    public List<Question> getbytag(int id, String result) {
        return questionMapper.getbytag(id,result);
    }

    public List<Question> gettopten() {
        List<Question> questions=questionMapper.gettopten();
        return questions;
    }

    public Integer countBySearch(QuestionQueryDTO questionQueryDTO){return questionMapper.countBySearch(questionQueryDTO);}

    public List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO){ return questionMapper.selectBySearch(questionQueryDTO);}



    //搜索功能
    //模糊查找 title的相关内容
//    public List<Question> getQuestionByTitle(String description){return questionMapper.}
//    (String contents);
    //搜索记录相关
//    List<QuestionQueryDTO> getSearchHistory();
//    int addSearchHistory(QuestionQueryDTO questionQueryDTO);


}
