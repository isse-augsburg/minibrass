package de.isse.conf;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DependsOn {
	String parameter();
	Class<? extends Enum<?>>[] enumClass() default {}; 
	String[] allowedValues() default {};
} 
