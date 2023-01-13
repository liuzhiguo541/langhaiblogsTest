package cc.langhai.controller.links;

import cc.langhai.config.annotation.RequestAuthority;
import cc.langhai.domain.Article;
import cc.langhai.domain.Label;
import cc.langhai.domain.Links;
import cc.langhai.domain.Message;
import cc.langhai.response.LabelReturnCode;
import cc.langhai.response.LinksReturnCode;
import cc.langhai.response.ResultResponse;
import cc.langhai.service.ArticleService;
import cc.langhai.service.LabelService;
import cc.langhai.service.LinksService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 首页超链接管理 控制器
 *
 * @author langhai
 * @date 2023-01-12 13:36
 */
@Controller
@RequestMapping("/links")
public class LinksController {

    @Autowired
    private LinksService linksService;


    /**
     * 跳转到 友情链接管理页面
     *
     * @return
     */
    @RequestAuthority(value = {"admin"})
    @GetMapping("/linksListPage")
    public String linksListPage(String message, Model model){

        if(StrUtil.isNotBlank(message)){
            model.addAttribute("message", message);
        }
        return "blogs/links/linksList";
    }

    /**
     * 跳转到 友情链接新增页面
     *
     * @return
     */
    @RequestAuthority(value = {"admin"})
    @GetMapping("/linksAddPage")
    public String linksAddPage(HttpSession session, Model model){


        return "blogs/links/linksAdd";
    }

    /**
     * 跳转到 标签更新页面
     *
     * @return
     */
    /*@GetMapping("/labelUpdatePage")
    public String labelUpdatePage(Long id, Model model, String content){

        model.addAttribute("id", id);
        model.addAttribute("content", content);
        return "blogs/label/labelUpdate";
    }
*/
    /**
     * 获取 友情链接 列表页面数据
     *
     * @return
     */
    @RequestAuthority(value = {"admin"})
    @GetMapping("/linksList")
    @ResponseBody
    public JSONObject articleList(HttpSession session, Model model) throws InterruptedException {
        JSONObject jsonObject = new JSONObject();

        List<Links> linksList = linksService.list();

        jsonObject.put("code", 0);

        jsonObject.put("data", linksList);

        jsonObject.put("count", linksList.size());

        return jsonObject;
    }

    /**
     * 新增友情链接
     *
     * @return
     */
    @PostMapping("/addLinks")
    @RequestAuthority(value = {"admin"})
    @ResponseBody
    public ResultResponse addLinks(@RequestBody @Validated Links links, Model model){
        if(ObjectUtil.isNull(links)){
            return ResultResponse.fail(LinksReturnCode.LINKS_ADD_FAIL_00002);
        }

        linksService.addLinks(links);

        return ResultResponse.success(LinksReturnCode.LINKS_ADD_SUCCESS_00001);
    }


    /**
     * 删除友情链接
     *
     * @return
     */
    @PostMapping("/deleteLinks")
    @RequestAuthority(value = {"admin"})
    @ResponseBody
    public ResultResponse deleteLinks(Long id, Model model){
        if(ObjectUtil.isNull(id)){
            return ResultResponse.fail(LinksReturnCode.LINKS_DELETE_FAIL_00004);
        }

        linksService.deleteLinks(id);
        return ResultResponse.success(LinksReturnCode.LINKS_DELETE_SUCCESS_00003);
    }

    /**
     * 更新标签
     *
     * @return
     *//*
    @PostMapping("/updateLabel")
    @ResponseBody
    public ResultResponse updateLabel(Long id, Model model, String content){
        if(ObjectUtil.isNull(id) || StrUtil.isBlank(content)){
            return ResultResponse.fail(LabelReturnCode.LABEL_UPDATE_FAIL_00006);
        }
        labelService.updateLabel(id, content);
        return ResultResponse.success(LabelReturnCode.LABEL_UPDATE_SUCCESS_00005);
    }
    */

}
 