package com.example.springboot_demo.modules.users.request.UserCatalouge;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

@Data
public class UpdateRequest {

    private String name;

    @Min(value = 0, message = "Giá trị trạng thái phải lớn hơn hoặc bằng 0")
    @Max(value = 2, message = "Giá trị trạng thái phải nhỏ hơn hoặc bằng 2")
    private Integer publish;
}
