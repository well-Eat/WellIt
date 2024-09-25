package com.wellit.project.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping("/answers")
    public Answer addAnswer(@RequestBody Answer answer) {
        return answerService.saveAnswer(answer.getQuestionId(), answer.getContent());
    }

    @GetMapping("/answers/{questionId}")
    public List<Answer> getAnswers(@PathVariable("questionId") Long questionId) {
        return answerService.getAnswersByQuestionId(questionId);
    }
    
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("answerId") Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
}
