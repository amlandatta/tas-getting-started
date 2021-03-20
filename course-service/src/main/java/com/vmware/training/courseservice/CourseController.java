package com.vmware.training.courseservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private Logger log = LoggerFactory.getLogger(CourseController.class);
    private Map<Integer, Course> courseRepository = new HashMap<>();


    public CourseController() {
        Course course1 = Course.builder().id(1).name("Spring Boot")
                .duration(20).build();
        Course course2 = Course.builder().id(2).name("Spring Cloud")
                .duration(30).build();
        Course course3 = Course.builder().id(3).name("Cloud Foundry")
                .duration(240).build();
        courseRepository.put(1, course1);
        courseRepository.put(2, course2);
        courseRepository.put(3, course3);
    }

    @GetMapping
    public Collection<Course> getCourses(){
        return courseRepository.values();
    }

    @PostMapping
    public ResponseEntity createCourse(@RequestBody Course course) {

        if(course != null && (course.getName() == null || course.getName().trim().length() == 0))
            return ResponseEntity.badRequest().body("Course name cannot be empty");

        course.setId(courseRepository.size() + 1);
        courseRepository.put(course.getId(), course);

        return ResponseEntity
                .ok(course)
                .status(201)
                .build();

    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable("id") Integer id, @RequestBody Course updatedCourse) {

        Course course = courseRepository.get(id);

        course.setName(updatedCourse.getName());
        course.setDuration(updatedCourse.getDuration());

        courseRepository.put(course.getId(), course);
        course = courseRepository.get(id);

        return course;

    }


    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable("id") Integer id) {
        Course course = courseRepository.get(id);
        log.info("Got course {}", course.toString());
        if(course != null)
            return ResponseEntity.ok(course);
        else
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCourse(@PathVariable("id") Integer id) {

        if(courseRepository.containsKey(id)) {
            courseRepository.remove(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }


}
