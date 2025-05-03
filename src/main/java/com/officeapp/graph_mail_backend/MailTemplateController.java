package com.officeapp.graph_mail_backend;

import com.officeapp.graph_mail_backend.dto.MailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class MailTemplateController {

    @Autowired
    private MailTemplateRepository repository;

    // 템플릿 저장
    @PostMapping("/save")
    public MailTemplate save(@RequestBody MailTemplate template) {
        return repository.save(template);
    }

    // 템플릿 전체 조회
    @GetMapping("/list")
    public List<MailTemplate> list() {
        return repository.findAll();
    }

    // 템플릿 삭제
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }
}
