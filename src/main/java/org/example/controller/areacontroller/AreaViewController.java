package org.example.controller.areacontroller;

import org.example.databaseaccess.area.Area;
import org.example.service.areaqueryservice.AreaQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller 注解表示 AreaViewController 是处理 HTTP 请求的传统 Spring MVC 控制器，
 * 它通常返回一个视图（View）给前端网页。
 */
@Controller
public class AreaViewController
{
    private AreaQueryService queryService;

    /**
     * 从构造器注入服务层实例，是 Spring MVC 控制器类实现的最佳实践。
     *
     * @param areaQueryService 服务层实例，在运行时由 Spring 自行完成注入。
     */
    @Autowired
    AreaViewController(AreaQueryService areaQueryService) {
        this.queryService = areaQueryService;
    }

    /**
     * 使用 Get 请求，再访问 /area/AreaList URL 的时候，
     * 搜索 citydata.area 数据表中的所有条目，并传递给 model，
     * 返回到 AreasList.html 页面中，由 thymeleaf 框架完成数据的展示。
     *
     * @param model 一个接口，负责在控制器和视图之间传递数据，
     *              用户无需手动实例化 Model 接口的实现类，
     *              在 Get Http 请求到达控制器方法时，
     *              Spring MVC 会自动注入实现类（如 ExtendedModelMap）。
     *
     * @return 返回的视图名称，Spring 会根据视图解析器配置找到对应的文件
     */
    @GetMapping("/area/AreaList")
    public String listAreas(Model model)
    {
        List<Area> areas = this.queryService.getAllAreas(); // 查询表中的所有记录

        /*
        * 将记录添加到 model 中，视图层（这里是 thymeleaf 框架）
        * 就可以通过 ${areas} 取出列表中的所有内容，并将其渲染在页面上。
        */
        model.addAttribute("areas", areas);

        return "AreasList";
    }
}
