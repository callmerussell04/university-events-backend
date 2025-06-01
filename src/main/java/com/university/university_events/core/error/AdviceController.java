package com.university.university_events.core.error;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AdviceController {
    private final Logger log = LoggerFactory.getLogger(AdviceController.class);

    private static Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    private static Map<String, Object> getAttributes(HttpServletRequest request, Throwable throwable) {
        final Throwable rootCause = getRootCause(throwable);
        final StackTraceElement firstError = rootCause.getStackTrace()[0];
        return Map.of(
                "message", rootCause.getMessage(),
                "url", request.getRequestURL(),
                "exception", rootCause.getClass().getName(),
                "file", firstError.getFileName(),
                "method", firstError.getMethodName(),
                "line", firstError.getLineNumber());
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Throwable throwable) throws Throwable {
        if (AnnotationUtils.findAnnotation(throwable.getClass(),
                ResponseStatus.class) != null) {
            throw throwable;
        }

        log.error("{}", throwable.getMessage());
        throwable.printStackTrace();
        final ModelAndView model = new ModelAndView();
        model.addAllObjects(getAttributes(request, throwable));
        model.setViewName("error");
        return model;
    }
}
