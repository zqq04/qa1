package com.group.qa.mapper;

import com.group.qa.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface CommentMapper {
    @Insert("insert into comment(parent_id,type,commentor,createtime,content) " +
            "values (#{parent_id},#{type},#{commentor},#{createtime},#{content})")
    void insert(Comment comment);

    @Select("select * from comment where parent_id=#{id} order by createtime desc")
    List<Comment> getByid(int id);


    @Select("select count(1) from comment where commentor=#{userid}")
    int countbyid(int userid);

    @Select("select * from comment where commentor=#{userid} limit #{offset},#{size}")
    List<Comment> listbyid(@Param("userid") int userid, @Param("offset") int offset, @Param("size") int size);


    @Select("select DISTINCT  commentor from comment order by createtime desc  limit #{offset},#{size}")
    List<Comment> list( @Param("offset") int offset, @Param("size") int size);

    @Select("select * from comment where parent_id=#{id} and type=#{type} order by createtime desc")
    List<Comment> getCommentByid(@Param("id") int id, @Param("type") int type);

    @Update("update comment set commentcount=commentcount+1 where id=#{parent_id}")
    void updatecommentcount(int parent_id);

    @Select("select * from comment where id=#{parent_id}")
    Comment getparentbyid(int parent_id);

    @Select("select content from comment where id=#{outer}")
    String getcontentbyid(int outerid);

    @Select("select parent_id from comment where id=#{id}")
    int getparentidbyid(int id);
}
