package com.sergio.memo_bot.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CommandHandlerLoggingAspect {

    @Before("execution(* com.sergio.memo_bot.command_handler.CommandHandler+.getReply(..))")
    public void logGetReplyCall(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        String className = target.getClass().getSimpleName();

        Object[] args = joinPoint.getArgs();
        log.info("Handler: {}, getReply called with: {}", className, args[0]);
    }

}
