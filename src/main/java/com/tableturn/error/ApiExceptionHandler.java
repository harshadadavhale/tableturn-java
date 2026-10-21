
package com.tableturn.error;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;

import com.tableturn.service.TableService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(TableService.NoSuchBranchException.class)
    public ProblemDetail handleNoSuchBranch(
            TableService.NoSuchBranchException ex,
            HttpServletRequest request) {

        return createProblem(
                HttpStatus.NOT_FOUND,
                "Branch not found",
                ex.getMessage(),
                "/errors/no-such-branch",
                request
        );
    }

    @ExceptionHandler(TableService.NoSuchTableException.class)
    public ProblemDetail handleNoSuchTable(
            TableService.NoSuchTableException ex,
            HttpServletRequest request) {

        return createProblem(
                HttpStatus.NOT_FOUND,
                "Table not found",
                ex.getMessage(),
                "/errors/no-such-table",
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleInvalidFilter(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid request parameter",
                "Invalid section or state value.",
                "/errors/validation",
                request
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail handleUnknownPath(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        return createProblem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                "The requested URL does not exist.",
                "/errors/not-found",
                request
        );
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            String type,
            HttpServletRequest request) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, detail);

        problem.setTitle(title);
        problem.setType(URI.create(type));
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }
}
