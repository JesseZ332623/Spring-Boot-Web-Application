package org.example.controller.areacontroller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import response.Response;
import org.example.databaseaccess.area.Area;
import org.example.service.areaqueryservice.AreaQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器类，数据再网页的渲染由它控制。
 */
@RestController
public class AreaController
{
    private AreaQueryService queryService;

    @Autowired
    AreaController(AreaQueryService areaQueryService) {
        this.queryService = areaQueryService;
    }

    /**
     * 使用 Get 请求访问服务器的 /area/{id} URL 时，
     * 显示 {id} 对应的数据行的 JSON 文本。
     *
     * @param id 要查询的数据行 id。
     *           该参数被 @PathVariable 注解，
     *           表明该参数会绑定为 URL 模板变量。<br>
     *
     *           比如从客户端传来的 Http Get 请求为 localhost:8080/area/14，
     *           那么这个 14 就会作为该控制器方法的参数。
     *
     * @return 返回一个响应体给客户端，表明请求成功或失败。<br>
     *         如果成功就显示查询的结果（JSON 文本）      <br>
     *         如果失败就显示原因。
     *
     */
    @GetMapping("/area/{id}")
    public Response<Area> getAreaById(@PathVariable int id)
    {
        try
        {
            Area queryArea = this.queryService.getAreaById(id);

            return Response.newSuccess(
                    queryArea,
                    String.format("Query id = {%d} table data row success.", id)
            );
        }
        catch (RuntimeException exception)
        {
            return Response.newFailed(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 使用 Post 请求，在访问 /area URL 时，向其发送一个 JSON 请求体（@RequestBody 注解），
     * 服务器再将数据加入数据表，返回加入数据表的这一行数据的 id。
     *
     * @param area 由 @RequestBody 注解，表明它一个网络请求体。<br>
     *
     *             比如用户通过 Http Post 请求发送下面的 JSON 文本：
     * <pre>{@code
     *       "provinceID": 0,
     *       "cityID": 0,
     *       "areaID": 0,
     *       "provinceName": "美国华盛顿",
     *       "cityName": "哥伦比亚特区",
     *       "areaName": "白宫",
     *       "code": "111111",
     *       "centerPos": "38°53′42.4″N, 77°02′12″W"
     *     }</pre>
     *
     *     它会被整合成一个 Request Body 交由该控制器方法。
     *
     * @return 返回响应体。
     */
    @PostMapping("/area")
    public Response<Integer> addNewItem(@RequestBody Area area)
    {
        try
        {
            Integer newItemId = this.queryService.addNewItem(area);

            return Response.newSuccess(
                    newItemId, String.format("Add new data row id = {%d}", newItemId)
            );
        }
        catch (IllegalStateException exception)
        {
            return Response.newFailed(
                    exception.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * 使用 Http Delete 请求，
     * 在访问服务器的 /area/{id} URL 时，删除 id 对应的数据条目。
     *
     * @param id 要查询的 id。
     *           该参数被 @PathVariable 注解，表明该参数会绑定为 URL 模板变量。<br>
     *
     *           比如用户发起 Https Post 请求：localhost:8080/area/114 <br>
     *           那么这个 114 就会作为该控制器方法的参数。
     *
     * @return 返回响应体。
     */
    @DeleteMapping("/area/{id}")
    public Response<Integer> deleteAreaById(@PathVariable int id)
    {
        try
        {
            Integer deleteItemId = this.queryService.deleteAreaById(id);

            return Response.newSuccess(
                    deleteItemId,
                    String.format("Delete data row id = {%d}.", deleteItemId)
            );
        }
        catch (IllegalStateException exception)
        {
            return Response.newFailed(
                    exception.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * 使用 Put 请求，在访问服务器的 /area/{id} URL 时，更新 id 对应的数据条目。
     * 此处没有使用请求体，因此在 Postman 中不能使用 JSON 文本向服务器发送数据。
     */
//    @PutMapping("/area/{id}")
//    public Response<Integer> updateArea(
//            @PathVariable int id,
//            @RequestParam(required = false) int provinceID,
//            @RequestParam(required = false) int cityID,
//            @RequestParam(required = false) int areaID,
//            @RequestParam(required = false) String proivnce,
//            @RequestParam(required = false) String city,
//            @RequestParam(required = false) String area,
//            @RequestParam(required = false) String code,
//            @RequestParam(required = false) String center
//    )
//    {
//        Integer updateId = this.queryService.updateAreaByID(
//                id, provinceID, cityID, areaID,
//                proivnce, city, area, code, center
//        ).getId();
//
//        return Response.newSuccess(
//                updateId,
//                String.format("Update data row id = {%d} success.\n", id)
//        );
//    }

    /**
     * 使用 Put 请求，在访问服务器的 /area/aid} URL 时，更新 id 对应的数据条目。<br>
     * 此处使用请求体，因此在 Postman 中应该使用 JSON 文本向服务器发送数据（我更喜欢这种操作方式）。
     *
     * @param area 将实体对象 rea 作为请求体，
     *             比如用户发起如下 Http Put 请求：<br>
     *             localhost:8080/area 并发送这样的 JSON 文本给服务器：
     *
     * <pre>
     *             {@code
     *                  "id": 3391,
     *                  "provinceID": 666,
     *                  "cityID": 66,
     *                  "areaID": 6,
     *                  "provinceName": "加利福尼亚州",
     *                  "cityName": "洛杉矶郡",
     *                  "areaName": "洛杉矶市",
     *                  "code": "1010101010",
     *                  "centerPos": "34°06′N 118°12′W"
     *            }
     * </pre>
     *
     * 那么这个 JSON 文本就会整合成请求体，作为该控制器方法的参数。
     *
     *
     * @return 返回响应体。
     */
    @PutMapping("/area")
    public Response<Integer> updateArea(@RequestBody Area area)
    {
        try
        {
            Integer updateId = this.queryService.updateAreaByID(area).getId();

            return Response.newSuccess(
                    updateId,
                    String.format("Update data row id = {%d} success.", updateId)
            );
        }
        catch (IllegalStateException exception)
        {
            return Response.newFailed(
                    exception.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }

    @RequestMapping(path = "/", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> getAllowMethod()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Allow", "GET, POST, PUT, DELETE, OPTION");

        return new ResponseEntity<>(
                "Allow GET, POST, PUT, DELETE, OPTION",
                headers, HttpStatus.OK
        );
    }
}

