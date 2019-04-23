package com.mondoralabs.workshop04;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.mondoralabs.workshop04.Workshop.Workshop;
import com.mondoralabs.workshop04.Workshop.WorkshopController;
import com.mondoralabs.workshop04.Workshop.WorkshopRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * WorkshopController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Workshop04Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WorkshopControllerTest {

    @Autowired
    private WorkshopController workshopController;

    @Autowired
    private WorkshopRepository repository;

    @Before
    public void initRepository() {
        Workshop workshop = new Workshop();
        repository.save(workshop);
    }

    @Test
    public void testEndpoint() {
        List<Workshop> response = workshopController.findAll();

        assertTrue(response.size() > 0);
    }
}
