package com.psl.reggie.dto;

import com.psl.reggie.entity.Setmeal;
import lombok.Data;

@Data
public class SetmealDto extends Setmeal {
    private String categoryName;
}
