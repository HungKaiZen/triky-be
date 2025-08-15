package vn.tayjava.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Constraint(validatedBy = {BirthDayValidator.class})
public @interface ValidateDate {
    String message() default "Ngày sinh không hợp lệ (dd/MM/yyyy)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
