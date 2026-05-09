package com.soft.module.controller;

import com.soft.base.service.SysUsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebController {

    private final SysUsersService sysUsersService;
}
