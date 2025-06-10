package org.uni_bag.uni_bag_spring_boot_app.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ExampleHolder {
    private Example holder;
    private String name;
    private int code;

    public static ExampleHolder of(Example holder, String name, int code){
        return ExampleHolder.builder()
                .holder(holder)
                .name(name)
                .code(code)
                .build();
    }
}