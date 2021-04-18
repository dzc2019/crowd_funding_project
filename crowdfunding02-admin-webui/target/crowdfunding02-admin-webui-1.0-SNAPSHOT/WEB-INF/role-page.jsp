<%@ page language="java" contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>
<link rel="stylesheet" href="ztree/zTreeStyle.css"/>
<script type="text/javascript" src="ztree/jquery.ztree.all-3.5.min.js"></script>
<body>
<%@ include file="/WEB-INF/include-nav.jsp" %>
<div class="container-fluid">
    <div class="row">
        <%@ include file="/WEB-INF/include-sidebar.jsp" %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><i class="glyphicon glyphicon-th"></i> 数据列表</h3>
                </div>
                <div class="panel-body">
                    <form id="roleKeyword" method="post" class="form-inline" role="form" style="float:left;">
                        <div class="form-group has-feedback">
                            <div class="input-group">
                                <div class="input-group-addon">查询条件</div>
                                <input id="keywordInput" class="form-control has-success" type="text"
                                       placeholder="请输入查询条件">
                            </div>
                        </div>
                        <button id="searchBtn" type="button" class="btn btn-warning"><i
                                class="glyphicon glyphicon-search"></i> 查询
                        </button>
                    </form>
                    <button id="deleteBtn" type="button" class="btn btn-danger" style="float:right;margin-left:10px;"><i
                            class=" glyphicon glyphicon-remove"></i> 删除
                    </button>
                    <button type="button" id="addRoleBtn" class="btn btn-primary" style="float:right;"><i
                            class="glyphicon glyphicon-plus"></i> 新增
                    </button>
                    <br>
                    <hr style="clear:both;">
                    <div class="table-responsive">
                        <table class="table  table-bordered">
                            <thead>
                            <tr>
                                <th width="30">#</th>
                                <th width="30"><input id="allCheckbox" type="checkbox"></th>
                                <th>名称</th>
                                <th width="100">操作</th>
                            </tr>
                            </thead>

                            <tbody id="rolePageBody">
                            <tr>
                                <td colspan='4' align='center'>抱歉！ 没有查询到您搜索的数据！</td>
                            </tr>
                            </tbody>

                            <tfoot>
                            <tr>
                                <td colspan="6" align="center">
                                    <div id="Pagination" class="pagination"><!-- 这里显示分页 --></div>
                                </td>
                            </tr>

                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/modal-role-add.jsp" %>
<%@include file="/WEB-INF/modal-role-edit.jsp"%>
<%@include file="modal-role-delete.jsp"%>
<%@include file="/WEB-INF/modal-role-assign-auth.jsp" %>
</body>
<script type="text/javascript" src="crowd/my-role.js"></script>

<script type="text/javascript">
    $(function () {
// 1.为分页操作准备初始化数据
        window.pageNum = 1;
        window.pageSize = 10;
        window.keyword = "";
// 2.调用执行分页的函数， 显示分页效果
        generatePage();

        var $rolePageBody = $("#rolePageBody");

        $("#searchBtn").click(function () {
            window.keyword = $("#keywordInput").val();
            //  console.log($("#keywordInput").val());
            generatePage();
            $("#keywordInput").attr("value", window.keyword);
            return false;
        });

        $("#addRoleBtn").click(function () {
            $("#addModal").modal("show");
            return false;
        });

        $("#saveRoleBtn").click(function () {
            $.ajax({
                url: "role/save.json",
                type: "post",
                data: {"name": $.trim($("#inputSuccess4").val())},
                dataType: "json",
                success: function (response) {
                    var result = response.operationResult;

                    if (result == "FAILED") {
                        layer.msg("保存失败，请稍后再试");
                    } else {
                        layer.msg("保存成功");
                        window.pageNum = 0x7ffffff;
                        generatePage();
                    }
                },
                error: function (response) {
                    layer.msg("保存失败，请稍后再试:" + response.status + " " + response.statusText);
                }
            });

            $("#addModal").modal("hide");
            // 清理模态框
            $("#addModal [name=roleName]").val("");
        });

        $rolePageBody.on("click", ".btn.btn-primary.btn-xs",function () {
            $("#editModal").modal("show");

            var roleName = $(this).parent().prev().text();
            $("#inputSuccess5").val(roleName);
            // console.log($(this).parent().parent().children() instanceof jQuery);
            // console.log($(this).parent().parent().children()[0] instanceof jQuery);
            // console.log($(this).parent().prev().prev().prev().prev() instanceof jQuery);
            // console.log($(this).parent().prev().prev().prev().prev().find("input") instanceof jQuery);
            // console.log($(this).parent().prevAll().length);

            window.roleName = roleName;
            window.roleId = $(this).parent().prevAll().last().val();
          //  alert(window.roleId);
           // console.log($(this).parent().prevAll().last());
          // layer.msg(window.roleId);
            return false;
        })

        $("#editRoleBtn").click(function () {
            var roleName = $("#inputSuccess5").val().trim();
            if(roleName == window.roleName){
                layer.msg("保存成功");
            }else{
                $.ajax({
                    url:"role/update.json",
                    type: "post",
                    data:{"id": window.roleId, "name": roleName},
                    dataType: "json",
                    success: function (response) {
                        var result = response.operationResult;

                        if (result == "FAILED") {
                            layer.msg("保存失败，请稍后再试");
                        } else {
                            layer.msg("保存成功");
                        //    window.pageNum = 0x7ffffff;
                            generatePage();
                        }
                    },
                    error: function (response) {
                        layer.msg("保存失败，请稍后再试:" + response.status + " " + response.statusText);
                    }
                });
            }
            $("#editModal").modal("hide");
            // 清理模态框
            $("#editModal [name=roleName]").val("");
            return false;
        });

        $("#allCheckbox").click(function () {
         //   console.log(this.checked);
            $("#rolePageBody input[type=checkbox]").prop("checked",this.checked);
        });

        $rolePageBody.on("click","input[type=checkbox]",changeAllCheckbox());

        $("#deleteBtn").click(function () {
            var $selectRole = $("#rolePageBody input[type=checkbox]:checked");
            if($selectRole.length == 0){
                layer.msg("请勾选要删除的选项");
                return
            }
            var roleArray = [];
            $selectRole.each(function (index,element) {
                roleArray.push({
                    "id": $(element).parent().prevAll().last().val(),
                    "name": $(element).parent().next().text()
                });
            });
            showDeleteModal(roleArray);
            return false;
        });

        $rolePageBody.on("click",".btn.btn-danger.btn-xs",function () {
            var roleArray = [{
                "id":$(this).parent().prevAll().last().val(),
                "name": $(this).parent().prev().text()
            }];
            showDeleteModal(roleArray);
            return false;
        });

        $("#deleteRoleBtn").click(function () {
            // console.log({"ids": window.roleIdArray});
            // console.log(JSON.stringify({"ids": window.roleIdArray}));
            $.ajax({
                url: "role/delete.json",
                type: "post",
                data: JSON.stringify(window.roleIdArray),
                contentType:"application/json;charset=UTF-8",
                dataType: "json",
                success: function (response) {
                    generatePage();
                    changeAllCheckbox();
                    layer.msg("删除成功");
                },
                error: function (response) {
                    layer.msg("删除失败，请重试:" + response.status + " " + response.statusText);
                }
            });

            $("#deleteModal").modal("hide");
            $("#rolePageBody input[type=checkbox]").prop("checked",this.checked);

            return false;
        });

        $rolePageBody.on("click",".checkBtn",function(){
            window.roleId = this.id;

            $("#assignModal").modal("show");
// 在模态框中装载树 Auth 的形结构数据
            fillAuthTree();
            return false;
        });

        $("#assignBtn").click(function() {
// ①收集树形结构的各个节点中被勾选的节点
// [1]声明一个专门的数组存放 id
            var authIdArray = [];
// [2]获取 zTreeObj 对象
            var zTreeObj = $.fn.zTree.getZTreeObj("authTreeDemo");
// [3]获取全部被勾选的节点
            var checkedNodes = zTreeObj.getCheckedNodes();
            // [4]遍历 checkedNodes
            for (var i = 0; i < checkedNodes.length; i++) {
                var checkedNode = checkedNodes[i];
                var authId = checkedNode.id;
                authIdArray.push(authId);
            } // ②发送请求执行分配
            var requestBody = {
                "authIdArray": authIdArray,
// 为了服务器端 handler 方法能够统一使用 List<Integer>方式接收数据， roleId 也存入数组
                "roleId": [window.roleId]
            };
            requestBody = JSON.stringify(requestBody);
            $.ajax({
                "url": "assign/do/role/assign/auth.json",
                "type": "post",
                "data": requestBody,
                "contentType": "application/json;charset=UTF-8",
                "dataType": "json",
                "success": function (response) {
                    var result = response.operationResult;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功！ ");
                    }
                    if
                    (result == "FAILED") {
                        layer.msg("操作失败！ " + response.message);
                    }
                },
                "error": function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }
            });

            $("#assignModal").modal("hide");
        });
    });


</script>
</html>