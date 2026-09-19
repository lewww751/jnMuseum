package com.jinanmuseum.controller.public_;

import com.jinanmuseum.entity.Exhibition;
import com.jinanmuseum.service.ExhibitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    @Autowired
    private ExhibitionService exhibitionService;

    @GetMapping
    public List<Exhibition> getExhibitions(String kind) {
        return exhibitionService.getExhibitions(kind);
    }

    @GetMapping("/{id}")
    public Exhibition getExhibitionById(@PathVariable Long id) {
        return exhibitionService.getExhibitionById(id);
    }
}