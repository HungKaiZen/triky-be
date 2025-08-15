package vn.tayjava.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class BirthDayValidator implements ConstraintValidator<ValidateDate, String> {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    @Override
    public void initialize(ValidateDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false; // hoặc true nếu bạn muốn cho phép trường null
        }
        try {
            LocalDate date = LocalDate.parse(value.trim(), FORMATTER);
            // Optional: kiểm tra ngày không lớn hơn hiện tại
            if (date.isAfter(LocalDate.now())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }



}
