<%--
  Created by IntelliJ IDEA.
  User: dzc
  Date: 2020/10/9
  Time: 20:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="editModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">尚筹网系统弹窗</h4>
            </div>
            <div class="modal-body">
                <form class="form-signin" role="form">
                    <div class="form-group has-success has-feedback">
                        <input type="text" name="roleName" class="form-control" id="inputSuccess5" placeholder="请输入角色名称" autofocus>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button id="editRoleBtn" type="button" class="btn btn-primary"> 更 新
                </button>
            </div>
        </div>
    </div>
</div>