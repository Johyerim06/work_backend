// com.officeapp.graph_mail_backend.controller.NoteTemplateController.java

package com.officeapp.graph_mail_backend.controller;

import com.officeapp.graph_mail_backend.model.NoteTemplate;
import com.officeapp.graph_mail_backend.repository.NoteTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/note-templates")
@CrossOrigin(origins = "http://localhost:5173") // 필요시 수정
public class NoteTemplateController {

    @Autowired
    private NoteTemplateRepository repository;

    @GetMapping
    public List<NoteTemplate> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public NoteTemplate create(@RequestBody NoteTemplate template) {
        return repository.save(template);
    }

    @PutMapping("/{id}")
    public NoteTemplate update(@PathVariable String id, @RequestBody NoteTemplate template) {
        template.setId(id);
        return repository.save(template);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }
}
