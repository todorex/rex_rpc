package com.todorex.api.entitiy;

import lombok.Builder;
import lombok.Data;

/**
 * @Author rex
 * 2018/8/7
 */
@Data
@Builder
public class Person {
    private String firstName;
    private String lastName;
}
