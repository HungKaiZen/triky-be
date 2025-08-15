package vn.tayjava.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Handle exception when validate data 404(@PathVariable, @RequestBody, @RequestParam)
     * @param e
     * @param request
     * @return responseError
     * */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class
    })
    public ResponseError handleValidationException(Exception e, WebRequest request) {
        ResponseError responseError = new ResponseError();
        responseError.setTimestamp(new Date());
        responseError.setStatus(HttpStatus.BAD_REQUEST.value());
        responseError.setPath(request.getDescription(false).replace("uri=", ""));

        if (e instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) e, responseError);
        } else if (e instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) e, responseError);
        } else if (e instanceof MissingServletRequestParameterException) {
            return handleMissingServletRequestParameterException((MissingServletRequestParameterException) e, responseError);
        } else if (e instanceof IllegalArgumentException) {
            return handleIllegalArgumentException((IllegalArgumentException)e, responseError);
        } else {
            responseError.setMessage(e.getMessage());
            responseError.setError("Invalid Data");
        }

        return responseError;
    }

    /*
     * Handle InternalAuthenticationServiceException and BadCredentialsException (401)
     * @param e
     * @param request
     * @return responseError
     * */
    @ExceptionHandler({
            InternalAuthenticationServiceException.class,
            BadCredentialsException.class
    })
    public ResponseError handleInternalAuthenticationServiceException(Exception e, WebRequest request) {
        ResponseError responseError = new ResponseError();
        responseError.setTimestamp(new Date());
        responseError.setStatus(UNAUTHORIZED.value());
        responseError.setPath(request.getDescription(false).replace("uri=", ""));

        if (e instanceof InternalAuthenticationServiceException) {
            responseError.setMessage("Username or password is incorrect");
            responseError.setError(UNAUTHORIZED.getReasonPhrase());
        }else if (e instanceof BadCredentialsException) {
            responseError.setMessage("Username or password is incorrect");
            responseError.setError("Bad Credentials");
        }

        return responseError;
    }




    /*
     * Handle Access Denied Exception (403)
     * @param e
     * @param request
     * @return responseError
     * */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseError handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        ResponseError responseError = new ResponseError();
        responseError.setTimestamp(new Date());
        responseError.setStatus(HttpStatus.FORBIDDEN.value());
        responseError.setPath(request.getDescription(false).replace("uri=", ""));
        responseError.setMessage("You do not have permission to access this resource");
        responseError.setError("Access Denied");

        return responseError;
    }

    /*
     * Handle Resource not found exception (404)
     * @param e
     * @param request
     * @return responseError
     * */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseError handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ResponseError responseError = new ResponseError();
        responseError.setTimestamp(new Date());
        responseError.setStatus(HttpStatus.NOT_FOUND.value());
        responseError.setPath(request.getDescription(false).replace("uri=", ""));
        responseError.setError(e.getMessage());
        responseError.setMessage("Resource not found");

        return responseError;
    }

    /*
     * Handle Method allow exception (405)
     * @param e
     * @param request
     * @return responseError
     * */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseError handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e, WebRequest request) {
        ResponseError responseError = new ResponseError();
        responseError.setTimestamp(new Date());
        responseError.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        responseError.setPath(request.getDescription(false).replace("uri=", ""));
        responseError.setError(e.getMessage() + " for this endpoint");
        responseError.setMessage("Method not allowed");

        return responseError;
    }

    /*
     * Handle Duplicate key exception (409)
     * @param e
     * @param request
     * @return responseError
     * */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseError handleDuplicateKeyException(DuplicateKeyException e, WebRequest request) {
        ResponseError responseError = new ResponseError();
        responseError.setTimestamp(new Date());
        responseError.setStatus(HttpStatus.CONFLICT.value());
        responseError.setPath(request.getDescription(false).replace("uri=", ""));
        responseError.setError("Duplicate Key Error");
        responseError.setMessage(e.getMessage());

        return responseError;
    }

    /*
     * Handle Internal Server errors (500)
     * @param e
     * @param request
     * @return responseError
     * */
    @ExceptionHandler(Exception.class)
    public ResponseError handleException(Exception e, WebRequest request) {
        ResponseError responseError = new ResponseError();
        responseError.setTimestamp(new Date());
        responseError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseError.setPath(request.getDescription(false).replace("uri=", ""));
        responseError.setMessage("An Unexpected error occurred, please try again");
        responseError.setError("Internal Server Error");

        return responseError;
    }

    // ----------------- PRIVATE METHODS FOR VALIDATION HANDLING ----------------

    private ResponseError handleMissingServletRequestParameterException(MissingServletRequestParameterException e, ResponseError responseError) {
        responseError.setMessage("Missing required parameter: " + e.getParameterName());
        responseError.setError("Invalid Parameter");
        return responseError;
    }

    private ResponseError handleConstraintViolationException(ConstraintViolationException e, ResponseError responseError) {
        responseError.setMessage("Validation Failed");
        responseError.setError("Invalid parameter");
        List<String> errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        responseError.setErrors(errors);

        return responseError;
    }

    private ResponseError handleMethodArgumentNotValidException(MethodArgumentNotValidException e, ResponseError responseError) {
        responseError.setMessage("Validation Failed");
        responseError.setError("Invalid Payload");
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        responseError.setErrors(errors);

        return responseError;
    }

    private ResponseError handleIllegalArgumentException(IllegalArgumentException e, ResponseError responseError) {
        responseError.setMessage(e.getMessage());
        responseError.setError("Invalid Argument");

        return responseError;
    }



}
