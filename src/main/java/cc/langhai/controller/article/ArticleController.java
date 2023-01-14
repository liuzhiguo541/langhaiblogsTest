package cc.langhai.controller.article;

import cc.langhai.domain.Article;
import cc.langhai.domain.Label;
import cc.langhai.domain.User;
import cc.langhai.dto.ArticleDTO;
import cc.langhai.response.ArticleReturnCode;
import cc.langhai.response.ResultResponse;
import cc.langhai.service.ArticleService;
import cc.langhai.service.LabelService;
import cc.langhai.service.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章控制器
 *
 * @author langhai
 * @date 2022-12-22 22:22
 */
@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private LabelService labelService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    /**
     * 跳转到 文章 新发布页面
     *
     * @return
     */
    @GetMapping("/newArticlePage")
    public String newArticlePage(Model model){
        List<Label> labelList = labelService.getAllLabelByUser();

        // arrayList 用来存储文章标签的内容
        List<String> collect = labelList.stream().map(label -> label.getContent()).collect(Collectors.toList());
        model.addAttribute("labelList", collect);

        return "blogs/article/newArticle";
    }

    /**
     * 场景：用户发布文章，保存到数据库。
     *
     * @return 数据 200代表成功 其他代表失败
     */
    @PostMapping("/issue")
    @ResponseBody
    public ResultResponse issue(@RequestBody @Validated ArticleDTO articleDTO){
        articleService.issue(articleDTO);
        return ResultResponse.success(ArticleReturnCode.ARTICLE_ISSUE_OK_00000);
    }

    /**
     * 跳转到 文章 列表页面
     *
     * @return
     */
    @GetMapping("/articleListPage")
    public String articleListPage(Model model,
                                  @RequestParam(defaultValue = "1") Integer curr,
                                  @RequestParam(defaultValue = "10") Integer limitArticle){
        // 开启分页助手
        PageHelper.startPage(curr, limitArticle);

        List<Article> allArticle = articleService.getAllArticle();
        List<Article> articleHeat = articleService.getArticleHeat(allArticle);

        model.addAttribute("allArticle", articleHeat);
        model.addAttribute("curr", curr);
        return "blogs/article/articleList";
    }

    /**
     * 获取 文章 列表页面数据
     *
     * @return
     */
    @GetMapping("/articleList")
    @ResponseBody
    public JSONObject articleList(@RequestParam(defaultValue = "1") Integer curr,
                                  @RequestParam(defaultValue = "10") Integer limitArticle){
        JSONObject jsonObject = new JSONObject();

        PageHelper.startPage(curr, limitArticle);

        List<Article> allArticle = articleService.getAllArticle();

        jsonObject.put("code", 0);
        jsonObject.put("data", allArticle);

        PageInfo<Article> pageInfo = new PageInfo<>(allArticle);
        jsonObject.put("count", pageInfo.getTotal());
        return jsonObject;
    }


    /**
     * 跳转到展示文章详细内容的页面
     *
     * @param   id 文章id
     * @return 文章公开的情况下，页面 blogs/article/articleShow。
     *         文章不公开的情况下，验证当前用户与文章作者是否匹配。
     *                          匹配 页面 blogs/article/articleShow
     *                          不匹配 页面 blogs/user/login
     */
    @GetMapping("/articleShow")
    public String articleShow(Long id, Model model, HttpSession session){
        Article article = articleService.getById(id);
        if(ObjectUtil.isNull(article)){
            return "blogs/user/login";
        }
        Article articleHeat = articleService.getArticleHeat(article);

        boolean judgeShow = articleService.judgeShow(session, article);
        if(judgeShow){
            model.addAttribute("article", articleHeat);
            return "blogs/article/articleShow";
        }

        return "blogs/user/login";
    }

    /**
     * 跳转到文章编辑页面
     *
     * @return
     */
    @GetMapping("/updateArticlePage")
    public String updateArticlePage(Model model, Long id){
        if(ObjectUtil.isNull(id)){
            return "blogs/user/login";
        }

        // 查询要更新的文章
        Article article = articleService.getById(id);
        model.addAttribute("article", article);

        List<Label> labelList = labelService.getAllLabelByUser();
        // arrayList 用来存储文章标签的内容
        List<String> collect = labelList.stream().map(label -> label.getContent()).collect(Collectors.toList());
        model.addAttribute("labelList", collect);

        return "blogs/article/updateArticle";
    }

    /**
     * 场景：用户更新文章，保存到数据库。
     *
     * @param articleDTO
     * @return 数据 200代表成功 其他代表失败
     */
    @PostMapping("/updateArticle")
    @ResponseBody
    public ResultResponse updateArticle(@RequestBody @Validated ArticleDTO articleDTO){
        articleService.updateArticle(articleDTO);
        return ResultResponse.success(ArticleReturnCode.ARTICLE_UPDATE_OK_00003);
    }

    /**
     * 场景：用户逻辑删除文章，保存到数据库。
     *
     * @param id
     * @return 数据 200代表成功 其他代表失败
     */
    @PostMapping("/deleteArticle")
    @ResponseBody
    public ResultResponse deleteArticle(Long id){
        articleService.deleteArticle(id);
        return ResultResponse.success(ArticleReturnCode.ARTICLE_DELETE_OK_00005);
    }

    /**
     * 跳转到 文章 搜索页面
     *
     * @return
     */
    @GetMapping("/articleSearchPage")
    public String articleSearchPage(Model model,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                    String searchArticleStr){
        PageInfo<Article> pageInfo = articleService.search(page, size, searchArticleStr);

        model.addAttribute("list", articleService.getArticleHeat(pageInfo.getList()));
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("pages", pageInfo.getPages());
        model.addAttribute("search", searchArticleStr);
        return "blogs/article/articleSearch";
    }


    /**
     * 跳转到 文章 搜索页面 用于ES搜索引擎
     * 如果没有es搜索引擎组件 直接注释掉即可
     * 还需要用到mq组件 所以需要安装 elasticSearch 和 rabbitMQ
     *
     * @return
     */
    @GetMapping("/articleSearchESPage")
    public String articleSearchESPage(Model model,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    String searchArticleStr) throws IOException {
        HashMap<String, Object> hashMap = articleService.searchES(page, size, searchArticleStr);

        List<Article> list = (List<Article>) hashMap.get("list");
        Long pages = (Long) hashMap.get("pages");
        model.addAttribute("list", articleService.getArticleHeat(list));
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("pages", pages);
        model.addAttribute("search", searchArticleStr);
        return "blogs/article/articleSearchES";
    }
}
