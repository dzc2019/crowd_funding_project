package com.zcdeng.crowd.mvc.handler;

import com.zcdeng.crowd.entity.Menu;
import com.zcdeng.crowd.util.ResultEntity;
import com.zcdeng.crowd.service.api.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MenuHandler {
    @Autowired
    private MenuService menuService;

    @ResponseBody
    @RequestMapping("/menu/get/whole/tree.json")
    public ResultEntity<List<Menu>> getWholeTreeNew(){
        List<Menu> menuList = menuService.getAll();
//        Map<Integer,Menu> menuMap = new HashMap<>();
//
//        Menu root = null;
//
//        for (Menu menu: menuList
//             ) {
//            menuMap.put(menu.getId(), menu);
//        }
//
//        for (Menu menu: menuList
//        ) {
//            if(menu.getPid() == null){
//                root = menu;
//            }else{
//                Menu father = menuMap.get(menu.getPid());
//                father.getChildren().add(menu);
//            }
//        }

        return ResultEntity.successWithData(menuList);
    }

    @ResponseBody
    @RequestMapping("/menu/save.json")
    public ResultEntity<String> saveMenu(Menu menu) {
// Thread.sleep(2000);
        menuService.saveMenu(menu);
        return ResultEntity.successWithoutData();
    }

    // handler 代码
    @ResponseBody
    @RequestMapping("/menu/update.json")
    public ResultEntity<String> updateMenu(Menu menu) {
        menuService.updateMenu(menu);
        return ResultEntity.successWithoutData();
    }

    // handler 代码
    @ResponseBody
    @RequestMapping("/menu/remove.json")
    public ResultEntity<String> removeMenu(@RequestParam("id") Integer id) {
        menuService.removeMenu(id);
        return ResultEntity.successWithoutData();
    }
}
