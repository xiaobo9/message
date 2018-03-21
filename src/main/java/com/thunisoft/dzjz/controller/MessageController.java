package com.thunisoft.dzjz.controller;

import com.thunisoft.dzjz.service.CocallMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author renxb
 * @version 4.0
 */
@Slf4j
@Controller
@RequestMapping("message")
public class MessageController {

    @Autowired
    private CocallMessageService service;

    @RequestMapping("/user/{user}")
    public ResponseEntity<Object> sendToUser(@PathVariable("user") String user) {
        if ("renxb".equals(user)) {
            service.testMessage(user);
        }

        return ResponseEntity.ok("test");
    }

    @RequestMapping("gitlab")
    public ResponseEntity<Object> gitlab(@RequestHeader("X-Gitlab-Event") String eventString, @RequestBody String messageString,
            HttpServletRequest request) {
        log.info(eventString);
        log.info("messageString: {}", messageString);
        service.dealEvent(eventString, messageString);
        return ResponseEntity.ok("test");
    }
}
