package com.c.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    @Pointcut("execution(* com.c.community.service.*.*(..))")
    public void pointcut() {
    }
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        // do something
        System.out.println("around before");
        Object object = joinPoint.proceed();
        // do something
        System.out.println("around after");

        return object;
    }
}
