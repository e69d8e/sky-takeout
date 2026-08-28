package com.li.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.li.pojo.entity.SetmealDish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetmealVO implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING) // 避免Long精度丢失
    private Long id;

    //分类id
    private Long categoryId;

    //套餐名称
    private String name;

    //套餐价格
    private BigDecimal price;

    //状态 0:停用 1:启用
    private Integer status;

    //描述信息
    private String description;

    //图片
    private String image;

    //更新时间
    // 格式化时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    //分类名称
    private String categoryName;

    //套餐和菜品的关联关系
    private List<SetmealDish> setmealDishes = new ArrayList<>();
}
