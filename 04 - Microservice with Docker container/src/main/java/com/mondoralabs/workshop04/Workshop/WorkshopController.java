package com.mondoralabs.workshop04.Workshop;

import java.util.List;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * WorkshopController
 */
@RestController
@RequestMapping("/api/workshop")
public class WorkshopController {

    private WorkshopRepository workshopRepository;

    @Autowired
    public WorkshopController(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    @GetMapping
    @ResponseBody
    public List<Workshop> findAll() {
        return Lists.newArrayList(workshopRepository.findAll());
    }

    @PostMapping
    public void insertOne(@RequestBody Workshop workshop) {
        workshopRepository.save(workshop);
    }
}
