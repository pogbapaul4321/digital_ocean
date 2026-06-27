package com.urlshortener.controller;

import com.urlshortener.service.UrlService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RedirectController {

    private final UrlService urlService;

    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{alias}")
    public String redirect(@PathVariable String alias) {
        return "redirect:" + urlService.resolveRedirect(alias);
    }
}
