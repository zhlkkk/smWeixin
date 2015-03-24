package com.sm.controller;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.sm.entity.Category;
import com.sm.entity.Consult;
import com.sm.entity.Resource;
import com.sm.entity.Video;
import com.sm.repository.CategoryRepository;
import com.sm.repository.ConsultRepository;
import com.sm.repository.ResourceRepository;
import com.sm.repository.VideoRepository;
import com.sm.util.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ez丶kkk on 15/2/7.
 */
@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ConsultRepository consultRepository;

    @RequestMapping(value={"/","/about"})
    public String index() {
        return "about";
    }

    @RequestMapping("resourceList")
    public ModelAndView resourceList() {
        ModelAndView mv = new ModelAndView();
        List<Category> categorys = categoryRepository.findByTypeNot("products");

        if (!CollectionUtils.isEmpty(categorys)) {
            final Map<Long,String> cateMap = Maps.newHashMapWithExpectedSize(categorys.size());

            for (Category cate : categorys) {
                cateMap.put(cate.getId(), cate.getName());
            }

            Multimap<String, Resource> multiMap = ArrayListMultimap.create();
            List<Resource> resources = resourceRepository.findByCategoryIdIn(cateMap.keySet());
            for (Resource resource : resources) {
                multiMap.put(cateMap.get(resource.getCategoryId()), resource);
            }
            mv.addObject("resultMap", multiMap);
            String infoType = categorys.get(0).getType();
            if(infoType.equals("products")){
                mv.addObject("infoType","产品篇");
            }else if(infoType.equals("schemes")){
                mv.addObject("infoType","方案篇");
            }else if(infoType.equals("case")){
                mv.addObject("infoType","案例篇");
            }else if(infoType.equals("demos")){
                mv.addObject("infoType","Demo篇");
            }else{
                mv.addObject("infoType","未知");
            }
        }else{
            mv.addObject("infoType","未知");
        }
        mv.setViewName("resourcesList");

        return mv;
    }

    @RequestMapping("resource")
    public ModelAndView resource(@RequestParam Long resourceId) {
        Resource resource = resourceRepository.findOne(resourceId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("resource", resource);
        mv.setViewName("resource");
        return mv;
    }

    @RequestMapping("videoList")
    public ModelAndView videoList() {
        List<Video> videos = videoRepository.findAll();
        ModelAndView mv = new ModelAndView();
        mv.addObject("videos", videos);
        mv.setViewName("videoList");
        mv.addObject("infoType","视频篇");
        return mv;
    }

    @RequestMapping("video")
    public ModelAndView video(@RequestParam Long videoId) {
        Video video = videoRepository.findOne(videoId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("video", video);
        mv.setViewName("video");
        return mv;
    }

    @RequestMapping("productIntro")
    public ModelAndView productIntro() {
        ModelAndView mv = new ModelAndView();
        List<Category> category = categoryRepository.findByType("products");
        if (!CollectionUtils.isEmpty(category)) {
            List<Resource> resources = resourceRepository.findByCategoryId(category.get(0).getId());
            List<Resource> temp = null;
            Map<String,List<Resource>> result = Maps.newHashMapWithExpectedSize(resources.size());
            for (Resource resource : resources) {
                String categoryName = resource.getCategoryName();
                if (result.containsKey(categoryName)) {
                    temp = result.get(categoryName);
                    temp.add(resource);
                }else{
                    temp = Lists.newArrayListWithExpectedSize(1);
                    temp.add(resource);
                    result.put(categoryName,temp);
                }
            }
            mv.addObject("resultMap", result);
            mv.addObject("infoType","产品篇");
        }else{
            mv.addObject("infoType","未知");
        }
        mv.setViewName("productIntro");
        return mv;
    }

    @RequestMapping("product")
    public ModelAndView product(@RequestParam Long resourceId) {
        Resource resource = resourceRepository.findOne(resourceId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("resource", resource);
        mv.setViewName("product");
        return mv;
    }

    @RequestMapping("newsList")
    public String newsList() {
        return "newsList";
    }

    @RequestMapping("news")
    public String news() {
        return "news";
    }

    @RequestMapping(value = "newConsult", method = RequestMethod.GET)
    public String newConsult() {
        return "newConsult";
    }

    @RequestMapping(value = "newConsultPost", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult newConsultPost(@ModelAttribute Consult consult) {
        JsonResult result = new JsonResult();
        consult.setCreated(DateTime.now());
        consult.setUpdated(DateTime.now());
        Consult con = consultRepository.save(consult);
        result.setSuccess(null != con.getId());
        return result;
    }

    @RequestMapping(value = "viewConsult", method = RequestMethod.GET)
    public ModelAndView viewConsult(@RequestParam Long consultId) {
        Consult consult = consultRepository.findOne(consultId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("consult", consult);
        mv.setViewName("viewConsult");
        return mv;
    }

    @RequestMapping(value = "consultList", method = RequestMethod.GET)
    public ModelAndView consultList() {
        List<Consult> consults = consultRepository.findAll();
        ModelAndView mv = new ModelAndView();
        mv.addObject("consults", consults);
        mv.setViewName("consultList");
        mv.addObject("infoType","咨询篇");
        return mv;
    }
}
