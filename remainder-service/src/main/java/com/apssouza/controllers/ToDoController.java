package com.apssouza.controllers;

import com.apssouza.services.ToDoService;
import com.apssouza.entities.ToDo;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author apssouza
 */
@RequestMapping("/todos")
@RestController
public class ToDoController {

    @Autowired
    ToDoService toDoService;

    @GetMapping
    public List<ToDo> all() {
        return this.toDoService.all();
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid ToDo todo) {
        ToDo saved = this.toDoService.save(todo);
        Long id = saved.getId();
        if(id != null){
            URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(id).toUri();
            return ResponseEntity.created(location).build();
        }
         return ResponseEntity.noContent().build();
    }
    
    
    @PutMapping("{id}")
    public ToDo update(@PathVariable long id) {
        ToDo todo = toDoService.findById(id);
        todo.setId(id);
        return toDoService.save(todo);
    }

    @GetMapping("{id}")
    public ToDo find(@PathVariable long id) {
        return toDoService.findById(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        toDoService.delete(id);
    }

    @PutMapping("{id}/status")
    public ResponseEntity<?>  statusUpdate(@PathVariable long id, JsonNode statusUpdate) {
        JsonNode done = statusUpdate.get("done");
        if (!done.asBoolean()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    header("reason", "JSON should contains field done").
                    build();
        }
        
        ToDo todo = toDoService.updateStatus(id, done.asBoolean());
        if (todo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    header("reason", "todo with id " + id + " does not exist").
                    build();
        } else {
            return ResponseEntity.ok(todo);
        }
    }

}
