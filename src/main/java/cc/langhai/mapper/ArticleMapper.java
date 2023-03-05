package cc.langhai.mapper;

import cc.langhai.domain.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文章表 Mapper
 *
 * @author langhai
 * @date 2022-12-24 16:35
 */
@Mapper
public interface ArticleMapper {

    /**
     * 获取用户某天发表文章数量
     *
     * @return
     */
    Integer getDayCount(Long userId, String beginDate, String endDate);

    /**
     * 新增文章
     *
     */
    void insertArticle(Article article);

    /**
     * 获取用户发布的所有文章
     *
     * @param userId 用户唯一标识
     * @param title
     * @param abstractText
     * @return 用户发布的所有文章list集合数据
     */
    List<Article> getAllArticle(Long userId, String title, String abstractText);

    /**
     * 获取一篇文章
     *
     * @param id
     * @return
     */
    Article getById(Long id);

    /**
     * 更新文章信息
     *
     * @param article
     */
    void updateArticle(Article article);

    /**
     * 对文章进行逻辑删除
     *
     * @param article
     */
    void deleteArticle(Article article);

    /**
     * 获取用户发布的所有文章
     *
     * @param labelId 标签id
     * @param searchArticleStr 文章搜索关键字
     * @return
     */
    List<Article> getAllArticlePublicShow(String searchArticleStr, Long labelId);
}
