package com.jinanmuseum.service;

import com.jinanmuseum.entity.MuseumEvent;
import com.jinanmuseum.mapper.MuseumEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MuseumEventService {

    @Autowired
    private MuseumEventMapper museumEventMapper;

    public List<MuseumEvent> getEvents(String category) {
        if (category == null || category.isEmpty()) {
            return museumEventMapper.selectList(null);
        }
        return museumEventMapper.selectByCategory(category);
    }

    public MuseumEvent getEventById(Long id) {
        return museumEventMapper.selectById(id);
    }

    public void saveEvent(MuseumEvent event) {
        if (event.getId() == null) {
            museumEventMapper.insert(event);
        } else {
            museumEventMapper.updateById(event);
        }
    }

    public void deleteEvent(Long id) {
        museumEventMapper.deleteById(id);
    }
}