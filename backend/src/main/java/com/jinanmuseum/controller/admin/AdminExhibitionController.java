package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.Exhibition;
import com.jinanmuseum.service.ExhibitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/exhibitions")
public class AdminExhibitionController {

    @Autowired
    private ExhibitionService exhibitionService;

    @GetMapping
    public Result<List<Exhibition>> list() {
        return Result.success(exhibitionService.getExhibitions(null));
    }

    @PostMapping
    public Result<Exhibition> save(@RequestBody Exhibition exhibition) {
        exhibitionService.saveExhibition(exhibition);
        return Result.success(exhibition);
    }

    @PutMapping
    public Result<Exhibition> update(@RequestBody Exhibition exhibition) {
        exhibitionService.saveExhibition(exhibition);
        return Result.success(exhibition);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        exhibitionService.deleteExhibition(id);
        return Result.success("删除成功");
    }
}