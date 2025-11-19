package com.example.todolist.service;

import com.example.todolist.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public List<Task> findAll() {
        return tasks;
    }

    public Task create(Task task) {
        task.setId(sequence.getAndIncrement());
        tasks.add(task);
        return task;
    }

    public Optional<Task> findById(Long id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public boolean delete(Long id) {
        return tasks.removeIf(t -> t.getId().equals(id));
    }

    public Optional<Task> update(Long id, Task updated) {
        Optional<Task> opt = findById(id);
        opt.ifPresent(t -> {
            t.setTitle(updated.getTitle());
            t.setDescription(updated.getDescription());
            t.setCompleted(updated.isCompleted());
        });
        return opt;
    }

    public Optional<Task> toggleComplete(Long id) {
        Optional<Task> opt = findById(id);
        opt.ifPresent(t -> t.setCompleted(!t.isCompleted()));
        return opt;
    }
}
