package com.vmware.training.courseservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/courses")
public class CourseController {

    private Logger log = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseRepository courseRepository;


    public CourseController() {
//        Course course1 = Course.builder().name("Spring Boot")
//                .duration(20).build();
//        Course course2 = Course.builder().name("Spring Cloud")
//                .duration(30).build();
//        Course course3 = Course.builder().name("Cloud Foundry")
//                .duration(240).build();
//        courseRepository.save(course1);
//        courseRepository.save(course2);
//        courseRepository.save(course3);
    }

    @GetMapping
    public Collection<Course> getCourses(){
        return courseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity createCourse(@RequestBody Course course) {

        if(course != null && (course.getName() == null || course.getName().trim().length() == 0))
            return ResponseEntity.badRequest().body("Course name cannot be empty");

        Course newcourse = courseRepository.save(course);

        log.info(newcourse.toString());

        return ResponseEntity
                .ok(course)
                .status(201)
                .build();

    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable("id") Integer id, @RequestBody Course updatedCourse) {
        Course course =  courseRepository.save(updatedCourse);
        return course;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable("id") Integer id) {
        Optional<Course> course = courseRepository.findById(id);
        log.info("Course {}: ",course);
        if (course.isPresent()) {
            return ResponseEntity.ok(course.get());
        }
        else
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCourse(@PathVariable("id") Integer id) {
        Optional<Course> course = courseRepository.findById(id);
        log.info("Course {}: ",course);
        if (course.isPresent()) {
            courseRepository.delete(course.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Value("${course-service.uri}")
    private String uri;

    @Value("${course-service.dbconnection}")
    private String dbconnection;


    @GetMapping("/custom-config")
    public String getCustomConfig(){
        return this.uri;
    }

    @GetMapping("/secret-config")
    public String getSecretConfig(){
        return this.dbconnection;
    }


}

