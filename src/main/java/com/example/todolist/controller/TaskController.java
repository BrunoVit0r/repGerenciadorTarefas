package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping({"/", "/tasks"})
    public String index(Model model) {
        model.addAttribute("tasks", service.findAll());
        model.addAttribute("taskForm", new Task());
        return "index";
    }

    @PostMapping(value = "/tasks", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String create(@Valid Task taskForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tasks", service.findAll());
            model.addAttribute("taskForm", taskForm);
            return "index";
        }
        service.create(taskForm);
        return "redirect:/";
    }

    // Ajax JSON create (optional)
    @PostMapping(value = "/api/tasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object createJson(@Valid @RequestBody Task task) {
        // Basic server-side validation handled by @Valid
        Task created = service.create(task);
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", created.getId());
        resp.put("title", created.getTitle());
        resp.put("description", created.getDescription());
        resp.put("completed", created.isCompleted());
        return resp;
    }

    @PostMapping("/tasks/{id}/toggle")
    @ResponseBody
    public Object toggle(@PathVariable Long id) {
        return service.toggleComplete(id).map(t -> Map.of("ok", true, "completed", t.isCompleted()))
                .orElse(Map.of("ok", false));
    }

    @PostMapping("/tasks/{id}/delete")
    @ResponseBody
    public Object delete(@PathVariable Long id) {
        boolean removed = service.delete(id);
        return Map.of("ok", removed);
    }

    @GetMapping("/tasks/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Task task = service.findById(id).orElse(null);
        if (task == null) {
            return "redirect:/";
        }
        model.addAttribute("taskForm", task);
        return "edit";
    }

    @PostMapping("/tasks/{id}/update")
    public String update(@PathVariable Long id, @Valid Task taskForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taskForm", taskForm);
            return "edit";
        }
        service.update(id, taskForm);
        return "redirect:/";
    }
}
