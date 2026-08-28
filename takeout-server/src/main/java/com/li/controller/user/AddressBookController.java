package com.li.controller.user;

import com.li.common.result.Result;
import com.li.pojo.entity.AddressBook;
import com.li.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService service;

    /*
    新增地址
     */
    @PostMapping
    public Result add(@RequestBody AddressBook address) {
        service.add(address);
        return Result.success();
    }


    /*
    获取地址列表
     */
    @GetMapping("/list")
    public Result list() {
        return Result.success(service.list());
    }

    /*
    设置默认地址
     */
    @PutMapping("/default")
    public Result setDefault(@RequestParam Long id) {
        service.setDefault(id);
        return Result.success();
    }

    /*
    获取默认地址
     */
    @GetMapping("/default")
    public Result getByIsDefault() {
        return Result.success(service.getByIsDefault());
    }

    /*
    修改地址
     */
    @PutMapping
    public Result update(@RequestBody AddressBook address) {
        service.update(address);
        return Result.success();
    }

    /*
    根据id查询地址
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(service.getById(id));
    }
    /*
    根据id删除地址
     */
    @DeleteMapping
    public Result deleteById(@RequestParam Long id) {
        service.delete(id);
        return Result.success();
    }
}
