package io.collective.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.restsupport.BasicHandler;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.collective.articles.ArticleInfo;

public class ArticlesController extends BasicHandler {
    private final ArticleDataGateway gateway;

    public ArticlesController(ObjectMapper mapper, ArticleDataGateway gateway) {
        super(mapper);
        this.gateway = gateway;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        get("/articles", List.of("application/json", "text/html"), request, servletResponse, () -> {

            { // todo - query the articles gateway for *all* articles, map record to infos, and send back a collection of article infos
                List<ArticleInfo> allArticle = this.gateway.findAll().stream().map(article -> new ArticleInfo(article.getId(), article.getTitle())).collect(Collectors.toList());
                this.writeJsonBody(servletResponse,allArticle);
            }
        });

        get("/available", List.of("application/json"), request, servletResponse, () -> {

            { // todo - query the articles gateway for *available* articles, map records to infos, and send back a collection of article infos
                List<ArticleInfo> allAvailableArticle = this.gateway.findAvailable().stream().map(articleRecord -> new ArticleInfo(articleRecord.getId(),articleRecord.getTitle())).collect(Collectors.toList());
                this.writeJsonBody(servletResponse,allAvailableArticle);
            }
        });
    }
}
