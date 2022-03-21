function like(btn, entityType, entityId, entityCreateUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {
            "entityType": entityType,
            "entityId": entityId,
            "entityCreateUserId": entityCreateUserId,
            "postId": postId
        },
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("c").text(data.likeCount);
                $(btn).children("i").text(data.likeStatus == 1 ? "已赞" : "赞");
            } else {
                alert(data.msg);
            }
        }
    )
}