package com.jinanmuseum.service;

import com.jinanmuseum.entity.Exhibition;
import com.jinanmuseum.mapper.ExhibitionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExhibitionService {

    @Autowired
    private ExhibitionMapper exhibitionMapper;

    public List<Exhibition> getExhibitions(String kind) {
        if (kind == null || kind.isEmpty()) {
            return exhibitionMapper.selectList(null);
        }
        return exhibitionMapper.selectByKind(kind);
    }

    public Exhibition getExhibitionById(Long id) {
        return exhibitionMapper.selectById(id);
    }

    public void saveExhibition(Exhibition exhibition) {
        if (exhibition.getId() == null) {
            exhibitionMapper.insert(exhibition);
        } else {
            exhibitionMapper.updateById(exhibition);
        }
    }

    public void deleteExhibition(Long id) {
        exhibitionMapper.deleteById(id);
    }
}