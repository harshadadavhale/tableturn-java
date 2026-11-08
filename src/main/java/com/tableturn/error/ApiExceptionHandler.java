
package com.tableturn.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.tableturn.service.TableService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    // TICKET 1: Unknown branch
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

    // TICKET 1: Unknown table
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

    // TICKET 1: Invalid section or state
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

    // TICKET 1: Unknown URL
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

    // TICKET 2: Request validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );

        problem.setType(URI.create("/errors/validation"));
        problem.setInstance(URI.create(request.getRequestURI()));

        List<Map<String, String>> errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> Map.of(
                                "field", error.getField(),
                                "message", error.getDefaultMessage() == null
                                        ? "Invalid value"
                                        : error.getDefaultMessage()
                        ))
                        .toList();

        problem.setProperty("errors", errors);

        return problem;
    }

    // TICKET 2: Invalid or unreadable request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleInvalidBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String field = "body";
        Throwable cause = ex;

        while (cause != null) {
            if (cause instanceof JsonMappingException mapping
                    && !mapping.getPath().isEmpty()) {

                String fieldName =
                        mapping.getPath().get(0).getFieldName();

                if (fieldName != null) {
                    field = fieldName;
                }

                break;
            }

            cause = cause.getCause();
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid request body"
        );

        problem.setType(URI.create("/errors/validation"));
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("errors", List.of(
                Map.of(
                        "field", field,
                        "message", "Invalid or missing value"
                )
        ));

        return problem;
    }

    // TICKET 2: Party size exceeds maximum table capacity
    @ExceptionHandler(PartyTooBig.class)
    public ProblemDetail handlePartyTooBig(
            PartyTooBig ex,
            HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problem.setType(URI.create("/errors/party-too-big"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("maxCovers", ex.getMaxCovers());

        return problem;
    }

    // TICKET 2: Unknown party
    @ExceptionHandler(NoSuchParty.class)
    public ProblemDetail handleNoSuchParty(
            NoSuchParty ex,
            HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problem.setType(URI.create("/errors/no-such-party"));
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    // Common method for Ticket 1 errors
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
