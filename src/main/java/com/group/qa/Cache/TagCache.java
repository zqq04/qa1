package com.group.qa.Cache;

import com.group.qa.dto.TagDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagCache {
    public List<TagDto> gettags(){
        List<TagDto> tags=new ArrayList<>();

        TagDto categ=new TagDto();
        categ.setCategoryname("全站");
        categ.setTags(Arrays.asList("科学","数码","时尚","影视","IT技术"));
        tags.add(categ);

        TagDto categ2=new TagDto();
        categ2.setCategoryname("校园");
        categ2.setTags(Arrays.asList("学习","生活","技术","美食","摄影"));
        tags.add(categ2);

        return tags;
    }
}
