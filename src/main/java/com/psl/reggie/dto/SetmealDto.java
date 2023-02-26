package com.psl.reggie.dto;

import com.psl.reggie.entity.Category;
import com.psl.reggie.entity.Setmeal;
import com.psl.reggie.entity.SetmealDish;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper =true)
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes =new ArrayList<>();
    private String categoryName;
}
