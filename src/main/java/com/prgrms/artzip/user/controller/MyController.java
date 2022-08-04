package com.prgrms.artzip.user.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"users-me"})
@RestController
@RequestMapping("api/v1/users/me")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class MyController {

}
