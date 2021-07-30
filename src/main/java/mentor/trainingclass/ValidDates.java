package mentor.trainingclass;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = DateValidator.class)
public @interface ValidDates {

    String message() default "Invalid end date!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
