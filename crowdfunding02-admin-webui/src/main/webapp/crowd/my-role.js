function changeAllCheckbox() {
    var $allBox = $("#rolePageBody input[type=checkbox]");
    var $checkedBox = $allBox.filter(":checked");
    $("#allCheckbox").prop("checked", $allBox.length === $checkedBox.length);
}

function showDeleteModal(roleArray) {
    var $roleList = $("#roleList");
    $roleList.empty();
    window.roleIdArray = [];
    for (var i = 0; i < roleArray.length; i++) {
        var list = "<li>" + roleArray[i].name + "</li>";
        $roleList.append(list);
        window.roleIdArray.push(roleArray[i].id);
    }
    $("#deleteModal").modal("show");

}

function getPageInfoRemote() {
    //var ajaxData = {"pageNum": window.pageNum, "pageSize": window.pageSize, "keyword": window.keyword};
    // console.log(JSON.stringify(ajaxData).serialize());
    // console.log({"pageNum": window.pageNum, "pageSize": window.pageSize, "keyword": window.keyword}.serialize());
    //此处没有写contentType，为表单的默认编码方式，所以handler可以用@RequestParam获取
    //默认编码方式不可传json字符串，否则将字符串看作一个键,没有值
    var ajaxResult = $.ajax({
        url: "role/get/page/info.json",
        type: "post",
        data: {"pageNum": window.pageNum, "pageSize": window.pageSize, "keyword": window.keyword},
        async: false,
        dataType: "json"
    });

    //   console.log(ajaxResult)

    var statusCode = ajaxResult.status;

    //   console.log((statusCode))
    if (statusCode != 200) {
        layer.msg("失败！ 响应状态码=" + statusCode + " 说明信息=" + ajaxResult.statusText);
        return null;
    }

    var resultEntity = ajaxResult.responseJSON;
    //   console.log(resultEntity)
    var result = resultEntity.operationResult;

    if (result === "FAILED") {
        layer.msg(resultEntity.operationMessage);
        return null;
    }

    var pageInfo = resultEntity.queryData;
    return pageInfo
}

function fillTableBody(pageInfo) {
    $("#rolePageBody").empty();
    $("#Pagination").empty();
    if (pageInfo == null || pageInfo == undefined || pageInfo.list == null || pageInfo.list.length == 0) {
        $("#rolePageBody").append("<tr><td colspan='4' align='center'>抱歉！ 没有查询到您搜索的数据！ </td></tr>");
        return;
    }
    for (var i = 0; i < pageInfo.list.length; i++) {
        var role = pageInfo.list[i];
        var roleId = role.id;
        var roleName = role.name;
        var roleIdTd = "<input type='hidden' value='" + roleId + "'>";
        var numberTd = "<td>" + (i + 1) + "</td>";
        var checkboxTd = "<td><input type='checkbox'></td>";
        var roleNameTd = "<td>" + roleName + "</td>";

        var checkBtn = "<button id='" + roleId + "' type='button' class='btn btn-success btn-xs checkBtn'><i class='glyphicon glyphicon-check'></i></button>";
        var pencilBtn = "<button type='button' class='btn btn-primary btn-xs'><i class='glyphicon glyphicon-pencil'></i></button>";
        var removeBtn = "<button type='button' class='btn btn-danger btn-xs'><i class='glyphicon glyphicon-remove'></i></button>";

        var buttonTd = "<td>" + checkBtn + " " + pencilBtn + " " + removeBtn + "</td>";

        var tr = "<tr>" + roleIdTd + numberTd + checkboxTd + roleNameTd + buttonTd + "</tr>";

        $("#rolePageBody").append(tr);
    }
    generateNavigator(pageInfo);

}

function generateNavigator(pageInfo) {
    var totalRecord = pageInfo.total;
    // 声明相关属性
    var properties = {
        "num_edge_entries": 3,
        "num_display_entries": 5,
        "callback": paginationCallBack,
        "items_per_page": pageInfo.pageSize,
        "current_page": pageInfo.pageNum - 1,
        "prev_text": "上一页",
        "next_text": "下一页"
    }; // 调用 pagination()函数

    $("#Pagination").pagination(totalRecord, properties);
}

function paginationCallBack(pageIndex, jQuery) {
    window.pageNum = pageIndex + 1;
    // 调用分页函数
    generatePage();
// 取消页码超链接的默认行为
    return false;
}

function generatePage() {
    var pageInfo = getPageInfoRemote();
    fillTableBody(pageInfo);
}

// 声明专门的函数用来在分配 Auth 的模态框中显示 Auth 的树形结构数据
function fillAuthTree() {
// 1.发送 Ajax 请求查询 Auth 数据
    var ajaxReturn = $.ajax({
        "url": "assgin/get/all/auth.json",
        "type": "post",
        "dataType": "json",
        "async": false
    });
    if (ajaxReturn.status != 200) {
        layer.msg(" 请 求 处 理 出 错 ！ 响 应 状 态 码 是 ： " + ajaxReturn.status + " 说 明 是 ：" + ajaxReturn.statusText);
        return;
    } // 2.从响应结果中获取 Auth 的 JSON 数据
// 从服务器端查询到的 list 不需要组装成树形结构， 这里我们交给 zTree 去组装
    var authList = ajaxReturn.responseJSON.queryData;
// 3.准备对 zTree 进行设置的 JSON 对象
    var setting = {
        "data": {
            "simpleData": {
// 开启简单 JSON 功能
                "enable": true,
// 使用 categoryId 属性关联父节点， 不用默认的 pId 了
                "pIdKey": "categoryId"
            },
            "key": {
// 使用 title 属性显示节点名称， 不用默认的 name 作为属性名了
                "name": "title"
            }
        },
        "check": {
            "enable": true
        }
    };
// 4.生成树形结构
// <ul id="authTreeDemo" class="ztree"></ul>
    $.fn.zTree.init($("#authTreeDemo"), setting, authList);
// 获取 zTreeObj 对象
    var zTreeObj = $.fn.zTree.getZTreeObj("authTreeDemo");
// 调用 zTreeObj 对象的方法， 把节点展开
    zTreeObj.expandAll(true);
// 5.查询已分配的 Auth 的 id 组成的数组
    ajaxReturn = $.ajax({
        "url": "assign/get/assigned/auth/id/by/role/id.json",
        "type": "post",
        "data": {
            "roleId": window.roleId
        },
        "dataType": "json",
        "async": false
    });
    if (ajaxReturn.status != 200) {
        layer.msg(" 请 求 处 理 出 错 ！ 响 应 状 态 码 是 ： " + ajaxReturn.status + " 说 明 是 ："+ajaxReturn.statusText);
        return;
    }
// 从响应结果中获取 authIdArray
    var authIdArray = ajaxReturn.responseJSON.queryData;
// 6.根据 authIdArray 把树形结构中对应的节点勾选上
// ①遍历 authIdArray
    for (var i = 0; i < authIdArray.length; i++) {
        var authId = authIdArray[i];
// ②根据 id 查询树形结构中对应的节点
        var treeNode = zTreeObj.getNodeByParam("id", authId);
// ③将 treeNode 设置为被勾选
// checked 设置为 true 表示节点勾选
        var checked = true;
// checkTypeFlag 设置为 false， 表示不“联动”， 不联动是为了避免把不该勾选的勾选上
        var checkTypeFlag = false;
// 执行
        zTreeObj.checkNode(treeNode, checked, checkTypeFlag);
    }

}